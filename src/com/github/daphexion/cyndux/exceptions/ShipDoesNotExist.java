package com.github.daphexion.cyndux.exceptions;

public class ShipDoesNotExist extends Exception {
	private static final long serialVersionUID = 8110669447459772228L;

	public ShipDoesNotExist(String ship) {
		super("The ship " + ship + " does not exist!");
	}

}
