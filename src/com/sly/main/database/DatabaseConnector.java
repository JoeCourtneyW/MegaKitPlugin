package com.sly.main.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector
{

	// TODO: Credentials file
	private String host;
	private int port = 3306;
	private String databaseName;
	private String username;
	private String password;

	public DatabaseConnector(String host, int port, String databaseName, String username, String password) {
		this.host = host;
		this.port = port;
		this.databaseName = databaseName;
		this.password = password;
	}

	public synchronized Database connect() {
		Connection connection;
		try {
			connection = DriverManager.getConnection("jdbc:mysql://" + host + ": " + port + "/" + databaseName,
					username, password);
		} catch (SQLException e) {
			System.out.println("[Taken Gaming] URGENT: Failed to connect to database server");
			return null;
		}

		System.out.println("[Taken Gaming] Connect to database server properly");
		return new Database(connection);
	}

}
