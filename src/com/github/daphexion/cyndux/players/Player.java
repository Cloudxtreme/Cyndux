package com.github.daphexion.cyndux.players;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import com.github.daphexion.cyndux.sectors.Map;

public class Player {
	private String username;
	private File file;
	private Properties playerProp = new Properties();
	private String ship;
	private ChatMode chatMode;
	PrintWriter out;
	private Screen screen = Screen.MAIN;
	private byte mapcursor = 5;
	public boolean cannotChat;

	public Player(String n, PrintWriter o) {
		username = n;
		out = o;
		file = new File("./users/" + this.username + ".properties");
	}

	public void printMap() {
		Map.printMap(this);
	}

	public String getShip() {
		return ship;
	}

	public String load() {
		if (!file.exists()) {
			return "NOTEXIST";
		} else {
			try {
				FileInputStream input = new FileInputStream(file);
				playerProp.load(input);
				input.close();
				return "OK";
			} catch (IOException e) {
				e.printStackTrace();
				return "FAIL";
			}
		}
	}

	public boolean authenticate(String password) {
		if (!playerProp.getProperty("password").equals(Integer.toString(password.hashCode()))) {
			return false;
		} else {
			return true;
		}
	}

	public String getLocation() {
		return playerProp.getProperty("location");
	}

	public void setLocation(int loc) {
		try {
			FileOutputStream output = new FileOutputStream("./users/" + username + ".properties");
			playerProp.setProperty("location", Integer.toString(loc));
			playerProp.store(output, null);
			output.flush();
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String register(String password) {
		if (file.exists()) {
			return "ALREADYEXIST";
		} else {
			try {
				file.createNewFile();
				FileInputStream input = new FileInputStream("./server.properties");
				Properties prop = new Properties();
				prop.load(input);
				FileOutputStream output = new FileOutputStream(file);
				playerProp.setProperty("money", prop.getProperty("starting.money"));
				playerProp.setProperty("password", Integer.toString(password.hashCode()));
				playerProp.setProperty("location", "");
				playerProp.setProperty("ship", prop.getProperty("starting.ship"));
				playerProp.store(output, null);
				this.ship = playerProp.getProperty("ship");
				output.flush();
				output.close();
				return "OK";
			} catch (Exception e) {
				e.printStackTrace();
				return "FAIL";
			}
		}
	}

	public String getUsername() {
		return this.username;
	}

	public void send(Object o) {
		out.println(o);
		return;
	}

	public void sendChat(Object o) {
		while(true){
			if (!cannotChat){
				out.println(o);
				break;
			}
		}
		return;
	}

	public Screen getScreen() {
		return this.screen;
	}

	public void setScreen(Screen state) {
		this.screen = state;
		return;
	}

	public void setChatStatus(ChatMode mode) {
		chatMode = mode;
	}

	public ChatMode getChatStatus() {
		return chatMode;
	}

	public byte getMapCursor() {
		return mapcursor;
	}

	public void setMapCursor(byte mapcursor) {
		this.mapcursor = mapcursor;
	}
}
