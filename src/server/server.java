/*WARNING, THIS IS WRITTEN WHEN I WAS 13,
WHEN I HAD NO UNDERSTANDING OF OPTIMISATION
OR DISCRETE MATHEMATICS, OR GENERAL GAME DESIGN
SO IF YOU ARE READING THIS BECAUSE I
SHOWED THIS OFF, OR A CODE REVIEW,
I KNOW MY CODE IS MESSY.
BUT AT LEAST I DIDNT GET EATEN
BY A VELOCIRAPTOR*/
package server;

//Import some stuff. One is for the Socket Connection, the other is for writing stuff.
import java.util.*;
import java.io.*;
import java.net.*;

public class server {

	/**
	 * The port that the server listens on.
	 */
	static Properties prop = new Properties();
	private static HashSet<PrintWriter> writers = new HashSet<PrintWriter>();
	private static HashSet<String> online = new HashSet<String>();

	/**
	 * The application main method, which just listens on a port and spawns
	 * handler threads.
	 */
	public static void main(String[] args) throws Exception {
		initializeServer();
		System.out.println("The server is running.");
		int port = Integer.parseInt(prop.getProperty("port"));
		ServerSocket listener;
		while (true) {
			try {
				listener = new ServerSocket(port);
				break;
			} catch (BindException e) {
				System.out.println("Port " + port + " already in use!");
				port++;
				System.out.println("Using " + port + " instead!");
			}
		}
		try {
			while (true) {
				new Handler(listener.accept()).start();
			}
		} finally {
			listener.close();
		}
	}
	private static class Handler extends Thread {
		private String response;
		private Socket socket;
		private BufferedReader in;
		private PrintWriter out;

		private enum Screen {
			MAIN, MAP, GOTO
		}

		/**
		 * Constructs a handler thread, squirreling away the socket. All the
		 * interesting work is done in the run method.
		 */
		public Handler(Socket socket) {
			this.socket = socket;
		}

