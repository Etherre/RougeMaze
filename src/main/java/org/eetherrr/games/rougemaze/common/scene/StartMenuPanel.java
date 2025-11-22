package org.eetherrr.games.rougemaze.common.scene;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class StartMenuPanel extends JPanel {
	public static final StartMenuPanel INSTANCE = new StartMenuPanel();
	private final GridBagLayout layout;
	private final JLabel title;
	private final JButton startButton;
	private final JButton quitButton;
	
	private StartMenuPanel() {
		this.layout = new GridBagLayout();
		this.title = new JLabel("Rouge-Maze", JLabel.CENTER);
		this.startButton = new JButton("Start");
		this.quitButton = new JButton("Quit");
		initUI();
	}
	
	private void initUI() {
		setBackground(Color.BLACK);
		title.setForeground(Color.GRAY);
		createStartMenu();
	}
	
	private void createStartMenu() {
		setLayout(layout);
		GridBagConstraints constraints = layout.getConstraints(this);
		// 设置组件上下左右的间距
		constraints.insets = new Insets(10, 0, 30, 0);
		// 第0行第0列
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.weightx = 0.5;
		title.setFont(new Font("Arial", Font.BOLD, 80));
		add(title, constraints);
		initButton();
		constraints.gridy = 1;
		add(startButton, constraints);
		constraints.gridy = 2;
		add(quitButton, constraints);
	}
	
	private void initButton() {
		Dimension buttonSize = new Dimension(150, 40);
		startButton.setPreferredSize(buttonSize);
		startButton.setBackground(Color.LIGHT_GRAY);
		startButton.setForeground(Color.GRAY);
		quitButton.setPreferredSize(buttonSize);
		quitButton.setBackground(Color.LIGHT_GRAY);
		quitButton.setForeground(Color.GRAY);
	}
	
	public void startButtonPressHandler(ActionListener listener) {
		startButton.addActionListener(listener);
	}
	
	public void quitButtonPressHandler(ActionListener listener) {
		quitButton.addActionListener(listener);
	}
}
