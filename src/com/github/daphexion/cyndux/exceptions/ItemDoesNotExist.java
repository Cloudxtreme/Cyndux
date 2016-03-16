package com.github.daphexion.cyndux.exceptions;

public class ItemDoesNotExist extends Exception {

	private static final long serialVersionUID = 3678579656552471521L;

	public ItemDoesNotExist(int ItemID){
		super("The ItemID "+ItemID+ " requested doesn't exist!");
	}
}