		/**
		 * Services this thread's client by requesting a screen name until a
		 * unique one has been submitted, then acknowledges the name and
		 * registers the output stream for the client in a global set, then
		 * repeatedly gets inputs and broadcasts them.
		 */
		public void run() {
			String username = null;
			Properties userprop = new Properties();
			try {
				// Create character streams for the socket.
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				out = new PrintWriter(socket.getOutputStream(), true);
				// Request a name from this client.Note that
				// checking for the existence of a name and adding the name
				System.out.println("User Connected from IP: " + socket.getRemoteSocketAddress() + " !");
				out.println("Are you registering or logging in?");
				boolean notloggedin = true;
				while (notloggedin) {
					response = in.readLine().toLowerCase();
					if (response.equals(null)) {
						return;
					} else {
						switch (response) {
						case "login":
							out.println("Enter your details in this format:[Username],[Password]");
							while (true) {
								response = in.readLine();
								if (response.equals(null)) {
									return;
								} else {
									String[] userpass = response.split(",");
									File userfile = new File("./users/" + userpass[0] + ".properties");
									if (!userfile.exists()) {
										out.println("We could not find your account!");
										out.println("Are you registering or logging in?");
										break;
									} else {
										try {
											FileInputStream input = new FileInputStream(userfile);
											userprop.load(input);
											input.close();
										} catch (IOException e) {
											e.printStackTrace();
										}
										if (userprop.getProperty("password").equals(userpass[1])) {
											username = userpass[0];
											if (!online.contains(username)) {
												synchronized (online) {
													online.add(username);
												}
												System.out.println("IP " + socket.getRemoteSocketAddress()
														+ " logged in as " + username);
											} else {
												out.println("This user is already logged in!");
												out.println("Are you registering or logging in?");
												break;
											}
											notloggedin = false;
											break;
										} else {
											out.println("Wrong password!");
											out.println("Are you registering or logging in?");
											break;
										}
									}
								}
							}
							break;
						case "register":
							out.println("Enter your details in this format:[Username],[Password]");
							while (true) {
								response = in.readLine();
								if (response.equals(null)) {
									return;
								} else {
									String[] userpass = response.split(",");
									File userfile = new File("./users/" + userpass[0] + ".properties");

									if (userfile.exists()) {
										out.println("This username is already taken!");
										out.println("Are you registering or logging in?");
										break;
									} else {
										try {
											userfile.createNewFile();
											FileInputStream input = new FileInputStream("./server.properties");
											prop.load(input);
											FileOutputStream output = new FileOutputStream(userfile);
											userprop.setProperty("money", prop.getProperty("starting.money"));
											userprop.setProperty("password", userpass[1]);
											userprop.setProperty("location", "");
											userprop.setProperty("ship", prop.getProperty("starting.ship"));
											userprop.store(output, null);
											output.flush();
											output.close();
										} catch (Exception e) {
											e.printStackTrace();
										} finally {
											username = userpass[0];
											synchronized (online) {
												online.add(username);
											}
											System.out.println("IP " + socket.getRemoteSocketAddress()
													+ " logged in as " + username);
											notloggedin = false;
										}
										break;
									}
								}
							}
						}
					}
				}
				if (userprop.getProperty("location").isEmpty()) {
					if (prop.getProperty("starting.location").isEmpty()) {
						Random r = new Random();
						String randomloc = Integer.toString(r.nextInt(10001));
						try {
							FileOutputStream output = new FileOutputStream("./users/" + username + ".properties");
							userprop.setProperty("location", randomloc);
							userprop.store(output, null);
							output.flush();
							output.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					} else {
						try {
							FileOutputStream output = new FileOutputStream("./users/" + username + ".properties");
							userprop.setProperty("location", prop.getProperty("starting.location"));
							userprop.store(output, null);
							output.flush();
							output.close();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				out.println("Logged in as " + username);
				Screen screen = Screen.MAIN;
				int mapcursor = 5;
				while (true) {
					// Dis where da magic happens.
					switch (screen) {
					case MAIN:
						Properties sectorprop = new Properties();
						File sector = new File("./sectors/" + userprop.getProperty("location") + ".properties");
						if (!sector.exists()) {
							System.out.println(
									"Sector " + userprop.getProperty("location") + "does not exist! Generating!");
							initializeSector(userprop.getProperty("location"));
						}
						out.println("You are now in " + userprop.getProperty("location"));
						try {
							FileInputStream sectorinput = new FileInputStream(
									"./sectors/" + userprop.getProperty("location") + ".properties");
							sectorprop.load(sectorinput);
							sectorinput.close();
							if (sectorprop.getProperty("station.exists") != null) {
								out.println("You see a station.");
							}
							if (sectorprop.getProperty("wormholes.1") != null) {
								out.println("You see a wormhole.");
							}
							if (sectorprop.getProperty("wormholes.2") != null) {
								out.println("You see another wormhole.");
							}
							if (sectorprop.getProperty("asteroidbelt") != null) {
								switch (sectorprop.getProperty("asteroidbelt")) {
								case "small":
									out.println("You see a small asteroid belt");
									break;
								case "medium":
									out.println("You see a medium asteroid belt");
									break;
								case "large":
									out.println("You see a large asteroid belt");
									break;
								}
							}
							if (sectorprop.getProperty("star.exists") != null) {
								out.println("You see a star.");
							}
							if (sectorprop.getProperty("nebula") != null) {
								switch (sectorprop.getProperty("nebula")) {
								case "small":
									out.println("You see a small nebula.");
									break;
								case "medium":
									out.println("You see a medium nebula.");
									break;
								case "large":
									out.println("You see a large nebula.");
									break;
								}
							}
							//You see another ship owned by someone named MichaelCera.
							//If only...
							out.println("What is your command?");
						} catch (IOException e) {
							e.printStackTrace();
						}
						break;
					case GOTO:
						break;
					case MAP:
						int maploc = Integer.parseInt(userprop.getProperty("location"));
						int mapn = (maploc - 100);
						int mape = maploc + 1; // how the fuck does increment
												// operators work
						int mapw = maploc - 1;
						int maps = maploc + 100;
						int mapne = maploc - 99;
						int mapse = maploc + 101;
						int mapsw = maploc + 99;
						int mapnw = maploc - 101; // TODO EDGES SECTORS
						out.println("╔═════╦═════╦═════╗");
						out.println(
								"║" + sectorThing(mapnw) + "║" + sectorThing(mapn) + "║" + sectorThing(mapne) + "║");
						switch (mapcursor) {

						case 1:
							out.println("║  ●  ║     ║     ║");
							break;
						case 2:
							out.println("║     ║  ●  ║     ║");
							break;
						case 3:
							out.println("║     ║     ║  ●  ║");
							break;
						default:
							out.println("║     ║     ║     ║");
							break;

						}
						out.println("╠═════╬═════╬═════╣");
						out.println(
								"║" + sectorThing(mapw) + "║" + sectorThing(maploc) + "║" + sectorThing(mape) + "║");
						switch (mapcursor) {

						case 4:
							out.println("║  ●  ║     ║     ║");
							break;
						case 5:
							out.println("║     ║  ●  ║     ║");
							break;
						case 6:
							out.println("║     ║     ║  ●  ║");
							break;
						default:
							out.println("║     ║     ║     ║");
							break;
						}
						out.println("╠═════╬═════╬═════╣");
						out.println(
								"║" + sectorThing(mapsw) + "║" + sectorThing(maps) + "║" + sectorThing(mapse) + "║");
						switch (mapcursor) {

						case 7:
							out.println("║  ●  ║     ║     ║");
							break;
						case 8:
							out.println("║     ║  ●  ║     ║");
							break;
						case 9:
							out.println("║     ║     ║  ●  ║");
							break;
						default:
							out.println("║     ║     ║     ║");
							break;

						}
						out.println("╚═════╩═════╩═════╝");
						out.println("To move the cursor, type up, down, left or right.");
						out.println("To warp to where your cursor is, type warp.");
						break;
					}
					// Print out whats in the sector
					while (true) {
						response = in.readLine();
						if (response.equals(null)) {
							return;
						}
						switch (response.toLowerCase()) {
						case "map":
							screen = Screen.MAP;
							break;

						case "up":
							if (screen != Screen.MAP) {
								out.println("Sorry captain, I did not understand.");
								break;
							} else {
								switch (mapcursor) {
								case 1:
									mapcursor = 7;
									break;
								case 2:
									mapcursor = 8;
									break;
								case 3:
									mapcursor = 9;
									break;
								default:
									mapcursor -= 3;
									break;
								}
							}
							break;
						case "down":
							if (screen != Screen.MAP) {
								out.println("Sorry captain, I did not understand.");
								break;
							}else{
								switch (mapcursor) {
								case 7:
									mapcursor = 1;
									break;
								case 8:
									mapcursor = 2;
									break;
								case 9:
									mapcursor = 3;
									break;
								default:
									mapcursor += 3;
									break;
								}
							}
							break;
						case "left":
							if (screen != Screen.MAP) {
								out.println("Sorry captain, I did not understand.");
								break;
							}else{
								switch (mapcursor) {
								case 1:
									mapcursor = 3;
									break;
								case 4:
									mapcursor = 6;
									break;
								case 7:
									mapcursor = 9;
									break;
								default:
									mapcursor--;
									break;
								}
							}
							break;
						case "right":
							if (screen != Screen.MAP) {
								out.println("Sorry captain, I did not understand.");
								break;
							}else{
								switch (mapcursor) {
								case 3:
									mapcursor = 1;
									break;
								case 6:
									mapcursor = 4;
									break;
								case 9:
									mapcursor = 7;
									break;
								default:
									mapcursor++;
									break;
								}
							}
							break;
						case "online":
							out.println("Online players: " + online.toString().replace("]", ""));
							break;
						default:
							out.println("Sorry captain, I did not understand.");
							break;
						}
						break;
					}
				}
			} catch (IOException e) {
				System.out.println(e);
			} finally {
				// client goes offline
				if (username != null) {
					online.remove(username);
				}
				if (out != null) {
					writers.remove(out);
				}
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					System.out.println(username + " logged out!");
				}
			}
		}

	}

	private static void initializeServer() {
		try {
			File sectorFolder = new File("./sectors");
			if (!sectorFolder.exists()) {
				sectorFolder.mkdirs();
			}
			FileInputStream input = new FileInputStream("./server.properties");
			prop.load(input);
			input.close();
		} catch (FileNotFoundException ex) {
			File serverprop = new File("./server.properties");
			if (!serverprop.exists()) {
				try {
					serverprop.createNewFile();
				} catch (IOException ex1) {
					ex1.printStackTrace();
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("Could not open one of the properties file..");
			return;
		}
		return;

	}

	/*
	 * Hey you! Do you hate copying and pasting the same code over again to do
	 * the same stuff dealing with storing properties? Use this.
	 */
	public static synchronized void initializeSector(String sectorNum) {
		File sector = new File("./sectors/" + sectorNum + ".properties");
		if (!sector.exists()) {
			Random sectorRand = new Random();
			int PlanetsAmount = sectorRand.nextInt(8);
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
					for (int i = 0; i < (PlanetsAmount - 1); i++) {
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
				String size = planetSize();
				for (int i = 0; i < (planets.length - 1); i++) {
					String num = Integer.toString(i);
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
				FileOutputStream output = new FileOutputStream("./sectors/" + sectorNum + ".properties");
				sect.store(output, null);
				output.flush();
				output.close();
				return;
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			return;
		}
	}

	public static String planetSize() {
		Random planetRand = new Random();
		String size = null;
		int sizeChance = planetRand.nextInt(101);
		if (sizeChance <= 10) {
			// Tiny
			size = "TINY";
		}
		if (sizeChance >= 11 && sizeChance <= 30) {
			// Small
			size = "SMALL";
		}
		if (sizeChance >= 31 && sizeChance <= 70) {
			// Medium
			size = "MEDIUM";
		}
		if (sizeChance >= 71 && sizeChance <= 90) {
			// Large
			size = "LARGE";
		}
		if (sizeChance >= 91) {
			// Huge
			size = "HUGE";
		}
		return size;
	}

	public static String sectorThing(int loc) {
		if (loc >= 10) {
			if (loc >= 100) {
				if (loc >= 1000) {
					if (loc == 10000) {
						return Integer.toString(loc);
					}
					return " " + Integer.toString(loc);

				}
				return " " + Integer.toString(loc) + " ";
			}

			return "  " + Integer.toString(loc) + " ";
		}
		return "  " + Integer.toString(loc) + "  ";

	}

	public static ArrayList<String> playersInSector(int sector) {
		ArrayList<String> players = new ArrayList<String>();
		File dir = new File("./users/");
		File[] directoryListing = dir.listFiles();
		for (File child : directoryListing) {
			Properties userprop = new Properties();
			try {
				FileInputStream input = new FileInputStream(child);
				userprop.load(input);
				if (userprop.getProperty("location") == Integer.toString(sector)) {
					players.add(child.getName().substring(0, child.getName().lastIndexOf(".")));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return players;
	}
}
