package com.github.daphexion.cyndux.exceptions;

public class WrongPassword extends Exception {

	private static final long serialVersionUID = 1022965181546987210L;

	public WrongPassword(){
		super("Wrong password!");
	}
}
