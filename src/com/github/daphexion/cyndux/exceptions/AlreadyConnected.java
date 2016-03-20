package com.github.daphexion.cyndux.exceptions;

public class AlreadyConnected extends Exception {
	private static final long serialVersionUID = 7255491545660640530L;
	public AlreadyConnected(String Username){
		super(Username + " is already logged in!");
	}
}
