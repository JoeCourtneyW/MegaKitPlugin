//    ______ _       _     _     ____  _ _                     _    _____ _       
//   |  ____(_)     | |   | |   |  _ \(_) |                   | |  / ____| |      
//   | |__   _  __ _| |__ | |_  | |_) |_| |_    __ _ _ __   __| | | (___ | |_   _ 
//   |  __| | |/ _` | '_ \| __| |  _ <| | __|  / _` | '_ \ / _` |  \___ \| | | | |
//   | |____| | (_| | | | | |_  | |_) | | |_  | (_| | | | | (_| |  ____) | | |_| |
//   |______|_|\__, |_| |_|\__| |____/|_|\__|  \__,_|_| |_|\__,_| |_____/|_|\__, |
//              __/ |                                                        __/ |
//             |___/                                                        |___/ 

package com.sly.main;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.ScoreboardManager;

import com.sly.main.commands.PlayerCommands;
import com.sly.main.commands.ServerCommands;
import com.sly.main.database.Database;
import com.sly.main.database.DatabaseConnector;
import com.sly.main.database.tables.ArenaTable;
import com.sly.main.database.tables.KitCustomizerTable;
import com.sly.main.database.tables.PlayerDataTable;
import com.sly.main.economy.Booster;
import com.sly.main.enums.AccessMode;
import com.sly.main.enums.Group;
import com.sly.main.gametype.Arena;
import com.sly.main.gametype.GameListeners;
import com.sly.main.gametype.GameType;
import com.sly.main.gametype.Queue;
import com.sly.main.guis.IconMenus;
import com.sly.main.guis.kitselector.KitSelectorListener;
import com.sly.main.kits.GlobalKitListener;
import com.sly.main.kits.Kit;
import com.sly.main.listeners.GameplayListeners;
import com.sly.main.listeners.InventoryListeners;
import com.sly.main.listeners.ServerListeners;
import com.sly.main.listeners.TeleportFix;
import com.sly.main.player.PlayerModel;
import com.sly.main.server.Broadcaster;

import de.slikey.effectlib.EffectManager;

public class Server extends JavaPlugin implements Listener
{
	/**
	 * The main instance of the class Accessed by using getInstance()
	 */
	private static Server plugin;
	/**
	 * Instance of EffectManager API
	 */
	private static EffectManager effectManager;
	/**
	 * All blocks that get placed in survival mode Used to be removed on reloads and restarts
	 * 
	 * @deprecated
	 */
	private static ArrayList<Location> blocks = new ArrayList<Location>();

	private static ArrayList<PlayerModel> playerModels = new ArrayList<PlayerModel>();
	private static Queue queue1v1;
	private static Queue queue2v2;
	private static ScoreboardManager sbm;
	private static IconMenus menus;
	private static AccessMode mode = AccessMode.ALL;
	private static Broadcaster broadcaster;

	private static Database database;
	private static PlayerDataTable playerDataTable;
	private static KitCustomizerTable kitCustomizerTable;
	private static ArenaTable arenaTable;

