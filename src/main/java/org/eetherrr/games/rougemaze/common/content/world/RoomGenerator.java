package org.eetherrr.games.rougemaze.common.content.world;

import org.eetherrr.games.rougemaze.common.content.world.base.block.Block;
import org.eetherrr.games.rougemaze.common.scene.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 房间生成器类
 * <p>
 * 用于生成游戏中的房间布局和迷宫结构 提供基础房间生成和复杂迷宫生成功能
 * </p>
 */
public class RoomGenerator {
	private static final Random random = new Random();
	
	/**
	 * 调试辅助方法：设置随机种子以在测试中生成确定性的结果
	 *
	 * @param seed 随机数生成器的种子值
	 */
	public static void setRandomSeed(long seed) {
		random.setSeed(seed);
	}
	
	/**
	 * 生成基础房间布局
	 * <p>
	 * 创建一个带有围墙和内部空地的基本房间结构 房间的四周边界将被设置为墙体，内部区域设置为空地
	 * </p>
	 *
	 * @param blocks 表示房间布局的二维方块数组
	 */
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
	
	/**
	 * 在房间内生成迷宫结构
	 * <p>
	 * 基于现有的房间结构，在其中生成复杂的迷宫布局 确保迷宫具有良好的连通性和可玩性
	 * </p>
	 *
	 * @param blocks 表示房间布局的二维方块数组
	 */
	public static void generateMazeInRoom(final Block[][] blocks) {
		int rows = blocks.length;
		int cols = blocks[0].length;
		// 初始化：将内部格子设为墙（保留边界和门）
		for(int i = 1; i<rows-1; i++) {
			for(int j = 1; j<cols-1; j++) {
				if(blocks[i][j].getType()!=Block.BlockType.GATE) {
					blocks[i][j] = new Block(Block.BlockType.WALL);
				}
			}
		}
		// 使用两阶段算法：先在逻辑 cell 网格上运行 Prim（不直接修改 blocks），
		// 然后把结果一次性映射回 blocks。这能避免中间多次修改导致的双格厚墙问题。
		generateMazeWithPrim(blocks);
		// 在映射完成后，确保门旁第一格被打开以保证连通性（如果需要）
		ensureGatesConnected(blocks);
		// 其余的修补逻辑保留，用于避免最边缘产生双层墙
		//		int lastInnerRow = rows-2;
		//		int lastInnerCol = cols-2;
		//		int prevRow = rows-3;
		//		int prevCol = cols-3;
		//		if(prevRow>=1) {
		//			for(int j = 1; j<=cols-2; j++) {
		//				if(blocks[lastInnerRow][j].getType()==Block.BlockType.WALL && blocks[prevRow][j].getType()==Block.BlockType.EMPTY) {
		//					blocks[lastInnerRow][j] = new Block(Block.BlockType.EMPTY);
		//				}
		//			}
		//		}
		//		if(prevCol>=1) {
		//			for(int i = 1; i<=rows-2; i++) {
		//				if(blocks[i][lastInnerCol].getType()==Block.BlockType.WALL && blocks[i][prevCol].getType()==Block.BlockType.EMPTY) {
		//					blocks[i][lastInnerCol] = new Block(Block.BlockType.EMPTY);
		//				}
		//			}
		//		}
		//		if(prevRow>=1) {
		//			for(int j = 1; j<=cols-2; j++) {
		//				if(blocks[prevRow][j].getType()==Block.BlockType.WALL && blocks[lastInnerRow][j].getType()==Block.BlockType.WALL) {
		//					if(blocks[prevRow][j].getType()!=Block.BlockType.GATE) {
		//						blocks[prevRow][j] = new Block(Block.BlockType.EMPTY);
		//					}
		//				}
		//			}
		//		}
		//		if(prevCol>=1) {
		//			for(int i = 1; i<=rows-2; i++) {
		//				if(blocks[i][prevCol].getType()==Block.BlockType.WALL && blocks[i][lastInnerCol].getType()==Block.BlockType.WALL) {
		//					if(blocks[i][prevCol].getType()!=Block.BlockType.GATE) {
		//						blocks[i][prevCol] = new Block(Block.BlockType.EMPTY);
		//					}
		//				}
		//			}
		//		}
		//		// 最终连通性修补
		//		int startRx = -1, startRy = -1;
		//		for(int i = 1; i<rows-1; i++) {
		//			for(int j = 1; j<cols-1; j++) {
		//				if(blocks[i][j].getType()==Block.BlockType.EMPTY) {
		//					startRx = i;
		//					startRy = j;
		//					break;
		//				}
		//			}
		//			if(startRx!=-1) {
		//				break;
		//			}
		//		}
		//		if(startRx!=-1) {
		//			boolean[][] seen = new boolean[rows][cols];
		//			java.util.ArrayDeque<int[]> dq = new java.util.ArrayDeque<>();
		//			seen[startRx][startRy] = true;
		//			dq.add(new int[]{startRx, startRy});
		//			while(!dq.isEmpty()) {
		//				int[] p = dq.poll();
		//				int x = p[0], y = p[1];
		//				int[][] d = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
		//				for(int[] dd : d) {
		//					int nx = x+dd[0], ny = y+dd[1];
		//					if(nx>=1 && nx<rows-1 && ny>=1 && ny<cols-1 && !seen[nx][ny] && blocks[nx][ny].getType()==Block.BlockType.EMPTY) {
		//						seen[nx][ny] = true;
		//						dq.add(new int[]{nx, ny});
		//					}
		//				}
		//			}
		//			java.util.List<int[]> unreachable = new java.util.ArrayList<>();
		//			for(int i = 1; i<rows-1; i++) {
		//				for(int j = 1; j<cols-1; j++) {
		//					if(blocks[i][j].getType()==Block.BlockType.EMPTY && !seen[i][j]) {
		//						unreachable.add(new int[]{i, j});
		//					}
		//				}
		//			}
		//			for(int[] u : unreachable) {
		//				int ux = u[0], uy = u[1];
		//				int bestDist = Integer.MAX_VALUE;
		//				int bx = -1, by = -1;
		//				for(int i = 1; i<rows-1; i++) {
		//					for(int j = 1; j<cols-1; j++) {
		//						if(seen[i][j]) {
		//							int dist = Math.abs(i-ux)+Math.abs(j-uy);
		//							if(dist<bestDist) {
		//								bestDist = dist;
		//								bx = i;
		//								by = j;
		//							}
		//						}
		//					}
		//				}
		//				if(bx==-1) {
		//					continue;
		//				}
		//				int cx = ux, cy = uy;
		//				while(cx!=bx || cy!=by) {
		//					if(cx<bx) {
		//						cx++;
		//					}else if(cx>bx) {
		//						cx--;
		//					}else if(cy<by) {
		//						cy++;
		//					}else {
		//						cy--;
		//					}
		//					if(blocks[cx][cy].getType()!=Block.BlockType.GATE) {
		//						blocks[cx][cy] = new Block(Block.BlockType.EMPTY);
		//					}
		//					seen[cx][cy] = true;
		//				}
		//			}
		//		}
	}
	
