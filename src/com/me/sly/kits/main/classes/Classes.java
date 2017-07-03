package com.me.sly.kits.main.classes;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.me.sly.kits.main.Corrupted;
import com.me.sly.kits.main.classes.listeners.Arcanist;
import com.me.sly.kits.main.classes.listeners.Blaze;
import com.me.sly.kits.main.classes.listeners.Creeper;
import com.me.sly.kits.main.classes.listeners.Dreadlord;
import com.me.sly.kits.main.classes.listeners.Enderman;
import com.me.sly.kits.main.classes.listeners.Golem;
import com.me.sly.kits.main.classes.listeners.Herobrine;
import com.me.sly.kits.main.classes.listeners.Hunter;
import com.me.sly.kits.main.classes.listeners.Pigman;
import com.me.sly.kits.main.classes.listeners.Pirate;
import com.me.sly.kits.main.classes.listeners.Shaman;
import com.me.sly.kits.main.classes.listeners.Skeleton;
import com.me.sly.kits.main.classes.listeners.Spider;
import com.me.sly.kits.main.classes.listeners.Squid;
import com.me.sly.kits.main.classes.listeners.Zombie;

public enum Classes {

	SKELETON(0, "Skeleton", new Skeleton(), "Explosive Arrow", Material.BONE, (short) 0, "+Ranged", "+AOE Range", "+High Range DPS", "+High Mobility", "-Low Melee DMG", "-Low Armour"),
	CREEPER(0, "Creeper", new Creeper(), "Detonate", Material.TNT, (short) 0, "+Strong AOE Ability", "+Fast Charge", "*Decent Defence", "*Decent Attack", "-Melee Only"),
	ZOMBIE(3000, "Zombie", new Zombie(), "Heal", Material.SKULL_ITEM, (short) 2, "+Team Class", "+AOE Healing", "+High Resistance", "-Melee Only", "-Low DMG"),
 	ENDERMAN(20000, "Enderman", new Enderman(), "Teleport", Material.ENDER_PEARL, (short) 0, "+Extremely High Mobility", "+Ability to Chase", "+Fast Charge", "-Low Defence", "-No Damaging Ability"),
	HEROBRINE(8000, "Herobrine", new Herobrine(), "Wrath", Material.NETHER_STAR, (short) 0, "+Strong AOE Ability", "+High DMG", "+High Mobility", "+Kill Effect", "-Melee Only", "-Very Low Armor"),
	DREADLORD(17000, "Dreadlord", new Dreadlord(), "Wither Blast", Material.SKULL_ITEM, (short) 1, "+High DMG", "+Ranged Ability", "+Kill Effect", "+Life Steal", "*Average Defence", "-Melee Range", "-Slow Charge"),
	GOLEM(12000, "Golem", new Golem(), "Iron Punch", Material.IRON_BLOCK, (short) 0, "+High Defence", "+AOE Ability", "+Ranged Counter (Resistance)", "-Melee Only", "-Low Mobility", "-Slow Charge"),
	ARCANIST(10000, "Arcanist", new Arcanist(), "Beam", Material.FIREWORK, (short) 0, "+High DPS", "+Fast Charge", "+Kill Effect", "+Ranged Ability", "-Melee Range", "-Mediocre Defence"),
	BLAZE(15000, "Blaze", new Blaze(), "Fireball", Material.BLAZE_POWDER, (short) 0, "+Ranged", "+Ranged Ability", "+Idle Charge", "+Kill Effect", "-Low Defence", "-Low Mobility", "-Slow Charge"),
	PIRATE(15000, "Pirate", new Pirate(), "Cannon Fire", Material.STONE_AXE, (short) 0, ""),
	SQUID(15000, "Squid", new Squid(), "Squid Splash", Material.INK_SACK, (short) 0, ""),
	HUNTER(15000, "Hunter", new Hunter(), "Eagle's Eye", Material.BOW, (short) 0, ""),
	SHAMAN(-1, "Shaman", new Shaman(), "Tornado", Material.MONSTER_EGG, (short) 95, "+AOE Ability", "+Debuff", "+Companions", "+High DPS", "-Slow Charge", "-Stationary Ability", "-Melee Only"),
	SPIDER(-1, "Spider", new Spider(), "Leap", Material.WEB, (short) 0, "+High Mobility", "+Idle Charge", "+High DMG Ability", "-Melee Only", "-Slow Charge", "-Take DMG to Deal DMG"),
	PIGMAN(-1, "Pigman", new Pigman(), "Burning Soul", Material.GRILLED_PORK, (short) 0, "+High Defence", "+AOE Ability", "+AOE Defensive", "-Melee Only", "-Low Mobility"),
	NONE(0, "None", new Skeleton(), "None", Material.AIR, (short) 0, "");
	//Price: -1 translates to a special way of obtaining, 0 means it comes free
	private int price;
	private String name;
	private Class c;
	private String ability;
	private Material displayMaterial;
	private short displayDurability;
	private String[] description;
	Classes(int price, String name, Class c, String ability, Material displayMaterial, short displayDurability, String... description){
		this.price = price;
		this.name = name;
		this.c = c;
		this.ability = ability;
		this.displayMaterial = displayMaterial;
		this.displayDurability = displayDurability;
		this.description = description;
	}
	
