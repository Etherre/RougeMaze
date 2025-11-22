package org.eetherrr.games.rougemaze.common.content.entity;

import org.eetherrr.games.rougemaze.RougeMaze;
import org.eetherrr.games.rougemaze.common.content.world.base.Direction;
import org.eetherrr.games.rougemaze.common.content.world.base.Position;
import org.eetherrr.games.rougemaze.common.content.world.base.block.Block;
import org.eetherrr.games.rougemaze.common.content.world.room.BaseRoom;
import org.eetherrr.games.rougemaze.common.scene.Config;
import org.eetherrr.games.rougemaze.common.scene.gameui.Game;

import javax.swing.*;
import java.awt.*;

public class Entity extends JPanel {
	protected final int MAX_HEALTH = 100;
	protected float health, attack, defense;
	protected Position pos; // 房间内坐标
	
	public Entity(float health, float attack, float defense) {
		this.health = health<=MAX_HEALTH ? health : MAX_HEALTH;
		this.attack = attack;
		this.defense = defense;
		// 设置尺寸
		setPreferredSize(new Dimension(20, 20));
		setBackground(Color.ORANGE);
		setOpaque(true);
	}
	
	public boolean isAlive() {
		return health>0;
	}
	
	public int getMaxHealth() {
		return MAX_HEALTH;
	}
	
	public float getHealth() {
		return health;
	}
	
	public float getAttack() {
		return attack;
	}
	
	public float getDefense() {
		return defense;
	}
	
	public Position getPos() {
		return pos;
	}
	
	public void setPos(Position pos) {
		this.pos = pos;
	}
	
	public void updatePos(Position pos) {
		if(pos==null) {
			return;
		}
		setPos(pos);
		setBounds(pos.y()*20, pos.x()*20, 20, 20);
	}
	
	public void moveUp() {
		System.out.println("move up");
		moveHandler(Direction.NORTH);
	}
	
	public void moveDown() {
		System.out.println("move down");
		moveHandler(Direction.SOUTH);
	}
	
	public void moveLeft() {
		System.out.println("move left");
		moveHandler(Direction.WEST);
	}
	
	public void moveRight() {
		System.out.println("move right");
		moveHandler(Direction.EAST);
	}
	
	public void moveHandler(Direction direction) {
		// 检查当前位置是否为空
		if(pos==null) {
			return;
		}
		// 计算下一个位置
		Position nextPosition = pos.getNextPosition(direction);
		BaseRoom currentRoom = Game.INSTANCE.getCurrentRoom();
		Position currentRoomPos = Game.INSTANCE.getCurrentRoomPosition();
		// 检查是否在房间内移动
		if(nextPosition.x()>=0 && nextPosition.x()<Config.ROOM_COLS && nextPosition.y()>=0 && nextPosition.y()<Config.ROOM_ROWS) {
			// 检查目标位置的方块类型
			Block targetBlock = currentRoom.getBlock(nextPosition);
			// 检查是否是门方块，如果是则切换房间
			if(targetBlock.getType()==Block.BlockType.GATE) {
				// 切换到相邻房间并获取坐标
				BaseRoom nextRoom = currentRoom.neighborRoom.get(direction);
				Position nextRoomPos = currentRoomPos.getNextPosition(direction);
				if(nextRoom!=null) {
					Position gatePosition = nextRoom.getGatePosition(Direction.opposite(direction));
					if(gatePosition!=null) {
						updatePos(gatePosition);
						Game.INSTANCE.switchRoom(nextRoomPos);
					}
				}
			}else if(targetBlock.getType()!=Block.BlockType.WALL) {
				// 如果不是墙壁，则可以移动
				updatePos(nextPosition);
			}else if(targetBlock.getType()==Block.BlockType.END) {
				System.out.print("end the game");
				RougeMaze.INSTANCE.endGame();
			}
		}
	}
}
	