package com.sly.main.economy;

import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

import com.sly.main.Server;
import com.sly.main.player.PlayerModel;
import com.sly.main.resources.FileIO;
import com.sly.main.resources.MathUtil;

public class Booster
{
	private static File boosterFile;
	private long time;

	/**
	 * 
	 * @param time
	 *            in seconds
	 */
	public Booster(long time) {
		this.time = time;
	}

	public double getTimeInHours() {
		return MathUtil.roundTwoPoints((double) (time / 3600.0));
		// 3600 = 60 seconds in 1 minute, 60 minutes in 1 hour
	}

	public long getTime() {
		return time;
	}

	/*
	 * Use this method at intervals to push local times to the file
	 */
	public static void updateBoosterFile() {
		FileIO writer = new FileIO(boosterFile); //Connect to booster file, grab contents, close stream
		writeBoosterFile(writer);
	}
	/*
	 * Use this method on server startup to initialize PlayerModel boosterTimes
	 */
	public static void initBoosterTimes() {
		FileIO reader = new FileIO(boosterFile);
		HashMap<PlayerModel, Long> players = parseBoosterFile(reader);
		for(PlayerModel player : players.keySet()){
			player.addBooster(new Booster(players.get(player)));
		}
	}
	private static HashMap<PlayerModel, Long> parseBoosterFile(FileIO io) {
		HashMap<PlayerModel, Long> parsedFile = new HashMap<PlayerModel, Long>(); //Map of Player-BoosterTime
		io.connectAsInputFile(); //Connect to stream
		Scanner reader = io.getReader();
		String line = "";
		while(reader.hasNextLine()){
			line = reader.nextLine().replaceAll("\n", ""); //Take away the line breaks
			if(line.contains(":")){ //Line format: UUID: TIME_IN_SECONDS\n
				UUID uuid = UUID.fromString(line.split(":")[0].trim());
				Long time = Long.parseLong(line.split(":")[1].trim());
				parsedFile.put(PlayerModel.getPlayerModel(uuid), time); //Push player model and time left to map
			}
		}
		io.closeStream();
		return parsedFile;
	}
	private static void writeBoosterFile(FileIO io) {
		io.connectAsOutputFile(false);
		StringBuilder fileContents = new StringBuilder();
		for(PlayerModel player : Server.getAllPlayers()){
			if(player.getBoosterTime() < 1)
				continue;
			fileContents.append(player.getUUID() + ":" + player.getBoosterTime());
			fileContents.append(": ");
			fileContents.append(player.getBoosterTime());
			fileContents.append("\n");
		}
		io.write(fileContents.toString());
		io.closeStream();
	}
}
