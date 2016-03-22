package com.github.daphexion.cyndux.screen;

import com.github.daphexion.cyndux.players.Player;

public class MapScreen {
	public static void print(Player player) {
		int maploc = player.getLocation();
		int mapn = (maploc - 100);
		int maps = maploc + 100;
		int mape = maploc + 1; // how the fuck does increment
								// operators work
		int mapw = maploc - 1;
		int mapne = mapn + 1;
		int mapse = maps + 1;
		int mapsw = maps - 1;
		int mapnw = mapn - 1;
		if (((maploc % 100) <= 1)){ //T R U L Y S P H E R I C A L B O I S
			switch (maploc % 100){
			case 0:
				mape = maploc - 99;
				break;
			case 1:
				mapw = maploc + 99;
				break;
			}
		}else{
			if (maploc <=100){
				mapn = maploc + 50;
			}
			if (maploc >= 9900){
				maps = maploc + 50;
			}
		} 
		player.send("╔═════╦═════╦═════╗");
		player.send(
				"║" + sectorThing(mapnw) + "║" + sectorThing(mapn) + "║" + sectorThing(mapne) + "║");
		switch (player.getMapCursor()) {

		case 1:
			player.send("║  ●  ║     ║     ║");
			break;
		case 2:
			player.send("║     ║  ●  ║     ║");
			break;
		case 3:
			player.send("║     ║     ║  ●  ║");
			break;
		default:
			player.send("║     ║     ║     ║");
			break;

		}
		player.send("╠═════╬═════╬═════╣");
		player.send(
				"║" + sectorThing(mapw) + "║" + sectorThing(maploc) + "║" + sectorThing(mape) + "║");
		switch (player.getMapCursor()) {

		case 4:
			player.send("║  ●  ║     ║     ║");
			break;
		case 5:
			player.send("║     ║  ●  ║     ║");
			break;
		case 6:
			player.send("║     ║     ║  ●  ║");
			break;
		default:
			player.send("║     ║     ║     ║");
			break;
		}
		player.send("╠═════╬═════╬═════╣");
		player.send(
				"║" + sectorThing(mapsw) + "║" + sectorThing(maps) + "║" + sectorThing(mapse) + "║");
		switch (player.getMapCursor()) {

		case 7:
			player.send("║  ●  ║     ║     ║");
			break;
		case 8:
			player.send("║     ║  ●  ║     ║");
			break;
		case 9:
			player.send("║     ║     ║  ●  ║");
			break;
		default:
			player.send("║     ║     ║     ║");
			break;

		}
		player.send("╚═════╩═════╩═════╝");
		player.send("To move the cursor, type up, down, left or right.");
		player.send("To warp to where your cursor is, type warp.");
	}
	private static String sectorThing(int loc) {
		if (loc >= 10) {
			if (loc >= 100) {
				if (loc >= 1000) {
					if (loc == 10000) {
						return Integer.toString(loc);
					}
					return " " + Integer.toString(loc);

				}
				return " " + Integer.toString(loc) + " ";
			}

			return "  " + Integer.toString(loc) + " ";
		}
		return "  " + Integer.toString(loc) + "  ";
	}
}
