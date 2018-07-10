package com.sly.main.database.tables;

import java.sql.Types;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.sly.main.database.Cell;
import com.sly.main.database.Database;
import com.sly.main.database.Row;
import com.sly.main.database.Table;
import com.sly.main.gametype.GameType;
import com.sly.main.resources.LocationUtil;

public class ArenaTable extends Table
{
	public ArenaTable(Database database) {
		super(database, "Arenas", loadDefaultRow());
	}

	private static Row loadDefaultRow() {

		ArrayList<Cell> columnAry = new ArrayList<Cell>();

		columnAry.add(new Cell("ID", UUID.randomUUID(), Types.VARCHAR).PRIMARY_KEY());

		columnAry.add(new Cell("GAMETYPE", GameType.FFA.name(), Types.VARCHAR));

		Location loc = new Location(Bukkit.getWorlds().get(0), 0D, 0D, 0D, 0F, 0F);
		columnAry.add(new Cell("BLUE_SPAWN", LocationUtil.serialize(loc), Types.VARCHAR));
		columnAry.add(new Cell("YELLOW_SPAWN", LocationUtil.serialize(loc), Types.VARCHAR));
		columnAry.add(new Cell("SPECTATOR_SPAWN", LocationUtil.serialize(loc), Types.VARCHAR));

		columnAry.add(new Cell("BOUNDARY_1", LocationUtil.serialize(loc), Types.VARCHAR));
		columnAry.add(new Cell("BOUNDARY_2", LocationUtil.serialize(loc), Types.VARCHAR));
		Row reference = new Row(columnAry.toArray(new Cell[columnAry.size()]));
		return reference;
	}
}
