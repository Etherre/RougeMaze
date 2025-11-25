package org.eetherrr.games.rougemaze.common.scene;

import org.eetherrr.games.rougemaze.common.scene.gameui.Game;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
	public static final GamePanel INSTANCE = new GamePanel();
	private final GridBagLayout layout;
	
	private GamePanel() {
		this.layout = new GridBagLayout();
		this.initUI();
	}
	
	private void initUI() {
		setLayout(layout);
		setBackground(Color.GRAY);
		// 主游戏区 - 占据整个面板
		GridBagConstraints constraints = new GridBagConstraints();
		constraints.fill = GridBagConstraints.BOTH;
		constraints.insets = new Insets(0, 0, 0, 0);
		constraints.weightx = 1.0;
		constraints.weighty = 1.0;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.CENTER;
		add(Game.INSTANCE, constraints);
	}
}