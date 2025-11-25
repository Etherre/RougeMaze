package org.eetherrr.games.rougemaze.common.scene.gameui;

import org.eetherrr.games.rougemaze.RougeMaze;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class WinPanel extends JPanel {
	public static final WinPanel INSTANCE = new WinPanel();
	
	private WinPanel() {
		initUI();
	}
	
	private void initUI() {
		setLayout(new GridBagLayout());
		setBackground(Color.BLACK);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		// 标题标签
		JLabel titleLabel = new JLabel("You Win!");
		titleLabel.setForeground(Color.WHITE);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.gridwidth = 2;
		add(titleLabel, gbc);
		// 返回主菜单按钮
		JButton menuButton = new JButton("返回主菜单");
		menuButton.setPreferredSize(new Dimension(150, 40));
		menuButton.setBackground(Color.LIGHT_GRAY);
		menuButton.setForeground(Color.BLACK);
		menuButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RougeMaze.INSTANCE.getContentPane().removeAll();
				RougeMaze.INSTANCE.initUI(); // 重新初始化UI以创建新的世界
				RougeMaze.INSTANCE.setupEventListeners(); // 重新设置事件监听器
				RougeMaze.INSTANCE
					.getLayout()
					.show(RougeMaze.INSTANCE.getContentPane(), "StartMenu");
			}
		});
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 1;
		add(menuButton, gbc);
		// 重新开始按钮
		JButton restartButton = new JButton("重新开始");
		restartButton.setPreferredSize(new Dimension(150, 40));
		restartButton.setBackground(Color.LIGHT_GRAY);
		restartButton.setForeground(Color.BLACK);
		restartButton.addActionListener(e->{
			// 重置游戏状态
			Game.INSTANCE.resetGame();
			// 切换到游戏界面
			RougeMaze.INSTANCE.getLayout().show(RougeMaze.INSTANCE.getContentPane(), "Game");
			RougeMaze.INSTANCE.requestFocus();
		});
		gbc.gridx = 1;
		gbc.gridy = 1;
		add(restartButton, gbc);
	}
}