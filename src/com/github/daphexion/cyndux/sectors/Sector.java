package com.github.daphexion.cyndux.sectors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;
import java.util.Vector;

import com.github.daphexion.cyndux.Main;
import com.github.daphexion.cyndux.players.Player;

public class Sector {
	public static synchronized void initializeSector(int sectorNum) {
		File sector = new File("./sectors/" + Integer.toString(sectorNum) + ".properties");
		if (!sector.exists()) {
			Random sectorRand = new Random();
			int PlanetsAmount = sectorRand.nextInt(7);
			boolean station = false;
			boolean star = false;
			int wormholes = 0;

			int asteroidBelt = 0;
			int nebula = 0;
			int[] planets = new int[PlanetsAmount];
			// Because fuck arrays.
			int stationChance = sectorRand.nextInt(101);
			if (stationChance >= 31) {
				// Yes station
				station = true;
			} else {
				// No station
				station = false;
			}
			int wormholeChance = sectorRand.nextInt(101);
			if (wormholeChance >= 76 && wormholeChance <= 90) {
				// 1 wormholes
				wormholes = 1;
			}
			if (wormholeChance >= 91) {
				// 2 wormholes
				wormholes = 2;
			}
			int asteroidBeltChance = sectorRand.nextInt(101);
			if (asteroidBeltChance >= 31 && asteroidBeltChance <= 70) {
				// Small Belt
				asteroidBelt = 1;
			}
			if (asteroidBeltChance >= 71 && asteroidBeltChance <= 90) {
				// Medium Belt
				asteroidBelt = 2;
			}
			if (asteroidBeltChance >= 91) {
				// Large Belt
				asteroidBelt = 3;
			}
			int starChance = sectorRand.nextInt(101);
			if (starChance >= 51) {
				star = true;
				// Yes star
				int solarSystemChance = sectorRand.nextInt(101);
				if (solarSystemChance >= 71) {
					// THERE IS A SOLARSYSTEM
					// Generate planets
					for (int i = 0; i < (PlanetsAmount); i++) {
						int planetType = sectorRand.nextInt(13);
						switch (planetType) {
						case 1:
							planets[i] = 1;
							// Terran
							break;
						case 2:
							planets[i] = 2;
							// Jungle
							break;
						case 3:
							planets[i] = 3;
							// Ocean
							break;
						case 4:
							planets[i] = 4;
							// Arid
							break;
						case 5:
							planets[i] = 5;
							// Tundra
							break;
						case 6:
							planets[i] = 6;
							// Desert
							break;
						case 7:
							planets[i] = 7;
							// Arctic
							break;
						case 8:
							planets[i] = 8;
							// Lava
							break;
						case 9:
							planets[i] = 9;
							// Barren
							break;
						case 10:
							planets[i] = 10;
							// Methane
							break;
						case 11:
							planets[i] = 11;
							// Hydrogen
							break;
						case 12:
							planets[i] = 12;
							// Helium
							break;
						}
					}
				}
			}
			int nebulaChance = sectorRand.nextInt(101);
			if (nebulaChance >= 81 && asteroidBeltChance <= 70) {
				// Small Nebula
				nebula = 1;
			}
			if (nebulaChance >= 91 && asteroidBeltChance <= 90) {
				// Medium Nebula
				nebula = 2;
			}
			if (nebulaChance >= 96) {
				// Large Nebula
				nebula = 3;
			}
			try {
				sector.createNewFile();
				Properties sect = new Properties();
				// TODO Allow for generation configuration.
				// Decide if the sector will have certain features or not.
				// Make planet size here.
				if (station == true) {
					sect.setProperty("station.exists", "true");
				} else {
					sect.setProperty("station.exists", "false");
				}
				switch (wormholes) {
				case 1:
					sect.setProperty("wormholes.1", Integer.toString(sectorRand.nextInt(10001)));
					break;
				case 2:
					sect.setProperty("wormholes.1", Integer.toString(sectorRand.nextInt(10001)));
					sect.setProperty("wormholes.2", Integer.toString(sectorRand.nextInt(10001)));
					break;
				}
				switch (asteroidBelt) {
				case 1:
					sect.setProperty("asteroidbelt", "small");
					break;
				case 2:
					sect.setProperty("asteroidbelt", "medium");
					break;
				case 3:
					sect.setProperty("asteroidbelt", "large");
					break;
				}
				if (star) {
					sect.setProperty("star.exists", "true");
				} else {
					sect.setProperty("star.exists", "false");
				}
				switch (nebula) {
				case 1:
					sect.setProperty("nebula", "small");
					break;
				case 2:
					sect.setProperty("nebula", "medium");
					break;
				case 3:
					sect.setProperty("nebula", "large");
					break;
				}
				for (int i = 0; i < (planets.length); i++) {
					String num = Integer.toString(i);
					String size = planetSize();
					switch (planets[i]) {
					case 1:
						sect.setProperty("planet." + num, "terran" + "," + size);
						break;
					case 2:
						sect.setProperty("planet." + num, "jungle" + "," + size);
						break;
					case 3:
						sect.setProperty("planet." + num, "ocean" + "," + size);
						break;
					case 4:
						sect.setProperty("planet." + num, "arid" + "," + size);
						break;
					case 5:
						sect.setProperty("planet." + num, "tundra" + "," + size);
						break;
					case 6:
						sect.setProperty("planet." + num, "desert" + "," + size);
						break;
					case 7:
						sect.setProperty("planet." + num, "arctic" + "," + size);
						break;
					case 8:
						sect.setProperty("planet." + num, "lava" + "," + size);
						break;
					case 9:
						sect.setProperty("planet." + num, "barren" + "," + size);
						break;
					case 10:
						sect.setProperty("planet." + num, "methane" + "," + size);
						break;
					case 11:
						sect.setProperty("planet." + num, "hydrogen" + "," + size);
						break;
					case 12:
						sect.setProperty("planet." + num, "helium" + "," + size);
						break;

					}
				}
				FileOutputStream output = new FileOutputStream(sector);
				sect.store(output, null);
				output.flush();
				output.close();
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	private static String planetSize() {
		Random planetRand = new Random();
		String size = null;
		int sizeChance = planetRand.nextInt(101);
		if (sizeChance <= 10) {
			// Tiny
			size = "tiny";
		}
		if (sizeChance >= 11 && sizeChance <= 30) {
			// Small
			size = "small";
		}
		if (sizeChance >= 31 && sizeChance <= 70) {
			// Medium
			size = "medium";
		}
		if (sizeChance >= 71 && sizeChance <= 90) {
			// Large
			size = "large";
		}
		if (sizeChance >= 91) {
			// Huge
			size = "huge";
		}
		return size;
	}

	public static Properties getProperties(int sectorNum) {
		Properties sectorprop = new Properties();
		try {
			FileInputStream sectorinput = new FileInputStream(new File("./sectors/" + Integer.toString(sectorNum) + ".properties"));
			sectorprop.load(sectorinput);
			sectorinput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sectorprop;
	}
	public static Vector<String> getObjects(int sectorNum){
		Properties sectorprop = new Properties();
		Vector<String> objects = new Vector<String>();
		try {
			FileInputStream sectorinput = new FileInputStream(new File("./sectors/" + Integer.toString(sectorNum) + ".properties"));
			sectorprop.load(sectorinput);
			sectorinput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		if (sectorprop.getProperty("station.exists") != null) {
			objects.addElement("station");
		}
		if (sectorprop.getProperty("wormholes.1") != null) {
			objects.addElement("wormhole");
		}
		if (sectorprop.getProperty("wormholes.2") != null) {
			objects.addElement("another wormhole");
		}
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
		for(int i=0;i<7;i++){
			String planet = "";
			if (sectorprop.getProperty("planet."+i) != null) {
				String[] details = sectorprop.getProperty("planet."+i).toLowerCase().split(",");
				switch(details[1]){
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
		for (Player plyr : players){
			objects.addElement("ship owned by "+plyr.getUsername());
		}
		return objects;
	}
	public static Vector<Player> getPlayersInSector(int sectorNum){
		Vector<Player> players = new Vector<Player>();
		for (Player plyr : Main.players) {
			if (plyr.getLocation().equals(Integer.toString(sectorNum))) {
				players.addElement(plyr);
			}
		}
		return players;
	}

}