package com.sly.main.resources;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.inventory.ItemStack;

import com.sly.main.gametype.Game;
import com.sly.main.gametype.GameType;
import com.sly.main.gametype.TeamColor;
import com.sly.main.player.PlayerModel;

public class MathUtil
{

	public static boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	public static int rand(int min, int max) {
		max += 1;
		return min + (new Random()).nextInt(max - min);
	}

	public static Double roundTwoPoints(double d) {

		DecimalFormat twoPointForm = new DecimalFormat("#.##");
		return Double.valueOf(twoPointForm.format(d));
	}

	public static Float roundTwoPoints(float f) {
		DecimalFormat twoPointForm = new DecimalFormat("#.##");
		return Float.valueOf(twoPointForm.format(f));
	}

	public static int booleanAsInt(boolean value) {
		if (value)
			return 1;
		else
			return 0;
	}

	public static boolean intAsBoolean(int value) {
		return value == 1;
	}

	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
		for (Entry<T, E> entry : map.entrySet()) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public static int limitValue(int number, int minCap, int maxCap) {
		if (number <= minCap)
			return minCap;
		if (number >= maxCap)
			return maxCap;
		return number;
	}

	public static int capValue(int number, int maxCap) {
		if (number >= maxCap)
			return maxCap;
		else
			return number;
	}

	public static double limitValue(double number, double minCap, double maxCap) {
		if (number <= minCap)
			return minCap;
		if (number >= maxCap)
			return maxCap;
		return number;
	}

	public static double capValue(double number, double maxCap) {
		if (number >= maxCap)
			return maxCap;
		else
			return number;
	}

	private static final int kFactor = 32;

	public static float[] getEloChangeInGame(Game game, TeamColor winningTeamColor) {

		float[] deltas = new float[game.getAllPlayers().size()]; // A return array that will contain all player's deltas

		double winningTeamRating = 0;
		double losingTeamRating = 0;
		for (PlayerModel player : game.getAllPlayers()) {
			if (game.getTeamColor(player) == winningTeamColor)
				winningTeamRating += player.getElo(game.getGameType());
			else
				losingTeamRating += player.getElo(game.getGameType());
		}

		winningTeamRating /= game.getTeam(winningTeamColor).size(); // Used to get average of teams ratings
		losingTeamRating /= game.getTeam(winningTeamColor.getOpposite()).size(); // Used to get average of team's ratings
		double totalRating = winningTeamRating + losingTeamRating; // Total rating is the combination of both team's averages

		for (int i = 0; i < deltas.length; i++) {
			PlayerModel player = game.getAllPlayers().get(i);
			double expectedOutcome = player.getElo(game.getGameType()) / totalRating; // Will return value between 0.0 and 1.0 that indicates how likely the
																						// player was to win based off of their rating vs average rating of the game
			double actualOutcome; // Gives the actual outcome for the given player, 1F for a win, 0F for a loss
			if (game.getTeamColor(player) == winningTeamColor)
				actualOutcome = 1F;
			else
				actualOutcome = 0F;

			// Used as a multiplier on kFactor
			double finalScore = actualOutcome - expectedOutcome; // On Win: Score approaches 1 if the player was less likely to win
																	// On Loss: Score approaches -1 if the player was more likely to win

			// Keep the deltas within the kFactor range
			deltas[i] = (float) limitValue(kFactor * finalScore, -kFactor, kFactor); // Gives the delta for the players rating in a range of -32 to 32

		}
		return deltas;
	}

	/**
	 * 
	 * @param player
	 *            the player that died in the encounter
	 * @param killer
	 *            can be null if player was killed by env
	 * @return A float array that contains the DELTAS for both player's ratings
	 */
	public static float[] getEloChangeInFFA(PlayerModel player, PlayerModel killer) {

		float[] deltas = new float[2]; // A return array that will contain all player's deltas

		if (killer == null) { // If the player died due to environmental causes, just kFactor/2
			deltas[0] = kFactor / 2;
			deltas[1] = 0;
			return deltas;
		}

		double playerRating = player.getElo(GameType.FFA);
		double killerRating = killer.getElo(GameType.FFA);

		double totalRating = playerRating + killerRating; // Total rating is the combination of both player's averages

		// Expected Outcome approaches 1 if the player is more likely to win, approaches 0 if they're less likely
		double expectedOutcome = playerRating / totalRating;

		// Actual Outcome is 0 because the player lost
		double actualOutcome = 0F;

		// Used as a multiplier on kFactor
		double finalScore = actualOutcome - expectedOutcome;// finalScore approaches 0 if the player was less likely to win
															// finalScore approaches -1 if the player was more likely to win
		// Keep the deltas within the kFactor range
		deltas[0] = (float) limitValue(kFactor * finalScore, -kFactor, kFactor); // loser
		deltas[1] = (float) limitValue(kFactor * -finalScore, -kFactor, kFactor); // winner, finalScore is negated because the winner will receive the opposite of the loser

		return deltas;
	}

