package com.github.daphexion.cyndux;

//Import some stuff. One is for the Socket Connection, the other is for writing stuff.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Random;
import com.github.daphexion.cyndux.exceptions.*;
import com.github.daphexion.cyndux.players.Chat;
import com.github.daphexion.cyndux.players.ChatMode;
import com.github.daphexion.cyndux.players.Player;
import com.github.daphexion.cyndux.screen.Screen;
import com.github.daphexion.cyndux.screen.ScreenMode;
import com.github.daphexion.cyndux.players.OnlinePlayers;
import com.github.daphexion.cyndux.sectors.*;

public class Main {
	/**
	 * The port that the server listens on.
	 */
	public static OnlinePlayers players = new OnlinePlayers();
	public static Server server;
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
				boolean registering = true;
				while (true) {
					response = in.readLine().toLowerCase();
					if (response == null) {
						return;
					} else {
						switch (response) {
						case "login":
							registering = false;
							break;
						case "register":
							registering = true;
							break;
						}
					}
					break;
				}
				loggedin:
				while(true){
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
							try {
								player = new Player(registering, userpass[0], userpass[1], out);
								players.add(player);
								System.out.println("IP " + socket.getRemoteSocketAddress() + " logged in as "
										+ player.getUsername());
								break loggedin;
							} catch (PlayerAlreadyExists|AlreadyConnected|PlayerDoesNotExist|WrongPassword e) {//FIXME Crashes when caught?
								player.send(e.getMessage());
								player.send("Are you registering or logging in?");
								break;
							}
						}
					}
				}
				if (player.getLocation()==0) {
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
					Sector.initializeSector(player.getLocation());
					//Print player screen.
					new Screen(player);
					while (true) {
						response = in.readLine();
						if (response == null) {
							return;
						}
						String firstresponse = (response.contains(" "))
								? response.toLowerCase().substring(0, response.indexOf(" ")) : response.toLowerCase();
						switch (firstresponse) {
						case "y":
							int warp = 0;
							switch (player.getScreen()) {
							case WARP:
								HashMap<String, Integer> sectors = Sector.getSurroundingSectors(player.getLocation());
								boolean warped = true;
								switch (player.getMapCursor()) {
								case 1:
									warp = sectors.get("mapnw");
									break;
								case 2:
									warp = sectors.get("mapn");
									break;
								case 3:
									warp = sectors.get("mapne");
									break;
								case 4:
									warp = sectors.get("mape");
									break;
								case 5:
									player.send("You are already here!");
									warped = false;
									break;
								case 6:
									warp = sectors.get("mapw");
									break;
								case 7:
									warp = sectors.get("mapsw");
									break;
								case 8:
									warp = sectors.get("maps");
									break;
								case 9:
									warp = sectors.get("mapse");
									break;
								}
								// TODO Ship warp delay.
								if (warped) {
									for (Player plyr : Sector
											.getPlayersInSector(player.getLocation())) {
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
								player.setScreen(ScreenMode.MAIN);
							default:
								break;
							}
							break;
						case "n":
							switch (player.getScreen()) {
							case WARP:
								player.setScreen(ScreenMode.MAP);
								player.setMapCursor((byte) 5);
								break;
							default:
								break;
							}
							break;
						case "map":
							player.setMapCursor((byte) 5);
							player.setScreen(ScreenMode.MAP);
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
							Chat.main(player,response);
							break;
						case "up":
							if (player.getScreen() != ScreenMode.MAP) {
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
							if (player.getScreen() != ScreenMode.MAP) {
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
							if (player.getScreen() != ScreenMode.MAP) {
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
							if (player.getScreen() != ScreenMode.MAP) {
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
							player.send("Amount of people online." + players.get().size());
							for (Player plyr : players.get().values()) {
								player.send("● " + plyr.getUsername());
							}
							player.cannotChat = false;
							break;
						case "warp":
							switch (player.getScreen()) {
							case MAP:
								player.setScreen(ScreenMode.WARP);
								break;
							default:
								out.println("Sorry captain, I did not understand.");
								break;
							}
							break;
						case "back":
							switch (player.getScreen()) {
							case MAP:
								player.setScreen(ScreenMode.MAIN);
								break;
							case OBJECT:
								player.setScreen(ScreenMode.MAIN);
							default:
								if (player.getScreen() == ScreenMode.GOTO) {
									out.println("Sorry captain, I did not understand.");
									break;
								} else {

								}

							}
							break;
						case "inventory":
							player.setScreen(ScreenMode.INVENTORY);
							break;
						default:
							player.send("Sorry captain, I did not understand.");
							break;
						}
						break;
					}
				}
			} catch (IOException e) {
				System.out.println(e);
			} finally {
				// client goes offline
				players.remove(player);
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
