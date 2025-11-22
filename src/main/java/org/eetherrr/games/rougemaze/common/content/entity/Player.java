package org.eetherrr.games.rougemaze.common.content.entity;

import org.eetherrr.games.rougemaze.common.content.item.Item;

import java.util.ArrayList;
import java.util.List;

public class Player extends Entity {
	private final List<Item> inventory;
	
	public Player(float health, float attack, float defense) {
		super(health, attack, defense);
		this.inventory = new ArrayList<>();
		System.out.println("Player has been created");
	}
	
	public List<Item> getInventory() {
		return inventory;
	}
}