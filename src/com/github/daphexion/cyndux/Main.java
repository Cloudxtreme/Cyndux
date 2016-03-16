package com.github.daphexion.cyndux;

//Import some stuff. One is for the Socket Connection, the other is for writing stuff.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;
import java.util.Random;
import java.util.Vector;

import com.github.daphexion.cyndux.exceptions.*;
import com.github.daphexion.cyndux.players.*;
import com.github.daphexion.cyndux.sectors.*;

public class Main {
	/**
	 * The port that the server listens on.
	 */
	public static HashSet<Player> players = new HashSet<Player>();
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
									if (!response.contains(",")) {
										out.println("Please enter a password!");
										break;
									}
									String[] userpass = response.split(",");
									player = new Player(userpass[0], out);
									try {
										player.load();
									} catch (PlayerDoesNotExist e) {
										player.send(e.getMessage());
										player.send("Are you registering or logging in?");
										break;
									}
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

							break;
						case "register":
							out.println("Enter your details in this format:[Username],[Password]");
							while (true) {
								response = in.readLine();
								if (response == null) {
									return;
								} else {
									if (!response.contains(",")) {
										out.println("Please enter a password!");
										break;
									}
									String[] userpass = response.split(",");
									player = new Player(userpass[0], out);
									try {
										player.register(userpass[1]);
									} catch (PlayerAlreadyExists e) {
										player.send(e.getMessage());
										player.send("Are you registering or logging in?");
										break;
									}
									synchronized (players) {
										players.add(player);
									}
									System.out.println("IP " + socket.getRemoteSocketAddress() + " logged in as "
											+ player.getUsername());
									notloggedin = false;
									break;
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
				while (true) {
					int maploc = Integer.parseInt(player.getLocation());
					int mapn = (maploc - 100);
					int maps = maploc + 100;
					int mape = maploc + 1; // how the fuck does increment
											// operators work
					int mapw = maploc - 1;
					int mapne = mapn + 1;
					int mapse = maps + 1;
					int mapsw = maps - 1;
					int mapnw = mapn - 1;
					int warp = 0;
					Sector.initializeSector(Integer.parseInt(player.getLocation()));
					Vector<String> objects = Sector.getObjects(Integer.parseInt(player.getLocation()));
					// Dis where da magic happens.
					switch (player.getScreen()) {
					case MAIN:
						player.cannotChat = true;
						player.send("You are now in " + player.getLocation());
						player.send("You see a:");
						for (String object : objects) {
							player.send("● " + object);
						}
						player.send("What is your command?");
						player.cannotChat = false;
						break;
					case GOTO:
						player.cannotChat = true;
						player.send("You have the option of going to:");
						for (String object : objects) {
							player.send("● A" + object);
						}
						player.send("What is your command?");
						player.cannotChat = false;
						break;
					case MAP:
						player.printMap();
						break;
					case WARP:
						switch (player.getMapCursor()) {
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
					case OBJECT:
						switch (player.getLocationInSector()) {
						case BELT:
							player.send("You are now in the asteroid belt of " + player.getLocation());
							break;
						case COMBAT:
							// TODO Combat
							break;
						case NEBULA:
							player.send("You are now in the nebula of " + player.getLocation());
							break;
						case STATION:
							player.send("Unfortunately, stations are not implemented for the time being.");
							// TODO Stations
							break;
						case FACTORY:
							player.send("Unfortunately, factories are not implemented for the time being.");
							break;
						case BLACKMARKET:
							player.send("Unfortunately, the blackmarket is not implemented for the time being.");
							break;
						default:
							player.send("Unfortunately, planets are not implemented for the time being.");
							break;
						}
						break;
					}
					while (true) {
						response = in.readLine();
						if (response == null) {
							return;
						}
						String firstresponse = (response.contains(" "))
								? response.toLowerCase().substring(0, response.indexOf(" ")) : response.toLowerCase();
						switch (firstresponse) {
						case "y":
							switch (player.getScreen()) {
							case WARP:
								boolean warped = true;
								switch (player.getMapCursor()) {
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
									player.send("You are already here!");
									warped = false;
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
								if (warped) {
									for (Player plyr : Sector
											.getPlayersInSector(Integer.parseInt(player.getLocation()))) {
										if (plyr != player) {
											plyr.sendChat(player.getUsername() + " just left your sector!");
										}
									}
									for (Player plyr : Sector.getPlayersInSector(warp)) {
										if (plyr != player) {
											plyr.sendChat(player.getUsername() + " just warped to your sector!");
										}
									}
									player.setLocation(warp);
								}
								player.setScreen(Screen.MAIN);
							default:
								break;
							}
							break;
						case "n":
							switch (player.getScreen()) {
							case WARP:
								player.setScreen(Screen.MAP);
								player.setMapCursor((byte) 5);
								break;
							default:
								break;
							}
							break;
						case "map":
							player.setMapCursor((byte) 5);
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
								player.send("Available chat modes: sector/on/off/group | Usage: chat [mode]");
								break;
							}
							break;
						case "say":
							if (!response.contains(" ")) {
								player.send("Enter something to say!");
								break;
							}
							switch (player.getChatStatus()) {
							case NOTINCHAT:
								player.send("You are not in any chat mode! Use the chat command!");
								break;
							case SYSTEM:
								for (Player plyr : players) {
									if (plyr.getChatStatus().equals(ChatMode.SYSTEM) && plyr != player) {
										plyr.sendChat(player.getUsername() + "> "
												+ (response.substring(response.indexOf(" ") + 1)));
									}
								}
								break;
							case SECTOR:
								for (Player plyr : players) {
									if (plyr.getChatStatus().equals(ChatMode.SECTOR) && plyr != player
											&& plyr.getLocation().equals(player.getLocation())) {
										plyr.sendChat(player.getUsername() + "> "
												+ (response.substring(response.indexOf(" ") + 1)));
									}
								}
								break;
							case GROUP:
								// TODO THE ENTIRE CORP THING
								break;
							}
							break;
						case "up":
							if (player.getScreen() != Screen.MAP) {
								player.send("Sorry captain, I did not understand.");
								break;
							} else {
								switch (player.getMapCursor()) {
								case 1:
									player.setMapCursor((byte) 7);
									break;
								case 2:
									player.setMapCursor((byte) 8);
									break;
								case 3:
									player.setMapCursor((byte) 9);
									break;
								default:
									player.setMapCursor((byte) (player.getMapCursor() - 3));
									break;
								}
							}
							break;
						case "down":
							if (player.getScreen() != Screen.MAP) {
								player.send("Sorry captain, I did not understand.");
								break;
							} else {
								switch (player.getMapCursor()) {
								case 7:
									player.setMapCursor((byte) 1);
									break;
								case 8:
									player.setMapCursor((byte) 2);
									break;
								case 9:
									player.setMapCursor((byte) 3);
									break;
								default:
									player.setMapCursor((byte) (player.getMapCursor() + 3));
									break;
								}
							}
							break;
						case "left":
							if (player.getScreen() != Screen.MAP) {
								player.send("Sorry captain, I did not understand.");
								break;
							} else {
								switch (player.getMapCursor()) {
								case 1:
									player.setMapCursor((byte) 3);
									break;
								case 4:
									player.setMapCursor((byte) 6);
									break;
								case 7:
									player.setMapCursor((byte) 9);
									break;
								default:
									player.setMapCursor((byte) (player.getMapCursor() - 1));
									break;
								}
							}
							break;
						case "right":
							if (player.getScreen() != Screen.MAP) {
								player.send("Sorry captain, I did not understand.");
								break;
							} else {
								switch (player.getMapCursor()) {
								case 3:
									player.setMapCursor((byte) 1);
									break;
								case 6:
									player.setMapCursor((byte) 4);
									break;
								case 9:
									player.setMapCursor((byte) 7);
									break;
								default:
									player.setMapCursor((byte) (player.getMapCursor() + 1));
									break;
								}
							}
							break;
						case "online":
							player.cannotChat = true;
							player.send("Amount of people online." + players.size());
							for (Player plyr : players) {
								player.send("● " + plyr.getUsername());
							}
							player.cannotChat = false;
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
								player.setScreen(Screen.MAIN);
								break;
							case OBJECT:
								player.setScreen(Screen.MAIN);
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
}
