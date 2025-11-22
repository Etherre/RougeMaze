package org.eetherrr.games.rougemaze.common.content.world.room;

import org.eetherrr.games.rougemaze.common.content.world.base.block.Block;
import org.eetherrr.games.rougemaze.common.scene.Config;

import java.util.Random;

public class EndRoom extends BaseRoom {
	private final Random random = new Random();
	
	public EndRoom() {
		int endX = 1+random.nextInt(Config.ROOM_ROWS-2);
		int endY = 1+random.nextInt(Config.ROOM_COLS-2);
		setBlock(new Block(Block.BlockType.END), endX, endY);
	}
}
