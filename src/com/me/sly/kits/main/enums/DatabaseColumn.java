package com.me.sly.kits.main.enums;

public enum DatabaseColumn {

	ONERATING("1v1Rating"),
	TWORATING("2v2Rating"),
	FFARATING("FFARating"),
	FFAKILLS("FFAKills"),
	FFADEATHS("FFADeaths"),
	COINS("Coins"),
	SPINS("Spins"),
	SKELETON("Skeleton"),
	CREEPER("Creeper"),
	ZOMBIE("Zombie"),
	ENDERMAN("Enderman"),
	HEROBRINE("Herobrine"),
	DREADLORD("Dreadlord"),
	ARCANIST("Arcanist"),
	SHAMAN("Shaman"),
	BLAZE("Blaze"),
	SPIDER("Spider"),
	PIGMAN("Pigman"),
	GOLEM("Golem");
	private String databaseName;
	DatabaseColumn(String databaseName){
		this.databaseName = databaseName;
	}
	public String getDatabaseName(){
		return this.databaseName;
	}
	public static boolean isColumn(String column){
		for(DatabaseColumn dc : values()){
			if(dc.name().equalsIgnoreCase(column.toUpperCase()) || dc.getDatabaseName().toUpperCase().equalsIgnoreCase(column.toUpperCase())){
				return true;
			}
		}
		return false;
	}
	public static DatabaseColumn getColumn(String columnName){
		for(DatabaseColumn dc : values()){
			if(dc.name().equalsIgnoreCase(columnName.toUpperCase()) || dc.getDatabaseName().toUpperCase().equalsIgnoreCase(columnName.toUpperCase())){
				return dc;
			}
		}
		return null;
	}
}
