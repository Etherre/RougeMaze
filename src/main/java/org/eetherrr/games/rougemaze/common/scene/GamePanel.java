package org.eetherrr.games.rougemaze.common.scene;

import org.eetherrr.games.rougemaze.common.scene.gameui.Game;
import org.eetherrr.games.rougemaze.common.scene.gameui.UI;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel {
	public static final GamePanel INSTANCE = new GamePanel();
	private final GridBagLayout layout;
	
	private GamePanel() {
		//TODO: 实现游戏界面一边是当前房间，另一边有血量道具和小地图等UI
		this.layout = new GridBagLayout();
		this.initUI();
	}
	
	private void initUI() {
		setLayout(layout);
		setBackground(Color.GRAY);
		GridBagConstraints constraints = layout.getConstraints(this);
		constraints.fill = GridBagConstraints.BOTH;
		constraints.gridx = 0;
		constraints.gridy = 0;
		constraints.anchor = GridBagConstraints.CENTER;
		add(Game.INSTANCE, constraints);
		constraints.gridy = 1;
		add(UI.INSTANCE, constraints);
	}
}