package com.github.daphexion.cyndux.players;

import java.io.File;
import java.sql.ResultSet;
import com.github.daphexion.cyndux.Main;
import com.github.daphexion.cyndux.exceptions.ItemDoesNotExist;

public class Inventory {
	private Player player;
	public Inventory(Player player){
		this.player = player;
	}
	public ResultSet getInventory() {
		return Main.server.database.Query("SELECT * " + "FROM Inventory " + "WHERE Username = '" + player.getUsername() + "'");
	}
	public void addToInventory(int ItemID) throws ItemDoesNotExist {
	if (new File("./items/" + ItemID + ".properties").exists()) {
		//TODO Add ship limit.
		//Add to inventory. If row already exists, increase quantity by one.
		Main.server.database.Update("INSERT OR REPLACE INTO Inventory VALUES ('hello',1,COALESCE((SELECT Quantity + 1 FROM Inventory WHERE Username = 'hello'), 1))");
	} else {
		throw new ItemDoesNotExist(ItemID);
	}
	return;
	}

	/*public void removeFromInventory(int ItemID) throws ItemDoesNotExist, NotInInventory {
	File file = new File("./items/" + ItemID + ".properties");
	if (file.exists()) {
		if (Inventory.contains(ItemID)) {
			Inventory.remove(ItemID);
		} else {
			throw new NotInInventory();
		}
	} else {
		throw new ItemDoesNotExist(ItemID);
	}
	return;
	}*/
}
