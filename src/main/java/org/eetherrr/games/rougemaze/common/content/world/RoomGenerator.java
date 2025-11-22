package org.eetherrr.games.rougemaze.common.content.world;

import org.eetherrr.games.rougemaze.common.content.world.base.block.Block;
import org.eetherrr.games.rougemaze.common.scene.Config;

import java.util.Random;

public class RoomGenerator {
	private static final Random random = new Random();
	
	// Debug helper: allow setting seed for deterministic generation in tests
	public static void setRandomSeed(long seed) {
		random.setSeed(seed);
	}
	
	public static void generateBase(final Block[][] blocks) {
		// 初始化顶部和底部的墙
		for(int j = 0; j<Config.ROOM_COLS; j++) {
			blocks[0][j] = new Block(Block.BlockType.WALL);
			blocks[Config.ROOM_ROWS-1][j] = new Block(Block.BlockType.WALL);
		}
		// 初始化中间行的墙和空地
		for(int i = 1; i<Config.ROOM_ROWS-1; i++) {
			blocks[i][0] = new Block(Block.BlockType.WALL);
			blocks[i][Config.ROOM_COLS-1] = new Block(Block.BlockType.WALL);
			for(int j = 1; j<Config.ROOM_COLS-1; j++) {
				blocks[i][j] = new Block(Block.BlockType.EMPTY);
			}
		}
	}
	
