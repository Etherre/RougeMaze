package org.eetherrr.games.rougemaze.common.content.world.base.block;

import javax.swing.*;
import java.awt.*;

public class Block extends JPanel {
	public static final int SIZE = 20; // 20像素
	protected BlockType type;
	
	public Block(BlockType type) {
		this.type = type;
		setFocusable(false);
		setPreferredSize(new Dimension(SIZE, SIZE));
		setBackground(getColorForType(type));
	}
	
	public static Color getColorForType(BlockType type) {
		return switch(type) {
			case WALL -> Color.DARK_GRAY;
			case EMPTY -> Color.WHITE;
			case GATE -> Color.GREEN;
			case START -> Color.YELLOW;
			case END -> Color.RED;
		};
	}
	
	public BlockType getType() {
		return type;
	}
	
	public enum BlockType {
		WALL, EMPTY, GATE, START, END
	}
}