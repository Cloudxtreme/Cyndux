package com.github.daphexion.cyndux;



//Import some stuff. One is for the Socket Connection, the other is for writing stuff.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Properties;
import java.util.Random;
import com.github.daphexion.cyndux.players.*;
import com.github.daphexion.cyndux.sectors.*;

public class Main {
	/**
	 * The port that the server listens on.
	 */
	private static HashSet<Player> players = new HashSet<Player>();
	static Server server;

	/**
	 * The application main method, which just listens on a port and spawns
	 * handler threads.
	 */
	public static void main(String[] args) throws Exception {
		server = new Server();
		try {
			while (true) {
				new Handler(server.listener.accept()).start();
			}
		} finally {
			server.listener.close();
		}
	}

	private static class Handler extends Thread {
		private String response;
		private Socket socket;
		private BufferedReader in;

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
			Player player = null;
			try {
				// Create character streams for the socket.
				in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
				// Request a name from this client.Note that
				// checking for the existence of a name and adding the name
				System.out.println("User Connected from IP: " + socket.getRemoteSocketAddress() + " !");
				out.println("Are you registering or logging in?");
				boolean notloggedin = true;
				while (notloggedin) {
					response = in.readLine().toLowerCase().toLowerCase();
					if (response == null) {
						return;
					} else {
						switch (response) {
						case "login":
							out.println("Enter your details in this format:[Username],[Password]");
							while (true) {
								response = in.readLine();
								if (response == null) {
									return;
								} else {
									String[] userpass = response.split(",");
									if (userpass.length <= 2) {
										player.send("Please enter a password!");
										break;
									}
									player = new Player(userpass[0], out);
									if (player.load().equals("NOTEXIST")) {
										player.send("We could not find your account!");
										player.send("Are you registering or logging in?");
										break;
									} else {
										if (player.authenticate(userpass[1])) {
											if (!players.contains(player)) {
												synchronized (players) {
													players.add(player);
												}
												System.out.println("IP " + socket.getRemoteSocketAddress()
														+ " logged in as " + player.getUsername());
											} else {
												player.send("This user is already logged in!");
												player.send("Are you registering or logging in?");
												break;
											}
											notloggedin = false;
											break;
										} else {
											player.send("Wrong password!");
											player.send("Are you registering or logging in?");
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
								if (response == null) {
									return;
								} else {
									String[] userpass = response.split(",");
									if (userpass.length <= 2) {
										player.send("Please enter a password!");
										break;
									}
									player = new Player(userpass[0], out);
									if (!player.register(userpass[1]).equals("ALREADYEXIST")) {
										synchronized (players) {
											players.add(player);
										}
										System.out.println("IP " + socket.getRemoteSocketAddress() + " logged in as "
												+ player.getUsername());
										notloggedin = false;
										break;
									} else {
										player.send("This account already exists!");
										player.send("Are you registering or logging in?");
										break;
									}
								}
							}
						}
					}
				}
				if (player.getLocation().isEmpty()) {
					if (server.prop.getProperty("starting.location").isEmpty()) {
						Random r = new Random();
						int randomloc = r.nextInt(10001);
						player.setLocation(randomloc);
					} else {
						player.setLocation(Integer.parseInt(server.prop.getProperty("starting.location")));
					}
				}
				player.send("Logged in as " + player.getUsername());
				byte mapcursor = 5;
				while (true) {
					Sector sector = new Sector(Integer.parseInt(player.getLocation()));
					Properties sectorprop = sector.getProperties();
					int maploc = Integer.parseInt(player.getLocation());
					int mapn = (maploc - 100);
					int mape = maploc + 1; // how the fuck does increment
											// operators work
					int mapw = maploc - 1;
					int maps = maploc + 100;
					int mapne = maploc - 99;
					int mapse = maploc + 101;
					int mapsw = maploc + 99;
					int mapnw = maploc - 101;
					// Dis where da magic happens.
					switch (player.getScreen()) {
					case MAIN:
						player.send("You are now in " + player.getLocation());

						if (sectorprop.getProperty("station.exists") != null) {
							player.send("You see a station.");
						}
						if (sectorprop.getProperty("wormholes.1") != null) {
							player.send("You see a wormhole.");
						}
						if (sectorprop.getProperty("wormholes.2") != null) {
							player.send("You see another wormhole.");
						}
						if (sectorprop.getProperty("asteroidbelt") != null) {
							switch (sectorprop.getProperty("asteroidbelt")) {
							case "small":
								player.send("You see a small asteroid belt");
								break;
							case "medium":
								player.send("You see a medium asteroid belt");
								break;
							case "large":
								player.send("You see a large asteroid belt");
								break;
							}
						}
						if (sectorprop.getProperty("star.exists") != null) {
							player.send("You see a star.");
						}
						if (sectorprop.getProperty("nebula") != null) {
							switch (sectorprop.getProperty("nebula")) {
							case "small":
								player.send("You see a small nebula.");
								break;
							case "medium":
								player.send("You see a medium nebula.");
								break;
							case "large":
								player.send("You see a large nebula.");
								break;
							}
						}
						// You see another ship owned by someone named
						// MichaelCera.
						// If only...
						player.send("What is your command?");

						break;
					case GOTO: // TODO PLANETS FOR BELOW AND ABOVE
						player.send("You have the option of going to:");
						if (sectorprop.getProperty("station.exists") != null) {
							player.send("● The station");
						}
						if (sectorprop.getProperty("wormholes.1") != null) {
							player.send("● A wormhole");
						}
						if (sectorprop.getProperty("wormholes.2") != null) {
							player.send("● A 2nd wormhole");
						}
						if (sectorprop.getProperty("asteroidbelt") != null) {
							player.send("● A Asteroid belt");
						}
						if (sectorprop.getProperty("nebula") != null) {
							player.send("● A Nebula");
						}
						break;
					case MAP: // TODO EDGES SECTORS
						player.send("╔═════╦═════╦═════╗");
						player.send(
								"║" + sectorThing(mapnw) + "║" + sectorThing(mapn) + "║" + sectorThing(mapne) + "║");
						switch (mapcursor) {

						case 1:
							player.send("║  ●  ║     ║     ║");
							break;
						case 2:
							player.send("║     ║  ●  ║     ║");
							break;
						case 3:
							player.send("║     ║     ║  ●  ║");
							break;
						default:
							player.send("║     ║     ║     ║");
							break;

						}
						player.send("╠═════╬═════╬═════╣");
						player.send(
								"║" + sectorThing(mapw) + "║" + sectorThing(maploc) + "║" + sectorThing(mape) + "║");
						switch (mapcursor) {

						case 4:
							player.send("║  ●  ║     ║     ║");
							break;
						case 5:
							player.send("║     ║  ●  ║     ║");
							break;
						case 6:
							player.send("║     ║     ║  ●  ║");
							break;
						default:
							player.send("║     ║     ║     ║");
							break;
						}
						player.send("╠═════╬═════╬═════╣");
						player.send(
								"║" + sectorThing(mapsw) + "║" + sectorThing(maps) + "║" + sectorThing(mapse) + "║");
						switch (mapcursor) {

						case 7:
							player.send("║  ●  ║     ║     ║");
							break;
						case 8:
							player.send("║     ║  ●  ║     ║");
							break;
						case 9:
							player.send("║     ║     ║  ●  ║");
							break;
						default:
							player.send("║     ║     ║     ║");
							break;

						}
						player.send("╚═════╩═════╩═════╝");
						player.send("To move the cursor, type up, down, left or right.");
						player.send("To warp to where your cursor is, type warp.");
						break;
					case WARP:
						int warp;
						switch (mapcursor) {
						case 1:
							warp = mapnw;
							break;
						case 2:
							warp = mapn;
							break;
						case 3:
							warp = mapne;
							break;
						case 4:
							warp = mapw;
							break;
						case 5:
							warp = maploc;
							break;
						case 6:
							warp = mape;
							break;
						case 7:
							warp = mapsw;
							break;
						case 8:
							warp = maps;
							break;
						case 9:
							warp = mapse;
							break;
						default:
							warp = maploc;
						}
						player.send("Are you sure you want to warp to sector " + warp + "?");
						break;

					}
					// Print out whats in the sector
					while (true) {
						response = in.readLine();
						if (response == null) {
							return;
						}
						switch (response.toLowerCase().substring(0, response.indexOf(" "))) {
						case "y":
							switch (player.getScreen()) {
							case WARP:
								int warp = 0;
								switch (mapcursor) {
								case 1:
									warp = mapnw;
									break;
								case 2:
									warp = mapn;
									break;
								case 3:
									warp = mapne;
									break;
								case 4:
									warp = mapw;
									break;
								case 6:
									warp = mape;
									break;
								case 7:
									warp = mapsw;
									break;
								case 8:
									warp = maps;
									break;
								case 9:
									warp = mapse;
									break;
								}
								// TODO Ship warp delay.
								for (Player plyr : players) {
									if (plyr.getLocation().equals(player.getLocation()) && plyr != player) {
										plyr.send(player.getUsername() + " just left your sector!");
									} else {
										if (plyr.getLocation().equals(warp)) {
											plyr.send(player.getUsername() + " just warped to your sector!");
										}
									}
								}
								player.setLocation(warp);
								player.setScreen(Screen.MAIN);
							default:
								break;
							}
							break;
						case "n":
							switch (player.getScreen()) {
							case WARP:
								player.setScreen(Screen.MAP);
								mapcursor = 5;
								break;
							default:
								break;
							}
							break;
						case "map":
							mapcursor = 5;
							player.setScreen(Screen.MAP);
							break;
						case "chat":
							switch (response.replace("chat ", "")) {
							case "sector":
								player.setChatStatus(ChatMode.SECTOR);
								break;
							case "on":
								player.setChatStatus(ChatMode.SYSTEM);
								break;
							case "off":
								player.setChatStatus(ChatMode.NOTINCHAT);
								break;
							case "group":
								player.setChatStatus(ChatMode.GROUP);
								break;
							default:
								if (player.getChatStatus().equals(ChatMode.NOTINCHAT)) {
									player.setChatStatus(ChatMode.SYSTEM);
								} else {
									player.setChatStatus(ChatMode.NOTINCHAT);
								}
								break;
							}
							break;
						case "say":
							switch (player.getChatStatus()){
							case NOTINCHAT:
								break;
							case SYSTEM:
								for (Player plyr : players) {
									if (plyr.getChatStatus().equals(ChatMode.SYSTEM) && plyr != player) {
										plyr.send(player.getUsername() + ">" + response.replace("say ", ""));
									}
								}
								break;
							case SECTOR:
								for (Player plyr : players) {
									if (plyr.getChatStatus().equals(ChatMode.SECTOR) && plyr != player && plyr.getLocation().equals(player.getLocation())) {
										plyr.send(player.getUsername() + ">" + response.replace("say ", ""));
									}
								}
								break;
							case GROUP:
								//TODO THE ENTIRE CORP THING
								break;
							}
							break;
						case "up":
							if (player.getScreen() != Screen.MAP) {
								player.send("Sorry captain, I did not understand.");
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
							if (player.getScreen() != Screen.MAP) {
								player.send("Sorry captain, I did not understand.");
								break;
							} else {
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
							if (player.getScreen() != Screen.MAP) {
								player.send("Sorry captain, I did not understand.");
								break;
							} else {
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
							if (player.getScreen() != Screen.MAP) {
								player.send("Sorry captain, I did not understand.");
								break;
							} else {
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
							player.send("Amount of people online." + players.size());
							for (Player plyr : players) {
								player.send("● " + plyr.getUsername());
							}

							break;
						case "warp":
							switch (player.getScreen()) {
							case MAP:
								player.setScreen(Screen.WARP);
								break;
							default:
								out.println("Sorry captain, I did not understand.");
								break;
							}
							break;
						case "back":
							switch (player.getScreen()) {
							case MAP:
								player.setScreen(Screen.MAP);
								break;
							default:
								out.println("Sorry captain, I did not understand.");
								break;
							}
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
				if (player != null) {
					players.remove(player.getUsername());
				}
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					System.out.println(player.getUsername() + " logged out!");
				}
			}
		}
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
}
