package com.github.daphexion.cyndux.screen;

import java.util.Vector;

import com.github.daphexion.cyndux.players.Player;
import com.github.daphexion.cyndux.sectors.Sector;

public class GotoScreen {
	public static void print(Player player){
		Vector<String> objects = Sector.getObjects(player.getLocation());
		player.send("You have the option of going to:");
		for (String object : objects) {
			player.send("● A" + object);
		}
		player.send("What is your command?");
	}
}
