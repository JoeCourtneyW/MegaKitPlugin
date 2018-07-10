package com.sly.main.gametype;

import org.bukkit.World;

public enum GameType
{
	ONEvONE("1v1", 30, 10),
	TWOvTWO("2v2", 40, 15),
	DUEL("DUEL", 0, 0),
	FFA("FFA", 20, 0);
	private String displayName;
	private int winReward;
	private int lossReward;

	GameType(String displayName, int winReward, int lossReward) {
		this.displayName = displayName;
		this.winReward = winReward;
		this.lossReward = lossReward;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getRatingColumnName() {
		return displayName + "_RATING";
	}
	
	public String getWinsColumnName() {
		return displayName + "_WINS";
	}
	
	public String getLossesColumnName() {
		return displayName + "_LOSSES";
	}

	public int getWinReward() {
		return winReward;
	}

	public int getLossReward() {
		return lossReward;
	}

	public static GameType getFromWorld(World w) {
		switch (w.getName())
		{
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
