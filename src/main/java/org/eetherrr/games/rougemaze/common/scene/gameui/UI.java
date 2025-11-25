package org.eetherrr.games.rougemaze.common.scene.gameui;

import org.eetherrr.games.rougemaze.common.content.entity.Player;
import org.eetherrr.games.rougemaze.common.content.world.base.Position;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UI extends JPanel {
	public static final UI INSTANCE = new UI();
	private final Timer timer;
	private JLabel healthLabel;
	private JLabel attackLabel;
	private JLabel defenseLabel;
	private JLabel positionLabel;
	//private JLabel roomTypeLabel;
	
	private UI() {
		initUI();
		// 创建定时器，定期更新UI信息
		timer = new Timer(
			100, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateUI();
			}
		});
		timer.start();
	}
	
	private void initUI() {
		// 初始化标签
		healthLabel = new JLabel("生命值: 100");
		attackLabel = new JLabel("攻击力: 10");
		defenseLabel = new JLabel("防御力: 10");
		positionLabel = new JLabel("坐标: (0,0)");
		//roomTypeLabel = new JLabel("房间类型: 普通房间");
		// 设置面板属性
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setBackground(new Color(50, 50, 50, 200)); // 半透明背景
		setOpaque(false); // 设置为半透明
		// 设置UI面板的位置和尺寸
		setBounds(600, 0, 200, 320); // 定位在右侧
		setPreferredSize(new Dimension(200, 320));
		// 设置标签样式
		Font font = new Font("微软雅黑", Font.PLAIN, 14);
		Color textColor = Color.WHITE;
		// 配置所有标签
		configureLabel(healthLabel, font, textColor);
		configureLabel(attackLabel, font, textColor);
		configureLabel(defenseLabel, font, textColor);
		configureLabel(positionLabel, font, textColor);
		//configureLabel(roomTypeLabel, font, textColor);
		// 添加标签到面板
		add(healthLabel);
		add(Box.createVerticalStrut(5));
		add(attackLabel);
		add(Box.createVerticalStrut(5));
		add(defenseLabel);
		add(Box.createVerticalStrut(5));
		add(positionLabel);
		//add(Box.createVerticalStrut(5));
		//add(roomTypeLabel);
	}
	
	/**
	 * 配置标签的通用属性
	 */
	private void configureLabel(JLabel label, Font font, Color foreground) {
		label.setFont(font);
		label.setForeground(foreground);
		label.setOpaque(true);
		label.setBackground(new Color(50, 50, 50)); // 与面板背景色一致
		label.setAlignmentX(Component.LEFT_ALIGNMENT);
	}
	
	/**
	 * 更新UI上显示的玩家信息
	 */
	public void updateUI() {
		// 确保标签已经初始化
		if(healthLabel==null || attackLabel==null || defenseLabel==null || positionLabel==null
			//|| roomTypeLabel==null
		) {
			return;
		}
		Player player = Game.INSTANCE.getPlayer();
		if(player!=null) {
			// 更新玩家属性
			healthLabel.setText("生命值: "+(int)player.getHealth()+"/"+player.getMaxHealth());
			attackLabel.setText("攻击力: "+(int)player.getAttack());
			defenseLabel.setText("防御力: "+(int)player.getDefense());
			// 更新玩家位置
			Position pos = player.getPos();
			if(pos!=null) {
				positionLabel.setText("坐标: ("+pos.x()+","+pos.y()+")");
			}else {
				positionLabel.setText("坐标: 未知");
			}
			// 确保标签背景正确
			healthLabel.setBackground(new Color(50, 50, 50));
			attackLabel.setBackground(new Color(50, 50, 50));
			defenseLabel.setBackground(new Color(50, 50, 50));
			positionLabel.setBackground(new Color(50, 50, 50));
		}
		//		// 更新房间类型
		//		BaseRoom currentRoom = Game.INSTANCE.getCurrentRoom();
		//		if(currentRoom!=null) {
		//			String roomType = "普通房间";
		//			if(currentRoom instanceof StartRoom) {
		//				roomType = "起始房间";
		//			}else if(currentRoom instanceof EndRoom) {
		//				roomType = "结束房间";
		//			}else if(currentRoom instanceof MazeRoom) {
		//				roomType = "迷宫房间";
		//			}
		//			roomTypeLabel.setText("房间类型: "+roomType);
		//			roomTypeLabel.setBackground(new Color(50, 50, 50));
		//		}else {
		//			roomTypeLabel.setText("房间类型: 未知");
		//			roomTypeLabel.setBackground(new Color(50, 50, 50));
		//		}
		// 强制重新绘制组件
		revalidate();
		repaint();
	}
	
	public void stopUpdates() {
		if(timer!=null && timer.isRunning()) {
			timer.stop();
		}
	}
}