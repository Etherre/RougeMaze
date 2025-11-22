package org.eetherrr.games.rougemaze;

import org.eetherrr.games.rougemaze.common.scene.GamePanel;
import org.eetherrr.games.rougemaze.common.scene.StartMenuPanel;
import org.eetherrr.games.rougemaze.common.scene.gameui.Game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class RougeMaze extends JFrame implements KeyListener {
	public static final RougeMaze INSTANCE = new RougeMaze();
	private final CardLayout layout = new CardLayout();
	
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
	
	private void initUI() {
		setLayout(layout);
		add(StartMenuPanel.INSTANCE, "StartMenu");
		add(GamePanel.INSTANCE, "Game");
		setPreferredSize(new Dimension(800, 600)); // 设置窗口首选大小
	}
	
	private void setupEventListeners() {
		StartMenuPanel.INSTANCE.startButtonPressHandler(e->{
			startGame();
		});
		StartMenuPanel.INSTANCE.quitButtonPressHandler(e->{System.exit(0);});
	}
	
	private void startGame() {
		// 主菜单切换到游戏界面
		layout.show(this.getContentPane(), "Game");
		requestFocus();
		addKeyListener(this);
	}
	
	public void endGame() {
		layout.show(this.getContentPane(), "StartMenu");
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()) {
			case KeyEvent.VK_UP -> Game.INSTANCE.getPlayer().moveUp();
			case KeyEvent.VK_DOWN -> Game.INSTANCE.getPlayer().moveDown();
			case KeyEvent.VK_LEFT -> Game.INSTANCE.getPlayer().moveLeft();
			case KeyEvent.VK_RIGHT -> Game.INSTANCE.getPlayer().moveRight();
		}
	}
	
	@Override
	public void keyReleased(KeyEvent e) {
	}
}