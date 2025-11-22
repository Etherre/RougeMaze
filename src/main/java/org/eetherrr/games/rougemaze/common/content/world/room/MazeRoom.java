package org.eetherrr.games.rougemaze.common.content.world.room;

import org.eetherrr.games.rougemaze.common.content.world.RoomGenerator;
import org.eetherrr.games.rougemaze.common.content.world.base.block.Block;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;

public class MazeRoom extends BaseRoom {
	private boolean mazeGenerated = false;
	
	public MazeRoom() {
		super();
	}
	
	/**
	 * 当房间需要显示时生成迷宫 这个方法应该在房间连接完成之后、玩家进入房间之前调用
	 */
	public void generateMazeIfNeeded() {
		if(!mazeGenerated) {
			Runnable gen = ()->{
				int gateCount = countGates();
				// generate only if room has at least two gates (restore original threshold)
				if(gateCount>=2) {
					RoomGenerator.generateMazeInRoom(blocks);
					updateBlocks();
				}
				mazeGenerated = true;
			};
			if(SwingUtilities.isEventDispatchThread()) {
				gen.run();
			}else {
				try {
					SwingUtilities.invokeAndWait(gen);
				}catch(InterruptedException|InvocationTargetException e) {
					gen.run();
				}
			}
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
	
	// 更新房间内的方块显示
	private void updateBlocks() {
		for(int i = 0; i<row; i++) {
			for(int j = 0; j<column; j++) {
				remove(i*column+j);
				add(blocks[i][j], i*column+j);
			}
		}
		revalidate();
		repaint();
	}
}