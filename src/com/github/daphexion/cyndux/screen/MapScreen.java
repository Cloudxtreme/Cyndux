package com.github.daphexion.cyndux.screen;

import java.util.HashMap;

import com.github.daphexion.cyndux.players.Player;
import com.github.daphexion.cyndux.sectors.Sector;

public class MapScreen {
	public static void print(Player player) {
		HashMap<String, Integer> SurroundingSectors = Sector.getSurroundingSectors(player.getLocation());
		player.send("╔═════╦═════╦═════╗");
		player.send(
				"║" + sectorThing(SurroundingSectors.get("mapnw")) + "║" + sectorThing(SurroundingSectors.get("mapn")) + "║" + SurroundingSectors.get("mapne") + "║");
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
				"║" + sectorThing(SurroundingSectors.get("mapw")) + "║" + sectorThing(SurroundingSectors.get("maploc")) + "║" + sectorThing(SurroundingSectors.get("mape")) + "║");
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
				"║" + sectorThing(SurroundingSectors.get("mapsw")) + "║" + sectorThing(SurroundingSectors.get("maps")) + "║" + sectorThing(SurroundingSectors.get("mapse")) + "║");
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