	public static void generateMazeInRoom(final Block[][] blocks) {
		int rows = blocks.length;
		int cols = blocks[0].length;
		// 初始化迷宫区域为墙（保留边界和门）
		for(int i = 1; i<rows-1; i++) {
			for(int j = 1; j<cols-1; j++) {
				if(blocks[i][j].getType()!=Block.BlockType.GATE) {
					blocks[i][j] = new Block(Block.BlockType.WALL);
				}
			}
		}
		// 确保门与内部区域连通
		ensureGatesConnected(blocks);
		// We'll carve a proper maze on the odd-cell grid to avoid parity issues.
		int cellRows = (rows-1)/2; // number of cells vertically
		int cellCols = (cols-1)/2; // number of cells horizontally
		boolean[][] visitedCells = new boolean[cellRows][cellCols];
		// Find a starting cell (prefer a cell adjacent to a gate)
		int[] startCell = findStartCell(blocks, visitedCells);
		if(startCell==null) {
			// fallback: choose center cell
			startCell = new int[]{cellRows/2, cellCols/2};
		}
		// Carve starting from startCell using cell-based recursive backtracking
		carveFromCell(blocks, visitedCells, startCell[0], startCell[1]);
		// Post-process: if the last inner row/column (adjacent to outer border) remains walls
		// while the cell next to them is empty, open them to avoid double-thick walls on south/east.
		int lastInnerRow = rows-2;
		int lastInnerCol = cols-2;
		int prevRow = rows-3;
		int prevCol = cols-3;
		// Open horizontal (south) inner row where appropriate
		if(prevRow>=1) {
			for(int j = 1; j<=cols-2; j++) {
				if(blocks[lastInnerRow][j].getType()==Block.BlockType.WALL && blocks[prevRow][j].getType()==Block.BlockType.EMPTY) {
					blocks[lastInnerRow][j] = new Block(Block.BlockType.EMPTY);
				}
			}
		}
		// Open vertical (east) inner column where appropriate
		if(prevCol>=1) {
			for(int i = 1; i<=rows-2; i++) {
				if(blocks[i][lastInnerCol].getType()==Block.BlockType.WALL && blocks[i][prevCol].getType()==Block.BlockType.EMPTY) {
					blocks[i][lastInnerCol] = new Block(Block.BlockType.EMPTY);
				}
			}
		}
		// Additional fix: if both prev and lastInner are WALL, open prev (closer to center) to ensure at least one empty
		if(prevRow>=1) {
			for(int j = 1; j<=cols-2; j++) {
				if(blocks[prevRow][j].getType()==Block.BlockType.WALL && blocks[lastInnerRow][j].getType()==Block.BlockType.WALL) {
					if(blocks[prevRow][j].getType()!=Block.BlockType.GATE) {
						blocks[prevRow][j] = new Block(Block.BlockType.EMPTY);
					}
				}
			}
		}
		if(prevCol>=1) {
			for(int i = 1; i<=rows-2; i++) {
				if(blocks[i][prevCol].getType()==Block.BlockType.WALL && blocks[i][lastInnerCol].getType()==Block.BlockType.WALL) {
					if(blocks[i][prevCol].getType()!=Block.BlockType.GATE) {
						blocks[i][prevCol] = new Block(Block.BlockType.EMPTY);
					}
				}
			}
		}
		// Final connectivity pass: ensure all EMPTY cells are mutually reachable. If an EMPTY cell is unreachable,
		// carve a simple Manhattan path to the closest reached EMPTY cell (turn walls into EMPTY) to fix isolated cells.
		int startRx = -1, startRy = -1;
		for(int i = 1; i<rows-1; i++) {
			for(int j = 1; j<cols-1; j++) {
				if(blocks[i][j].getType()==Block.BlockType.EMPTY) {
					startRx = i;
					startRy = j;
					break;
				}
			}
			if(startRx!=-1) {
				break;
			}
		}
		if(startRx!=-1) {
			boolean[][] seen = new boolean[rows][cols];
			java.util.ArrayDeque<int[]> dq = new java.util.ArrayDeque<>();
			seen[startRx][startRy] = true;
			dq.add(new int[]{startRx, startRy});
			while(!dq.isEmpty()) {
				int[] p = dq.poll();
				int x = p[0], y = p[1];
				int[][] d = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
				for(int[] dd : d) {
					int nx = x+dd[0], ny = y+dd[1];
					if(nx>=1 && nx<rows-1 && ny>=1 && ny<cols-1 && !seen[nx][ny] && blocks[nx][ny].getType()==Block.BlockType.EMPTY) {
						seen[nx][ny] = true;
						dq.add(new int[]{nx, ny});
					}
				}
			}
			// collect unreachable empties
			java.util.List<int[]> unreachable = new java.util.ArrayList<>();
			for(int i = 1; i<rows-1; i++) {
				for(int j = 1; j<cols-1; j++) {
					if(blocks[i][j].getType()==Block.BlockType.EMPTY && !seen[i][j]) {
						unreachable.add(new int[]{i, j});
					}
				}
			}
			for(int[] u : unreachable) {
				int ux = u[0], uy = u[1];
				// find nearest reached cell by Manhattan distance
				int bestDist = Integer.MAX_VALUE;
				int bx = -1, by = -1;
				for(int i = 1; i<rows-1; i++) {
					for(int j = 1; j<cols-1; j++) {
						if(seen[i][j]) {
							int dist = Math.abs(i-ux)+Math.abs(j-uy);
							if(dist<bestDist) {
								bestDist = dist;
								bx = i;
								by = j;
							}
						}
					}
				}
				if(bx==-1) {
					continue;
				}
				// carve a simple Manhattan path from (ux,uy) to (bx,by)
				int cx = ux, cy = uy;
				while(cx!=bx || cy!=by) {
					if(cx<bx) {
						cx++;
					}else if(cx>bx) {
						cx--;
					}else if(cy<by) {
						cy++;
					}else if(cy>by) {
						cy--;
					}
					if(blocks[cx][cy].getType()!=Block.BlockType.GATE) {
						blocks[cx][cy] = new Block(Block.BlockType.EMPTY);
					}
					seen[cx][cy] = true;
				}
			}
		}
	}
	
