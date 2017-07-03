package com.me.sly.kits.main.gametype;

import org.bukkit.World;

public enum GameType {
	ONEvONE("1v1"),
	TWOvTWO("2v2"),
	DUEL("DUEL"),
	FFA("FFA");
	private String databaseName;
	GameType(String databaseName){
		this.databaseName = databaseName;
	}
	public String getDatabaseName(){
		return databaseName;
	}
	public static GameType getFromWorld(World w){
		switch(w.getName()){
		case "1v1":
			return ONEvONE;
		case "2v2":
			return TWOvTWO;
		case "FFA":
			return FFA;
		default:
			return FFA;
		}
	}
}
