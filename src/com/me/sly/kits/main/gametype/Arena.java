package com.me.sly.kits.main.gametype;

import org.bukkit.Location;

public class Arena {

	private GameType gameType;
	private int id;
	private Location spawn1;
	private Location spawn2;
	private boolean inUse;
	private Location spawn3;
	public Arena(GameType gameType, int id, Location spawn1, Location spawn2, Location spawn3, boolean inUse){
		this.gameType = gameType;
		this.id = id;
		this.spawn1 = spawn1;
		this.spawn2 = spawn2;
		this.spawn3 = spawn3;
		this.inUse = inUse;
	}
	public GameType getGameType(){
		return gameType;
	}
	public int getID(){
		return id;
	}
	public Location getSpawn1(){
		return spawn1;
	}
	public Location getSpawn2(){
		return spawn2;
	}
	public Location getSpawn3(){
		return spawn3;
	}
	public boolean isInUse(){
		return inUse;
	}
	public void setIsInUse(boolean isInUse){
		this.inUse = isInUse;
	}
}