	/**
	 * 使用随机Prim算法（两阶段实现：逻辑生成 -> 一次性映射回 blocks）
	 */
	private static void generateMazeWithPrim(Block[][] blocks) {
		int rows = blocks.length;
		int cols = blocks[0].length;
		int cellRows = (rows-1)/2;
		int cellCols = (cols-1)/2;
		// 逻辑 visited 表示单元格是否已加入迷宫
		boolean[][] visited = new boolean[cellRows][cellCols];
		// 存放已打开的墙（在映射阶段会应用到 blocks）
		List<int[]> openedWalls = new ArrayList<>();
		// frontier 表：每项 int[]{wallR, wallC, targetCR, targetCC}
		List<int[]> frontier = new ArrayList<>();
		// 选择起点单元格（优先靠近门）
		int[] startCell = findStartCell(blocks, new boolean[cellRows][cellCols]);
		if(startCell==null) {
			startCell = new int[]{cellRows/2, cellCols/2};
		}
		int sr = startCell[0], sc = startCell[1];
		visited[sr][sc] = true;
		int br = sr*2+1, bc = sc*2+1;
		// 将起点的周围墙加入 frontier（逻辑上）
		if(sr-1>=0) {
			frontier.add(new int[]{br-1, bc, sr-1, sc});
		}
		if(sr+1<cellRows) {
			frontier.add(new int[]{br+1, bc, sr+1, sc});
		}
		if(sc-1>=0) {
			frontier.add(new int[]{br, bc-1, sr, sc-1});
		}
		if(sc+1<cellCols) {
			frontier.add(new int[]{br, bc+1, sr, sc+1});
		}
		// 随机 Prim 主循环（仅在逻辑结构上操作）
		while(!frontier.isEmpty()) {
			int idx = random.nextInt(frontier.size());
			int[] w = frontier.remove(idx);
			int wallR = w[0], wallC = w[1], tr = w[2], tc = w[3];
			int tbr = tr*2+1, tbc = tc*2+1;
			if(!visited[tr][tc]) {
				// 标记墙为打开（记录以便后续映射），并把目标单元格标记为已访问
				openedWalls.add(new int[]{wallR, wallC});
				visited[tr][tc] = true;
				// 把目标单元格周围的墙加入 frontier
				if(tr-1>=0) {
					frontier.add(new int[]{tbr-1, tbc, tr-1, tc});
				}
				if(tr+1<cellRows) {
					frontier.add(new int[]{tbr+1, tbc, tr+1, tc});
				}
				if(tc-1>=0) {
					frontier.add(new int[]{tbr, tbc-1, tr, tc-1});
				}
				if(tc+1<cellCols) {
					frontier.add(new int[]{tbr, tbc+1, tr, tc+1});
				}
			}
		}
		// 映射阶段：重置内部为墙（保留门），然后把 visited 单元格和 openedWalls 写回 blocks
		for(int i = 1; i<rows-1; i++) {
			for(int j = 1; j<cols-1; j++) {
				if(blocks[i][j].getType()!=Block.BlockType.GATE) {
					blocks[i][j] = new Block(Block.BlockType.WALL);
				}
			}
		}
		for(int r = 0; r<cellRows; r++) {
			for(int c = 0; c<cellCols; c++) {
				if(visited[r][c]) {
					int cellR = r*2+1, cellC = c*2+1;
					if(blocks[cellR][cellC].getType()!=Block.BlockType.GATE) {
						blocks[cellR][cellC] = new Block(Block.BlockType.EMPTY);
					}
				}
			}
		}
		for(int[] w : openedWalls) {
			int wr = w[0], wc = w[1];
			if(blocks[wr][wc].getType()!=Block.BlockType.GATE) {
				blocks[wr][wc] = new Block(Block.BlockType.EMPTY);
			}
		}
	}
	
