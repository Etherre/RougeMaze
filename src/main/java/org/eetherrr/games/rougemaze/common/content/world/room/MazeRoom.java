package org.eetherrr.games.rougemaze.common.content.world.room;

import org.eetherrr.games.rougemaze.common.content.world.RoomGenerator;
import org.eetherrr.games.rougemaze.common.content.world.base.block.Block;

public class MazeRoom extends BaseRoom {
	private boolean mazeGenerated = false;
	
	public MazeRoom() {
		super();
		// 只创建基本房间，不生成迷宫
		RoomGenerator.generateBase(blocks);
	}
	
	/**
	 * 当房间需要显示时生成迷宫 这个方法应该在房间连接完成之后、玩家进入房间之前调用
	 */
	public void generateMazeIfNeeded() {
		if(!mazeGenerated) {
			// 检查是否有至少两个门
			int gateCount = countGates();
			if(gateCount>=2) {
				// 生成迷宫
				RoomGenerator.generateMazeInRoom(blocks);
			}
			mazeGenerated = true;
		}
	}
	
	private int countGates() {
		int count = 0;
		// 检查北墙（第一行）
		for(int j = 0; j<column; j++) {
			if(blocks[0][j].getType()==Block.BlockType.GATE) {
				count++;
				break;
			}
		}
		// 检查南墙（最后一行）
		for(int j = 0; j<column; j++) {
			if(blocks[row-1][j].getType()==Block.BlockType.GATE) {
				count++;
				break;
			}
		}
		// 检查西墙（第一列）
		for(int i = 0; i<row; i++) {
			if(blocks[i][0].getType()==Block.BlockType.GATE) {
				count++;
				break;
			}
		}
		// 检查东墙（最后一列）
		for(int i = 0; i<row; i++) {
			if(blocks[i][column-1].getType()==Block.BlockType.GATE) {
				count++;
				break;
			}
		}
		return count;
	}
}
