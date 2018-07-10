package com.sly.main.kits;

import java.util.HashMap;
import java.util.StringJoiner;
import java.util.function.Function;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import com.sly.main.kits.listeners.Arcanist;
import com.sly.main.kits.listeners.Blaze;
import com.sly.main.kits.listeners.Creeper;
import com.sly.main.kits.listeners.Dreadlord;
import com.sly.main.kits.listeners.Enderman;
import com.sly.main.kits.listeners.Golem;
import com.sly.main.kits.listeners.Herobrine;
import com.sly.main.kits.listeners.Hunter;
import com.sly.main.kits.listeners.Pigman;
import com.sly.main.kits.listeners.Pirate;
import com.sly.main.kits.listeners.Shaman;
import com.sly.main.kits.listeners.Skeleton;
import com.sly.main.kits.listeners.Spider;
import com.sly.main.kits.listeners.Squid;
import com.sly.main.kits.listeners.Zombie;
import com.sly.main.player.PlayerModel;
import com.sly.main.resources.BetterItem;
import com.sly.main.slots.SlotItem;

public enum Kits implements
	SlotItem
{

	SKELETON(0, "Skeleton", new Skeleton(), "Explosive Arrow", Material.BONE, (short) 0, "+Ranged", "+AOE Range",
			"+High Range DPS", "+High Mobility", "-Low Melee DMG", "-Low Armour"),
	CREEPER(0, "Creeper", new Creeper(), "Detonate", Material.TNT, (short) 0, "+Strong AOE Ability", "+Fast Charge",
			"*Decent Defence", "*Decent Attack", "-Melee Only"),
	ZOMBIE(3000, "Zombie", new Zombie(), "Heal", Material.SKULL_ITEM, (short) 2, "+Team Class", "+AOE Healing",
			"+High Resistance", "-Melee Only", "-Low DMG"),
	ENDERMAN(20000, "Enderman", new Enderman(), "Teleport", Material.ENDER_PEARL, (short) 0, "+Extremely High Mobility",
			"+Ability to Chase", "+Fast Charge", "-Low Defence", "-No Damaging Ability"),
	HEROBRINE(8000, "Herobrine", new Herobrine(), "Wrath", Material.NETHER_STAR, (short) 0, "+Strong AOE Ability",
			"+High DMG", "+High Mobility", "+Kill Effect", "-Melee Only", "-Very Low Armor"),
	DREADLORD(17000, "Dreadlord", new Dreadlord(), "Wither Blast", Material.SKULL_ITEM, (short) 1, "+High DMG",
			"+Ranged Ability", "+Kill Effect", "+Life Steal", "*Average Defence", "-Melee Range", "-Slow Charge"),
	GOLEM(12000, "Golem", new Golem(), "Iron Punch", Material.IRON_BLOCK, (short) 0, "+High Defence", "+AOE Ability",
			"+Ranged Counter (Resistance)", "-Melee Only", "-Low Mobility", "-Slow Charge"),
	ARCANIST(10000, "Arcanist", new Arcanist(), "Beam", Material.FIREWORK, (short) 0, "+High DPS", "+Fast Charge",
			"+Kill Effect", "+Ranged Ability", "-Melee Range", "-Mediocre Defence"),
	BLAZE(15000, "Blaze", new Blaze(), "Fireball", Material.BLAZE_POWDER, (short) 0, "+Ranged", "+Ranged Ability",
			"+Idle Charge", "+Kill Effect", "-Low Defence", "-Low Mobility", "-Slow Charge"),
	PIRATE(15000, "Pirate", new Pirate(), "Cannon Fire", Material.STONE_AXE, (short) 0, ""),
	SQUID(15000, "Squid", new Squid(), "Squid Splash", Material.INK_SACK, (short) 0, ""),
	HUNTER(15000, "Hunter", new Hunter(), "Eagle's Eye", Material.BOW, (short) 0, ""),
	SHAMAN(-1, "Shaman", new Shaman(), "Tornado", Material.MONSTER_EGG, (short) 95, "+AOE Ability", "+Debuff",
			"+Companions", "+High DPS", "-Slow Charge", "-Stationary Ability", "-Melee Only"),
	SPIDER(-1, "Spider", new Spider(), "Leap", Material.WEB, (short) 0, "+High Mobility", "+Idle Charge",
			"+High DMG Ability", "-Melee Only", "-Slow Charge", "-Take DMG to Deal DMG"),
	PIGMAN(-1, "Pigman", new Pigman(), "Burning Soul", Material.GRILLED_PORK, (short) 0, "+High Defence",
			"+AOE Ability", "+AOE Defensive", "-Melee Only", "-Low Mobility"),
	NONE(0, "None", new Skeleton(), "None", Material.AIR, (short) 0, "");
	// Price: -1 translates to a special way of obtaining, 0 means it comes free
	private int price;
	private String name;
	private Kit c;
	private String ability;
	private Material displayMaterial;
	private short displayData;
	private String[] description;

	Kits(int price, String name, Kit c, String ability, Material displayMaterial, short displayData,
			String... description) {
		this.price = price;
		this.name = name;
		this.c = c;
		this.ability = ability;
		this.displayMaterial = displayMaterial;
		this.displayData = displayData;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public Kit getListenerClass() {
		return c;
	}

	public String getAbility() {
		return ability;
	}

	public Material getDisplayMaterial() {
		return displayMaterial;
	}

	public short getDisplayData() {
		return displayData;
	}

	public String[] getDescription() {
		for (int i = 0; i < this.description.length; i++) {
			if (this.description[i].startsWith("+")) {
				this.description[i] = "§a" + this.description[i];
			} else if (this.description[i].startsWith("-")) {
				this.description[i] = "§c" + this.description[i];
			} else if (this.description[i].startsWith("*")) {
				this.description[i] = "§b" + this.description[i];
			}
		}
		return this.description;
	}

	public boolean isDefaultKit() {
		return getPrice() == 0;
	}

	public boolean isSpecialKit() {
		return getPrice() < 0;
	}

	public HashMap<String, ItemStack> getItems() {
		return getListenerClass().getKitItems();
	}

	public boolean hasEnoughCoins(PlayerModel p) {
		return p.getDatabaseValue("COINS").asInt() >= price;
	}

	public boolean hasKit(PlayerModel p) {
		String serializedKit = p.getDatabaseValue(name()).asString();
		return deserialize(serializedKit).get("hasKit") == 1; // == 1 changes the int value to a boolean
	}

	public boolean isPrestige(PlayerModel p) {
		String serializedKit = p.getDatabaseValue(name()).asString();
		return deserialize(serializedKit).get("isPrestiged") == 1; // == 1 changes the int value to a boolean
	}

	public int getKills(PlayerModel p) {
		String serializedKit = p.getDatabaseValue(name()).asString();
		return deserialize(serializedKit).get("kills");
	}

	public int getDeaths(PlayerModel p) {
		String serializedKit = p.getDatabaseValue(name()).asString();
		return deserialize(serializedKit).get("deaths");
	}

	public void mutateKills(PlayerModel p, Function<Integer, Integer> mutator) {
		String serializedKit = p.getDatabaseValue(name()).asString();

		HashMap<String, Integer> values = deserialize(serializedKit);
		values.put("kills", mutator.apply(values.get("kills")));

		serializedKit = serialize(values);
		p.getDatabaseCell(name()).setValue(serializedKit);
		p.pushRowToDatabase();
	}

	public void mutateDeaths(PlayerModel p, Function<Integer, Integer> mutator) {
		String serializedKit = p.getDatabaseValue(name()).asString();

		HashMap<String, Integer> values = deserialize(serializedKit);
		values.put("deaths", mutator.apply(values.get("deaths")));

		serializedKit = serialize(values);
		p.getDatabaseCell(name()).setValue(serializedKit);
		p.pushRowToDatabase();
	}

	public void prestige(PlayerModel p) {
		String serializedKit = p.getDatabaseValue(name()).asString();

		HashMap<String, Integer> values = deserialize(serializedKit);
		values.put("isPrestiged", 1);

		serializedKit = serialize(values);
		p.getDatabaseCell(name()).setValue(serializedKit);
		p.pushRowToDatabase();
	}

	public int getPrice() {
		return price;
	}

	public void onPurchase(PlayerModel p) {
		String serializedKit = p.getDatabaseValue(name()).asString();

		HashMap<String, Integer> values = deserialize(serializedKit);
		values.put("hasKit", 1);

		serializedKit = serialize(values);
		p.getDatabaseCell(name()).setValue(serializedKit);
		p.pushRowToDatabase();

	}

	public void onRevoke(PlayerModel p) {
		String serializedKit = p.getDatabaseValue(name()).asString();

		HashMap<String, Integer> values = deserialize(serializedKit);
		values.put("hasKit", 0);

		serializedKit = serialize(values);
		p.getDatabaseCell(name()).setValue(serializedKit);
		p.pushRowToDatabase();
	}

	public ItemStack getKitSelectorItemStack(PlayerModel player) {
		BetterItem item = new BetterItem().material(getDisplayMaterial()).amount(1).data(getDisplayData());

		if (hasKit(player) || player.getPlayer().isOp())
			item.displayName("§2" + getName());
		else
			item.displayName("§4" + getName());

		if (isPrestige(player))
			item.addLoreLine("§d§lPRESTIGED");
		for (String line : getDescription())
			item.addLoreLine(line);

		return item.grab();
	}

	// Serialize a players kit data
	public String serialize(HashMap<String, Integer> playerValues) {
		StringJoiner serialized = new StringJoiner(",");

		serialized.add(playerValues.get("hasKit") + "");
		serialized.add(playerValues.get("isPrestiged") + "");
		serialized.add(playerValues.get("kills") + "");
		serialized.add(playerValues.get("deaths") + "");

		return serialized.toString();
	}

	// Returns hashmap with keys: hasKit, isPrestiged, kills, and deaths
	public HashMap<String, Integer> deserialize(String serialized) {
		HashMap<String, Integer> deserialized = new HashMap<String, Integer>();
		String[] values = serialized.split(",");

		deserialized.put("hasKit", Integer.parseInt(values[0]));
		deserialized.put("isPrestiged", Integer.parseInt(values[1]));
		deserialized.put("kills", Integer.parseInt(values[2]));
		deserialized.put("deaths", Integer.parseInt(values[3]));

		return deserialized;
	}

	public static boolean isKit(String string) {

		for (Kits kits : Kits.getKits()) {
			if (kits.name().equalsIgnoreCase(string)) {
				return true;
			}
		}

		return false;
	}

	public static Kits getKitFromString(String string) {
		for (Kits kit : Kits.getKits()) {
			if (kit.name().equalsIgnoreCase(string)) {
				return kit;
			}
		}

		return Kits.NONE;
	}

	public static Kits[] getKits() {
		Kits[] kits = new Kits[values().length - 1]; // Length-1 will exclude the NONE kit, better for looping through
		for (int i = 0; i <= kits.length; i++) {
			kits[i] = values()[i];
		}
		return kits;
	}

	@Override
	public void onWin(PlayerModel player) {
		// TODO Auto-generated method stub

	}

}
