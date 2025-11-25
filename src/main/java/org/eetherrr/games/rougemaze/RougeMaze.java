package org.eetherrr.games.rougemaze;

import org.eetherrr.games.rougemaze.common.scene.GamePanel;
import org.eetherrr.games.rougemaze.common.scene.StartMenuPanel;
import org.eetherrr.games.rougemaze.common.scene.gameui.Game;
import org.eetherrr.games.rougemaze.common.scene.gameui.WinPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class RougeMaze extends JFrame implements KeyListener {
	public static final RougeMaze INSTANCE = new RougeMaze();
	private final CardLayout layout = new CardLayout();
	private final java.util.Timer moveTimer = new java.util.Timer();
	private boolean isMoving = false;
	
	private RougeMaze() {
		super("RougeMaze");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		//初始化
		initUI();
		setupEventListeners();
		// 自动化窗口大小并居中显示
		pack();
		setLocationRelativeTo(null);
	}
	
	public void initUI() {
		getContentPane().removeAll();
		setLayout(layout);
		add(StartMenuPanel.INSTANCE, "StartMenu");
		add(GamePanel.INSTANCE, "Game");
		add(WinPanel.INSTANCE, "WinPanel");
		setPreferredSize(new Dimension(800, 600)); // 设置窗口首选大小
	}
	
	public void setupEventListeners() {
		StartMenuPanel.INSTANCE.startButtonPressHandler(e->{
			startGame();
		});
		StartMenuPanel.INSTANCE.quitButtonPressHandler(e->{System.exit(0);});
	}
	
	public CardLayout getLayout() {
		return layout;
	}
	
	private void startGame() {
		// 重置游戏状态
		Game.INSTANCE.resetGame();
		// 主菜单切换到游戏界面
		layout.show(this.getContentPane(), "Game");
		requestFocus();
		addKeyListener(this);
	}
	
	public void endGame() {
		layout.show(this.getContentPane(), "WinPanel");
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		// 如果已经在移动，忽略新的按键事件
		if(isMoving) {
			return;
		}
		isMoving = true;
		switch(e.getKeyCode()) {
			case KeyEvent.VK_UP -> Game.INSTANCE.getPlayer().moveUp();
			case KeyEvent.VK_DOWN -> Game.INSTANCE.getPlayer().moveDown();
			case KeyEvent.VK_LEFT -> Game.INSTANCE.getPlayer().moveLeft();
			case KeyEvent.VK_RIGHT -> Game.INSTANCE.getPlayer().moveRight();
			case KeyEvent.VK_ESCAPE -> {
				Game.INSTANCE.resetGame();
				layout.show(this.getContentPane(), "StartMenu");
			}
		}
		// 设置一个短暂的冷却时间，防止重复触发
		java.util.TimerTask moveTask = new java.util.TimerTask() {
			@Override
			public void run() {
				isMoving = false;
			}
		};
		moveTimer.schedule(moveTask, 150); // 150毫秒的冷却时间
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
	}
}