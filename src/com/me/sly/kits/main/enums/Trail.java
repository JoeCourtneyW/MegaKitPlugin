package com.me.sly.kits.main.enums;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.me.sly.kits.main.Corrupted;
import com.me.sly.kits.main.resources.ParticleEffects;

public enum Trail {
	NONE(null, 0, "NONE", Material.THIN_GLASS),
	MAGMA(ParticleEffects.DRIP_LAVA, 5, "Magma Trail", Material.LAVA_BUCKET),
	WATER(ParticleEffects.DRIP_WATER, 5, "Water Trail", Material.WATER_BUCKET),
	MAGIC(ParticleEffects.MAGIC_CRIT, 5, "Magic Trail", Material.ENCHANTED_BOOK),
	SMOKE(ParticleEffects.RED_DUST, 5, "Smoke Trail", Material.SULPHUR),
	SLIME(ParticleEffects.SLIME, 3, "Slime Trail", Material.SLIME_BALL),
	SNOW(ParticleEffects.SNOWBALL_POOF, 3, "Snow Trail", Material.SNOW_BALL),
	ENCHANTED(ParticleEffects.ENCHANTMENT_TABLE, 5, "Enchanted Trail", Material.ENCHANTMENT_TABLE),
	VOID(ParticleEffects.TOWN_AURA, 15, "Void Trail", Material.BEDROCK),
	PORTAL(ParticleEffects.PORTAL, 5, "Portal Trail", Material.PORTAL);
	private Material material;
	private String displayName;
	private ParticleEffects pe;
	private int amount;
	Trail(ParticleEffects pe, int amount, String displayName, Material material){
		this.material = material;
		this.displayName = displayName;
		this.pe = pe;
		this.amount = amount;
	}
	public int getSerializationID(){
		for(int i = 0; i < Trail.values().length; i++){
			if(Trail.values()[i].name().equalsIgnoreCase(name())){
				return i+1;
			}
			
		}
		return 0;
	}
	public String getDisplayName() {
		return displayName;
	}
	public ParticleEffects getParticleEffect(){
		return pe;
	}
	public int getParticleAmount(){
		return amount;
	}
	public Material getMaterial() {
		return material;
	}
	public static Trail fromInvName(String name){
		for(Trail s : values()){
			if(s.getDisplayName().equalsIgnoreCase(name)){
				return s;
			}
		}
		return null;
	}
	public boolean hasTrail(Player p){
		String g = (String) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Trail");
		return g.split("\\|")[getSerializationID()-1].equalsIgnoreCase("1");
	}
	public void setTrail(Player p, boolean value){
		String d = (String) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Trail");
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
			Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Trail", beforeValue + values + afterValue);	
		}
		Corrupted.getInstance().updateDatabase(p);
	}
	public void setTrail(UUID p, boolean value){
		String d = (String) Corrupted.getDatabaseCache().get(p.toString()).get("Trail");
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
			Corrupted.getDatabaseCache().get(p.toString()).put("Trail", beforeValue + values + afterValue);	
		}
		Corrupted.getInstance().updateDatabase(Bukkit.getOfflinePlayer(p));
	}
	public void setSelectedTrail(Player p){
		String d = (String) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Trail");
		String beforeValue = d.substring(0, 20);
		String values = d.substring(21);
		values = name();
		Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Trail", beforeValue + values);	
		Corrupted.getInstance().updateDatabase(p);
	}
	public static Trail getSelectedTrail(Player p){
		String d = (String) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Trail");
		String values = d.substring(20);
		return Trail.valueOf(values);//change hashmap to arraylist
	}
	public static Trail getSelectedTrail(UUID p){
		String d = (String) Corrupted.getDatabaseCache().get(p.toString()).get("Trail");
		String values = d.substring(20);
		return Trail.valueOf(values);//change hashmap to arraylist
	}
}
