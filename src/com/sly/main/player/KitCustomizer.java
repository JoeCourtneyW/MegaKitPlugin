package com.sly.main.player;

import java.util.StringJoiner;

import com.sly.main.Server;
import com.sly.main.database.Row;
import com.sly.main.kits.Kits;

public class KitCustomizer
{
	//TODO I DONT KNOW WHAT THIS IS SUPPOSED TO BEEEEEE
	private Row row;

	public KitCustomizer(PlayerModel player) {
		row = Server.getKitCustomizerTable().getRow(player.getUUID());
	}

	public void setCustomization(Kits kit, Integer[] spots) {
		row.getCell(kit.getName()).setValue(serialize(spots));
	}

	public Integer[] getCustomization(Kits kit) {
		return deserialize(row.getCell(kit.getName()).getValue().toString());
	}

	public void loadSpots(Row kitCustomizerRow) {
		row = kitCustomizerRow;
	}

	public static Integer[] getDefaultCustomization() {
		Integer[] normal = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
		return normal;
	}

	public static String getDefaultSerialization() {
		Integer[] normal = { 0, 1, 2, 3, 4, 5, 6, 7, 8 };
		return serialize(normal);
	}

	// Serialize a players customizer data
	public static String serialize(Integer[] spots) { // 0,1,2,3,4,5,6,7,8
		StringJoiner serialized = new StringJoiner(",");

		for (int spot : spots) {
			serialized.add(spot + "");
		}

		return serialized.toString();
	}

	public static Integer[] deserialize(String serialized) {
		Integer[] deserialized = new Integer[9]; //9 == hotbar length
		String[] spots = serialized.split(",");

		for (int i = 0; i < spots.length; i++) {
			deserialized[i] = Integer.parseInt(spots[i]);
		}

		return deserialized;
	}

	public void pushRowToDatabase() {
		Server.getKitCustomizerTable().updateEntry(row);
	}
}
