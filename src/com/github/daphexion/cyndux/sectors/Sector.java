package com.github.daphexion.cyndux.sectors;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import com.github.daphexion.cyndux.Main;
import com.github.daphexion.cyndux.players.Player;

public class Sector {
	public static synchronized void initializeSector(int sectorNum) {
		try {
			if (Main.server.database.Query("SELECT * FROM Sectors WHERE Location = '" + sectorNum + "'").next()) {
				return;
			} else {
				Main.server.database.Update("INSERT INTO Sectors(Location) Values (" + sectorNum+")");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Random sectorRand = new Random();
		int starChance = sectorRand.nextInt(101);
		if (starChance >= 51) {
			Main.server.database.Update("UPDATE Sectors SET Star = 1 WHERE Location = " + sectorNum);
			int systemChance = sectorRand.nextInt(101);
			if (systemChance >= 0) {//71
				int PlanetsAmount = sectorRand.nextInt(7);
				for (int i = 1; i < (PlanetsAmount+1); i++) {//FIXME No planets?
					int planetType = sectorRand.nextInt(13);
					// Terran:1
					// Jungle:2
					// Ocean:3
					// Arid:4
					// Tundra:5
					// Desert:6
					// Arctic:7
					// Lava:8
					// Barren:9
					// Methane:10
					// Hydrogen:11
					// Helium:12
					Main.server.database.Update("UPDATE Sectors SET Planet" + i + " = " + Integer.toString(planetSize()) + planetType
							+ " WHERE Location = " + sectorNum);
				}
			}
		}
		int stationChance = sectorRand.nextInt(101);
		if (stationChance >= 31) {
			// Yes station
			Main.server.database.Update("UPDATE Sectors SET Station = 1 WHERE Location = " + sectorNum);
		}
		switch (sectorRand.nextInt(3)) {
		case 1:
			Main.server.database.Update("UPDATE Sectors SET Wormhole1 =" + Integer.toString(sectorRand.nextInt(10001))
					+ " WHERE Location = " + sectorNum);
			break;
		case 2:
			Main.server.database.Update("UPDATE Sectors SET Wormhole1 =" + Integer.toString(sectorRand.nextInt(10001))
					+ " WHERE Location = " + sectorNum);
			Main.server.database.Update("UPDATE Sectors SET Wormhole2 =" + Integer.toString(sectorRand.nextInt(10001))
					+ " WHERE Location = " + sectorNum);
			break;
		}
		int asteroidBeltChance = sectorRand.nextInt(101);
		int beltSize = (asteroidBeltChance >= 31 && asteroidBeltChance <= 70) ? 1
				: ((asteroidBeltChance >= 71 && asteroidBeltChance <= 90) ? 2 : ((asteroidBeltChance >= 91) ? 3 : 0));
		Main.server.database.Update("UPDATE Sectors SET AsteroidBelt =" + beltSize + " WHERE Location = " + sectorNum);
		int nebulaChance = sectorRand.nextInt(101);
		int nebulaSize = (nebulaChance >= 71 && nebulaChance <= 85) ? 1
				: ((nebulaChance >= 86 && nebulaChance <= 90) ? 2 : ((nebulaChance >= 96) ? 3 : 0));
		Main.server.database.Update("UPDATE Sectors SET Nebula =" + nebulaSize + " WHERE Location = " + sectorNum);
	}

	private static int planetSize() {
		Random planetRand = new Random();
		int size = 0;
		int sizeChance = planetRand.nextInt(101);
		if (sizeChance <= 10) {
			// Tiny
			size = 1;
		}
		if (sizeChance >= 11 && sizeChance <= 30) {
			// Small
			size = 2;
		}
		if (sizeChance >= 31 && sizeChance <= 70) {
			// Medium
			size = 3;
		}
		if (sizeChance >= 71 && sizeChance <= 90) {
			// Large
			size = 4;
		}
		if (sizeChance >= 91) {
			// Huge
			size = 5;
		}
		return size;
	}
	public static Vector<String> getObjects(int sectorNum) {
		Vector<String> objects = new Vector<String>();
		try{
			ResultSet rs = Main.server.database.Query("SELECT * FROM Sectors WHERE Location = "+sectorNum+";");
			if(rs.getInt("Star")==1){
				objects.addElement("star");
			}
			if(rs.getInt("Station")==1){
				objects.addElement("station");
			}
			if(rs.getInt("Wormhole1")!=0){
				objects.addElement("wormhole");
			}
			if(rs.getInt("Wormhole2")!=0){
				objects.addElement("another wormhole");
			}
			switch(rs.getInt("AsteroidBelt")){
			case 1:
				objects.addElement("small asteroid belt");
				break;
			case 2:
				objects.addElement("medium asteroid belt");
				break;
			case 3:
				objects.addElement("large asteroid belt");
				break;
			}
			switch(rs.getInt("Nebula")){
			case 1:
				objects.addElement("small nebula");
				break;
			case 2:
				objects.addElement("medium nebula");
				break;
			case 3:
				objects.addElement("large nebula");
				break;
			}
			for(int i = 1;i<7;i++){
				if (rs.getInt("Planet"+i)!=0){
					String planetdesc = "";
					String planet = Integer.toString(rs.getInt("Planet"+i));
					switch(planet.substring(0, 1)){
					case "1":
						planetdesc+="tiny";
						break;
					case "2":
						planetdesc+="small";
						break;
					case "3":
						planetdesc+="medium";
						break;
					case "4":
						planetdesc+="large";
						break;
					case "5":
						planetdesc+="huge";
						break;
					}
					planetdesc+=" ";
					switch(planet.substring(1)){
					case "1":
						planetdesc+="terran";
						break;
					case "2":
						planetdesc+="jungle";
						break;
					case "3":
						planetdesc+="ocean";
						break;
					case "4":
						planetdesc+="arid";
						break;
					case "5":
						planetdesc+="tundra";
						break;
					case "6":
						planetdesc+="desert";
						break;
					case "7":
						planetdesc+="arctic";
						break;
					case "8":
						planetdesc+="lava";
						break;
					case "9":
						planetdesc+="barren";
						break;
					case "10":
						planetdesc+="methane";
						break;
					case "11":
						planetdesc+="hydrogen";
						break;
					case "12":
						planetdesc+="helium";
						break;
					}
					planetdesc+=" planet";
					objects.add(planetdesc);
				}
			}
		} catch (SQLException e){
			e.printStackTrace();
		}
		/*
		if (sectorprop.getProperty("asteroidbelt") != null) {
			switch (sectorprop.getProperty("asteroidbelt")) {
			case "small":
				objects.addElement("small asteroid belt");
				break;
			case "medium":
				objects.addElement("medium asteroid belt");
				break;
			case "large":
				objects.addElement("large asteroid belt");
				break;
			}
		}
		if (sectorprop.getProperty("star.exists") != null) {
			objects.addElement("star");
		}
		if (sectorprop.getProperty("nebula") != null) {
			switch (sectorprop.getProperty("nebula")) {
			case "small":
				objects.addElement("small nebula");
				break;
			case "medium":
				objects.addElement("medium nebula");
				break;
			case "large":
				objects.addElement("large nebula");
				break;
			}
		}
		for (int i = 0; i < 7; i++) {
			String planet = "";
			if (sectorprop.getProperty("planet." + i) != null) {
				String[] details = sectorprop.getProperty("planet." + i).toLowerCase().split(",");
				switch (details[1]) {
				case "tiny":
					planet = "tiny ";
					break;
				case "small":
					planet = "small ";
					break;
				case "medium":
					planet = "medium ";
					break;
				case "large":
					planet = "large ";
					break;
				case "huge":
					planet = "huge ";
					break;
				default:
					planet = "";
					break;
				}
				planet += details[0] + " planet";
				objects.addElement(planet);
			}
		}
		Vector<Player> players = getPlayersInSector(sectorNum);
		for (Player plyr : players) {
			objects.addElement("ship owned by " + plyr.getUsername());
		}*/
		return objects;
	}

	public static Vector<Player> getPlayersInSector(int sectorNum) {
		Vector<Player> Sectorplayers = new Vector<Player>();
		for (Player plyr : Main.players.get().values()) {
			if (plyr.getLocation() == sectorNum) {
				Sectorplayers.addElement(plyr);
			}
		}
		return Sectorplayers;
	}

	public static void Goto(Player player) {
		// TODO
	}

	public static HashMap<String, Integer> getSurroundingSectors(int sectorNum) {
		HashMap<String, Integer> SurroundingSectors = new HashMap<String, Integer>();
		int maploc = sectorNum;
		int mapn = (maploc - 100);
		int maps = maploc + 100;
		int mape = maploc + 1; // how the fuck does increment
								// operators work
		int mapw = maploc - 1;
		int mapne = mapn + 1;
		int mapse = maps + 1;
		int mapsw = maps - 1;
		int mapnw = mapn - 1;
		if (((maploc % 100) <= 1)) { // T R U L Y S P H E R I C A L B O I S
			switch (maploc % 100) {
			case 0:
				mape = maploc - 99;
				break;
			case 1:
				mapw = maploc + 99;
				break;
			}
		} else {
			if (maploc <= 100) {
				mapn = maploc + 50;
			}
			if (maploc >= 9900) {
				maps = maploc + 50;
			}
		}
		SurroundingSectors.put("maploc", maploc);
		SurroundingSectors.put("mapnw", mapnw);
		SurroundingSectors.put("mapn", mapn);
		SurroundingSectors.put("mapne", mapne);
		SurroundingSectors.put("mapw", mapw);
		SurroundingSectors.put("mape", mape);
		SurroundingSectors.put("mapsw", mapsw);
		SurroundingSectors.put("maps", maps);
		SurroundingSectors.put("mapse", mapse);
		return SurroundingSectors;
	}
}