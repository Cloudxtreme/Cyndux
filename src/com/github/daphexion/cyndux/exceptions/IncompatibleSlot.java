package com.github.daphexion.cyndux.exceptions;

import com.github.daphexion.cyndux.items.Item;
import com.github.daphexion.cyndux.ships.ShipSlot;

public class IncompatibleSlot extends Exception {
	private static final long serialVersionUID = -3408686162555393810L;
	public IncompatibleSlot(int ItemID, ShipSlot slot) throws ItemDoesNotExist{
		super("The item " + Item.getName(ItemID)+" is not compatible with the "+ slot.getSlot()+" slot!");
	}
}
