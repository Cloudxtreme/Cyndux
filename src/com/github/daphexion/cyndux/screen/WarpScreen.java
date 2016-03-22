package com.github.daphexion.cyndux.screen;

import java.util.HashMap;

import com.github.daphexion.cyndux.players.Player;
import com.github.daphexion.cyndux.sectors.Sector;

public class WarpScreen {
	public static void print(Player player){
		int warp = 0;
		HashMap<String, Integer> SurroundingSectors = Sector.getSurroundingSectors(player.getLocation());
		switch (player.getMapCursor()) {
		case 1:
			warp = SurroundingSectors.get("mapnw");
			break;
		case 2:
			warp = SurroundingSectors.get("mapn");
			break;
		case 3:
			warp = SurroundingSectors.get("mapne");
			break;
		case 4:
			warp = SurroundingSectors.get("mapw");
			break;
		case 6:
			warp = SurroundingSectors.get("mape");
			break;
		case 7:
			warp = SurroundingSectors.get("mapsw");
			break;
		case 8:
			warp = SurroundingSectors.get("maps");
			break;
		case 9:
			warp = SurroundingSectors.get("mapse");
			break;
		default:
			warp = SurroundingSectors.get("maploc");
			break;
		}
		if (player.getMapCursor()!=5){
		player.send("Are you sure you want to warp to sector " + warp + "?");
		} else {
			player.send("You already are here!");
		}
	}
}
