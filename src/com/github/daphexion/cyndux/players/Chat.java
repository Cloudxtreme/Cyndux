package com.github.daphexion.cyndux.players;

import com.github.daphexion.cyndux.Main;

public class Chat {

	public static void main(Player player, String message) {
		switch (player.getChatStatus()) {
		case NOTINCHAT:
			player.send("You are not in any chat mode! Use the chat command!");
			break;
		case SYSTEM:
			for (Player plyr : Main.players.get().values()) {
				if (plyr.getChatStatus().equals(ChatMode.SYSTEM) && plyr != player) {
					plyr.sendChat(player.getUsername() + "> " + message);
				}
			}
			break;
		case SECTOR:
			for (Player plyr : Main.players.get().values()) {
				if (plyr.getChatStatus().equals(ChatMode.SECTOR) && plyr != player
						&& (player.getLocation()==plyr.getLocation())) {
					plyr.sendChat(player.getUsername() + "> " + message);
				}
			}
			break;
		case GROUP:
			// TODO THE ENTIRE CORP THING
			break;
		}
	}

}
