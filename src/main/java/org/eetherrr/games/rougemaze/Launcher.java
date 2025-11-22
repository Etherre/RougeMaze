package org.eetherrr.games.rougemaze;

import javax.swing.*;

public class Launcher {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(()->RougeMaze.INSTANCE.setVisible(true));
	}
}