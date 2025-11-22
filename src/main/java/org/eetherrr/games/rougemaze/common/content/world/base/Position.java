package org.eetherrr.games.rougemaze.common.content.world.base;

public record Position(int x, int y) {
	@Override
	public boolean equals(Object obj) {
		if(this==obj) {
			return true;
		}
		if(obj==null || getClass()!=obj.getClass()) {
			return false;
		}
		Position position = (Position)obj;
		return x==position.x && y==position.y;
	}
	
	@Override
	public int hashCode() {
		return java.util.Objects.hash(x, y);
	}
	
	public Position getNextPosition(Direction direction) {
		return switch(direction) {
			case NORTH -> new Position(x-1, y);
			case EAST -> new Position(x, y+1);
			case SOUTH -> new Position(x+1, y);
			case WEST -> new Position(x, y-1);
		};
	}
	
	public boolean isValid(int width, int height) {
		return x>=0 && x<width && y>=0 && y<height;
	}
}