package com.github.daphexion.cyndux.exceptions;

public class PlayerDoesNotExist extends Exception {
	private static final long serialVersionUID = 3876398947552915585L;

	public PlayerDoesNotExist(String Username) {
		super("The player "+Username+ " doesn't exist!");
	}

}