	public void onEnable() {
		plugin = this;

		database = new DatabaseConnector("Credentials file", 3306, "Cred", "file", "please").connect();

		playerDataTable = new PlayerDataTable(database); // Both of these are SYNCHRYNOUS database accesses
		kitCustomizerTable = new KitCustomizerTable(database);

		// Load all the models from the uuids given by the playerDataTable
		playerDataTable.forEach(playerRow -> playerModels
				.add(PlayerModel.loadPlayerModel(playerRow.getPrimaryKey().getValue().asUUID())));

		System.out.println("[Taken Kits] Player Database values loaded");

		// Load all the kitcustomizers to the playermodel's class'
		kitCustomizerTable
				.forEach(customizerRow -> PlayerModel.getPlayerModel(customizerRow.getPrimaryKey().getValue().asUUID())
						.getKitCustomizer().loadSpots(customizerRow));

		System.out.println("[Taken Kits] KitCustomizer values loaded");

		// Load all the arenas into the Arena list
		arenaTable.forEach(arenaRow -> Arena.loadArena(arenaRow.getPrimaryKey().getValue().asUUID(), arenaRow));

		System.out.println("[Taken Kits] Arenas loaded");

		effectManager = new EffectManager(this);
		System.out.println("[Taken Kits] EffectLib loaded ");

		sbm = Bukkit.getServer().getScoreboardManager();
		System.out.println("[Taken Kits] Scoreboard Manager loaded");

		queue1v1 = new Queue(GameType.ONEvONE);
		queue2v2 = new Queue(GameType.TWOvTWO);
		System.out.println("[Taken Kits] Queues registered");

		// FFAboard = new Leaderboard(DatabaseColumn.FFARATING, 9, new Location(Bukkit.getWorld("Nube"), -513, 102, -613));
		// ONEboard = new Leaderboard(DatabaseColumn.ONERATING, 9, new Location(Bukkit.getWorld("Nube"), -513, 102, -617));
		// TWOboard = new Leaderboard(DatabaseColumn.TWORATING, 9, new Location(Bukkit.getWorld("Nube"), -513, 102, -609));
		System.out.println("[Taken Kits] Leaderboards enabled");

		getOnlinePlayers().forEach(p -> getOnlinePlayers().forEach(p2 -> p.getPlayer().showPlayer(p2.getPlayer())));

		for (PlayerModel p : getOnlinePlayers()) {
			p.updateScoreboard();
			Group.registerPlayerPermissions(p);
		}
		System.out.println("[Taken Kits] Permissions Registered");

		registerPlayerCommands("kys", "Tpallclass", "Spawn", "Arena", "Idea", "Bug", "Sc", "Group", "STFU", "Team",
				"Booster", "Messages", "Stats", "vote", "Nick", "Staff");
		registerServerCommands("broadcast", "ClearDB", "GCM", "Package", "SetAccess", "DB");
		System.out.println("[Taken Kits] Commands registered");

		registerListeners(this, new GameplayListeners(), new ServerListeners(), new GameListeners(),
				new InventoryListeners(), new KitSelectorListener(), new TeleportFix(), new GlobalKitListener());
		System.out.println("[Taken Kits] Listeners registered");

		Kit.registerKits();
		System.out.println("[Taken Kits] Kits registered");

		menus = new IconMenus(this);
		System.out.println("[Taken Kits] Icon Menus loaded");
		/*
		 * ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ConnectionSide.SERVER_SIDE, Packets.Server.NAMED_SOUND_EFFECT) { public void onPacketSending(PacketEvent
		 * event) { PacketContainer packet = event.getPacket(); String soundName = packet.getStrings().read(0); if (soundName.startsWith("random.level")) event.setCancelled(true); } });
		 */
		new BukkitRunnable() {
			public void run() {

				for (PlayerModel p : getOnlinePlayers()) {
					p.getPlayer().getInventory().remove(Material.GLASS_BOTTLE);
					p.getPlayer().updateInventory(); // Make sure the player has no empty potions after drinking them

					p.getPlayer().setCompassTarget(p.getNearestPlayer().getLocation()); // Change the player's compass to point to nearest player

					if (p.isFullEnergy()) { // If they are full energy, create the flashing exp bar effect
						if (p.getPlayer().getExp() == 0F)
							p.getPlayer().setExp(1F);
						else
							p.getPlayer().setExp(0F);
					} else { // If not full energy, make sure the exp bar is empty
						p.getPlayer().setExp(0F);
					}
				}
			}
		}.runTaskTimer(plugin, 10, 10);
		new BukkitRunnable() {
			public void run() {
				for (PlayerModel p : getPlayers(player -> player.isOnline() && player.hasBooster())) {// If they are online, they are using their booster
					p.tickBoosterTime(); // Decrement
					if (p.getBoosterTime() < 1) {
						p.sendPrefixedMessage("§eYour §b2x Coin Booster §ehas expired!");
						p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.WITHER_SPAWN, 1, 2);
					}
				}
				Booster.updateBoosterFile(); // Update the booster file after every tick
				GlobalKitListener.energyOverTime();
			}
		}.runTaskTimer(plugin, 20, 20);
		/*
		 * new BukkitRunnable() { public void run() { FFAboard.update(); ONEboard.update(); TWOboard.update(); } }.runTaskTimer(plugin, 100, 20 * 60 * 5);
		 */
		new BukkitRunnable() {
			public void run() {
				getPlayers(p -> p.isOnline() && p.isFullEnergy()).forEach(p -> p.sendAbilityMessage());
			}
		}.runTaskTimer(plugin, 10, 8 * 20);
		new BukkitRunnable() {
			public void run() {
				queue1v1.process();
			}
		}.runTaskTimer(plugin, 10, 10 * 20);
		System.out.println("[Taken Kits] Queue Processor enabled");

