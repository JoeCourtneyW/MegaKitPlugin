package com.sly.main.economy;

import com.sly.main.player.PlayerModel;

public class Economy
{
	private static int globalMultiplier = 1;
	private PlayerModel player;

	public Economy(PlayerModel player) {
		this.player = player;
	}

	public PlayerModel getPlayer() {
		return player;
	}

	public int getCoins() {
		return player.getDatabaseValue("COINS").asInt();
	}

	public void setCoins(int coins) {
		player.getDatabaseCell("COINS").setValue(coins);
	}

	// Returns the new coins value after the coins have been added
	public void addCoins(int coins) {
		player.getDatabaseCell("COINS").mutateValue(c -> c + coins);
	}

	public int addMultipliedCoins(int coins) {
		int multiplied = coins * getMultiplier();
		player.getDatabaseCell("COINS").mutateValue(c -> c + multiplied);
		return multiplied;
	}

	public void deductCoins(int coins) {
		player.getDatabaseCell("COINS").mutateValue(c -> c - coins);
	}

	public boolean hasEnoughCoins(int coins) {
		return getCoins() >= coins;
	}

	public int getMultiplier() {
		int multiplier = 0;
		for (int i = 5; i > 0; i--) {
			if (player.getPlayer().hasPermission("Taken.Multiplier." + i)) { // Donator ranks give players multipliers
				multiplier = i;
				break;
			}
		}

		if (player.hasBooster()) // Increase multiplier by 2x for personal booster
			multiplier += 2;

		if (globalMultiplier > 1) // Don't add on 1x or else they get a free multiplier
			multiplier += globalMultiplier;

		if (multiplier < 1) // Make sure they finish at least with a 1x multiplier
			multiplier = 1;

		return multiplier;
	}

	public static void setGlobalMultiplier(int multiplier) {
		globalMultiplier = multiplier;
	}

	public static int getGlobalMultiplier() {
		return globalMultiplier;
	}
}