	// Map cell coords (cr,cc) to block coords: br = cr*2+1, bc = cc*2+1
	private static void carveFromCell(Block[][] blocks, boolean[][] visitedCells, int cr, int cc) {
		int cellRows = visitedCells.length;
		int cellCols = visitedCells[0].length;
		if(cr<0 || cr>=cellRows || cc<0 || cc>=cellCols) {
			return;
		}
		if(visitedCells[cr][cc]) {
			return;
		}
		visitedCells[cr][cc] = true;
		int br = cr*2+1;
		int bc = cc*2+1;
		// carve the cell center
		if(blocks[br][bc].getType()!=Block.BlockType.GATE) {
			blocks[br][bc] = new Block(Block.BlockType.EMPTY);
		}
		// randomized directions
		int[] dirs = new int[]{0, 1, 2, 3};
		for(int i = dirs.length-1; i>0; i--) {
			int idx = random.nextInt(i+1);
			int tmp = dirs[idx];
			dirs[idx] = dirs[i];
			dirs[i] = tmp;
		}
		for(int d : dirs) {
			int ncr = cr, ncc = cc;
			int wallR = br, wallC = bc;
			switch(d) {
				case 0 -> {
					ncr = cr-1;
					ncc = cc;
					wallR = br-1;
					wallC = bc;
				} // NORTH
				case 1 -> {
					ncr = cr+1;
					ncc = cc;
					wallR = br+1;
					wallC = bc;
				} // SOUTH
				case 2 -> {
					ncr = cr;
					ncc = cc-1;
					wallR = br;
					wallC = bc-1;
				} // WEST
				case 3 -> {
					ncr = cr;
					ncc = cc+1;
					wallR = br;
					wallC = bc+1;
				} // EAST
			}
			if(ncr>=0 && ncr<cellRows && ncc>=0 && ncc<cellCols && !visitedCells[ncr][ncc]) {
				// carve wall between
				if(blocks[wallR][wallC].getType()!=Block.BlockType.GATE) {
					blocks[wallR][wallC] = new Block(Block.BlockType.EMPTY);
				}
				// recurse
				carveFromCell(blocks, visitedCells, ncr, ncc);
			}
		}
	}
	
	// choose a start cell; prefer cells whose center is adjacent to a gate (already opened by ensureGatesConnected)
	private static int[] findStartCell(Block[][] blocks, boolean[][] visitedCells) {
		int rows = blocks.length;
		int cols = blocks[0].length;
		int cellRows = visitedCells.length;
		int cellCols = visitedCells[0].length;
		// check cells adjacent to gates
		for(int i = 0; i<rows; i++) {
			for(int j = 0; j<cols; j++) {
				if(blocks[i][j].getType()==Block.BlockType.GATE) {
					// adjacent inside
					int ai = i==0 ? 1 : (i==rows-1 ? rows-2 : i);
					int aj = j==0 ? 1 : (j==cols-1 ? cols-2 : j);
					// snap to nearest odd (cell center)
					int br = (ai%2==1) ? ai : (ai-1>=1 ? ai-1 : ai+1);
					int bc = (aj%2==1) ? aj : (aj-1>=1 ? aj-1 : aj+1);
					int cr = (br-1)/2;
					int cc = (bc-1)/2;
					if(cr>=0 && cr<cellRows && cc>=0 && cc<cellCols) {
						return new int[]{cr, cc};
					}
				}
			}
		}
		// otherwise pick random cell
		int attempts = 200;
		for(int a = 0; a<attempts; a++) {
			int cr = random.nextInt(cellRows);
			int cc = random.nextInt(cellCols);
			if (!visitedCells[cr][cc]) {
				return new int[]{cr, cc};
			}
		}
		return null;
	}
	
	// 确保门与内部区域连通（仅打开门旁第一格）
	private static void ensureGatesConnected(Block[][] blocks) {
		int rows = blocks.length;
		int cols = blocks[0].length;
		
		// 处理水平方向的门（北墙和南墙）
		for (int j = 1; j < cols-1; j++) {
			// 北墙门（第一行）
			if (blocks[0][j].getType() == Block.BlockType.GATE) {
				if (blocks[1][j].getType() == Block.BlockType.WALL) {
					blocks[1][j] = new Block(Block.BlockType.EMPTY);
				}
			}
			
			// 南墙门（最后一行）
			if (blocks[rows-1][j].getType() == Block.BlockType.GATE) {
				if (blocks[rows-2][j].getType() == Block.BlockType.WALL) {
					blocks[rows-2][j] = new Block(Block.BlockType.EMPTY);
				}
			}
		}
		
		// 处理垂直方向的门（西墙和东墙）
		for (int i = 1; i < rows-1; i++) {
			// 西墙门（第一列）
			if (blocks[i][0].getType() == Block.BlockType.GATE) {
				if (blocks[i][1].getType() == Block.BlockType.WALL) {
					blocks[i][1] = new Block(Block.BlockType.EMPTY);
				}
			}
			
			// 东墙门（最后一列）
			if (blocks[i][cols-1].getType() == Block.BlockType.GATE) {
				if (blocks[i][cols-2].getType() == Block.BlockType.WALL) {
					blocks[i][cols-2] = new Block(Block.BlockType.EMPTY);
				}
			}
		}
	}
}
