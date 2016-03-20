package com.github.daphexion.cyndux;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.util.Properties;

public class Server {
	public ServerSocket listener;
	private int port;
	public Properties prop = new Properties();
	public DBConnection database;
	public Server() {
		database = new DBConnection();
		initializeServer();
		port = Integer.parseInt(prop.getProperty("port"));
		while (true) {
			try {
				listener = new ServerSocket(port);
				break;
			} catch (BindException e) {
				System.out.println("Port " + port + " already in use!");
				port++;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Using " + port + "!");
		System.out.println("Cyndux Server is running.");
	}

	private void initializeServer(){
		try {
			database.initialize();
			File itemsFolder = new File("./items");
			if (!itemsFolder.exists()) {
				itemsFolder.mkdirs();
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

	public int getPort() {
		return port;
	}
}