package org.eetherrr.games.rougemaze.common.content.world.base;

public enum Direction {
	NORTH, EAST, SOUTH, WEST;
	
	public static Direction opposite(Direction direction) {
		return switch(direction) {
			case NORTH -> SOUTH;
			case EAST -> WEST;
			case SOUTH -> NORTH;
			case WEST -> EAST;
		};
	}
	
	public static Direction getRandomDirection() {
		return values()[(int)(Math.random()*values().length)];
	}
}
