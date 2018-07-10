package com.sly.main.enums;

import org.bukkit.Material;

import com.sly.main.player.PlayerModel;
import com.sly.main.resources.SerializationUtil;

public enum VanityArmor
{
	NONE("NONE", Material.THIN_GLASS),
	CHAINMAIL("Chainmail Vanity Armor", Material.CHAINMAIL_CHESTPLATE),
	IRON("Iron Vanity Armor", Material.IRON_CHESTPLATE),
	GOLD("Gold Vanity Armor", Material.GOLD_CHESTPLATE),
	DIAMOND("Diamond Vanity Armor", Material.DIAMOND_CHESTPLATE);
	private Material material;
	private String displayName;

	VanityArmor(String displayName, Material material) {
		this.material = material;
		this.displayName = displayName;
	}

	public int getSerializationID() {
		if (name().equalsIgnoreCase("NONE")) {
			return 1;
		} else if (name().equalsIgnoreCase("CHAINMAIL")) {
			return 2;
		} else if (name().equalsIgnoreCase("IRON")) {
			return 3;
		} else if (name().equalsIgnoreCase("GOLD")) {
			return 4;
		} else if (name().equalsIgnoreCase("DIAMOND")) {
			return 5;
		}
		for (int i = 0; i < Trail.values().length; i++) {
			if (Trail.values()[i].name().equalsIgnoreCase(name())) {
				return i + 1;
			} else {
				System.out.println("Failed to find serialization ID for" + name());
			}

		}
		return 0;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Material getMaterial() {
		return material;
	}

	public static VanityArmor fromInvName(String name) {
		for (VanityArmor s : values()) {
			if (s.getDisplayName().equalsIgnoreCase(name)) {
				return s;
			}
		}
		return null;
	}

	public boolean isUnlocked(PlayerModel player) {
		String serialized = player.getDatabaseValue("VANITY_ARMOR").toString();
		Integer[] playerValues = SerializationUtil.deserialize(serialized);

		for (int i = 0; i < playerValues.length; i++) {
			if (values()[i] == this) { // If the index matches the armor we're setting, run the check
				return playerValues[i] >= 1; // If it's 1 or 2 (Unlocked or selected)
			}
		}
		return false;
	}

	public void setUnlocked(PlayerModel player) {
		String serialized = player.getDatabaseValue("VANITY_ARMOR").toString();
		Integer[] playerValues = SerializationUtil.deserialize(serialized);

		for (int i = 0; i < playerValues.length; i++) {
			if (values()[i] == this) { // If the index matches the armor we're setting, change the playerValue
				playerValues[i] = 1;
			}
		}
	}

	public void setSelected(PlayerModel player) {
		String serialized = player.getDatabaseValue("VANITY_ARMOR").toString();
		Integer[] playerValues = SerializationUtil.deserialize(serialized);

		for (int i = 0; i < playerValues.length; i++) {
			if (values()[i] == this) { // If the index matches the armor we're selecting, change the playerValue
				playerValues[i] = 2;
			} else if (playerValues[i] == 2) { // Otherwise, if this armor was selected previously, deselect it
				playerValues[i] = 1;
			}

		}
	}

	public static VanityArmor getSelectedVanityArmor(PlayerModel player) {
		String serialized = player.getDatabaseValue("VANITY_ARMOR").toString();
		Integer[] values = SerializationUtil.deserialize(serialized);

		for (int i = 0; i < values.length; i++) {
			if (values[i] == 2) {
				return VanityArmor.values()[i];
			}
		}
		return null; // Shouldn't happen, there should always be one with value 2
	}

	public static String getDefaultSerialization() {
		Integer[] defaultValues = new Integer[values().length];
		defaultValues[0] = 2;
		for (int i = 1; i < defaultValues.length; i++) {
			defaultValues[i] = 0;
		}
		return SerializationUtil.serialize(defaultValues);
	}
}
