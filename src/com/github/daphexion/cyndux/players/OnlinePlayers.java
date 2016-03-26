package com.github.daphexion.cyndux.players;

import java.util.HashMap;

import com.github.daphexion.cyndux.exceptions.AlreadyConnected;

public class OnlinePlayers {
	private static HashMap<String, Player> OnlinePlayers = new HashMap<String, Player>();

	public HashMap<String, Player> get() {
		return OnlinePlayers;
	}

	public synchronized void add(Player player) throws AlreadyConnected {
		if (OnlinePlayers.containsValue(player) || OnlinePlayers.containsKey(player.getUsername())) {
			throw new AlreadyConnected(player.getUsername());
		} else {
			OnlinePlayers.put(player.getUsername(), player);
		}
	}

	public synchronized void remove(Player player) {
		if (player != null) {
			OnlinePlayers.remove(player.getUsername());
		}
	}

}
