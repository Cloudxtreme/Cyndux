package com.github.daphexion.cyndux.ships;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.github.daphexion.cyndux.exceptions.IncompatiblePart;
import com.github.daphexion.cyndux.exceptions.IncompatibleSlot;
import com.github.daphexion.cyndux.items.Item;
import com.github.daphexion.cyndux.players.Player;

public class Ship {
	private Player player;
	Properties shipProp = new Properties();
	String Ship;
	public Ship(Player player,String Ship) {
		this.player = player;
		this.Ship = Ship;

	}

	public void changeParts(int ItemID, ShipSlot slot) {
		try {
			FileOutputStream output = new FileOutputStream("./users/" + player.getUsername() + ".properties");
			if (!shipProp.getProperty("compatibility").equals(Item.getCompatiblity(ItemID))) {
				output.flush();
				output.close();
				throw new IncompatiblePart(ItemID,Ship);
			} else {
				if (!Item.getSlot(ItemID).equals(slot.getSlot())) {
				} else {
					output.flush();
					output.close();
					throw new IncompatibleSlot(ItemID,slot);
				}
			}
			output.flush();
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Properties getShip(String ShipName) {
		Properties ship = new Properties();
		try {
			FileInputStream input = new FileInputStream("./ships/" + ShipName + ".properties");
			ship.load(input);
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ship;
	}
}

/*player.playerProp.setProperty("ships.slots." + slot.getPropName(), Integer.toString(ItemID));
player.playerProp.store(output, null);*/