package com.sly.main.database.tables;

import java.sql.Types;
import java.util.ArrayList;
import java.util.UUID;

import com.sly.main.database.Cell;
import com.sly.main.database.Database;
import com.sly.main.database.Row;
import com.sly.main.database.Table;
import com.sly.main.enums.CustomBlock;
import com.sly.main.enums.Group;
import com.sly.main.enums.Trail;
import com.sly.main.enums.VanityArmor;
import com.sly.main.gametype.GameType;
import com.sly.main.kits.Kits;

public class PlayerDataTable extends Table
{

	public PlayerDataTable(Database database) {
		super(database, "PlayerData", loadDefaultRow());
	}

	private static Row loadDefaultRow() {
		ArrayList<Cell> columnAry = new ArrayList<Cell>();

		columnAry.add(new Cell("UUID", UUID.randomUUID(), Types.VARCHAR).PRIMARY_KEY());

		for (GameType gameType : GameType.values()) {
			if (gameType != GameType.DUEL) // No rating for duels
				columnAry.add(new Cell(gameType.getDisplayName() + "_RATING", 1000D, Types.DOUBLE));
			columnAry.add(new Cell(gameType.getDisplayName() + "_WINS", 0, Types.INTEGER));
			columnAry.add(new Cell(gameType.getDisplayName() + "_LOSSES", 0, Types.INTEGER));
		}

		for (Kits kit : Kits.getKits()) {
			String defaultValue = "0,0,0";
			if (kit.isDefaultKit()) // If it's a default kit, everyone has it
				defaultValue = "1,0,0";
			columnAry.add(new Cell(kit.name(), defaultValue, Types.VARCHAR));
		}

		columnAry.add(new Cell("BLOCKS", CustomBlock.getDefaultSerialization(), Types.VARCHAR)); // All the weird serialized data
		columnAry.add(new Cell("VANITY_ARMOR", VanityArmor.getDefaultSerialization(), Types.VARCHAR));
		columnAry.add(new Cell("TRAIL", Trail.getDefaultSerialization(), Types.VARCHAR));
		columnAry.add(new Cell("GROUPS", Group.DEFAULT.name(), Types.VARCHAR));

		columnAry.add(new Cell("NAME", "", Types.VARCHAR));
		columnAry.add(new Cell("IP", "127.0.0.1", Types.VARCHAR));

		Row reference = new Row(columnAry.toArray(new Cell[columnAry.size()]));
		return reference;
	}
}
