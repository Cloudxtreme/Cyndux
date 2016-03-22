package com.github.daphexion.cyndux.screen;

import java.util.ArrayList;
import java.util.Vector;

import com.github.daphexion.cyndux.exceptions.ItemDoesNotExist;
import com.github.daphexion.cyndux.items.Item;
import com.github.daphexion.cyndux.players.Player;
import com.github.daphexion.cyndux.sectors.Sector;

public class Screen {
	public Screen(Player player){
		player.cannotChat=true;
		switch (player.getScreen()) {
		case MAIN:
			MainScreen.print(player);
			break;
		case GOTO:
			GotoScreen.print(player);
			break;
		case INVENTORY:
			InventoryScreen.print(player);
			break;
		case MAP:
			MapScreen.print(player);
			break;
		case WARP:
			WarpScreen.print(player);
			break;
		case OBJECT:
			ObjectScreen.print(player);
			break;
		}
		player.cannotChat=false;
	}
}
