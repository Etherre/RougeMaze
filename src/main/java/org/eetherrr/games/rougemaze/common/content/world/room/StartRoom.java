package org.eetherrr.games.rougemaze.common.content.world.room;

import org.eetherrr.games.rougemaze.common.content.world.base.Position;
import org.eetherrr.games.rougemaze.common.content.world.base.block.Block;
import org.eetherrr.games.rougemaze.common.scene.Config;

import java.util.Random;

public class StartRoom extends BaseRoom {
	private final Random random = new Random();
	private final int startX = 1+random.nextInt(Config.ROOM_ROWS-2);
	private final int startY = 1+random.nextInt(Config.ROOM_COLS-2);
	
	public StartRoom() {
		// 在房间中随机放置一个START块
		setBlock(new Block(Block.BlockType.START), startX, startY);
	}
	
	public Position getStartBlock() {
		return new Position(startX, startY);
	}
}