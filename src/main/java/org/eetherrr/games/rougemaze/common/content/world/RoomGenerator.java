package org.eetherrr.games.rougemaze.common.content.world;

import org.eetherrr.games.rougemaze.common.content.world.base.block.Block;
import org.eetherrr.games.rougemaze.common.scene.Config;

import java.util.Random;

public class RoomGenerator {
	private static final Random random = new Random();
	
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
	 * 在房间内生成迷宫，确保所有门之间连通
	 *
	 * @param blocks 方块数组
	 */
	public static void generateMazeInRoom(Block[][] blocks) {
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
		// 使用深度优先搜索生成迷宫
		generateMazeRecursive(blocks, 1, 1);
	}
	
	private static void generateMazeRecursive(Block[][] blocks, int i, int i1) {
		// TODO: 添加迷宫房间实现
	}
}