	/**
	 * 查找迷宫生成的起始单元格
	 * <p>
	 * 优先选择与门相邻的单元格作为起始点 如果找不到合适的位置，则随机选择一个未访问的单元格
	 * </p>
	 *
	 * @param blocks       表示房间布局的二维方块数组
	 * @param visitedCells 标记已访问单元格的二维布尔数组
	 *
	 * @return 包含起始单元格行列坐标的整数数组，格式为 [行, 列]
	 */
	private static int[] findStartCell(Block[][] blocks, boolean[][] visitedCells) {
		int rows = blocks.length;
		int cols = blocks[0].length;
		int cellRows = visitedCells.length;
		int cellCols = visitedCells[0].length;
		for(int i = 0; i<rows; i++) {
			for(int j = 0; j<cols; j++) {
				if(blocks[i][j].getType()==Block.BlockType.GATE) {
					int ai = i==0 ? 1 : (i==rows-1 ? rows-2 : i);
					int aj = j==0 ? 1 : (j==cols-1 ? cols-2 : j);
					int br = (ai%2==1) ? ai : (ai-1>=1 ? ai-1 : ai+1);
					int bc = (aj%2==1) ? aj : (aj-1>=1 ? aj-1 : aj+1);
					int cr = (br-1)/2;
					int cc = (bc-1)/2;
					if(cr<cellRows && cc>=0 && cc<cellCols) {
						return new int[]{cr, cc};
					}
				}
			}
		}
		int attempts = 200;
		for(int a = 0; a<attempts; a++) {
			int cr = random.nextInt(cellRows);
			int cc = random.nextInt(cellCols);
			if(!visitedCells[cr][cc]) {
				return new int[]{cr, cc};
			}
		}
		return null;
	}
	
	/**
	 * 确保门与内部区域连通
	 * <p>
	 * 检查房间四周的门是否与内部区域正确连接 如果门旁边是墙壁，则将其打通以确保玩家可以进入
	 * </p>
	 *
	 * @param blocks 表示房间布局的二维方块数组
	 */
	// 确保门与内部区域连通（仅打开门旁第一格）
	private static void ensureGatesConnected(Block[][] blocks) {
		int rows = blocks.length;
		int cols = blocks[0].length;
		// 处理水平方向的门（北墙和南墙）
		for(int j = 1; j<cols-1; j++) {
			// 北墙门（第一行）
			if(blocks[0][j].getType()==Block.BlockType.GATE) {
				if(blocks[1][j].getType()==Block.BlockType.WALL) {
					blocks[1][j] = new Block(Block.BlockType.EMPTY);
				}
			}
			// 南墙门（最后一行）
			if(blocks[rows-1][j].getType()==Block.BlockType.GATE) {
				if(blocks[rows-2][j].getType()==Block.BlockType.WALL) {
					blocks[rows-2][j] = new Block(Block.BlockType.EMPTY);
				}
			}
		}
		// 处理垂直方向的门（西墙和东墙）
		for(int i = 1; i<rows-1; i++) {
			// 西墙门（第一列）
			if(blocks[i][0].getType()==Block.BlockType.GATE) {
				if(blocks[i][1].getType()==Block.BlockType.WALL) {
					blocks[i][1] = new Block(Block.BlockType.EMPTY);
				}
			}
			// 东墙门（最后一列）
			if(blocks[i][cols-1].getType()==Block.BlockType.GATE) {
				if(blocks[i][cols-2].getType()==Block.BlockType.WALL) {
					blocks[i][cols-2] = new Block(Block.BlockType.EMPTY);
				}
			}
		}
	}
}
