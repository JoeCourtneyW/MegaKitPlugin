package com.sly.main.database.tables;

import java.sql.Types;
import java.util.ArrayList;
import java.util.UUID;

import com.sly.main.database.Cell;
import com.sly.main.database.Database;
import com.sly.main.database.Row;
import com.sly.main.database.Table;
import com.sly.main.kits.Kits;
import com.sly.main.player.KitCustomizer;

public class KitCustomizerTable extends Table
{
	public KitCustomizerTable(Database database) {
		super(database, "KitCustomizer", loadDefaultRow());
	}

	private static Row loadDefaultRow() {
		ArrayList<Cell> columnAry = new ArrayList<Cell>();

		columnAry.add(new Cell("UUID", UUID.randomUUID(), Types.VARCHAR).PRIMARY_KEY());

		for (Kits kit : Kits.getKits()) {
			columnAry.add(new Cell(kit.name(), KitCustomizer.getDefaultSerialization(), Types.VARCHAR));
		}

		Row reference = new Row(columnAry.toArray(new Cell[columnAry.size()])){
			
		};
		return reference;
	}
}
