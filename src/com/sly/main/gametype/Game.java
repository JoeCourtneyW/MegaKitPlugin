package com.sly.main.gametype;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import com.sly.main.Server;
import com.sly.main.player.PlayerModel;
import com.sly.main.resources.MathUtil;

public class Game
{

	private Arena arena;
	private GameType gameType;
	private Collection<PlayerModel> blueTeam;
	private Collection<PlayerModel> yellowTeam;
	private boolean isOver;
	private TeamColor winners;
	private UUID id;

	public Game(Arena arena, GameType gameType, Collection<PlayerModel> blueTeam, Collection<PlayerModel> yellowTeam) {
		this.arena = arena;
		this.gameType = gameType;
		this.blueTeam = blueTeam;
		this.yellowTeam = yellowTeam;
		this.winners = null;
		this.isOver = false;
		this.id = UUID.randomUUID();
		startGame();
	}

	private void startGame() {
		// TODO: Better spectator handling
		arena.setIsInUse(true);
		for (PlayerModel player : getAllPlayers()) {
			if (!player.isOnline()) // Make sure the player hasn't logged out and exists
				continue;
			TeamColor team = getTeamColor(player); // Get the team the player is on
			player.setGame(this); // Set the player as in game to this instance
			player.getPlayer().setFallDistance(0); // Make sure they don't die from fall damage while teleporting in
			player.getPlayer().teleport(getArena().getTeamSpawn(team));
			player.setFullHealth(); // Reset the players health

			StringJoiner[] playerList = playerListToString(team.getOpposite());

			player.sendPrefixedMessage("§eYou have been matched up against §b" + playerList[0].toString() + "§e " + "("
					+ playerList[1].toString() + ")");
		}
	}

	public Arena getArena() {
		return arena;
	}

	public GameType getGameType() {
		return gameType;
	}

	// Returns array, index[0] = names, index[1] = ratings
	public StringJoiner[] playerListToString(TeamColor team) {
		StringJoiner[] lists = { new StringJoiner("§c and §b"), new StringJoiner("§e, §b") };
		Iterator<PlayerModel> players = getTeam(team).iterator(); // Thank god for TeamColor enum
		while (players.hasNext()) { // Loop through the players to build info messages
			PlayerModel player = players.next();
			lists[0].add(player.getName());
			lists[1].add("" + player.getElo(gameType));
		}

		return lists;
	}

	// Returns arraylist of all players in the game
	public List<PlayerModel> getAllPlayers() {
		List<PlayerModel> all = new ArrayList<PlayerModel>();
		all.addAll(getTeam(TeamColor.BLUE));
		all.addAll(getTeam(TeamColor.YELLOW));
		return all;
	}

	// Returns list of players on a team
	public Collection<PlayerModel> getTeam(TeamColor t) {
		if (t == TeamColor.BLUE) {
			return Collections.unmodifiableCollection(blueTeam);
		} else {
			return Collections.unmodifiableCollection(yellowTeam);
		}
	}

	public TeamColor getTeamColor(PlayerModel p) {
		if (blueTeam.contains(p)) {
			return TeamColor.BLUE;
		} else if (yellowTeam.contains(p)) {
			return TeamColor.YELLOW;
		} else {
			return null;
		}
	}

	public boolean isTeamDead(TeamColor t) {
		for (PlayerModel player : getTeam(t)) {
			if (!(player.isSpectating() || player.getPlayer().isDead())) {
				return false;
			}
		}
		return true;
	}

	public boolean isOver() {
		return isOver;
	}

	public UUID getID() {
		return id;
	}

	public void finish(TeamColor winningTeam) {
		this.winners = winningTeam;
		float[] deltas = MathUtil.getEloChangeInGame(this, winningTeam);
		PlayerModel player;
		for (int i = 0; i < deltas.length; i++) { // deltas align with these indices
			player = getAllPlayers().get(i);
			String verb = ""; // Verb that corresponds to win or loss
			int coins = 0; // Grabs the amount of coins for winning or losing this gameType
			if (getTeamColor(player) == winningTeam) { // They are on the winning team
				verb = "beating";
				coins = player.getEconomy().addMultipliedCoins(getGameType().getWinReward()); // adds the actual coins
			} else {
				verb = "losing to";
				coins = player.getEconomy().addMultipliedCoins(getGameType().getLossReward());
			}
			StringJoiner[] playerList = playerListToString(winningTeam.getOpposite()); // To display chat messages containing opponents

			player.updateElo(getGameType(), Math.round(deltas[i]));

			String rating = String.format("%+d", MathUtil.roundTwoPoints(deltas[i]));
			player.sendPrefixedMessage("§e" + rating + " " + getGameType().getDisplayName() + " rating §cfor " + verb
					+ " §b" + playerList[0] + "§c.");

			if (coins > 0) // Don't let them down and give them 0 coins :(
				player.sendPrefixedMessage("§e+" + coins + " coins §cfor " + verb + " §b" + playerList[0] + "§c.");

			player.pushRowToDatabase(); // Make sure to update backend + hud values
			player.updateScoreboard();
		}
		dissolve(60);
	}

	private void timeoutPlayers(long delay) {
		if (delay > 0) { //If there's a delay, let the winners fly during it
			for (PlayerModel winner : getTeam(winners)) {
				winner.getPlayer().setNoDamageTicks(100000);
				winner.getPlayer().setAllowFlight(true);
				winner.getPlayer().setFlying(true);
			}
		}
		
		new BukkitRunnable() { // After delay, sendToSpawn()
			public void run() {
				for (PlayerModel p : getAllPlayers()) {
					p.sendToSpawn();
				}
			}
		}.runTaskLater(Server.getInstance(), delay);
	}

	public void dissolve(long delay) {
		timeoutPlayers(delay);
		
		isOver = true;
		
		for (PlayerModel p : getAllPlayers()) {
			p.setGame(null); // Remove this game from the players' models
		}
		
		arena.setIsInUse(false);

	}
}
