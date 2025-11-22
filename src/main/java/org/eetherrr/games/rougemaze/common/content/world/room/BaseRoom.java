package org.eetherrr.games.rougemaze.common.content.world.room;

import org.eetherrr.games.rougemaze.common.content.entity.Player;
import org.eetherrr.games.rougemaze.common.content.world.RoomGenerator;
import org.eetherrr.games.rougemaze.common.content.world.base.Direction;
import org.eetherrr.games.rougemaze.common.content.world.base.Position;
import org.eetherrr.games.rougemaze.common.content.world.base.block.Block;
import org.eetherrr.games.rougemaze.common.scene.Config;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BaseRoom extends JPanel {
	public final Block[][] blocks;
	public final Map<Direction, BaseRoom> neighborRoom;
	protected int row;
	protected int column;
	protected Player player; // 当前房间中的玩家
	
	public BaseRoom() {
		this(Config.ROOM_ROWS, Config.ROOM_COLS);
	}
	
	public BaseRoom(int row, int column) {
		super(new GridLayout(row, column));
		this.row = row;
		this.column = column;
		this.blocks = new Block[row][column];
		this.neighborRoom = new HashMap<>();
		// 设置房间的首选尺寸
		setPreferredSize(new Dimension(column*Block.SIZE, row*Block.SIZE));
		setSize(new Dimension(column*Block.SIZE, row*Block.SIZE));
		setBounds(0, 0, column*Block.SIZE, row*Block.SIZE);
		// 设置背景色以便于看到房间
		setBackground(Color.BLACK);
		setOpaque(true);
		// 初始化所有block
		initializeBlocks();
		addBlocks();
	}
	
	/**
	 * 初始化blocks数组中的所有Block对象
	 */
	private void initializeBlocks() {
		RoomGenerator.generateBase(blocks);
	}
	
	private void addBlocks() {
		for(int i = 0; i<row; i++) {
			for(int j = 0; j<column; j++) {
				add(blocks[i][j]);
			}
		}
	}
	
	public void setNeighbor(Direction direction, BaseRoom neighbor) {
		neighborRoom.put(direction, neighbor);
	}
	
	public Position getGatePosition(Direction direction) {
		switch(direction) {
			case NORTH -> {
				for(int i = 0; i<column; i++) {
					if(blocks[0][i].getType()==Block.BlockType.GATE) {
						return new Position(0, i);
					}
				}
			}
			case WEST -> {
				for(int i = 0; i<row; i++) {
					if(blocks[i][0].getType()==Block.BlockType.GATE) {
						return new Position(i, 0);
					}
				}
			}
			case SOUTH -> {
				for(int i = 0; i<column; i++) {
					if(blocks[row-1][i].getType()==Block.BlockType.GATE) {
						return new Position(row-1, i);
					}
				}
			}
			case EAST -> {
				for(int i = 0; i<row; i++) {
					if(blocks[i][column-1].getType()==Block.BlockType.GATE) {
						return new Position(i, column-1);
					}
				}
			}
		}
		return null;
	}
	
	public void placeGate(Position position) {
		this.placeGate(position.x(), position.y());
	}
	
	public void placeGate(int x, int y) {
		this.setBlock(new Block(Block.BlockType.GATE), x, y);
	}
	
	public void setBlock(Block block, Position position) {
		this.setBlock(block, position.x(), position.y());
	}
	
	public void setBlock(Block block, int x, int y) {
		remove(x*column+y);
		// 更新blocks数组
		blocks[x][y] = block;
		// 添加新的block组件到正确的位置
		add(block, x*column+y);
		// 重新验证和重绘
		revalidate();
		repaint();
	}
	
	public Block getBlock(Position point) {
		return getBlock(point.x(), point.y());
	}
	
	public Block getBlock(int x, int y) {
		return blocks[x][y];
	}
	
	public int getRoomRow() {
		return row;
	}
	
	public int getRoomColumn() {
		return column;
	}
}