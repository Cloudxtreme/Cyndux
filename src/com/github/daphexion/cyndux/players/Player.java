package com.github.daphexion.cyndux.players;

import java.io.File;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import com.github.daphexion.cyndux.Main;
import com.github.daphexion.cyndux.exceptions.ItemDoesNotExist;
import com.github.daphexion.cyndux.exceptions.NotInInventory;
import com.github.daphexion.cyndux.exceptions.PlayerAlreadyExists;
import com.github.daphexion.cyndux.exceptions.PlayerDoesNotExist;
import com.github.daphexion.cyndux.screen.ScreenMode;
import com.github.daphexion.cyndux.sectors.LocationInSector;

public class Player {
	private String username;
	private ArrayList<Integer> Inventory = new ArrayList<Integer>();
	private String ship;
	private ChatMode chatMode;
	PrintWriter out;
	private ScreenMode screen = ScreenMode.MAIN;
	private byte mapcursor = 5;
	public boolean cannotChat;
	private LocationInSector locationInSector;

	public Player(String n, PrintWriter o) {
		username = n;
		out = o;
		cannotChat = false;
		chatMode = ChatMode.NOTINCHAT;
	}
	public void load() throws PlayerDoesNotExist{
		try {
			if(Main.server.database.Query("SELECT * FROM Players WHERE Username = '"+ username+"'").next()){
				return;
			} else {
				throw new PlayerDoesNotExist(username);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public String getShip() {
		return ship;
	}

	public boolean authenticate(String password) {
		boolean a = false;
		try {
			if (!(password.hashCode()== Main.server.database.Query("SELECT Password FROM Players WHERE Username = '"+ username+"';").getInt("Password"))) {
				a=false;
			} else {
				a=true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return a;
	}

	public int getLocation() {
		int a = 0;
		try {
			a=Main
					.server
					.database
					.Query("SELECT Location "
							+ "FROM Players "
							+ "WHERE Username = '"+ username+"'").getInt("Location");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return a;
	}
	public void setLocation(int loc) {
		Main.server.database.Update("UPDATE Players SET Location = "+loc+" WHERE Username = '"+username+"'");
		return;
	}

	public void register(String password) throws PlayerAlreadyExists {
		Main.server.database.Update("INSERT INTO Players (Username, Password, Money, Location, Inventory) "
									+ "VALUES ( '"
									+ username + "', "
									+ password.hashCode()+", "
									+ Main.server.prop.getProperty("starting.money")+", "
									+ "NULL, "
									+ "NULL )");
		return;
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

	public ScreenMode getScreen(){ 
		return this.screen;
	}

	public void setScreen(ScreenMode state) {
		this.screen = state;
		return;
	}

	public void setChatStatus(ChatMode mode) {
		chatMode = mode;
		switch(mode){
		case GROUP:
			send("Changed chat mode to GROUP");
			break;
		case NOTINCHAT:
			send("Changed chat mode to off");
			break;
		case SECTOR:
			send("Changed chat mode to SECTOR");
			break;
		case SYSTEM:
			send("Changed chat mode to SYSTEM");
			break;
		default:
			break;
		}
		return;
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

	public LocationInSector getLocationInSector() {
		return locationInSector;
	}

	public void setLocationInSector(LocationInSector locationInSector) {
		this.locationInSector = locationInSector;
	}
	public ArrayList<Integer> getInventory(){
		return Inventory;
	}
	public void addToInventory(int ItemID) throws ItemDoesNotExist{
		File file = new File("./items/"+ItemID+".properties");
		if (file.exists()){
			Inventory.add(ItemID);
		} else {
			throw new ItemDoesNotExist(ItemID);
		}
		return;
	}
	public void removeFromInventory(int ItemID) throws ItemDoesNotExist,NotInInventory{
		File file = new File("./items/"+ItemID+".properties");
		if (file.exists()){
			if (Inventory.contains(ItemID)){
				Inventory.remove(ItemID);
			}else{	
				throw new NotInInventory();
			}
		} else {
			throw new ItemDoesNotExist(ItemID);
		}
		return;
	}
}