package com.sly.main.listeners;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.sly.main.Server;
import com.sly.main.enums.AccessMode;
import com.sly.main.enums.Group;
import com.sly.main.enums.Trail;
import com.sly.main.gametype.GameType;
import com.sly.main.gametype.Squad;
import com.sly.main.kits.Kits;
import com.sly.main.player.PlayerModel;
import com.sly.main.resources.InventoryUtil;
import com.sly.main.resources.LocationUtil;
import com.sly.main.server.Chat;

@SuppressWarnings("deprecation")
public class ServerListeners implements Listener
{

	@EventHandler
	public void clearClassOnDeath(PlayerDeathEvent event) {
		PlayerModel player = PlayerModel.getPlayerModel(event.getEntity());

		event.setDroppedExp(0);
		event.getDrops().clear();
		event.setDeathMessage("");

		player.resetEnergy();

	}

	@EventHandler
	public void itemsOnRespawn(PlayerRespawnEvent event) { // Checks
		PlayerModel p = PlayerModel.getPlayerModel(event.getPlayer());
		if (p.isInGame() && p.getGame().getGameType() == GameType.TWOvTWO) {
			event.setRespawnLocation(p.getGame().getArena().getSpectatorSpawn());
			p.setSpectatingInventory();
			p.setSpectating(true);
		} else {
			p.setDefaultInventory();
			p.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ZOMBIE_UNFECT, 1, 1);
			event.setRespawnLocation(p.getSpawnPoint());
			if (p.getPlayer().hasPermission("Taken.FlyInlobby")) {
				p.getPlayer().setAllowFlight(true);
				p.getPlayer().setFlying(true);
			}
		}
	}

	@EventHandler
	public void onExpGain(EntityDeathEvent event) { // Cancels dropping exp orbs on death
		event.setDroppedExp(0);
	}

	@EventHandler
	public void disableDropItem(PlayerDropItemEvent event) { // Cancels dropping items
		event.setCancelled(true);
	}

	@EventHandler
	public void spawnOnJoin(PlayerJoinEvent event) { // Sends the player an MOTD, login sound, and sends them to spawn
		PlayerModel p = PlayerModel.getPlayerModel(event.getPlayer());
		p.sendMessage("§f§l                                Patch " + Server.getInstance().getDescription().getVersion()
				+ " Beta");
		p.sendMessage("§7§o                         http://takengaming.buycraft.net/");
		p.sendMessage("§f§l                             Developed by SlyVitality");
		p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.ORB_PICKUP, 1, (float) 0.5);
		p.sendToSpawn();
	}

	@EventHandler
	public void setScoreboard(PlayerJoinEvent event) { // Gives the player an updated scoreboard when they join the server
		PlayerModel.getPlayerModel(event.getPlayer()).updateScoreboard();

	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {

		}
	}

	@EventHandler
	public void onRegen(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {

		}
	}

	// TODO: Move this into a timer instead of playermoveevent, uses up too many resources
	@EventHandler
	public void onTrailMove(PlayerMoveEvent event) {
		PlayerModel player = PlayerModel.getPlayerModel(event.getPlayer());
		if (event.getTo().getBlockX() != event.getFrom().getBlockX()
				|| event.getTo().getBlockY() != event.getFrom().getBlockY()
				|| event.getTo().getBlockZ() != event.getFrom().getBlockZ()) { // Only use event if they moved to a diff coord

			if (event.getTo().getBlockY() < 2) { // If they are falling into the void, teleport them to spawn
				player.getPlayer().setFallDistance(0);
				event.setTo(player.getSpawnPoint());
				return;
			}
			if (Trail.getSelectedTrail(player) != Trail.NONE) { // If they have a trail active
				if (player.isModMode()) // Don't spawn their trail in mod mode, supposed to be invisible
					return;
				Trail.getSelectedTrail(player).getParticleEffect().sendToAllPlayers(
						event.getPlayer().getLocation().clone().add(0, 0.2, 0), 0.13F,
						Trail.getSelectedTrail(player).getParticleAmount());
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST) // Make this lowest so that it gets called FIRST
	public void createConfigOnJoin(final PlayerJoinEvent event) {
		final String ip = event.getPlayer().getAddress().getAddress().getHostAddress();
		final String name = event.getPlayer().getName();
		final PlayerModel p;
		if (!PlayerModel.playerExists(event.getPlayer().getUniqueId())) { // First time the player has joined
			p = PlayerModel.newPlayer(event.getPlayer());
			p.updateScoreboard();
			Chat.broadcastPrefixedMessage(
					"§7It is §b" + p.getName() + "§7's first time playing Taken Kits. Welcome them!");
		} else { // They exist, update their IP and Username
			p = PlayerModel.getPlayerModel(event.getPlayer());
			p.getDatabaseCell("IP").setValue(ip);
			p.getDatabaseCell("NAME").setValue(name);
			p.pushRowToDatabase(); // Update the ip and name
			p.updateScoreboard();
		}

	}

	// Don't let players in when there's a fucking access mode change dumbass
	@EventHandler
	public void onAccessMode(PlayerPreLoginEvent event) {
		AccessMode accessMode = Server.getMode();

		// Make sure it isn't a new player, if it is, we can't handle it
		if (PlayerModel.playerExists(event.getUniqueId()) && accessMode.equals(AccessMode.ALL)) {
			event.setResult(Result.KICK_OTHER);
			event.setKickMessage("§eServer Access Mode is set to: §b" + Server.getMode().name());
		}

		PlayerModel player = PlayerModel.getPlayerModel(event.getUniqueId());

		switch (Server.getMode())
		{
		case DEV:
			if (Group.DEV.isInGroupInheritance(player))
				return;
		case BUILDER:
			if (Group.BUILDER.isInGroupInheritance(player))
				return;
		case STAFF:
			if (Group.HELPER.isInGroupInheritance(player) || Group.BUILDER.isInGroupInheritance(player)) // Staff includes builders, but builder does not include staff
				return;
		case DONOR:
			if (Group.SUB.isInGroupInheritance(player))
				return;
		case ALL:
			return;
		}

		event.setResult(Result.KICK_OTHER);
		event.setKickMessage("§eServer Access Mode is set to: §b" + accessMode.name());
	}

	@EventHandler
	public void onPermissionLeave(PlayerQuitEvent event) {
		PlayerModel player = PlayerModel.getPlayerModel(event.getPlayer());
		Group.unregisterPlayerPermissions(player);
		event.setQuitMessage("§7[§c§l-§7] §c" + player.getName());
	}

	@EventHandler
	public void onPermissionKick(PlayerKickEvent event) {
		PlayerModel player = PlayerModel.getPlayerModel(event.getPlayer());
		Group.unregisterPlayerPermissions(player);
		event.setLeaveMessage("§7[§c§l-§7] §c" + player.getName());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPermissionJoin(final PlayerJoinEvent event) {
		PlayerModel player = PlayerModel.getPlayerModel(event.getPlayer());

		Group.registerPlayerPermissions(player);
		event.setJoinMessage("§7[§a§l+§7] §a" + player.getName());
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory().getTitle().contains("Slot Machine")) {
			event.setCancelled(true);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChatFormat(AsyncPlayerChatEvent event) {
		PlayerModel player = PlayerModel.getPlayerModel(event.getPlayer());
		if (Chat.isServerMuted()) {
			if (!player.getPlayer().hasPermission("Taken.STFU.bypass")) {
				player.sendPrefixedMessage("Chat is currently turned off");
				event.setCancelled(true);
			}

		}

		// In 1.8, longer messages will crash clients, as will percent signs?
		String messageOutput = event.getMessage().replaceAll("%", "percent");
		if (messageOutput.length() > 100)
			messageOutput = messageOutput.substring(0, 99);

		if (player.isStaffChat()) {
			Chat.broadcastMessage("§7[§eSTAFF§7] " + player.getFormattedChatMessage(messageOutput.replaceAll("&", "§")),
					"Taken.SC");
			event.setCancelled(true);
		} else {
			event.setFormat(player.getFormattedChatMessage(messageOutput));
		}
	}

	@EventHandler
	public void onHunger(FoodLevelChangeEvent event) {
		event.setFoodLevel(20);
	}

	@EventHandler
	public void onBlockChange(EntityChangeBlockEvent event) {
		if (event.getEntityType() == EntityType.WITHER_SKULL) {
			event.setCancelled(true);
		}
		if (event.getEntityType() == EntityType.FIREBALL) {
			event.setCancelled(true);
		}
		if (event.getEntityType() == EntityType.SMALL_FIREBALL) {
			event.setCancelled(true);
		}
	}

	public ArrayList<PlayerModel> preModMode = new ArrayList<PlayerModel>();

	@EventHandler
	public void onModMode(PlayerInteractEvent event) {
		PlayerModel p = PlayerModel.getPlayerModel(event.getPlayer());
		if (event.getItem() != null && event.getItem().getType() == Material.EYE_OF_ENDER
				&& event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()
				&& event.getItem().getItemMeta().getDisplayName().contains("MOD MODE")) {
			event.setCancelled(true);
			if (p.isModMode() || preModMode.contains(event.getPlayer().getName())) {
				p.setModMode(false);
				p.getPlayer().teleport(p.getSpawnPoint());
				p.setDefaultInventory();
				preModMode.remove(p);
				p.sendPrefixedMessage("§eYou have left Mod Mode");
				p.sendPrefixedMessage("§eIf you decided the player is hacking, use /tempban [player] [time] [reason]");
				for (Player players : Bukkit.getOnlinePlayers()) {
					players.showPlayer(p.getPlayer());
				}
			} else {
				preModMode.add(p);
				p.sendPrefixedMessage("§eYou are now in Mod Mode");
				p.sendPrefixedMessage("§eType the player's name you want to spectate in chat");
				p.setModModeInventory();
			}
		}
	}

	@EventHandler
	public void onModmodeChat(AsyncPlayerChatEvent event) {
		final PlayerModel p = PlayerModel.getPlayerModel(event.getPlayer());
		if (preModMode.contains(p)) {
			event.setCancelled(true);
			if (Bukkit.getPlayer(event.getMessage()) != null) { // Make sure the player exists and is online
				Player spectate = Bukkit.getPlayer(event.getMessage());
				if (spectate.getUniqueId().equals(p.getUUID())) {
					p.sendPrefixedMessage("§eYou can't spectate yourself");
					return;
				}
				p.getPlayer().teleport(LocationUtil.getSafeDestination(spectate.getLocation()));
				p.setModMode(true);
				PlayerModel.getPlayerModel(spectate).getWatchedBy().add(p);
				p.sendPrefixedMessage("§eYou are now spectating " + spectate.getName());
				for (PlayerModel watchers : PlayerModel.getPlayerModel(spectate).getWatchedBy()) {
					if (!watchers.getPlayer().getName().equalsIgnoreCase(p.getPlayer().getName())) {
						p.sendPrefixedMessage("§b" + watchers.getPlayer().getName() + " §eis also spectating §b"
								+ spectate.getName());
						watchers.sendPrefixedMessage(
								"§b" + p.getPlayer().getName() + " §eis now spectating §b" + spectate.getName());
					}
				}
				new BukkitRunnable() {
					public void run() {
						p.getPlayer().setGameMode(GameMode.CREATIVE);
					}
				}.runTaskLater(Server.getInstance(), 10);
				for (Player online : Bukkit.getOnlinePlayers()) {
					online.hidePlayer(p.getPlayer());
				}
			} else {
				p.sendPrefixedMessage("§cPlayer not found!");
			}
		}
	}

	@EventHandler
	public void onTeleportModMode(PlayerTeleportEvent event) {
		PlayerModel player = PlayerModel.getPlayerModel(event.getPlayer());
		for (PlayerModel watchers : player.getWatchedBy()) {
			watchers.getPlayer().teleport(event.getTo());
		}
	}

	@EventHandler
	public void onLeaveModMode(PlayerQuitEvent event) {
		PlayerModel player = PlayerModel.getPlayerModel(event.getPlayer());
		if (player.getWatchedBy().size() > 0) {
			for (PlayerModel watchers : player.getWatchedBy()) {
				watchers.setModMode(false);
				watchers.setWatching(null);
				watchers.sendPrefixedMessage("§cYour target has logged off");
				watchers.getPlayer().teleport(watchers.getSpawnPoint());
				watchers.setDefaultInventory();
				preModMode.remove(player);
				watchers.sendPrefixedMessage("§eYou have left Mod Mode");
				for (Player p : Bukkit.getOnlinePlayers()) {
					p.showPlayer(event.getPlayer());
				}
			}
			player.getWatchedBy().clear();
		}
		if (player.isModMode()) {
			player.setModMode(false);
			player.setWatching(null);
			player.getWatchedBy().clear();
			for (Player p : Bukkit.getOnlinePlayers()) {
				p.showPlayer(event.getPlayer());
			}
		}
		preModMode.remove(player);
	}

	@EventHandler
	public void onDeathModMode(PlayerDeathEvent event) {
		PlayerModel dead = PlayerModel.getPlayerModel(event.getEntity());
		if (dead.getWatchedBy().size() > 0) {
			for (PlayerModel watchers : dead.getWatchedBy()) {
				watchers.sendPrefixedMessage("§cYour target has died");
				watchers.getPlayer().teleport(dead.getSpawnPoint());
			}
		}
	}

	/*
	 * @EventHandler public void onNickRecieve(AsyncPlayerReceiveNameTagEvent event){ if(Corrupted.getNickedPlayers().containsKey(event.getNamedPlayer().getName()) &&
	 * event.getNamedPlayer().hasMetadata("Class") && event.getNamedPlayer().getWorld().getName().contains("Nube")){ if(event.getPlayer().hasPermission("Taken.staff")) return;
	 * event.setTag(Corrupted.getNickedPlayers().get(event.getNamedPlayer().getName())); } }
	 */
	@EventHandler
	public void onHideJoin(PlayerJoinEvent event) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (PlayerModel.getPlayerModel(p).isModMode()) {
				event.getPlayer().hidePlayer(p);
			}
		}
	}

	@EventHandler
	public void preventSpawnKill(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			PlayerModel damager = PlayerModel.getPlayerModel((Player) event.getDamager());
			PlayerModel entity = PlayerModel.getPlayerModel((Player) event.getEntity());
			if (damager.getKit() == Kits.NONE || entity.getKit() == Kits.NONE) {
				event.setDamage(0);
				event.setCancelled(true);
			}
		}
	}

	/*
	 * TODO: This was lit, try and reimplement it
	 * 
	 * @EventHandler public void spawnOnJoin(final PlayerJoinEvent event){ //if(((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() < 47){ Location l = new
	 * Location(Bukkit.getWorld("Nube"), -549.5, 101, -552.5, 206.62F, 1.82F); event.getPlayer().teleport(l); Corrupted.getInstance().setDefaultInventory(event.getPlayer());
	 * event.getPlayer().setLevel(0); //}else{ /** Prevent people from joining the server using 1.8 (Applicable on protocol version to limit compatability issues while still retaining plugin versions)
	 */
	/*
	 * new BukkitRunnable(){ public void run(){ event.getPlayer().kickPlayer("§cYou must be on 1.7 to play on this server!"); } }.runTaskLater(Corrupted.getInstance(), 30);
	 * 
	 * } }
	 */
	@EventHandler
	public void onJoinDelayTeleportToSpawn(final PlayerJoinEvent event) {
		new BukkitRunnable() {
			public void run() {
				PlayerModel.getPlayerModel(event.getPlayer()).sendToSpawn();
			}
		}.runTaskLater(Server.getInstance(), 15);

	}

	@EventHandler
	public void preventCraft(CraftItemEvent event) {
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void sendToSpawnOnRespawn(PlayerRespawnEvent event) {
		PlayerModel player = PlayerModel.getPlayerModel(event.getPlayer());
		event.setRespawnLocation(player.getSpawnPoint());
		player.sendToSpawn();
	}

	// TODO: Make this a little more dynamic and shit
	@EventHandler
	public void onPing(ServerListPingEvent event) {
		event.setMaxPlayers(90);
		if (event.getAddress().getHostAddress().contains("")) { // Check which taken server they're accessing
			event.setMotd("§eTaken Gaming §7| §f§lPatch " + Server.getInstance().getDescription().getVersion());
		} else {
			event.setMotd("§eTaken Dev Server §7| §f§lPatch " + Server.getInstance().getDescription().getVersion()
					+ "\n§b§lMain Server: §c§nPlay.TakenGaming.Net");
		}
	}

	@EventHandler
	public void onCreateSquad(PlayerInteractEvent event) {
		if (InventoryUtil.isItemSimilar(event.getItem(), "Create Squad", Material.NAME_TAG)) {
			PlayerModel p = PlayerModel.getPlayerModel(event.getPlayer());
			if (!p.isInSquad()) {
				if (p.getKit() == Kits.NONE) {
					Squad squad = new Squad(p);
					p.setSquad(squad);
					p.sendPrefixedMessage("§eYou have created a squad");
				} else {
					p.sendPrefixedMessage("You must be at spawn to create a squad");
				}
			} else {
				p.sendPrefixedMessage("You are already in a squad");
			}
		}
	}
}
