package org.eetherrr.games.rougemaze.common.scene.gameui;

import org.eetherrr.games.rougemaze.common.content.entity.Player;
import org.eetherrr.games.rougemaze.common.content.world.WorldGenerator;
import org.eetherrr.games.rougemaze.common.content.world.base.Position;
import org.eetherrr.games.rougemaze.common.content.world.base.block.Block;
import org.eetherrr.games.rougemaze.common.content.world.room.BaseRoom;
import org.eetherrr.games.rougemaze.common.content.world.room.MazeRoom;
import org.eetherrr.games.rougemaze.common.content.world.room.StartRoom;
import org.eetherrr.games.rougemaze.common.scene.Config;

import javax.swing.*;
import java.awt.*;

public class Game extends JLayeredPane {
	private static final Integer UI_LAYER = 200; // 定义UI层
	public static Game INSTANCE = new Game();
	private final CardLayout cardLayout = new CardLayout();
	private WorldGenerator worldGenerator;
	private Player player;
	private JPanel worldPanel;
	private Position currentRoomPosition;
	
	private Game() {
		resetGame();
	}
	
	public void resetGame() {
		// 移除旧的组件
		removeAll();
		// 创建新的游戏对象
		this.player = new Player(100, 10, 10);
		this.worldGenerator = new WorldGenerator();
		this.currentRoomPosition = null;
		// 初始化世界面板
		this.initWorldPanel();
		// 重新验证和重绘
		revalidate();
		repaint();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public WorldGenerator getWorldGenerator() {
		return worldGenerator;
	}
	
	private void initWorldPanel() {
		// 设置Game面板的首选尺寸
		int roomWidth = Config.ROOM_COLS*Block.SIZE;
		int roomHeight = Config.ROOM_ROWS*Block.SIZE;
		setPreferredSize(new Dimension(roomWidth, roomHeight));
		worldPanel = new JPanel(cardLayout);
		worldPanel.setPreferredSize(new Dimension(roomWidth, roomHeight));
		// 设置worldPanel的边界使其填满整个Game面板
		worldPanel.setBounds(0, 0, roomWidth, roomHeight);
		for(int i = 0; i<Config.WORLD_SIZE; i++) {
			for(int j = 0; j<Config.WORLD_SIZE; j++) {
				worldPanel.add(worldGenerator.worldMap[i][j], i+"-"+j);
			}
		}
		add(worldPanel, DEFAULT_LAYER);
		// 获取起始房间的位置（世界坐标）
		Position startPosition = worldGenerator.getStartPosition();
		// 切换到起始房间
		switchRoom(startPosition);
		// 获取起始房间中的START方块位置（房间内坐标）
		StartRoom startRoom = (StartRoom)worldGenerator.worldMap[startPosition.x()][startPosition.y()];
		Position startBlockPosition = startRoom.getStartBlock();
		// 设置玩家的坐标
		add(player, PALETTE_LAYER);
		player.updatePos(startBlockPosition);
		add(UI.INSTANCE, UI_LAYER);
		UI.INSTANCE.setVisible(true);
	}
	
	public void switchRoom(Position position) {
		cardLayout.show(worldPanel, position.x()+"-"+position.y());
		currentRoomPosition = position; // 保存当前房间位置
		// 如果是迷宫房间，生成迷宫
		BaseRoom currentRoom = getCurrentRoom();
		if(currentRoom instanceof MazeRoom) {
			((MazeRoom)currentRoom).generateMazeIfNeeded();
		}
	}
	
	public <T extends BaseRoom> T getCurrentRoom() {
		if(currentRoomPosition!=null) {
			return (T)worldGenerator.worldMap[currentRoomPosition.x()][currentRoomPosition.y()];
		}
		return null;
	}
	
	public Position getCurrentRoomPosition() {
		return currentRoomPosition;
	}
}