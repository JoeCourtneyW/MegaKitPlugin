//    ______ _       _     _     ____  _ _                     _    _____ _       
//   |  ____(_)     | |   | |   |  _ \(_) |                   | |  / ____| |      
//   | |__   _  __ _| |__ | |_  | |_) |_| |_    __ _ _ __   __| | | (___ | |_   _ 
//   |  __| | |/ _` | '_ \| __| |  _ <| | __|  / _` | '_ \ / _` |  \___ \| | | | |
//   | |____| | (_| | | | | |_  | |_) | | |_  | (_| | | | | (_| |  ____) | | |_| |
//   |______|_|\__, |_| |_|\__| |____/|_|\__|  \__,_|_| |_|\__,_| |_____/|_|\__, |
//              __/ |                                                        __/ |
//             |___/                                                        |___/ 

package com.me.sly.kits.main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import com.me.sly.kits.classselector.ClassSelectorListener;
import com.me.sly.kits.main.classes.Class;
import com.me.sly.kits.main.classes.Classes;
import com.me.sly.kits.main.commands.PlayerCommands;
import com.me.sly.kits.main.commands.ServerCommands;
import com.me.sly.kits.main.enums.AccessMode;
import com.me.sly.kits.main.enums.Group;
import com.me.sly.kits.main.enums.VanityArmor;
import com.me.sly.kits.main.gametype.Arena;
import com.me.sly.kits.main.gametype.Duel;
import com.me.sly.kits.main.gametype.Game;
import com.me.sly.kits.main.gametype.GameType;
import com.me.sly.kits.main.gametype.GameTypeListeners;
import com.me.sly.kits.main.gametype.Queue;
import com.me.sly.kits.main.gametype.Team;
import com.me.sly.kits.main.listeners.GameplayListeners;
import com.me.sly.kits.main.listeners.ServerListeners;
import com.me.sly.kits.main.listeners.StatListeners;
import com.me.sly.kits.main.listeners.TeleportFix;
import com.me.sly.kits.main.resources.BetterItem;
import com.me.sly.kits.main.resources.IconMenu;

import de.slikey.effectlib.EffectManager;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import net.minecraft.server.v1_7_R4.NBTTagList;

@SuppressWarnings("deprecation")
public class Corrupted extends JavaPlugin implements Listener {
	/**
	 * The main instance of the class Accessed by using getInstance()
	 */
	private static Corrupted plugin;
	/**
	 * Instance of EffectManager API
	 */
	private static EffectManager effectManager;
	/**
	 * MySQL Connection Prepare statements with this, DO NOT EDIT
	 */
	private static Connection connection;
	/**
	 * The basic items for all Classes Filled in the onEnable() by looping
	 * through Classes and getting their listeners
	 */
	private static HashMap<String, HashMap<String, ItemStack>> kitItems = new HashMap<String, HashMap<String, ItemStack>>();
	/**
	 * All blocks that get placed in survival mode Used to be removed on reloads
	 * and restarts
	 * 
	 * @deprecated
	 */
	private static ArrayList<Location> blocks = new ArrayList<Location>();
	/**
	 * Access Cache, Only access this, Never query the database for results sync
	 * Can be edited async
	 */
	private static ConcurrentHashMap<String, HashMap<String, Object>> databaseCache = new ConcurrentHashMap<String, HashMap<String, Object>>();
	/**
	 * Access Cache, Only access this, Never query the database for results sync
	 * Never needed except when giving players the class items, or showing them
	 * the kit customizer menu Can be edited async
	 */
	private static ConcurrentHashMap<String, HashMap<String, String>> kitCustomizerCache = new ConcurrentHashMap<String, HashMap<String, String>>();
	/**
	 * All 1v1 Arenas Loop through randomly to find random open arena (while
	 * loop)
	 */
	private static ArrayList<Arena> arenas1v1 = new ArrayList<Arena>();
	/**
	 * All 2v2 Arenas Loop through randomly to find random open arena (while
	 * loop)
	 */
	private static ArrayList<Arena> arenas2v2 = new ArrayList<Arena>();
	/**
	 * All Duel Arenas Loop through randomly to find random open arena (while
	 * loop)
	 */
	private static ArrayList<Arena> arenasDuel = new ArrayList<Arena>();
	/**
	 * All games, When a player joins a game, he's added to this map with the
	 * game as the Value One for each player in the game
	 */
	private static HashMap<String, Game> games = new HashMap<String, Game>();
	/**
	 * All games, When a player joins a duel, he's added to this map with the
	 * game as the Value One for each player in the game
	 */
	private static HashMap<String, Duel> duels = new HashMap<String, Duel>();
	/**
	 * Teams, one for each player in a team
	 */
	private static HashMap<String, Team> teams = new HashMap<String, Team>();
	/**
	 * A list of spectators for each game One list per game
	 */
	private static HashMap<Game, ArrayList<String>> spectators = new HashMap<Game, ArrayList<String>>();
	private static int[] inventorySpots = { 11, 12, 13, 14, 15, 20, 21, 22, 23, 24, 29, 30, 31, 32, 33 };
	private static Queue queue;
	private static ScoreboardManager sbm;
	private static IconMenus menus;
	private static ArrayList<String> staffChat = new ArrayList<String>();
	private static HashMap<UUID, PermissionAttachment> attachments = new HashMap<UUID, PermissionAttachment>();
	private static int globalCoinMultiplier = 1;
	private static boolean isSTFU = false;
	private static AccessMode mode = AccessMode.ALL;
	public static Location SPAWN_POINT = new Location(Bukkit.getWorld("Nube"), -549.5, 101, -552.5, 206.62F, 1.82F);
	private static HashMap<String, String> cooldowns = new HashMap<String, String>();
	private static ArrayList<String> invitesToggled = new ArrayList<String>();
	/**
	 * Nicknames are stored in the values while the player with them is the Key.
	 * Only Accessed by the command, the nametag event, and the scoreboard No
	 * need to view
	 */
	private static HashMap<String, String> nickedPlayers = new HashMap<String, String>();
	/**
	 * Invites and Invitees
	 */
	private static HashMap<String, String> invites = new HashMap<String, String>();
	private static HashMap<String, String> modMode = new HashMap<String, String>();
	//private Leaderboard FFAboard;
	//private Leaderboard ONEboard;
	//private Leaderboard TWOboard;
	private Broadcaster broadcaster;