	public int getPrice(){
		return price;
	}
	public String getName(){
		return name;
	}
	public Class getListenerClass(){
		return c;
	}
	public String getAbility(){
		return ability;
	}
	public Material getDisplayMaterial(){
		return displayMaterial;
	}
	public short getDisplayDurability(){
		return displayDurability;
	}
	public String[] getDescription(){
		for(int i = 0; i < this.description.length; i++){
			if(this.description[i].startsWith("+")){
				this.description[i] = "§a" + this.description[i];
			}else if(this.description[i].startsWith("-")){
				this.description[i] = "§c" + this.description[i];
			}else if(this.description[i].startsWith("*")){
				this.description[i] = "§b" + this.description[i];
			}
		}
		return this.description;
	}
	public boolean hasEnoughCoins(Player p){
		return ((int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins") >= price);
	}
	public boolean hasKit(Player p){
		return (Integer.parseInt(((String)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get(name)).split("\\|")[0]) == 1);
	}
	public boolean isPrestige(Player p){
		return (Integer.parseInt(((String)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get(name)).split("\\|")[1]) == 1);
	}
	public int getKills(Player p){
		return Integer.parseInt(((String)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get(name)).split("\\|")[2]);
	}
	public int getKills(UUID p){
		return Integer.parseInt(((String)Corrupted.getDatabaseCache().get(p.toString()).get(name)).split("\\|")[2]);
	}
	public void setKills(Player p, int kills){
		String s = (String)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get(name);
		String g = s.split("\\|")[0];
		s = s.split("\\|")[1];
		Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put(name, g + "|" + s + "|" + kills);
		Corrupted.getInstance().updateDatabase(p);
	}
	public void setKit(Player p, boolean value){
		String s = (String)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get(name);
		String g = s.split("\\|")[2];
		s = s.split("\\|")[1];
		Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put(name, value == true ? "1|" + s + "|" + g: "0|" + s + "|" + g);
		Corrupted.getInstance().updateDatabase(p);
	}
	public void setKit(UUID p, boolean value){
		String s = (String)Corrupted.getDatabaseCache().get(p.toString()).get(name);
		String g = s.split("\\|")[2];
		s = s.split("\\|")[1];
		Corrupted.getDatabaseCache().get(p.toString()).put(name, value == true ? "1|" + s + "|" + g : "0|" + s + "|" + g);
		Corrupted.getInstance().updateDatabase(Bukkit.getOfflinePlayer(p));
	}
	public void setPrestiged(Player p, boolean value){
		String s = (String)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get(name);
		String g = s.split("\\|")[2];
		s = s.split("\\|")[0];
		Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put(name, value ?  s + "|1" + "|" + g: s + "|0" + "|" + g);
		Corrupted.getInstance().updateDatabase(p);
	}
	public static Classes getClass(Player p){
		if(p.hasMetadata("Class")){
			return valueOf(p.getMetadata("Class").get(0).value().toString().toUpperCase());
		}else{
			return NONE;
		}
	}
	public static boolean isClass(String string) {

	    for (Classes cr : Classes.values()) {
	        if (cr.name().equals(string)) {
	            return true;
	        }
	    }

	    return false;
	}
}
