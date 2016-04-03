package com.github.daphexion.cyndux.screen;

import java.lang.reflect.InvocationTargetException;

import com.github.daphexion.cyndux.players.Player;

public enum ScreenMode {
	MAIN("com.github.daphexion.cyndux.screen.MainScreen"),
	MAP("com.github.daphexion.cyndux.screen.MapScreen"),
	GOTO("com.github.daphexion.cyndux.screen.GotoScreen"),
	WARP("com.github.daphexion.cyndux.screen.WarpScreen"),
	OBJECT("com.github.daphexion.cyndux.screen.ObjectScreen"),
	INVENTORY("com.github.daphexion.cyndux.screen.InventoryScreen")
	;
	private final String c;
	ScreenMode(String c){
		this.c = c;
	}
	public void print(Player player){
		player.cannotChat=true;
		try {
			Class.forName(c).getMethod("print", void.class).invoke(null, player);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		player.cannotChat=false;
		return;
	}
}