package com.github.daphexion.cyndux.screen;

import java.util.ArrayList;

import com.github.daphexion.cyndux.exceptions.ItemDoesNotExist;
import com.github.daphexion.cyndux.items.Item;
import com.github.daphexion.cyndux.players.Player;

public class InventoryScreen {
	public static void print(Player player){
		player.send("You have in your ship:");
		ArrayList<Integer> inventory = player.getInventory();
		if (inventory.isEmpty()){
			player.send("● Nothing");
		}
		for (int ItemID : inventory) {
			try {
				player.send("● " + Item.getName(ItemID));
			} catch (ItemDoesNotExist e) {
				e.printStackTrace();
			}
		}
	}
}
