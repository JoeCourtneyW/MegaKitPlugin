package com.me.sly.kits.main.enums;

public enum Server {

	DEV("98.169.21.51", "PlayersDEV"),
	GAME("167.114.48.198", "Players"),
	BETA("", "PlayersBETA");
	private String ip;
	private String databaseTable;
	Server(String ip, String databaseTable){
		this.ip = ip;
		this.databaseTable = databaseTable;
	}
	public String getIP(){
		return ip;
	}
	public String getTable(){
		return databaseTable;
	}
	public static Server getServer(String ip){
		if(ip.contains(DEV.getIP())){
			return DEV;
		}else if(ip.contains(GAME.getIP())){
			return GAME;
		}else if(ip.contains(BETA.getIP())){
			return BETA;
		}
		return GAME;
	}
}
