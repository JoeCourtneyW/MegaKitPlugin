package com.me.sly.kits.main.enums;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.me.sly.kits.main.Corrupted;

public enum CustomBlock {
	WOOD("Wooden Planks", Material.WOOD, (short)3, 0),
	COAL("Coal", Material.COAL_BLOCK, (short)0, 500),
	LAPIS("Lapis", Material.LAPIS_BLOCK, (short)0, 2500),
	IRON("Iron", Material.IRON_BLOCK, (short)0, 7500),
	EMERALD("Emerald", Material.EMERALD_BLOCK, (short)0, 15000),
	GOLD("Gold", Material.GOLD_BLOCK, (short)0, 45000),
	DIAMOND("Diamond", Material.DIAMOND_BLOCK, (short)0, 100000),
	LEAF("Leaves", Material.LEAVES, (short)0, -1),
	HAY("Hay Bale", Material.HAY_BLOCK, (short)0, -1),
	MOSS_STONE("Mossy Stone", Material.MOSSY_COBBLESTONE, (short)0, -1),
	BOOKSHELF("Bookshelf", Material.BOOKSHELF, (short)0, -1),
	NETHER_BRICK("Nether Bricks", Material.NETHER_BRICK, (short)0, -1),
	OBSIDIAN("Obsidian", Material.OBSIDIAN, (short)0, -1),
	GLOWSTONE("Glowstone", Material.GLOWSTONE, (short)0, -1),
	TNT("TNT", Material.TNT, (short)0, -2),
	MELON("Melon", Material.MELON_BLOCK, (short)0, -2),
	BEDROCK("Bedrock", Material.BEDROCK, (short)0, -2),
	SPONGE("Sponge", Material.SPONGE, (short)0, -2),
	ICE("Ice", Material.ICE, (short)0, -2),
	RAINBOW_WOOL("Rainbow Wool", Material.WOOL, (short)2, -2);
	
	private Material material;
	private String displayName;
	private short durability;
	private int price;
	CustomBlock(String displayName, Material material, short durability, int price){
		this.material = material;
		this.displayName = displayName;
		this.durability = durability;
		this.price = price;
	}
	public int getSerializationID(){
		for(int i = 0; i < CustomBlock.values().length; i++){
			if(CustomBlock.values()[i].getDisplayName().equalsIgnoreCase(this.getDisplayName())){
				return i+1;
			}
			
		}
		return 0;
	}
	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public Material getMaterial() {
		return material;
	}
	public int getPrice(){
		return price;
	}
	public void setMaterial(Material material) {
		this.material = material;
	}

	public short getDurability() {
		return durability;
	}

	public void setDurability(short durability) {
		this.durability = durability;
	}
	public static CustomBlock fromInvName(String name){
		for(CustomBlock s : values()){
			if(s.getDisplayName().equalsIgnoreCase(name)){
				return s;
			}
		}
		return null;
	}
	public boolean hasBlock(Player p){
		String g = (String) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Block");
		return g.split("\\|")[getSerializationID()-1].equalsIgnoreCase("1");
	}
	public boolean hasEnoughCoins(Player p){
		int g = (int) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins");
		return g >= price;
	}
	public void setBlock(Player p, boolean value){
		String d = (String) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Block");
		String beforeValue = d.substring(0, (getSerializationID()*2)-2);
		String values = d.substring((getSerializationID()*2)-2).split("\\|")[0];
		String afterValue = "";
		for(int i = 1; i < d.substring((getSerializationID()*2)-2).split("\\|").length; i++){
			if(i == 1){
			afterValue = d.substring((getSerializationID()*2)-2).split("\\|")[i];
			}else{
				afterValue =  afterValue + "|" +d.substring((getSerializationID()*2)-2).split("\\|")[i];
			}
			values = value? "1" : "0";
			afterValue = d.substring((getSerializationID()*2)-1);
			Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Block", beforeValue + values + afterValue);	
		}
		Corrupted.getInstance().updateDatabase(p);
	}
	public void setBlock(UUID p, boolean value){
		String d = (String) Corrupted.getDatabaseCache().get(p.toString()).get("Block");
		String beforeValue = d.substring(0, (getSerializationID()*2)-2);
		String values = d.substring((getSerializationID()*2)-2).split("\\|")[0];
		String afterValue = "";
		for(int i = 1; i < d.substring((getSerializationID()*2)-2).split("\\|").length; i++){
			if(i == 1){
			afterValue = d.substring((getSerializationID()*2)-2).split("\\|")[i];
			}else{
				afterValue =  afterValue + "|" +d.substring((getSerializationID()*2)-2).split("\\|")[i];
			}
			values = value ? "1" : "0";
			Corrupted.getDatabaseCache().get(p.toString()).put("Block", beforeValue + values + afterValue);	
		}
		Corrupted.getInstance().updateDatabase(Bukkit.getOfflinePlayer(p));
	}
	public void setSelectedBlock(Player p){
		String d = (String) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Block");
		String beforeValue = d.substring(0, 40);
		String values = d.substring(41);
		values = name();
		Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Block", beforeValue + values);	
		Corrupted.getInstance().updateDatabase(p);
	}
	public static CustomBlock getSelectedBlock(Player p){
		String d = (String) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Block");
		String values = d.substring(40);
		return CustomBlock.valueOf(values);//change hashmap to arraylist
	}
	public static CustomBlock getSelectedBlock(UUID p){
		String d = (String) Corrupted.getDatabaseCache().get(p.toString()).get("Block");
		String values = d.substring(40);
		return CustomBlock.valueOf(values);//change hashmap to arraylist
	}
}