		broadcaster = new Broadcaster();
		broadcaster.startBroadcaster();
	}

	public void onDisable() {
		getOnlinePlayers().forEach(p -> getOnlinePlayers().forEach(p2 -> p.getPlayer().showPlayer(p2.getPlayer())));

		for (PlayerModel player : getOnlinePlayers()) {
			if (player.isInGame())
				player.getGame().dissolve(0); // Instantly time the players out and send them to spawn

			// Send all player's to their spawnpoint
			player.sendToSpawn();

			// Unregister all player permissions
			Group.unregisterPlayerPermissions(player);

		}

		System.out.println("[Taken Kits] Games dissolved");
		System.out.println("[Taken Kits] Permissions Unregistered");

		for (Location b : blocks) {
			b.getWorld().getBlockAt(b).setType(Material.AIR);
		}
		// TODO: Remove use of metadata
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.hasMetadata("lastPlayerDamage"))
				p.removeMetadata("lastPlayerDamage", plugin);
		}
		effectManager.dispose();
		System.out.println("[Taken Kits] EffectLib disabled");

		try {
			if (getConnection() != null && !getConnection().isClosed())
				database.flush();
		} catch (SQLException e) {
			System.out.println("[Taken Kits] URGENT: Failed to close connection");
		}
		System.out.println("[Taken Kits] Connection closed successfully");

		plugin = null;
	}

	public static Server getInstance() {
		return plugin;
	}

	private void registerListeners(Listener... l) {
		for (Listener r : l) {
			Bukkit.getPluginManager().registerEvents(r, plugin);
		}
	}

	private void registerPlayerCommands(String... s) {
		for (String r : s) {
			getCommand(r).setExecutor(new PlayerCommands());
		}
	}

	private void registerServerCommands(String... s) {
		for (String r : s) {
			getCommand(r).setExecutor(new ServerCommands());
		}
	}

	public static Collection<PlayerModel> getAllPlayers() {
		return Collections.unmodifiableCollection(playerModels);
	}

	public static Collection<PlayerModel> getPlayers(Predicate<PlayerModel> predicate) {
		Collection<PlayerModel> players = new ArrayList<PlayerModel>();
		for (PlayerModel player : getAllPlayers()) { // Loops through every player
			if (predicate.test(player)) { // Tests each player with the given predicate
				players.add(player); // Those who pass match the filter
			}
		}
		return Collections.unmodifiableCollection(players);
	}

	public static Collection<PlayerModel> getOnlinePlayers() {
		return getPlayers(player -> player.isOnline());
	}

	public void removeBlock(Location b) {
		blocks.remove(b);
	}

	public static PlayerDataTable getPlayerDataTable() {
		return playerDataTable;
	}

	public static KitCustomizerTable getKitCustomizerTable() {
		return kitCustomizerTable;
	}

	public static ArenaTable getArenaTable() {
		return arenaTable;
	}

	public static IconMenus getMenus() {
		return menus;
	}

	public static EffectManager getEffectManager() {
		return effectManager;
	}

	public static Connection getConnection() {
		return database.getConnection();
	}

	public static ArrayList<Location> getBlocks() {
		return blocks;
	}

	public static Queue getQueue(GameType gameType) {
		if (gameType == GameType.ONEvONE)
			return queue1v1;
		else
			return queue2v2;
	}

	public static AccessMode getMode() {
		return mode;
	}

	public static void setMode(AccessMode mode) {
		Server.mode = mode;
	}

	public Broadcaster getBroadcaster() {
		return broadcaster;
	}

	public ScoreboardManager getScoreboardManager() {
		return sbm;
	}
}