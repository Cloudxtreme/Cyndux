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
