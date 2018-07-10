package com.sly.main.gametype;

import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.sly.main.Server;
import com.sly.main.guis.kitselector.KitSelector;
import com.sly.main.player.PlayerModel;
import com.sly.main.resources.InventoryUtil;
import com.sly.main.resources.MathUtil;

public class GameListeners implements Listener
{

	@EventHandler
	public void finishGameOnDeath(PlayerDeathEvent event) {
		final PlayerModel loser = PlayerModel.getPlayerModel(event.getEntity());
		if (loser.isInGame()) { // Make sure they're actually in a game before handling
			final Game g = loser.getGame();
			TeamColor losingTeam = g.getTeamColor(loser);
			if (g.getGameType().equals(GameType.TWOvTWO)) { // Handle 2v2 sending player to spectator box

				// Set the player as a spectator and send them to the box
				loser.setSpectating(true);
				loser.setFullHealth();
				loser.getPlayer().teleport(g.getArena().getSpectatorSpawn());
				loser.setSpectatingInventory();

				if (!g.isTeamDead(losingTeam)) // If there is still a player alive, don't end the game
					return;
			}
			g.finish(losingTeam.getOpposite()); // Handles updating player elo and giving player coins
		}
	}

	/**
	 * TODO: Need to scale rating change scale = (1000/rating) 4000 = 0.25 2000 = 0.5 1000 = 1.0 500 = 2.0
	 * 
	 * double killScale = (1000 / killRating); double deathScale = (1000 / deathRating); killRatingToAdd *= killScale; deathRatingToAdd *= deathScale;
	 */
	@EventHandler
	public void finishFFAOnDeath(PlayerDeathEvent event) {
		PlayerModel player = PlayerModel.getPlayerModel(event.getEntity());
		PlayerModel killer = null;
		if (player.getPlayer().getKiller() != null)
			killer = PlayerModel.getPlayerModel(player.getPlayer().getKiller());
		if (!player.isInGame()) { // Make sure they died while not in a game, AKA: FFA

			float[] deltas = MathUtil.getEloChangeInFFA(player, killer);
			String rating;

			if (killer != null) {
				killer.updateElo(GameType.FFA, Math.round(deltas[1]));

				rating = String.format("%+d", MathUtil.roundTwoPoints(deltas[1]));
				player.sendPrefixedMessage("§e" + rating + " " + GameType.FFA.getDisplayName()
						+ " rating §cfor beating §b" + player.getName() + "§c.");

				int coins = killer.getEconomy().addMultipliedCoins(GameType.FFA.getWinReward());
				if (coins > 0) // Don't let them down and tell them they get 0 coins :(
					player.sendPrefixedMessage("§e+" + coins + " coins §cfor beating §b" + player.getName() + "§c.");

				killer.getKit().mutateKills(killer, k -> k + 1);
				killer.getDatabaseCell("FFA_KILLS").mutateValue(k -> k + 1); // Mutate kit and general kills

				killer.pushRowToDatabase();
				killer.updateScoreboard();

			}
			player.updateElo(GameType.FFA, Math.round(deltas[1]));

			rating = String.format("%+d", MathUtil.roundTwoPoints(deltas[1]));
			player.sendPrefixedMessage("§e" + rating + " " + GameType.FFA.getDisplayName()
					+ " rating §cfor losing to §b" + player.getName() + "§c.");
			player.getKit().mutateDeaths(player, d -> d + 1);
			player.getDatabaseCell("FFA_DEATHS").mutateValue(d -> d + 1); // Increment deaths

			player.pushRowToDatabase(); // Make sure to update backend + hud values
			player.updateScoreboard();
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void noDamagerSquadmate(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			PlayerModel entity = PlayerModel.getPlayerModel((Player) event.getEntity());
			PlayerModel damager;
			if (event.getDamager() instanceof Player) { // If the damager attacked through melee
				damager = PlayerModel.getPlayerModel((Player) event.getDamager());
			} else if (event.getDamager() instanceof Projectile) { // If the damager attacked through projectile
				ProjectileSource shooter = ((Projectile) event.getDamager()).getShooter();
				if (shooter instanceof Player)
					damager = PlayerModel.getPlayerModel((Player) shooter);
				else
					return;
			} else { // If the damage was not of a player origin
				return;
			}

			if (entity.sameSquad(damager)) { // Don't let squadmates damage eachother
				event.setCancelled(true);
				event.setDamage(0D);
			}
		}
	}

	@EventHandler
	public void joinQueue(PlayerInteractEvent event) {
		PlayerModel player = PlayerModel.getPlayerModel(event.getPlayer());
		if (InventoryUtil.isItemSimilar(event.getItem(), "Ranked 1v1 Queue", Material.IRON_SWORD)) {
			if (!player.isInSquad()) {
				KitSelector selector = new KitSelector(player);
				selector.openGUI(GameType.ONEvONE);
			} else {
				player.sendPrefixedMessage("You can not queue for 1v1 while in a squad. Use /squad disband");
			}
		} else if (InventoryUtil.isItemSimilar(event.getItem(), "Ranked 2v2 Queue", Material.DIAMOND_SWORD)) {
			if (player.isInSquad() && player.isSquadLeader() && player.getSquad().readyToQueue()) { // Make sure player is in a squad, owns it, and has a squadmate who is ready
				for (PlayerModel squad : player.getSquad().getSquadMembers()) { // Show all squad members the queue GUI
					KitSelector selector = new KitSelector(squad);
					selector.openGUI(GameType.TWOvTWO);
				}
			} else {
				player.sendPrefixedMessage("You must be in a squad to queue for 2v2");
			}
		}
	}

	@EventHandler
	public void leaveQueue(PlayerInteractEvent event) {
		PlayerModel player = PlayerModel.getPlayerModel(event.getPlayer());
		if (InventoryUtil.isItemSimilar(event.getItem(), "Leave Queue", Material.REDSTONE)) { // If they used the leave queue item
			if (Server.getQueue(GameType.ONEvONE).isQueued(Arrays.asList(player))) {
				Server.getQueue(GameType.ONEvONE).removeFromQueue(Arrays.asList(player));
			} else if (player.isInSquad()
					&& Server.getQueue(GameType.TWOvTWO).isQueued(player.getSquad().getSquadMembers())) {
				Server.getQueue(GameType.TWOvTWO).removeFromQueue(player.getSquad().getSquadMembers());
			} else {
				player.setDefaultInventory();
			}
		}
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		PlayerModel player = PlayerModel.getPlayerModel(event.getPlayer());
		if (player.isInGame()) {
			player.getPlayer().setHealth(0D); // This will force the EntityDeathEvent, calling the proper code to finish and dissolve the game
		}
		if (player.isInSquad()) {
			if (Server.getQueue(GameType.TWOvTWO).isQueued(player.getSquad().getSquadMembers()))
				Server.getQueue(GameType.TWOvTWO).removeFromQueue(player.getSquad().getSquadMembers());
			Squad squad = player.getSquad();
			if (squad.getLeader().equals(player)) {
				for (PlayerModel squadmate : squad.getSquadMembers()) {
					squadmate.setSquad(null);
					squadmate.sendPrefixedMessage(
							"§eYour squad has been dissolved because §b" + player.getName() + " §ehas logged out");
				}
				squad.dissolve();
			} else {
				squad.removeSquadMate();
				squad.getLeader().sendPrefixedMessage("§eYour squadmate has logged out");
			}

		} else {
			if (Server.getQueue(GameType.ONEvONE).isQueued(Arrays.asList(player)))
				Server.getQueue(GameType.ONEvONE).removeFromQueue(Arrays.asList(player));
		}
	}

	@EventHandler
	public void onInvitationAnswer(InventoryClickEvent event) {
		if (event.getInventory() == null || !event.getInventory().getTitle().contains("Invitation"))
			return;
		PlayerModel player = PlayerModel.getPlayerModel((Player) event.getWhoClicked());
		if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.WOOL) {
			if (event.getCurrentItem().getDurability() == (short) 5) {
				if (player.getInvitedBy() != null && player.getInvitedBy().isOnline()) {
					PlayerModel inviter = player.getInvitedBy();
					if (inviter.isSquadLeader()) {
						if (inviter.getSquad().isFull()) {

							inviter.getSquad().setSquadMate(player);

							inviter.sendPrefixedMessage(
									"§b" + event.getWhoClicked().getName() + " §ehas joined your squad!");
							player.sendPrefixedMessage("§eYou have joined §b" + inviter.getName() + "§e's squad!");
						} else {
							player.sendPrefixedMessage("The person's squad is full");
						}
					} else {
						player.sendPrefixedMessage("The person who invited you does not own a squad!");
					}
				} else {
					player.sendPrefixedMessage("The person who invited you has logged offline!");
				}
			} else if (event.getCurrentItem().getDurability() == (short) 14) {
				// Lmao, they actually declined the invitation, ha
				if (player.getInvitedBy() != null && player.getInvitedBy().isOnline())
					player.getInvitedBy()
							.sendPrefixedMessage("§b" + player.getName() + " has declined your invitation");

			}
			player.setInvitedBy(null);
			event.getWhoClicked().closeInventory();
		}
	}

}
