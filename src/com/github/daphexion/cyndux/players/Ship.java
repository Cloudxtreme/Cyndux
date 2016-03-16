package com.github.daphexion.cyndux.players;
import java.util.Properties;

public class Ship {
	private Player player;
	public Ship(Player player) {
		this.player = player;
		load();
	}
	private void load(){
		player.getShip();
		Properties playerprop = player.playerProp;
		
	}

}
