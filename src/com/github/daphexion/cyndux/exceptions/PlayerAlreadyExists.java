package com.github.daphexion.cyndux.exceptions;
public class PlayerAlreadyExists extends Exception {
	private static final long serialVersionUID = -7951366847119566753L;
	public PlayerAlreadyExists(String Username) {
		super("The player "+Username+ " already exists!");
	}

}
