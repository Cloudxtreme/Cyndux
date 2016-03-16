package com.github.daphexion.cyndux.players;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.github.daphexion.cyndux.exceptions.ItemDoesNotExist;

public class Item {
	public static Properties get(int ItemID) throws ItemDoesNotExist{
		Properties ItemProp = new Properties();
		File file = new File("./items/"+ItemID+".properties");
		if (!file.exists()) {
			throw new ItemDoesNotExist(ItemID);
		} else {
			try {
				FileInputStream input = new FileInputStream(file);
				ItemProp.load(input);
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return ItemProp;
	}
}
