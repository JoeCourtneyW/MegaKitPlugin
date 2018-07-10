package com.sly.main.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class Config {
	@SuppressWarnings("unused")
	private UUID playerUUID;
	File f;
	YamlConfiguration yl;
	public Config(UUID playerUUID, JavaPlugin pl){
		this.playerUUID = playerUUID;
		f = new File(pl.getDataFolder(),  "Players");
		f.mkdir();
		f = new File(pl.getDataFolder() + "/Players",  playerUUID.toString() + ".yml");
		yl = YamlConfiguration.loadConfiguration(f);
		
	}
	
	
	public boolean doesExist(){
		return f.exists();
	}
	public Config create(){
		try {f.createNewFile();} catch (IOException e) {e.printStackTrace();}
		save();
		return this;
	}
	public YamlConfiguration getYaml(){
		return yl;
	}
	public void set(String path, Object o){
		yl.set(path, o);
		save();
	}
	public Object get(String path){
		return yl.get(path);
	}
	public String getString(String path){
		return yl.getString(path);
	}
	public boolean contains(String path){
		return yl.contains(path);
	}
	
	public int getInt(String path){
		return yl.getInt(path);
	}
	public boolean getBoolean(String path){
		return yl.getBoolean(path);
	}
	public ItemStack getItemStack(String path){
		return yl.getItemStack(path);
	}
	@SuppressWarnings("unchecked")
	public List<ItemStack> getItemStackList(String path){
		return (List<ItemStack>) yl.getList(path);
	}
	public void save(){
		try {yl.save(f);} catch (IOException e) {}
	}
}
