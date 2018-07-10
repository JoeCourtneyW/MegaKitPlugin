package com.sly.main.gametype;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Location;

import com.sly.main.Server;
import com.sly.main.database.Row;
import com.sly.main.resources.LocationUtil;

public class Arena
{
	private static ArrayList<Arena> arenas = new ArrayList<Arena>();

	private GameType gameType;
	private UUID id;
	private Location blueTeamSpawn;
	private Location yellowTeamSpawn;
	private Location spectatorSpawn;
	private Location[] arenaBounds;
	private boolean inUse;

	private Arena(UUID id, GameType gameType, Location blueTeamSpawn, Location yellowTeamSpawn, Location spectatorSpawn,
			Location[] arenaBounds) {
		this.gameType = gameType;
		this.id = id;
		this.blueTeamSpawn = blueTeamSpawn;
		this.yellowTeamSpawn = yellowTeamSpawn;
		this.spectatorSpawn = spectatorSpawn;
		this.arenaBounds = arenaBounds;
		this.inUse = false;

		arenas.add(this);
	}

	public GameType getGameType() {
		return gameType;
	}

	public UUID getID() {
		return id;
	}

	public Location getTeamSpawn(TeamColor team) {
		if (team == TeamColor.BLUE) {
			return blueTeamSpawn.clone();
		} else {
			return yellowTeamSpawn.clone();
		}
	}

	public Location getSpectatorSpawn() {
		return spectatorSpawn;
	}

	public Location[] getArenaBounds() {
		return this.arenaBounds;
	}

	public boolean isInUse() {
		return inUse;
	}

	public void setIsInUse(boolean isInUse) {
		this.inUse = isInUse;
	}

	public static Arena loadArena(UUID id, Row row) {// GameType gameType, Location[] spawnLocations, Location[] arenaBounds
		GameType gameType = GameType.valueOf(row.getCell("GAMETYPE").getValue().toString());
		Location blueTeamSpawn = LocationUtil.deserialize(row.getCell("BLUE_SPAWN").getValue().toString());
		Location yellowTeamSpawn = LocationUtil.deserialize(row.getCell("YELLOW_SPAWN").getValue().toString());
		Location spectatorSpawn = LocationUtil.deserialize(row.getCell("SPECTATOR_SPAWN").getValue().toString());
		Location boundary1 = LocationUtil.deserialize(row.getCell("BOUNDARY_1").getValue().toString());
		Location boundary2 = LocationUtil.deserialize(row.getCell("BOUNDARY_2").getValue().toString());

		Location[] bounds = { boundary1, boundary2 };
		return new Arena(id, gameType, blueTeamSpawn, yellowTeamSpawn, spectatorSpawn, bounds);
	}

	public static Arena newArena(GameType gameType, Location[] spawnLocations, Location[] arenaBounds) {
		final Row row = Server.getArenaTable().getDefaultRow();
		row.getPrimaryKey().setValue(UUID.randomUUID());

		row.getCell("GAMETYPE").setValue(gameType.name());
		row.getCell("BLUE_SPAWN").setValue(LocationUtil.serialize(spawnLocations[0]));
		row.getCell("YELLOW_SPAWN").setValue(LocationUtil.serialize(spawnLocations[1]));
		row.getCell("SPECTATOR_SPAWN").setValue(LocationUtil.serialize(spawnLocations[2]));
		row.getCell("BOUNDARY_1").setValue(LocationUtil.serialize(arenaBounds[0]));
		row.getCell("BOUNDARY_2").setValue(LocationUtil.serialize(arenaBounds[1]));

		Arena arena = loadArena(UUID.fromString(row.getPrimaryKey().getValue().toString()), row);

		Server.getArenaTable().newEntry(row);

		return arena;
	}
	public static ArrayList<Arena> getArenas(){
		return arenas;
	}
}
