package com.github.daphexion.cyndux.exceptions;

import com.github.daphexion.cyndux.items.Item;

public class IncompatiblePart extends Exception {
	private static final long serialVersionUID = -6411827887784606810L;
	public IncompatiblePart(int ItemID, String Ship) throws ItemDoesNotExist{
		super("The part " + Item.getName(ItemID)+" is not compatible with the ship" + Ship + "!");
	}
	
}
