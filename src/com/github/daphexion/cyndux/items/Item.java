package com.github.daphexion.cyndux.items;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import com.github.daphexion.cyndux.exceptions.ItemDoesNotExist;

public class Item{
	public static String getName(int ItemID) throws ItemDoesNotExist{
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
		return ItemProp.getProperty("name");
	}
	public static Properties getDetails(int ItemID) throws ItemDoesNotExist{
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

	public static ItemType getType(int ItemID) throws ItemDoesNotExist{
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
		ItemType type = null;
		switch(ItemProp.getProperty("name")){
		case "gasharvester":
			type = ItemType.GASHARVESTER;
		case "oreminer":
			type = ItemType.OREMINER;
		case "weapon":
			type = ItemType.WEAPON;
		case "ore":
			type = ItemType.ORE;
		case "component":
			type = ItemType.COMPONENT;
		case "misc":
			type = ItemType.MISC;
		}
		return type;
	}
}
