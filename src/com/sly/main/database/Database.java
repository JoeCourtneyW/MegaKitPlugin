package com.sly.main.database;

import java.sql.Connection;
import java.sql.SQLException;

public class Database
{

	private Connection connection;

	public Database(Connection connection) {
		this.connection = connection;
		/*// Creating a Mongo client
		MongoClient mongo = new MongoClient("localhost", 27017);

		mongo.
		// Creating Credentials
		MongoCredential credential;
		credential = MongoCredential.createCredential("sampleUser", "myDb", "password".toCharArray());
		System.out.println("Connected to the database successfully");

		// Accessing the database
		MongoDatabase database = mongo.getDatabase("myDb");
		System.out.println("Credentials ::" + credential);*/
	}

	public Connection getConnection() {
		return this.connection;
	}

	public synchronized void flush() throws SQLException {
		connection.close();
	}
}
