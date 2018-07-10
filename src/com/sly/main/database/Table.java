package com.sly.main.database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringJoiner;

import org.bukkit.scheduler.BukkitRunnable;

import com.sly.main.Server;

public abstract class Table implements Iterable<Row>
{
	private Database database;

	private ArrayList<Row> contents;
	private Cell primaryKey;
	private Row defaultRow;
	private String tableName;

	// TODO: Maybe make dumpTableContents async and add a callback to it
	public Table(Database database, String tableName, Row defaultRow) {
		this.database = database;
		this.tableName = tableName;
		this.defaultRow = defaultRow;
		this.primaryKey = defaultRow.getPrimaryKey();
		dumpTableContents();
	}

	public void createSQLTable() {
		new BukkitRunnable() {
			public void run() {
				try {
					StringBuilder statementBuilder = new StringBuilder();
					statementBuilder.append("CREATE TABLE IF NOT EXISTS ");
					statementBuilder.append(tableName);
					statementBuilder.append("(");

					for (Cell cell : defaultRow.getCells()) {
						statementBuilder.append(cell.getColumnName());
						statementBuilder.append(" ");
						statementBuilder.append(cell.getDatabaseType());
						statementBuilder.append(", ");
					}

					statementBuilder.append("PRIMARY KEY (");
					statementBuilder.append(primaryKey.getColumnName());
					statementBuilder.append(")");

					statementBuilder.append(");");

					PreparedStatement createTable = database.getConnection()
							.prepareStatement("CREATE TABLE IF NOT EXISTS " + tableName + "(" + null + ")");
					createTable.execute();
					createTable.close();
					System.out.println("[Database] Attempted to create table: `" + getTableName() + "`");
				} catch (SQLException e) {
					System.out.println("[Database] Failed to create table: `" + getTableName() + "`");
				}
			}
		}.runTaskAsynchronously(Server.getInstance());
	}

	public void newEntry(final Row row) {
		contents.add(row);
		new BukkitRunnable() {
			public void run() {
				try {
					StringJoiner questionMarks = new StringJoiner(",");
					for (int i = 0; i < row.getCells().length; i++)
						questionMarks.add("?");
					StringBuilder statementBuilder = new StringBuilder();
					statementBuilder.append("INSERT INTO ");
					statementBuilder.append(tableName);

					statementBuilder.append(" values(");
					statementBuilder.append(questionMarks.toString());
					statementBuilder.append(")");

					PreparedStatement statement = database.getConnection()
							.prepareStatement(statementBuilder.toString());

					int count = 1; // Setting question marks requires an index
					for (Cell cell : row) {
						if (cell.isPrimaryKey()) // Don't set primary key to arbitrary default key
							statement.setObject(count, cell.getValue().asObject());
						else
							statement.setObject(count, cell.getDefaultValue().asObject());
						count++;
					}
					statement.execute();
					statement.close();
					System.out.println("[Database] New entry in `" + getTableName() + "` with Primary Key: "
							+ row.getPrimaryKey().getValue().toString());
				} catch (SQLException e) {
					System.out.println("[Database] Failed to create new entry in `" + getTableName()
							+ "` with Primary Key: " + primaryKey.toString());
				}
			}
		}.runTaskAsynchronously(Server.getInstance());
	}

	public void updateEntry(final Row row) {
		new BukkitRunnable() {
			public void run() {
				try {
					StringJoiner cells = new StringJoiner(",");
					for (Cell cell : row.getCells()) {
						if (!cell.isPrimaryKey())
							cells.add("`" + cell.getColumnName() + "`=?");
					}
					StringBuilder statementBuilder = new StringBuilder();
					statementBuilder.append("UPDATE ");
					statementBuilder.append(tableName);

					statementBuilder.append(" SET ");
					statementBuilder.append(cells.toString());
					statementBuilder.append(" WHERE ");
					statementBuilder.append("`" + row.getPrimaryKey().getColumnName() + "`=?");

					PreparedStatement statement = database.getConnection()
							.prepareStatement(statementBuilder.toString());

					int count = 1; // Setting question marks requires an index
					for (Cell cell : row) {
						if (cell.isPrimaryKey())
							continue;

						statement.setObject(count, cell.getValue().asObject(), cell.getDatabaseType());
						count++;
					}
					statement.setString(count, row.getPrimaryKey().getValue().toString());

					statement.execute();
					statement.close();
					System.out.println("[Database] Updated entry in `" + getTableName() + "` with primary key: "
							+ row.getPrimaryKey().getValue().toString());
				} catch (SQLException e) {
					System.out.println("[Database] Failed to update entry in `" + getTableName()
							+ "` with primary key: " + row.getPrimaryKey().getValue().toString());
				}
			}
		}.runTaskAsynchronously(Server.getInstance());
	}

	private void dumpTableContents() {
		ArrayList<Row> contents = new ArrayList<Row>();
		try {
			PreparedStatement statement = database.getConnection().prepareStatement("SELECT * FROM " + tableName);
			ResultSet resultSet = statement.executeQuery();
			Row row;
			while (resultSet.next()) {
				row = getDefaultRow();
				for (Cell cell : row) {
					cell.setValue(resultSet.getObject(cell.getColumnName()));
				}
				contents.add(row);
			}
			statement.close();
			System.out.println("[Database] Dumped contents of `" + getTableName() + "`");
		} catch (SQLException e) {
			System.out.println("[Database] Couldn't dump contents of `" + getTableName() + "`");
		}
		this.contents = contents;
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Row> getTableContents() {
		return (ArrayList<Row>) contents.clone();// Clone the object so it doesn't change our values
	}

	public Row getRow(Object primaryKeyValue) {
		for (Row row : this) {
			if (row.getPrimaryKey().equals(primaryKeyValue))
				return row;
		}
		return getDefaultRow();
	}

	public Row getDefaultRow() {
		return new Row(defaultRow.getCells()); // Clone the object so it doesn't change our default values
	}

	public String getTableName() {
		return tableName;
	}

	@Override
	public Iterator<Row> iterator() {
		Iterator<Row> iterator = new Iterator<Row>() {
			private int index = 0;

			@Override
			public boolean hasNext() {
				return index < getTableContents().size();
			}

			@Override
			public Row next() {
				return getTableContents().get(index++);
			}

		};
		return iterator;
	}
}