	public void onEnable() {
		plugin = this;
		openConnection();
		System.out.println("[Taken Kits] Database connected");
		//createTables();
		effectManager = new EffectManager(this);
		System.out.println("[Taken Kits] EffectLib loaded ");
		sbm = Bukkit.getServer().getScoreboardManager();
		System.out.println("[Taken Kits] Scoreboard Manager loaded");
		queue = new Queue();
		System.out.println("[Taken Kits] Queue registered");
		for (int i = 0; i < 1000; i++) {
			if (Corrupted.plugin.getConfig().contains("Arenas.1v1." + i)) {
				GameType gameType = GameType.ONEvONE;
				int id = i;
				Location spawn1 = new Location(Bukkit.getWorld(Corrupted.plugin.getConfig().getString("Arenas.1v1." + i + ".1.world")), Corrupted.plugin.getConfig().getDouble("Arenas.1v1." + i + ".1.x"), Corrupted.plugin.getConfig().getDouble("Arenas.1v1." + i + ".1.y"), Corrupted.plugin.getConfig().getDouble("Arenas.1v1." + i + ".1.z"), (float) Corrupted.plugin.getConfig().getDouble("Arenas.1v1." + i + ".1.yaw"), (float) Corrupted.plugin.getConfig().getDouble("Arenas.1v1." + i + ".1.pitch"));
				Location spawn2 = new Location(Bukkit.getWorld(Corrupted.plugin.getConfig().getString("Arenas.1v1." + i + ".2.world")), Corrupted.plugin.getConfig().getDouble("Arenas.1v1." + i + ".2.x"), Corrupted.plugin.getConfig().getDouble("Arenas.1v1." + i + ".2.y"), Corrupted.plugin.getConfig().getDouble("Arenas.1v1." + i + ".2.z"), (float) Corrupted.plugin.getConfig().getDouble("Arenas.1v1." + i + ".2.yaw"), (float) Corrupted.plugin.getConfig().getDouble("Arenas.1v1." + i + ".2.pitch"));
				Arena a = new Arena(gameType, id, spawn1, spawn2, null, false);
				arenas1v1.add(a);
			} else {
				continue;
			}
		}
		for (int i = 0; i < 1000; i++) {
			if (Corrupted.plugin.getConfig().contains("Arenas.2v2." + i)) {
				GameType gameType = GameType.TWOvTWO;
				int id = i;
				Location spawn1 = new Location(Bukkit.getWorld(Corrupted.plugin.getConfig().getString("Arenas.2v2." + i + ".1.world")), Corrupted.plugin.getConfig().getDouble("Arenas.2v2." + i + ".1.x"), Corrupted.plugin.getConfig().getDouble("Arenas.2v2." + i + ".1.y"), Corrupted.plugin.getConfig().getDouble("Arenas.2v2." + i + ".1.z"), (float) Corrupted.plugin.getConfig().getDouble("Arenas.2v2." + i + ".1.yaw"), (float) Corrupted.plugin.getConfig().getDouble("Arenas.2v2." + i + ".1.pitch"));
				Location spawn2 = new Location(Bukkit.getWorld(Corrupted.plugin.getConfig().getString("Arenas.2v2." + i + ".2.world")), Corrupted.plugin.getConfig().getDouble("Arenas.2v2." + i + ".2.x"), Corrupted.plugin.getConfig().getDouble("Arenas.2v2." + i + ".2.y"), Corrupted.plugin.getConfig().getDouble("Arenas.2v2." + i + ".2.z"), (float) Corrupted.plugin.getConfig().getDouble("Arenas.2v2." + i + ".2.yaw"), (float) Corrupted.plugin.getConfig().getDouble("Arenas.2v2." + i + ".2.pitch"));
				Location spawn3 = new Location(Bukkit.getWorld(Corrupted.plugin.getConfig().getString("Arenas.2v2." + i + ".3.world")), Corrupted.plugin.getConfig().getDouble("Arenas.2v2." + i + ".3.x"), Corrupted.plugin.getConfig().getDouble("Arenas.2v2." + i + ".3.y"), Corrupted.plugin.getConfig().getDouble("Arenas.2v2." + i + ".3.z"), (float) Corrupted.plugin.getConfig().getDouble("Arenas.2v2." + i + ".3.yaw"), (float) Corrupted.plugin.getConfig().getDouble("Arenas.2v2." + i + ".3.pitch"));
				Arena a = new Arena(gameType, id, spawn1, spawn2, spawn3, false);
				arenas2v2.add(a);
			} else {
				continue;
			}
		}
		for (int i = 0; i < 1000; i++) {
			if (Corrupted.plugin.getConfig().contains("Arenas.Duel." + i)) {
				GameType gameType = GameType.DUEL;
				int id = i;
				Location spawn1 = new Location(Bukkit.getWorld(Corrupted.plugin.getConfig().getString("Arenas.Duel." + i + ".1.world")), Corrupted.plugin.getConfig().getDouble("Arenas.Duel." + i + ".1.x"), Corrupted.plugin.getConfig().getDouble("Arenas.Duel." + i + ".1.y"), Corrupted.plugin.getConfig().getDouble("Arenas.Duel." + i + ".1.z"), (float) Corrupted.plugin.getConfig().getDouble("Arenas.Duel." + i + ".1.yaw"), (float) Corrupted.plugin.getConfig().getDouble("Arenas.Duel." + i + ".1.pitch"));
				Location spawn2 = new Location(Bukkit.getWorld(Corrupted.plugin.getConfig().getString("Arenas.Duel." + i + ".2.world")), Corrupted.plugin.getConfig().getDouble("Arenas.Duel." + i + ".2.x"), Corrupted.plugin.getConfig().getDouble("Arenas.Duel." + i + ".2.y"), Corrupted.plugin.getConfig().getDouble("Arenas.Duel." + i + ".2.z"), (float) Corrupted.plugin.getConfig().getDouble("Arenas.Duel." + i + ".2.yaw"), (float) Corrupted.plugin.getConfig().getDouble("Arenas.Duel." + i + ".2.pitch"));
				Arena a = new Arena(gameType, id, spawn1, spawn2, null, false);
				arenasDuel.add(a);
			} else {
				continue;
			}
		}
		System.out.println("[Taken Kits] Arenas registered");
		try {
			PreparedStatement sql2;
			sql2 = Corrupted.connection.prepareStatement("SELECT * FROM `Players`");
			ResultSet rs = sql2.executeQuery();
			while (rs.next()) {
				HashMap<String, Object> toCache = new HashMap<String, Object>();
				toCache.put("Rank", rs.getString("Rank"));
				toCache.put("Block", rs.getString("Block"));
				toCache.put("VanityArmor", rs.getString("VanityArmor"));
				toCache.put("Trail", rs.getString("Trail"));
				toCache.put("IP", rs.getString("IP"));
				toCache.put("PlayerName", rs.getString("PlayerName"));
				toCache.put("1v1Rating", rs.getInt("1v1Rating"));
				toCache.put("2v2Rating", rs.getInt("2v2Rating"));
				toCache.put("FFARating", rs.getInt("FFARating"));
				toCache.put("FFAKills", rs.getInt("FFAKills"));
				toCache.put("FFADeaths", rs.getInt("FFADeaths"));
				toCache.put("Coins", rs.getInt("Coins"));
				toCache.put("Spins", rs.getInt("Spins"));
				toCache.put("Skeleton", rs.getString("Skeleton"));
				toCache.put("Creeper", rs.getString("Creeper"));
				toCache.put("Zombie", rs.getString("Zombie"));
				toCache.put("Enderman", rs.getString("Enderman"));
				toCache.put("Herobrine", rs.getString("Herobrine"));
				toCache.put("Dreadlord", rs.getString("Dreadlord"));
				toCache.put("Golem", rs.getString("Golem"));
				toCache.put("Arcanist", rs.getString("Arcanist"));
				toCache.put("Shaman", rs.getString("Shaman"));
				toCache.put("Blaze", rs.getString("Blaze"));
				toCache.put("Pigman", rs.getString("Pigman"));
				toCache.put("Spider", rs.getString("Spider"));
				toCache.put("Pirate", rs.getString("Pirate"));
				toCache.put("Squid", rs.getString("Squid"));
				toCache.put("Hunter", rs.getString("Hunter"));
				databaseCache.put(rs.getString("UUID"), toCache);
			}
		} catch (SQLException e) {
			System.out.println("[Taken Kits] Player Database values were unable to be loaded");
		}
		try {
			PreparedStatement sql2;
			sql2 = Corrupted.connection.prepareStatement("SELECT * FROM `KitCustomizer`");
			ResultSet rs = sql2.executeQuery();
			while (rs.next()) {
				HashMap<String, String> toCache = new HashMap<String, String>();
				toCache.put("Skeleton", rs.getString("Skeleton"));
				toCache.put("Creeper", rs.getString("Creeper"));
				toCache.put("Zombie", rs.getString("Zombie"));
				toCache.put("Enderman", rs.getString("Enderman"));
				toCache.put("Herobrine", rs.getString("Herobrine"));
				toCache.put("Dreadlord", rs.getString("Dreadlord"));
				toCache.put("Golem", rs.getString("Golem"));
				toCache.put("Arcanist", rs.getString("Arcanist"));
				toCache.put("Shaman", rs.getString("Shaman"));
				toCache.put("Blaze", rs.getString("Blaze"));
				toCache.put("Pigman", rs.getString("Pigman"));
				toCache.put("Spider", rs.getString("Spider"));
				toCache.put("Pirate", rs.getString("Pirate"));
				toCache.put("Squid", rs.getString("Squid"));
				toCache.put("Hunter", rs.getString("Hunter"));
				kitCustomizerCache.put(rs.getString("UUID"), toCache);
			}
		} catch (SQLException e) {
			System.out.println("[Taken Kits] Player Database values were unable to be loaded");
		}
		System.out.println("[Taken Kits] Player Database values loaded");
		//FFAboard = new Leaderboard(DatabaseColumn.FFARATING, 9, new Location(Bukkit.getWorld("Nube"), -513, 102, -613));
		//ONEboard = new Leaderboard(DatabaseColumn.ONERATING, 9, new Location(Bukkit.getWorld("Nube"), -513, 102, -617));
		//TWOboard = new Leaderboard(DatabaseColumn.TWORATING, 9, new Location(Bukkit.getWorld("Nube"), -513, 102, -609));
		System.out.println("[Taken Kits] Leaderboards enabled");
		for (Player p : Bukkit.getOnlinePlayers()) {
			updateScoreboard(p);
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			Group.registerPlayerPermissions(p);
		}
		for (Player players : Bukkit.getOnlinePlayers()) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				players.showPlayer(p);
			}
		}
		System.out.println("[Taken Kits] Permissions Registered");
		registerPlayerCommands("kys", "Tpallclass", "Spawn", "Arena", "Idea", "Bug", "Sc", "Group", "STFU", "Team", "Booster", "Messages", "Stats", "vote", "Nick", "Staff");
		registerServerCommands("broadcast", "ClearDB", "GCM", "Package", "SetAccess", "DB");
		System.out.println("[Taken Kits] Commands registered");
		registerListeners(this, new GameplayListeners(), new ServerListeners(), new GameTypeListeners(), new StatListeners(), new ClassSelectorListener(), new TeleportFix(this, Bukkit.getServer()));
		Class.registerClassListeners();
		for (Classes c : Classes.values()) {
			if (c.equals(Classes.NONE))
				continue;
			c.getListenerClass().fillKit();
			kitItems.put(c.getName(), c.getListenerClass().getKitItems());
		}
		menus = new IconMenus(this);
		System.out.println("[Taken Kits] Icon Menus loaded");
		System.out.println("[Taken Kits] Listeners registered");
		/*ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(this, ConnectionSide.SERVER_SIDE, Packets.Server.NAMED_SOUND_EFFECT) {
			public void onPacketSending(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				String soundName = packet.getStrings().read(0);
				if (soundName.startsWith("random.level"))
					event.setCancelled(true);
			}
		});*/
		new BukkitRunnable() {
			public void run() {

				for (Player p : Bukkit.getOnlinePlayers()) {
					if (p.getInventory().contains(Material.GLASS_BOTTLE)) {
						p.getInventory().remove(Material.GLASS_BOTTLE);
						p.updateInventory();
					}
					p.setCompassTarget(getNearestPlayer(p).getLocation());
					if (p.getLevel() == 100) {
						if (p.getExp() == 0F)
							p.setExp(1F);
						else p.setExp(0F);
					} else if (p.getLevel() > 100) {
						p.setLevel(100);
						p.setExp(0F);
					} else {
						p.setExp(0F);
					}
				}
			}
		}.runTaskTimer(plugin, 10, 10);
		new BukkitRunnable() {
			public void run() {
				if (getActiveBoosters() != null) {
					for (UUID g : getActiveBoosters().keySet()) {
						if (getActiveBoosters().get(g) < 2) {
							OfflinePlayer op = Bukkit.getOfflinePlayer(g);
							if (op.isOnline()) {
								Player p = Bukkit.getPlayer(g);
								Chat.sendPlayerPrefixedMessage(p, "§eYour §b2x Coin Booster §ehas expired!");
								p.playSound(p.getLocation(), Sound.WITHER_SPAWN, 1, 2);
								getConfig().set("Boosters." + p.getUniqueId().toString() + ".timeLeft", null);
								saveConfig();
							}
						} else {
							getConfig().set("Boosters." + g.toString() + ".timeLeft", getConfig().getInt("Boosters." + g.toString() + ".timeLeft") - 1);
							saveConfig();
						}
					}
				}
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (Classes.getClass(p) == Classes.BLAZE) {
						addEnergy(p, 6);
					}
					if (Classes.getClass(p) == Classes.SPIDER) {
						addEnergy(p, 7);
					}
					if (Classes.getClass(p) == Classes.SKELETON) {
						addEnergy(p, 1);
					}
				}
			}
		}.runTaskTimer(plugin, 20, 20);
		/*new BukkitRunnable() {
			public void run() {
				FFAboard.update();
				ONEboard.update();
				TWOboard.update();
			}
		}.runTaskTimer(plugin, 100, 20 * 60 * 5);*/
		new BukkitRunnable() {
			public void run() {
				for (Player p : Bukkit.getOnlinePlayers()) {
					if (p.getLevel() >= 100) {
						Chat.sendAbilityMessage(p);
					}
				}
			}
		}.runTaskTimer(plugin, 10, 8 * 20);
		new BukkitRunnable() {
			public void run() {
				queue.processQueue();
			}
		}.runTaskTimer(plugin, 10, 10 * 20);
		System.out.println("[Taken Kits] Queue Processor enabled");

		broadcaster = new Broadcaster();
		broadcaster.startBroadcaster();
	}

	public void onDisable() {
		for (Player players : Bukkit.getOnlinePlayers()) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				players.showPlayer(p);
			}
		}
		for (Game g : getGames().values()) {
			final Location l = new Location(Bukkit.getWorld("Nube"), -549.5, 101, -552.5, 206.62F, 1.82F);
			if (!g.isOver()) {
				for (String r : g.getTeam1()) {
					Bukkit.getPlayer(r).teleport(l);
					setDefaultInventory(Bukkit.getPlayer(r));
					Bukkit.getPlayer(r).removeMetadata("Class", Corrupted.getInstance());
				}
				for (String r : g.getTeam2()) {
					Bukkit.getPlayer(r).teleport(l);
					setDefaultInventory(Bukkit.getPlayer(r));
					Bukkit.getPlayer(r).removeMetadata("Class", Corrupted.getInstance());
				}
			}
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			Group.unregisterPlayerPermissions(p);
		}
		System.out.println("[Taken Kits] Permissions Unregistered");
		for (Location b : blocks) {
			b.getWorld().getBlockAt(b).setType(Material.AIR);
		}
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.hasMetadata("lastPlayerDamage"))
				p.removeMetadata("lastPlayerDamage", plugin);
		}
		effectManager.dispose();
		System.out.println("[Taken Kits] EffectLib disabled");
		plugin = null;
		try {
			if (connection != null && !connection.isClosed())
				closeConnection();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println("[Taken Kits] Connection closed successfully");
	}

	public static Corrupted getInstance() {
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
	private void createTables(){
		
		try {
			PreparedStatement createPlayers = Corrupted.connection.prepareStatement("CREATE TABLE IF NOT EXISTS Players("
					+ "UUID varchar(50), "
					+ "1v1Rating int, "
					+ "2v2Rating int, "
					+ "FFARating int, "
					+ "FFAKills int, " 
					+ "FFADeaths int, "
					+ "Coins int, "
					+ "Spins int, "
					+ "Skeleton varchar(13), "
					+ "Creeper varchar(13), "
					+ "Zombie varchar(13), "
					+ "Enderman varchar(13), "
					+ "Herobrine varchar(13), "
					+ "Dreadlord varchar(13), "
					+ "Arcanist varchar(13), "
					+ "Shaman varchar(13), "
					+ "Golem varchar(13), "
					+ "Pigman varchar(13), "
					+ "Blaze varchar(13), "
					+ "Spider varchar(13), "
					+ "Pirate varchar(13), " 
					+ "Squid varchar(13), "
					+ "Hunter varchar(13), "
					+ "Block varchar(50), "
					+ "VanityArmor varchar(50), "
					+ "Trail varchar(50), "
					+ "PlayerName varchar(20), "
					+ "Rank varchar(25), "
					+ "IP varchar(15)"
					+ ")");
			PreparedStatement createKitCustomizer = Corrupted.connection.prepareStatement("CREATE TABLE IF NOT EXISTS KitCustomizer("
					+ "UUID varchar(50), "
					+ "Skeleton varchar(17), "
					+ "Creeper varchar(17), "
					+ "Zombie varchar(17), "
					+ "Enderman varchar(17), "
					+ "Herobrine varchar(17), "
					+ "Dreadlord varchar(17), "
					+ "Arcanist varchar(17), "
					+ "Shaman varchar(17), "
					+ "Golem varchar(17), "
					+ "Pigman varchar(17), "
					+ "Blaze varchar(17), "
					+ "Spider varchar(17), "
					+ "Pirate varchar(17), "
					+ "Squid varchar(17), "
					+ "Hunter varchar(17)"
					+ ")");
			createPlayers.execute();
			createKitCustomizer.execute();
			createPlayers.close();
			createKitCustomizer.close();
			System.out.println("[Taken Kits] Tables adjusted");
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("[Taken Kits] Unable to create the database tables");
		}
	}
//Plugin won't work until I make a "createTableOnStart" method
	public void updateDatabase(OfflinePlayer p) {
		final String uuid = p.getUniqueId().toString();
		Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
			public void run() {
				try {
					PreparedStatement ps2;
					ps2 = Corrupted.connection.prepareStatement("UPDATE `Players` SET `1v1Rating`=?, `2v2Rating`=?, `FFARating`=?, `FFAKills`=?, `FFADeaths`=?, `Coins`=?, `Spins`=?, `Skeleton`=?, `Creeper`=?, `Zombie`=?, `Enderman`=?, `Herobrine`=?, `Dreadlord`=?, `Arcanist`=?, `Shaman`=?, `Golem`=?, `Pigman`=?, `Blaze`=?, `Spider`=?, `Pirate`=?, `Squid`=?, `Hunter`=?, `Rank`=?, `Block`=?,`VanityArmor`=?,`Trail`=?, `PlayerName`=?, `IP`=? WHERE `UUID`=?");
					ps2.setInt(1, (int) databaseCache.get(uuid).get("1v1Rating"));
					ps2.setInt(2, (int) databaseCache.get(uuid).get("2v2Rating"));
					ps2.setInt(3, (int) databaseCache.get(uuid).get("FFARating"));
					ps2.setInt(4, (int) databaseCache.get(uuid).get("FFAKills"));
					ps2.setInt(5, (int) databaseCache.get(uuid).get("FFADeaths"));
					ps2.setInt(6, (int) databaseCache.get(uuid).get("Coins"));
					ps2.setInt(7, (int) databaseCache.get(uuid).get("Spins"));
					ps2.setString(8, (String) databaseCache.get(uuid).get("Skeleton"));
					ps2.setString(9, (String) databaseCache.get(uuid).get("Creeper"));
					ps2.setString(10, (String) databaseCache.get(uuid).get("Zombie"));
					ps2.setString(11, (String) databaseCache.get(uuid).get("Enderman"));
					ps2.setString(12, (String) databaseCache.get(uuid).get("Herobrine"));
					ps2.setString(13, (String) databaseCache.get(uuid).get("Dreadlord"));
					ps2.setString(14, (String) databaseCache.get(uuid).get("Arcanist"));
					ps2.setString(15, (String) databaseCache.get(uuid).get("Shaman"));
					ps2.setString(16, (String) databaseCache.get(uuid).get("Golem"));
					ps2.setString(17, (String) databaseCache.get(uuid).get("Pigman"));
					ps2.setString(18, (String) databaseCache.get(uuid).get("Blaze"));
					ps2.setString(19, (String) databaseCache.get(uuid).get("Spider"));
					ps2.setString(20, (String) databaseCache.get(uuid).get("Pirate"));
					ps2.setString(21, (String) databaseCache.get(uuid).get("Squid"));
					ps2.setString(22, (String) databaseCache.get(uuid).get("Hunter"));
					ps2.setString(23, databaseCache.get(uuid).get("Rank").toString());
					ps2.setString(24, databaseCache.get(uuid).get("Block").toString());
					ps2.setString(25, databaseCache.get(uuid).get("VanityArmor").toString());
					ps2.setString(26, databaseCache.get(uuid).get("Trail").toString());
					ps2.setString(27, databaseCache.get(uuid).get("PlayerName").toString());
					ps2.setString(28, databaseCache.get(uuid).get("IP").toString());
					ps2.setString(29, uuid);
					ps2.executeUpdate();
					ps2.close();
					PreparedStatement ps1;
					ps1 = Corrupted.connection.prepareStatement("UPDATE KitCustomizer SET Skeleton = ?, Creeper = ?, Zombie = ?, Enderman = ?, Herobrine = ?, Dreadlord = ?, Arcanist = ?, Shaman = ?, Golem = ?, Pigman = ?, Blaze = ?, Spider = ?, `Pirate`=?, `Squid`=?, `Hunter`=? WHERE UUID = ?");
					ps1.setString(1, kitCustomizerCache.get(uuid).get("Skeleton"));
					ps1.setString(2, kitCustomizerCache.get(uuid).get("Creeper"));
					ps1.setString(3, kitCustomizerCache.get(uuid).get("Zombie"));
					ps1.setString(4, kitCustomizerCache.get(uuid).get("Enderman"));
					ps1.setString(5, kitCustomizerCache.get(uuid).get("Herobrine"));
					ps1.setString(6, kitCustomizerCache.get(uuid).get("Dreadlord"));
					ps1.setString(7, kitCustomizerCache.get(uuid).get("Arcanist"));
					ps1.setString(8, kitCustomizerCache.get(uuid).get("Shaman"));
					ps1.setString(9, kitCustomizerCache.get(uuid).get("Golem"));
					ps1.setString(10, kitCustomizerCache.get(uuid).get("Pigman"));
					ps1.setString(11, kitCustomizerCache.get(uuid).get("Blaze"));
					ps1.setString(12, kitCustomizerCache.get(uuid).get("Spider"));
					ps1.setString(13, kitCustomizerCache.get(uuid).get("Pirate"));
					ps1.setString(14, kitCustomizerCache.get(uuid).get("Squid"));
					ps1.setString(15, kitCustomizerCache.get(uuid).get("Hunter"));
					ps1.setString(16, uuid);
					ps1.executeUpdate();
					ps1.close();
				} catch (SQLException e) {
					e.printStackTrace();
					System.out.println(e.getCause());
				}
			}
		}, 0L);
	}

	public void updateDatabase(Player p) {
		final String uuid = p.getUniqueId().toString();
		Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
			public void run() {
				try {
					PreparedStatement ps2;
					ps2 = Corrupted.connection.prepareStatement("UPDATE `Players` SET `1v1Rating`=?, `2v2Rating`=?, `FFARating`=?, `FFAKills`=?, `FFADeaths`=?, `Coins`=?, `Spins`=?, `Skeleton`=?, `Creeper`=?, `Zombie`=?, `Enderman`=?, `Herobrine`=?, `Dreadlord`=?, `Arcanist`=?, `Shaman`=?, `Golem`=?, `Pigman`=?, `Blaze`=?, `Spider`=?, `Pirate`=?, `Squid`=?, `Hunter`=?, `Rank`=?, `Block`=?, `VanityArmor`=?, `Trail`=?, `PlayerName`=?, `IP`=? WHERE `UUID`=?");

					ps2.setInt(1, (int) databaseCache.get(uuid).get("1v1Rating"));
					ps2.setInt(2, (int) databaseCache.get(uuid).get("2v2Rating"));
					ps2.setInt(3, (int) databaseCache.get(uuid).get("FFARating"));
					ps2.setInt(4, (int) databaseCache.get(uuid).get("FFAKills"));
					ps2.setInt(5, (int) databaseCache.get(uuid).get("FFADeaths"));
					ps2.setInt(6, (int) databaseCache.get(uuid).get("Coins"));
					ps2.setInt(7, (int) databaseCache.get(uuid).get("Spins"));
					ps2.setString(8, (String) databaseCache.get(uuid).get("Skeleton"));
					ps2.setString(9, (String) databaseCache.get(uuid).get("Creeper"));
					ps2.setString(10, (String) databaseCache.get(uuid).get("Zombie"));
					ps2.setString(11, (String) databaseCache.get(uuid).get("Enderman"));
					ps2.setString(12, (String) databaseCache.get(uuid).get("Herobrine"));
					ps2.setString(13, (String) databaseCache.get(uuid).get("Dreadlord"));
					ps2.setString(14, (String) databaseCache.get(uuid).get("Arcanist"));
					ps2.setString(15, (String) databaseCache.get(uuid).get("Shaman"));
					ps2.setString(16, (String) databaseCache.get(uuid).get("Golem"));
					ps2.setString(17, (String) databaseCache.get(uuid).get("Pigman"));
					ps2.setString(18, (String) databaseCache.get(uuid).get("Blaze"));
					ps2.setString(19, (String) databaseCache.get(uuid).get("Spider"));
					ps2.setString(20, (String) databaseCache.get(uuid).get("Pirate"));
					ps2.setString(21, (String) databaseCache.get(uuid).get("Squid"));
					ps2.setString(22, (String) databaseCache.get(uuid).get("Hunter"));
					ps2.setString(23, databaseCache.get(uuid).get("Rank").toString());
					ps2.setString(24, databaseCache.get(uuid).get("Block").toString());
					ps2.setString(25, databaseCache.get(uuid).get("VanityArmor").toString());
					ps2.setString(26, databaseCache.get(uuid).get("Trail").toString());
					ps2.setString(27, databaseCache.get(uuid).get("PlayerName").toString());
					ps2.setString(28, databaseCache.get(uuid).get("IP").toString());
					ps2.setString(29, uuid);
					ps2.executeUpdate();
					ps2.close();
					PreparedStatement ps1;
					ps1 = Corrupted.connection.prepareStatement("UPDATE KitCustomizer SET Skeleton = ?, Creeper = ?, Zombie = ?, Enderman = ?, Herobrine = ?, Dreadlord = ?, Arcanist = ?, Shaman = ?, Golem = ?, Pigman = ?, Blaze = ?, Spider = ?, `Pirate`=?, `Squid`=?, `Hunter`=? WHERE UUID = ?");
					ps1.setString(1, kitCustomizerCache.get(uuid).get("Skeleton"));
					ps1.setString(2, kitCustomizerCache.get(uuid).get("Creeper"));
					ps1.setString(3, kitCustomizerCache.get(uuid).get("Zombie"));
					ps1.setString(4, kitCustomizerCache.get(uuid).get("Enderman"));
					ps1.setString(5, kitCustomizerCache.get(uuid).get("Herobrine"));
					ps1.setString(6, kitCustomizerCache.get(uuid).get("Dreadlord"));
					ps1.setString(7, kitCustomizerCache.get(uuid).get("Arcanist"));
					ps1.setString(8, kitCustomizerCache.get(uuid).get("Shaman"));
					ps1.setString(9, kitCustomizerCache.get(uuid).get("Golem"));
					ps1.setString(10, kitCustomizerCache.get(uuid).get("Pigman"));
					ps1.setString(11, kitCustomizerCache.get(uuid).get("Blaze"));
					ps1.setString(12, kitCustomizerCache.get(uuid).get("Spider"));
					ps1.setString(13, kitCustomizerCache.get(uuid).get("Pirate"));
					ps1.setString(14, kitCustomizerCache.get(uuid).get("Squid"));
					ps1.setString(15, kitCustomizerCache.get(uuid).get("Hunter"));
					ps1.setString(16, uuid);
					ps1.executeUpdate();
					ps1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}, 0L);
	}

	public void newDatabaseEntry(Player p) {
		final String uuid = p.getUniqueId().toString();
		final String ip = p.getAddress().getAddress().getHostAddress();
		final String name = p.getName();
		Bukkit.getScheduler().scheduleAsyncDelayedTask(plugin, new Runnable() {
			public void run() {
				try {
					PreparedStatement ps2;
					ps2 = Corrupted.connection.prepareStatement("INSERT INTO `Players` values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					ps2.setString(1, uuid);
					ps2.setInt(2, 1000);
					ps2.setInt(3, 1000);
					ps2.setInt(4, 1000);
					ps2.setInt(5, 0);
					ps2.setInt(6, 0);
					ps2.setInt(7, 0);
					ps2.setInt(8, 1);
					ps2.setString(9, "1|0|0");
					ps2.setString(10, "1|0|0");
					ps2.setString(11, "0|0|0");
					ps2.setString(12, "0|0|0");
					ps2.setString(13, "0|0|0");
					ps2.setString(14, "0|0|0");
					ps2.setString(15, "0|0|0");
					ps2.setString(16, "0|0|0");
					ps2.setString(17, "0|0|0");
					ps2.setString(18, "0|0|0");
					ps2.setString(19, "0|0|0");
					ps2.setString(20, "0|0|0");
					ps2.setString(21, "0|0|0");
					ps2.setString(22, "0|0|0");
					ps2.setString(23, "0|0|0");
					ps2.setString(24, "1|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|WOOD");
					ps2.setString(25, "1|0|0|0|0|NONE");
					ps2.setString(26, "1|0|0|0|0|0|0|0|0|0|NONE");
					ps2.setString(27, name);
					ps2.setString(28, "DEFAULT");
					ps2.setString(29, ip);

					ps2.execute();
					ps2.close();
					PreparedStatement ps1;
					ps1 = Corrupted.connection.prepareStatement("INSERT INTO `KitCustomizer` values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
					ps1.setString(1, uuid);
					ps1.setString(2, "1|2|3|4|5|6|7|8|9");
					ps1.setString(3, "1|2|3|4|5|6|7|8|9");
					ps1.setString(4, "1|2|3|4|5|6|7|8|9");
					ps1.setString(5, "1|2|3|4|5|6|7|8|9");
					ps1.setString(6, "1|2|3|4|5|6|7|8|9");
					ps1.setString(7, "1|2|3|4|5|6|7|8|9");
					ps1.setString(8, "1|2|3|4|5|6|7|8|9");
					ps1.setString(9, "1|2|3|4|5|6|7|8|9");
					ps1.setString(10, "1|2|3|4|5|6|7|8|9");
					ps1.setString(11, "1|2|3|4|5|6|7|8|9");
					ps1.setString(12, "1|2|3|4|5|6|7|8|9");
					ps1.setString(13, "1|2|3|4|5|6|7|8|9");
					ps1.setString(14, "1|2|3|4|5|6|7|8|9");
					ps1.setString(15, "1|2|3|4|5|6|7|8|9");
					ps1.setString(16, "1|2|3|4|5|6|7|8|9");
					ps1.execute();
					ps1.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}, 0L);
		HashMap<String, Object> toCache = new HashMap<String, Object>();
		toCache.put("Rank", "DEFAULT");
		toCache.put("1v1Rating", 1000);
		toCache.put("2v2Rating", 1000);
		toCache.put("FFARating", 1000);
		toCache.put("FFAKills", 0);
		toCache.put("FFADeaths", 0);
		toCache.put("Coins", 0);
		toCache.put("Spins", 1);
		toCache.put("Skeleton", "1|0|0");
		toCache.put("Creeper", "1|0|0");
		toCache.put("Zombie", "0|0|0");
		toCache.put("Enderman", "0|0|0");
		toCache.put("Herobrine", "0|0|0");
		toCache.put("Dreadlord", "0|0|0");
		toCache.put("Golem", "0|0|0");
		toCache.put("Arcanist", "0|0|0");
		toCache.put("Shaman", "0|0|0");
		toCache.put("Blaze", "0|0|0");
		toCache.put("Pigman", "0|0|0");
		toCache.put("Spider", "0|0|0");
		toCache.put("Pirate", "0|0|0");
		toCache.put("Squid", "0|0|0");
		toCache.put("Hunter", "0|0|0");
		toCache.put("Block", "1|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|0|WOOD");
		toCache.put("VanityArmor", "1|0|0|0|0|NONE");
		toCache.put("Trail", "1|0|0|0|0|0|0|0|0|0|NONE");
		toCache.put("PlayerName", name);
		toCache.put("IP", ip);
		databaseCache.put(uuid, toCache);

		HashMap<String, String> toCache2 = new HashMap<String, String>();
		toCache2.put("Skeleton", "1|2|3|4|5|6|7|8|9");
		toCache2.put("Creeper", "1|2|3|4|5|6|7|8|9");
		toCache2.put("Zombie", "1|2|3|4|5|6|7|8|9");
		toCache2.put("Enderman", "1|2|3|4|5|6|7|8|9");
		toCache2.put("Herobrine", "1|2|3|4|5|6|7|8|9");
		toCache2.put("Dreadlord", "1|2|3|4|5|6|7|8|9");
		toCache2.put("Golem", "1|2|3|4|5|6|7|8|9");
		toCache2.put("Arcanist", "1|2|3|4|5|6|7|8|9");
		toCache2.put("Shaman", "1|2|3|4|5|6|7|8|9");
		toCache2.put("Blaze", "1|2|3|4|5|6|7|8|9");
		toCache2.put("Pigman", "1|2|3|4|5|6|7|8|9");
		toCache2.put("Spider", "1|2|3|4|5|6|7|8|9");
		toCache2.put("Pirate", "1|2|3|4|5|6|7|8|9");
		toCache2.put("Squid", "1|2|3|4|5|6|7|8|9");
		toCache2.put("Hunter", "1|2|3|4|5|6|7|8|9");
		kitCustomizerCache.put(uuid, toCache2);
		System.out.println("[Taken Kits] New Database entry for " + p.getName());
	}

	private synchronized static void openConnection() {
		try {
			connection = DriverManager.getConnection("jdbc:mysql://173.79.225.185:3306/TakenGaming", "root", "Laxbro12");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private synchronized static void closeConnection() {
		try {
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void removeBlock(Location b) {
		blocks.remove(b);
	}

	public void fixPlayer(Player player) {
		player.teleport(player.getLocation().add(0.0D, 1.1D, 0.0D));
		refreshPlayer(player);
	}

	public void refreshPlayer(final Player player) {
		for (Player players : Bukkit.getOnlinePlayers()) {
			players.hidePlayer(player);
		}
		Bukkit.getScheduler().runTaskLater(this, new Runnable() {
			public void run() {
				for (Player players : Bukkit.getOnlinePlayers()) {
					players.showPlayer(player);
				}
			}
		}, 2L);
	}

	public boolean isNumeric(String str) {
		try {
			Integer.parseInt(str);
		} catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	private Player getNearestPlayer(Player p) {
		Player nearestPlayer = p;
		double distanceSquared = Double.MAX_VALUE;
		for (Player r : Bukkit.getOnlinePlayers()) {
			if (r.getWorld().getName().equalsIgnoreCase(p.getWorld().getName())) {
				if (p.getLocation().distanceSquared(r.getLocation()) != 0 && p.getLocation().distanceSquared(r.getLocation()) < distanceSquared) {
					distanceSquared = p.getLocation().distanceSquared(r.getLocation());
					nearestPlayer = r;
				}
			}
		}
		return nearestPlayer;
	}

	public int getMultipliedCoins(Player p, int coins) {
		int multiplier = 1;
		if (p.hasPermission("Taken.Multiplier.5")) {
			multiplier = 5;
			if (Corrupted.getGlobalCoinMultiplier() == 1 && !(getActiveBoosters().containsKey(p.getUniqueId()))) {
				return coins * (multiplier);
			} else {
				if (Corrupted.getGlobalCoinMultiplier() != 1) {
					multiplier += Corrupted.getGlobalCoinMultiplier();
				}
				if (getActiveBoosters().containsKey(p.getUniqueId())) {
					multiplier += 2;
				}
				return coins * (multiplier);
			}
		} else if (p.hasPermission("Taken.Multiplier.4")) {
			multiplier = 4;
			if (Corrupted.getGlobalCoinMultiplier() == 1 && !(getActiveBoosters().containsKey(p.getUniqueId()))) {
				return coins * (multiplier);
			} else {
				if (Corrupted.getGlobalCoinMultiplier() != 1) {
					multiplier += Corrupted.getGlobalCoinMultiplier();
				}
				if (getActiveBoosters().containsKey(p.getUniqueId())) {
					multiplier += 2;
				}
				return coins * (multiplier);
			}
		} else if (p.hasPermission("Taken.Multiplier.3")) {
			multiplier = 3;
			if (Corrupted.getGlobalCoinMultiplier() == 1 && !(getActiveBoosters().containsKey(p.getUniqueId()))) {
				return coins * (multiplier);
			} else {
				if (Corrupted.getGlobalCoinMultiplier() != 1) {
					multiplier += Corrupted.getGlobalCoinMultiplier();
				}
				if (getActiveBoosters().containsKey(p.getUniqueId())) {
					multiplier += 2;
				}
				return coins * (multiplier);
			}
		} else if (p.hasPermission("Taken.Multiplier.2")) {
			multiplier = 2;
			if (Corrupted.getGlobalCoinMultiplier() == 1 && !(getActiveBoosters().containsKey(p.getUniqueId()))) {
				return coins * (multiplier);
			} else {
				if (Corrupted.getGlobalCoinMultiplier() != 1) {
					multiplier += Corrupted.getGlobalCoinMultiplier();
				}
				if (getActiveBoosters().containsKey(p.getUniqueId())) {
					multiplier += 2;
				}
				return coins * (multiplier);
			}
		} else {
			multiplier = 0;
			if (Corrupted.getGlobalCoinMultiplier() == 1 && !(getActiveBoosters().containsKey(p.getUniqueId()))) {
				return coins;
			} else {
				if (Corrupted.getGlobalCoinMultiplier() != 1) {
					multiplier += Corrupted.getGlobalCoinMultiplier();
				}
				if (getActiveBoosters().containsKey(p.getUniqueId())) {
					multiplier += 2;
				}
				return coins * (multiplier);
			}
		}
	}

	public void updateScoreboard(Player p) {
		HashMap<String, Object> values = databaseCache.get(p.getUniqueId().toString());
		Scoreboard board = sbm.getNewScoreboard();
		board.clearSlot(DisplaySlot.SIDEBAR);
		if (board.getObjective("PlayerStatistics") != null)
			board.getObjective("PlayerStatistics").unregister();
		Objective obj = board.registerNewObjective("PlaObjectistics", "PlayerStatistics");
		obj.setDisplayName("§bTaken Kits §c[BETA]");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		Objective health = board.getObjective(DisplaySlot.BELOW_NAME) == null ? board.registerNewObjective("§c❤", "health") : board.getObjective(DisplaySlot.BELOW_NAME);
		health.setDisplaySlot(DisplaySlot.BELOW_NAME);
		for (Player r : Bukkit.getOnlinePlayers()) {
			if (nickedPlayers.containsKey(r.getName())) {
				health.getScore(nickedPlayers.get(r.getName())).setScore(Math.toIntExact(Math.round(((CraftPlayer) r).getHealth())));
				if (health.getScore(nickedPlayers.get(r.getName())).getScore() == 0) {
					health.getScore(nickedPlayers.get(r.getName())).setScore(Math.toIntExact(Math.round(((CraftPlayer) r).getHealth())));
				}
			}

			if (health.getScore(r.getName()).getScore() == 0) {
				health.getScore(r.getName()).setScore(Math.toIntExact(Math.round(((CraftPlayer) r).getHealth())));
			}
		}
		if (values != null) {
			Score spacer0 = obj.getScore("        ");
			spacer0.setScore(13);
			Score Title1v1 = obj.getScore("§a[1v1]");
			Title1v1.setScore(12);
			Score rating1v1Score = obj.getScore("Rating: §e" + values.get("1v1Rating") + " ");
			rating1v1Score.setScore(11);
			Score spacer1 = obj.getScore("   ");
			spacer1.setScore(10);
			Score Title2v2 = obj.getScore("§a[2v2]");
			Title2v2.setScore(9);
			Score rating2v2Score = obj.getScore("Rating: §e" + values.get("2v2Rating") + "  ");
			rating2v2Score.setScore(8);
			Score spacer2 = obj.getScore("  ");
			spacer2.setScore(7);
			Score TitleFFA = obj.getScore("§a[FFA]");
			TitleFFA.setScore(6);
			Score ratingFFAScore = obj.getScore("Rating: §e" + values.get("FFARating") + "");
			ratingFFAScore.setScore(5);
			Score killsFFA = obj.getScore("Kills: §e" + values.get("FFAKills") + "");
			killsFFA.setScore(4);
			Score deathsFFA = obj.getScore("Deaths: §e" + values.get("FFADeaths") + "");
			deathsFFA.setScore(3);
			Score spacer4 = obj.getScore("     ");
			spacer4.setScore(2);
			if(values.get("Coins").toString().length() > 15){
			Score coins = obj.getScore(("Coins: §e" + values.get("Coins") + "").substring(0, 15));
			coins.setScore(1);
			}else{
			Score coins = obj.getScore(("Coins: §e" + values.get("Coins") + ""));
			coins.setScore(1);
			}
			Score spins = obj.getScore("Spins: §b" + values.get("Spins") + "");
			spins.setScore(0);
			p.setScoreboard(board);
		}

	}

	public int rand(int min, int max) {
		max += 1;
		return min + (new Random()).nextInt(max - min);
	}

	public Double roundTwoDecimals(double d) {

		DecimalFormat twoDForm = new DecimalFormat("#.##");
		return Double.valueOf(twoDForm.format(d));

	}

	public void damagePure(Player p, Double dmg, Entity damager) {
		if (!p.isDead() && p.getGameMode() != GameMode.CREATIVE) {
			if (damager instanceof Player) {
				if (Team.sameTeam(p, (Player) damager)) {
					return;
				}
			}
			if (dmg <= 0)
				return;
			p.damage(0.01, damager);
			if (((CraftPlayer) p).getHealth() - dmg <= 0D)
				p.setHealth(0D);
			else p.setHealth(((CraftPlayer) p).getHealth() - dmg);
		}
	}

	public void damageImpure(Player p, Double dmg, Entity damager) {
		if (!p.isDead() && p.getGameMode() != GameMode.CREATIVE) {
			if (damager instanceof Player) {
				if (Team.sameTeam(p, (Player) damager)) {
					return;
				}
			}
			if (dmg <= 0)
				return;
			ItemStack boots = p.getInventory().getBoots();
			ItemStack helmet = p.getInventory().getLeggings();
			ItemStack chest = p.getInventory().getChestplate();
			ItemStack pants = p.getInventory().getLeggings();
			double red = 0.0;
			if (helmet != null) {
				if (helmet.getType() == Material.LEATHER_HELMET) {
					red = red + 0.04;
				} else if (helmet.getType() == Material.GOLD_HELMET) {
					red = red + 0.08;
				} else if (helmet.getType() == Material.CHAINMAIL_HELMET) {
					red = red + 0.08;
				} else if (helmet.getType() == Material.IRON_HELMET) {
					red = red + 0.08;
				} else if (helmet.getType() == Material.DIAMOND_HELMET) {
					red = red + 0.12;
				}
			}
			//
			if (boots != null) {
				if (boots.getType() == Material.LEATHER_BOOTS) {
					red = red + 0.04;
				} else if (boots.getType() == Material.GOLD_BOOTS) {
					red = red + 0.04;
				} else if (boots.getType() == Material.CHAINMAIL_BOOTS) {
					red = red + 0.04;
				} else if (boots.getType() == Material.IRON_BOOTS) {
					red = red + 0.08;
				} else if (boots.getType() == Material.DIAMOND_BOOTS) {
					red = red + 0.12;
				}
			}
			//
			if (pants != null) {
				if (pants.getType() == Material.LEATHER_LEGGINGS) {
					red = red + 0.08;
				} else if (pants.getType() == Material.GOLD_LEGGINGS) {
					red = red + 0.12;
				} else if (pants.getType() == Material.CHAINMAIL_LEGGINGS) {
					red = red + 0.16;
				} else if (pants.getType() == Material.IRON_LEGGINGS) {
					red = red + 0.20;
				} else if (pants.getType() == Material.DIAMOND_LEGGINGS) {
					red = red + 0.24;
				}
			}//
			if (chest != null) {
				if (chest.getType() == Material.LEATHER_CHESTPLATE) {
					red = red + 0.12;
				} else if (chest.getType() == Material.GOLD_CHESTPLATE) {
					red = red + 0.20;
				} else if (chest.getType() == Material.CHAINMAIL_CHESTPLATE) {
					red = red + 0.20;
				} else if (chest.getType() == Material.IRON_CHESTPLATE) {
					red = red + 0.24;
				} else if (chest.getType() == Material.DIAMOND_CHESTPLATE) {
					red = red + 0.32;
				}
			}
			p.damage(0.01, damager);
			dmg *= red;
			if (((CraftPlayer) p).getHealth() - dmg <= 0D)
				p.setHealth(0D);
			else p.setHealth(((CraftPlayer) p).getHealth() - dmg);
		}
	}

	public ItemStack addGlow(ItemStack item) {
		net.minecraft.server.v1_7_R4.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
		NBTTagCompound tag = null;
		if (!nmsStack.hasTag()) {
			tag = new NBTTagCompound();
			nmsStack.setTag(tag);
		}
		if (tag == null)
			tag = nmsStack.getTag();
		NBTTagList ench = new NBTTagList();
		tag.set("ench", ench);
		nmsStack.setTag(tag);
		return CraftItemStack.asCraftMirror(nmsStack);
	}

	public void setDefaultInventory(Player p) {
		p.getInventory().clear();
		p.setLevel(0);
		p.setExp(0F);
		p.removeMetadata("Class", plugin);
		p.setMaxHealth(40.0);
		p.setHealth(40.0);
		p.getInventory().setArmorContents(new ItemStack[4]);
		if (!databaseCache.containsKey(p.getUniqueId().toString()))
			return;
		switch (VanityArmor.getSelectedVanityArmor(p)) {
			case NONE:
				break;
			case CHAINMAIL:
				p.getInventory().setChestplate(IconMenu.setItemNameAndLore(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1), "§bVanity Armor", new String[0]));
				p.getInventory().setLeggings(IconMenu.setItemNameAndLore(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1), "§bVanity Armor", new String[0]));
				p.getInventory().setBoots(IconMenu.setItemNameAndLore(new ItemStack(Material.CHAINMAIL_BOOTS, 1), "§bVanity Armor", new String[0]));
				break;
			case IRON:
				p.getInventory().setChestplate(IconMenu.setItemNameAndLore(new ItemStack(Material.IRON_CHESTPLATE, 1), "§bVanity Armor", new String[0]));
				p.getInventory().setLeggings(IconMenu.setItemNameAndLore(new ItemStack(Material.IRON_LEGGINGS, 1), "§bVanity Armor", new String[0]));
				p.getInventory().setBoots(IconMenu.setItemNameAndLore(new ItemStack(Material.IRON_BOOTS, 1), "§bVanity Armor", new String[0]));
				break;
			case GOLD:
				p.getInventory().setChestplate(IconMenu.setItemNameAndLore(new ItemStack(Material.GOLD_CHESTPLATE, 1), "§bVanity Armor", new String[0]));
				p.getInventory().setLeggings(IconMenu.setItemNameAndLore(new ItemStack(Material.GOLD_LEGGINGS, 1), "§bVanity Armor", new String[0]));
				p.getInventory().setBoots(IconMenu.setItemNameAndLore(new ItemStack(Material.GOLD_BOOTS, 1), "§bVanity Armor", new String[0]));
				break;
			case DIAMOND:
				p.getInventory().setChestplate(IconMenu.setItemNameAndLore(new ItemStack(Material.DIAMOND_CHESTPLATE, 1), "§bVanity Armor", new String[0]));
				p.getInventory().setLeggings(IconMenu.setItemNameAndLore(new ItemStack(Material.DIAMOND_LEGGINGS, 1), "§bVanity Armor", new String[0]));
				p.getInventory().setBoots(IconMenu.setItemNameAndLore(new ItemStack(Material.DIAMOND_BOOTS, 1), "§bVanity Armor", new String[0]));
				break;
		}
		p.getInventory().setItem(1, new BetterItem(Material.IRON_SWORD, 1, "§bRanked 1v1 Queue §7(Right Click)").grab());
		p.getInventory().setItem(2, new BetterItem(Material.DIAMOND_SWORD, 1, "§bRanked 2v2 Queue §7(Right Click)").grab());
		p.getInventory().setItem(3, new BetterItem(Material.GOLD_NUGGET, 1, "§aShop §7(Right Click)").grab());
		p.getInventory().setItem(0, new BetterItem(Material.FEATHER, 1, "§aFFA Class Selector §7(Right Click)").grab());
		p.getInventory().setItem(4, new BetterItem(Material.NAME_TAG, 1, "§eCreate Team").grab());
		if (p.hasPermission("Taken.staff")) {
			p.getInventory().setItem(8, new BetterItem(Material.EYE_OF_ENDER, 1, "§c§lMOD MODE").grab());
		}
		p.updateInventory();
	}

	public void setQueuedInventory(Player p) {
		p.getInventory().clear();
		p.setLevel(0);
		p.setExp(0F);
		p.removeMetadata("Class", plugin);
		p.getInventory().setItem(0, new BetterItem(Material.REDSTONE, 1, "§cLeave Queue §7(Right Click)").grab());
		p.updateInventory();
	}

	public HashMap<UUID, Integer> getActiveBoosters() {
		HashMap<UUID, Integer> list = new HashMap<UUID, Integer>();
		if (getConfig().getConfigurationSection("Boosters") != null) {
			for (String s : getConfig().getConfigurationSection("Boosters").getKeys(false)) {
				if (getConfig().get("Boosters." + s + ".timeLeft") != null) {
					list.put(UUID.fromString(s), getConfig().getInt("Boosters." + s + ".timeLeft"));
				}
			}
		}
		return list;

	}

	public void addBooster(Player p, int time) {
		if (getConfig().get("Boosters." + p.getUniqueId().toString() + ".timeLeft") != null) {
			getConfig().set("Boosters." + p.getUniqueId().toString() + ".timeLeft", getConfig().getInt("Boosters." + p.getUniqueId().toString() + ".timeLeft") + time);
			Chat.sendPlayerPrefixedMessage(p, "§b" + (((double) time) / 60.0 / 60.0) + "§e hour(s) have been added to your current 2x booster time");
		} else {
			getConfig().set("Boosters." + p.getUniqueId().toString() + ".timeLeft", time);
			Chat.sendPlayerPrefixedMessage(p, "§eYour §b" + (((double) time) / 60.0 / 60.0) + "§e hour 2x booster has been activated");
		}
		saveConfig();
	}

	public void addEnergy(Player p, int energy) {
		if (p.getLevel() >= 100)
			return;
		if (p.getLevel() + energy > 100) {
			p.setLevel(100);
			Chat.sendAbilityMessage(p);
		} else p.giveExpLevels(energy);
	}

	public boolean isActivateAbilityEvent(PlayerInteractEvent event) {
		if (event.getItem() == null || event.getPlayer().getLevel() < 100)
			return false;
		if ((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && (event.getItem().getType() == Material.IRON_SWORD || event.getItem().getType() == Material.IRON_AXE || event.getItem().getType() == Material.DIAMOND_SWORD))
			return true;
		else if ((event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK)) && event.getItem().getType().equals(Material.BOW))
			return true;
		return false;
	}

	public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
		for (Entry<T, E> entry : map.entrySet()) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public void addNoReductionEffect(Player p, PotionEffect effect) {
		for (PotionEffect pe : p.getActivePotionEffects()) {
			if (pe.getDuration() > 100000)
				continue;
			if (pe.getType().getId() == effect.getType().getId()) {
				if (pe.getAmplifier() > effect.getAmplifier() || (pe.getAmplifier() == effect.getAmplifier() && pe.getDuration() > effect.getDuration()))
					continue;
				p.removePotionEffect(effect.getType());
				p.addPotionEffect(effect);
				continue;
			} else continue;
		}
		p.addPotionEffect(effect);
	}

	public static IconMenus getMenus() {
		return menus;
	}

	public static EffectManager getEffectManager() {
		return effectManager;
	}

	public static Connection getConnection() {
		return connection;
	}

	public static HashMap<String, HashMap<String, ItemStack>> getKitItems() {
		return kitItems;
	}

	public static ArrayList<Location> getBlocks() {
		return blocks;
	}

	public static ConcurrentHashMap<String, HashMap<String, Object>> getDatabaseCache() {
		return databaseCache;
	}

	public static ConcurrentHashMap<String, HashMap<String, String>> getKitCustomizerCache() {
		return kitCustomizerCache;
	}

	public static ArrayList<Arena> getArenas1v1() {
		return arenas1v1;
	}

	public static ArrayList<Arena> getArenas2v2() {
		return arenas2v2;
	}
	
	public static ArrayList<Arena> getArenasDuel() {
		return arenasDuel;
	}
	
	public static HashMap<String, Game> getGames() {
		return games;
	}

	public static int[] getInventorySpots() {
		return inventorySpots;
	}

	public static Queue getQueue() {
		return queue;
	}

	public static ArrayList<String> getStaffChat() {
		return staffChat;
	}

	public static int getGlobalCoinMultiplier() {
		return globalCoinMultiplier;
	}

	public static void setGlobalCoinMultiplier(int globalCoinMultiplier) {
		Corrupted.globalCoinMultiplier = globalCoinMultiplier;
	}

	public static boolean isSTFU() {
		return isSTFU;
	}

	public static void setSTFU(boolean isSTFU) {
		Corrupted.isSTFU = isSTFU;
	}

	public static HashMap<UUID, PermissionAttachment> getAttachments() {
		return attachments;
	}

	public static AccessMode getMode() {
		return mode;
	}

	public static void setMode(AccessMode mode) {
		Corrupted.mode = mode;
	}

	public static HashMap<String, Team> getTeams() {
		return teams;
	}

	public static HashMap<Game, ArrayList<String>> getSpectators() {
		return spectators;
	}

	public static HashMap<String, String> getCooldowns() {
		return cooldowns;
	}

	public static ArrayList<String> getInvitesToggled() {
		return invitesToggled;
	}

	public static HashMap<String, String> getModMode() {
		return modMode;
	}

	public Broadcaster getBroadcaster() {
		return broadcaster;
	}

	public static HashMap<String, String> getNickedPlayers() {
		return nickedPlayers;
	}

	public static HashMap<String, String> getInvites() {
		return invites;
	}

	public static HashMap<String, Duel> getDuels() {
		return duels;
	}
}