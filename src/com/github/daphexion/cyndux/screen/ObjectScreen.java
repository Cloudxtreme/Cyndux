package com.github.daphexion.cyndux.screen;

import com.github.daphexion.cyndux.players.Player;

public class ObjectScreen {
	public static void print(Player player){
		switch (player.getLocationInSector()) {
		case BELT:
			player.send("You are now in the asteroid belt of " + player.getLocation());
			break;
		case COMBAT:
			// TODO Combat
			break;
		case NEBULA:
			player.send("You are now in the nebula of " + player.getLocation());
			break;
		case STATION:
			player.send("Unfortunately, stations are not implemented for the time being.");
			// TODO Stations
			break;
		case FACTORY:
			player.send("Unfortunately, factories are not implemented for the time being.");
			break;
		case BLACKMARKET:
			player.send("Unfortunately, the blackmarket is not implemented for the time being.");
			break;
		default:
			player.send("Unfortunately, planets are not implemented for the time being.");
			break;
		}
	}

}
