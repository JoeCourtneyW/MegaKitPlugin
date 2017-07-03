package com.me.sly.kits.main.enums;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.me.sly.kits.main.Corrupted;

public enum VanityArmor {
	NONE("NONE", Material.THIN_GLASS),
	CHAINMAIL("Chainmail Vanity Armor", Material.CHAINMAIL_CHESTPLATE),
	IRON("Iron Vanity Armor", Material.IRON_CHESTPLATE),
	GOLD("Gold Vanity Armor", Material.GOLD_CHESTPLATE),
	DIAMOND("Diamond Vanity Armor", Material.DIAMOND_CHESTPLATE);
	private Material material;
	private String displayName;
	VanityArmor(String displayName, Material material){
		this.material = material;
		this.displayName = displayName;
	}
	public int getSerializationID(){
		if(name().equalsIgnoreCase("NONE")){
			return 1;
		} else if (name().equalsIgnoreCase("CHAINMAIL")){
			return 2;
		} else if (name().equalsIgnoreCase("IRON")){
			return 3;
		} else if (name().equalsIgnoreCase("GOLD")){
			return 4;
		} else if (name().equalsIgnoreCase("DIAMOND")){
			return 5;
		}
		for(int i = 0; i < Trail.values().length; i++){
			if(Trail.values()[i].name().equalsIgnoreCase(name())){
				return i+1;
			}else{
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
	public static VanityArmor fromInvName(String name){
		for(VanityArmor s : values()){
			if(s.getDisplayName().equalsIgnoreCase(name)){
				return s;
			}
		}
		return null;
	}
	public boolean hasVanityArmor(Player p){
		String g = (String) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("VanityArmor");
		return g.split("\\|")[getSerializationID()-1].equalsIgnoreCase("1");
	}
	public void setVanityArmor(Player p, boolean value){
		String d = (String) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("VanityArmor");
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
			Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("VanityArmor", beforeValue + values + afterValue);	
		}
		Corrupted.getInstance().updateDatabase(p);
	}
	public void setVanityArmor(UUID p, boolean value){
		String d = (String) Corrupted.getDatabaseCache().get(p.toString()).get("VanityArmor");
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
			Corrupted.getDatabaseCache().get(p.toString()).put("VanityArmor", beforeValue + values + afterValue);	
		}
		Corrupted.getInstance().updateDatabase(Bukkit.getOfflinePlayer(p));
	}
	public void setSelectedVanityArmor(Player p){
		String d = (String) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("VanityArmor");
		String beforeValue = d.substring(0, 10);
		String values = d.substring(11);
		values = name();
		Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("VanityArmor", beforeValue + values);	
		Corrupted.getInstance().updateDatabase(p);
	}
	public static VanityArmor getSelectedVanityArmor(Player p){
		String d = (String) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("VanityArmor");
		String values = d.substring(10);
		return VanityArmor.valueOf(values);//change hashmap to arraylist
	}
	public static VanityArmor getSelectedVanityArmor(UUID p){
		String d = (String) Corrupted.getDatabaseCache().get(p.toString()).get("VanityArmor");
		String values = d.substring(10);
		return VanityArmor.valueOf(values);//change hashmap to arraylist
	}
}