	/*
	 * Keep this here just in case: double pRating = 1.0 * (int) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("2v2Rating"); double p2Rating = 1.0 (int)
	 * Corrupted.getDatabaseCache().get(p2.getUniqueId().toString()).get("2v2Rating"); double deathRating = 1.0 (int) Corrupted.getDatabaseCache().get(death.getUniqueId().toString()).get("2v2Rating");
	 * double death2Rating = 1.0 (int) Corrupted.getDatabaseCache().get(death2.getUniqueId().toString()).get("2v2Rating"); double deathAVGRating = (deathRating + death2Rating) / 2.0; double
	 * killAVGRating = (pRating + p2Rating) / 2.0; double pExpectedScore = (pRating) / (pRating + deathAVGRating); double p2ExpectedScore = (p2Rating) / (p2Rating + deathAVGRating); double
	 * deathExpectedScore = (deathRating) / (deathRating + killAVGRating); double death2ExpectedScore = (death2Rating) / (death2Rating + killAVGRating); double killActualScore = 1.0; double
	 * deathActualScore = 0.0; double pScore = killActualScore - pExpectedScore; double p2Score = killActualScore - p2ExpectedScore; double deathScore = deathActualScore - deathExpectedScore; double
	 * death2Score = deathActualScore - death2ExpectedScore; double newpRating = (pRating + (32 * (pScore) < 0 ? 0 : 32 * (pScore))); double newp2Rating = (p2Rating + (32 * (p2Score) < 0 ? 0 : 32 *
	 * (p2Score))); double newdeathRating = (deathRating + (32 * (deathScore) > 32 ? 32 : 32 * (deathScore))); double newdeath2Rating = (death2Rating + (32 * (death2Score) > 32 ? 32 : 32 *
	 * (death2Score)));
	 */
	public static double getArmorReductionPercentage(PlayerModel player) {
		ItemStack helmet = player.getPlayer().getInventory().getLeggings();
		ItemStack chestplate = player.getPlayer().getInventory().getChestplate();
		ItemStack leggings = player.getPlayer().getInventory().getLeggings();
		ItemStack boots = player.getPlayer().getInventory().getBoots();

		double totalReduction = 0D;

		// Helmet
		switch (helmet.getType())
		{
		case LEATHER_HELMET:
			totalReduction += .04;
			break;
		case GOLD_HELMET:
			totalReduction += .08;
			break;
		case CHAINMAIL_HELMET:
			totalReduction += .08;
			break;
		case IRON_HELMET:
			totalReduction += .08;
			break;
		case DIAMOND_HELMET:
			totalReduction += .12;
			break;
		default:
			break;
		}

		// Chest plate
		switch (chestplate.getType())
		{
		case LEATHER_CHESTPLATE:
			totalReduction += .12;
			break;
		case GOLD_CHESTPLATE:
			totalReduction += .20;
			break;
		case CHAINMAIL_CHESTPLATE:
			totalReduction += .20;
			break;
		case IRON_CHESTPLATE:
			totalReduction += .24;
			break;
		case DIAMOND_CHESTPLATE:
			totalReduction += .32;
			break;
		default:
			break;
		}

		// Leggings
		switch (leggings.getType())
		{
		case LEATHER_LEGGINGS:
			totalReduction += .8;
			break;
		case GOLD_LEGGINGS:
			totalReduction += .12;
			break;
		case CHAINMAIL_LEGGINGS:
			totalReduction += .16;
			break;
		case IRON_LEGGINGS:
			totalReduction += .20;
			break;
		case DIAMOND_LEGGINGS:
			totalReduction += .24;
			break;
		default:
			break;
		}
		// Boots
		switch (boots.getType())
		{
		case LEATHER_BOOTS:
			totalReduction += .04;
			break;
		case GOLD_BOOTS:
			totalReduction += .04;
			break;
		case CHAINMAIL_BOOTS:
			totalReduction += .04;
			break;
		case IRON_BOOTS:
			totalReduction += .08;
			break;
		case DIAMOND_BOOTS:
			totalReduction += .12;
			break;
		default:
			break;
		}

		return 1D - totalReduction;
	}
}
