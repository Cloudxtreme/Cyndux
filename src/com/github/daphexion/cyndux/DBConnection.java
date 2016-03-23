package com.github.daphexion.cyndux;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DBConnection {
	Connection connection;
	Statement statement;
	public DBConnection() {
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:Cyndux.db");
			statement=connection.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		System.out.println("Opened database successfully");
	}
	public void initialize(){
		try{
			statement.execute("CREATE TABLE IF NOT EXISTS Players ("
								+ "Username TEXT PRIMARY KEY NOT NULL,"
								+ "Password INT NOT NULL,"
								+ "Money INT NOT NULL,"
								+ "Location INT,"
								+ "Inventory TEXT"
								+ ")");
			statement.execute("CREATE TABLE IF NOT EXISTS Ships ("
								+ "Username TEXT NOT NULL,"
								+ "Ship TEXT NOT NULL,"
								+ "HiSlot1 INT NOT NULL,"
								+ "HiSlot2 INT NOT NULL,"
								+ "HiSlot3 INT NOT NULL,"
								+ "MidSlot1 INT NOT NULL,"
								+ "MidSlot2 INT NOT NULL,"
								+ "MidSlot3 INT NOT NULL,"
								+ "LoSlot1 INT NOT NULL,"
								+ "LoSlot2 INT NOT NULL,"
								+ "LoSlot3 INT NOT NULL,"
								+ "FOREIGN KEY(Username) REFERENCES Players(Username)"
								+ ")");
			statement.execute("CREATE TABLE IF NOT EXISTS Stations ("
								+ "Location INT PRIMARY KEY NOT NULL,"
								+ "UpgradedPlayers TEXT NOT NULL"
								+ ")");
			statement.execute("CREATE TABLE IF NOT EXISTS StationStorage ("
								+ "Username TEXT NOT NULL,"
								+ "Station INT NOT NULL,"
								+ "Quantity INT NOT NULL,"
								+ "FOREIGN KEY(Username) REFERENCES Players(Username),"
								+ "FOREIGN KEY(Station) REFERENCES Stations(Location)"
								+")");
			statement.execute("CREATE TABLE IF NOT EXISTS Sectors ("
					+ "Location INT PRIMARY KEY NOT NULL,"
					+ "Star INT NOT NULL DEFAULT 0,"//Boolean 1 or 0
					+ "Station INT NOT NULL DEFAULT 0,"//Boolean 1 or 0
					+ "Wormhole1 INT NOT NULL DEFAULT 0,"
					+ "Wormhole2 INT NOT NULL DEFAULT,"
					+ "AsteroidBelt INT NOT NULL DEFAULT 0,"// None: 0;Small:1, Medium:2, Large:3
					+ "Star INT NOT NULL DEFAULT 0,"//Boolean 1 or 0
					+ "Nebula INT NOT NULL DEFAULT 0,"// Refer to Asteroid Belt Column.
					+ "Planet1 INT NOT NULL DEFAULT 0,"//Planet Format is SizeNumberTypeNumber
					+ "Planet2 INT NOT NULL DEFAULT 0,"//where both can be referred to in the Sector class.
					+ "Planet3 INT NOT NULL DEFAULT 0,"
					+ "Planet4 INT NOT NULL DEFAULT 0,"
					+ "Planet5 INT NOT NULL DEFAULT 0,"
					+ "Planet6 INT NOT NULL DEFAULT 0,"
					+")");
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	public void Update(String query){
		try {
			statement.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public ResultSet Query(String query){
		ResultSet rs = null;
		try {
			 rs=statement.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}
}
