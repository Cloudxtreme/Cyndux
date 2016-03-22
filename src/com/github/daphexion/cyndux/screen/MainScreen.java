package com.github.daphexion.cyndux.screen;

import java.util.Vector;

import com.github.daphexion.cyndux.players.Player;
import com.github.daphexion.cyndux.sectors.Sector;

public class MainScreen {
	public static void print(Player player){
		Vector<String> objects = Sector.getObjects(player.getLocation());
		player.send("You are now in " + player.getLocation());
		player.send("You see a:");
		for (String object : objects) {
			player.send("● " + object);
		}
		player.send("What is your command?");
	}
}
