package com.sly.main.enums;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.sly.main.player.PlayerModel;
import com.sly.main.resources.BetterItem;
import com.sly.main.resources.SerializationUtil;

public enum CustomBlock
{
	WOOD("Wooden Planks", Material.WOOD, (short) 3, 0),
	COAL("Coal", Material.COAL_BLOCK, (short) 0, 500),
	LAPIS("Lapis", Material.LAPIS_BLOCK, (short) 0, 2500),
	IRON("Iron", Material.IRON_BLOCK, (short) 0, 7500),
	EMERALD("Emerald", Material.EMERALD_BLOCK, (short) 0, 15000),
	GOLD("Gold", Material.GOLD_BLOCK, (short) 0, 45000),
	DIAMOND("Diamond", Material.DIAMOND_BLOCK, (short) 0, 100000),
	LEAF("Leaves", Material.LEAVES, (short) 0, -1),
	HAY("Hay Bale", Material.HAY_BLOCK, (short) 0, -1),
	MOSS_STONE("Mossy Stone", Material.MOSSY_COBBLESTONE, (short) 0, -1),
	BOOKSHELF("Bookshelf", Material.BOOKSHELF, (short) 0, -1),
	NETHER_BRICK("Nether Bricks", Material.NETHER_BRICK, (short) 0, -1),
	OBSIDIAN("Obsidian", Material.OBSIDIAN, (short) 0, -1),
	GLOWSTONE("Glowstone", Material.GLOWSTONE, (short) 0, -1),
	TNT("TNT", Material.TNT, (short) 0, -2),
	MELON("Melon", Material.MELON_BLOCK, (short) 0, -2),
	BEDROCK("Bedrock", Material.BEDROCK, (short) 0, -2),
	SPONGE("Sponge", Material.SPONGE, (short) 0, -2),
	ICE("Ice", Material.ICE, (short) 0, -2),
	RAINBOW_WOOL("Rainbow Wool", Material.WOOL, (short) 2, -2);

	private Material material;
	private String displayName;
	private short data;
	private int price;

	CustomBlock(String displayName, Material material, short data, int price) {
		this.material = material;
		this.displayName = displayName;
		this.data = data;
		this.price = price;
	}

	public String getDisplayName() {
		return displayName;
	}

	public Material getMaterial() {
		return material;
	}

	public short getData() {
		return data;
	}

	public int getPrice() {
		return price;
	}

	public ItemStack getItemStack() {
		return new BetterItem().material(getMaterial()).amount(64).data(getData()).displayName(getDisplayName()).grab();
	}

	public static CustomBlock fromInvName(String name) {
		for (CustomBlock s : values()) {
			if (s.getDisplayName().equalsIgnoreCase(name)) {
				return s;
			}
		}
		return null;
	}

	public boolean canPurchase(PlayerModel p) {
		return getPrice() > 0 && p.getEconomy().hasEnoughCoins(getPrice());
	}

	public boolean isUnlocked(PlayerModel player) {
		String serialized = player.getDatabaseValue("BLOCKS").toString();
		Integer[] playerValues = SerializationUtil.deserialize(serialized);
		return playerValues[this.ordinal()] >= 1;// If it's 1 or 2 (Unlocked or selected)
	}

	public void setUnlocked(PlayerModel player) {
		String serialized = player.getDatabaseValue("BLOCKS").toString();
		Integer[] playerValues = SerializationUtil.deserialize(serialized);
		playerValues[this.ordinal()] = 1; // Set the playerValues index to 1 (Unlocked)
	}

	public void setSelected(PlayerModel player) {
		String serialized = player.getDatabaseValue("BLOCKS").toString();
		Integer[] playerValues = SerializationUtil.deserialize(serialized);

		for (int i = 0; i < playerValues.length; i++) {
			if (values()[i] == this) { // If the index matches the block we're setting, change the playerValue
				playerValues[i] = 2;
			} else if (playerValues[i] == 2) { // Otherwise, if this block was selected previously, deselect it
				playerValues[i] = 1;
			}

		}
	}

	public static CustomBlock getSelectedBlock(PlayerModel player) {
		String serialized = player.getDatabaseValue("BLOCKS").toString();
		Integer[] values = SerializationUtil.deserialize(serialized);

		for (int i = 0; i < values.length; i++) {
			if (values[i] == 2) {
				return CustomBlock.values()[i];
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
