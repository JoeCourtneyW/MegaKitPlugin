package com.sly.main.player;

import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import com.sly.main.Server;
import com.sly.main.database.Cell;
import com.sly.main.database.Cell.CellValue;
import com.sly.main.database.Row;
import com.sly.main.economy.Booster;
import com.sly.main.economy.Economy;
import com.sly.main.enums.Group;
import com.sly.main.enums.VanityArmor;
import com.sly.main.gametype.Game;
import com.sly.main.gametype.GameType;
import com.sly.main.gametype.Squad;
import com.sly.main.kits.Kits;
import com.sly.main.resources.BetterItem;
import com.sly.main.resources.MathUtil;
import com.sly.main.server.Chat;

import net.md_5.bungee.api.ChatColor;

public class PlayerModel
{

	private UUID uuid = null;
	private PermissionAttachment permissionAttachment = null;
	private KitCustomizer kitCustomizer = null;
	private Economy economy = null;
	private Kits playerKit = Kits.NONE;
	private Game game = null;
	private Squad squad = null;
	private Kits chosenKit = Kits.NONE; // Used while queuing for games, need to store the chosen kit somewhere
	private boolean isSpectating = false;
	private boolean isModMode = false;
	private boolean isStaffChat = false;
	private boolean isInviteToggled = false;
	private PlayerModel invitedBy = null;
	private String nickname = "";
	private ArrayList<PlayerModel> watchedBy = new ArrayList<PlayerModel>();
	private PlayerModel watching = null;
	private long boosterTimeLeft = 0;

	private PlayerModel(UUID uuid) {
		this.uuid = uuid;
		this.kitCustomizer = new KitCustomizer(this);
		this.economy = new Economy(this);

	}

	public UUID getUUID() {
		return this.uuid;
	}

	public Location getSpawnPoint() {// TODO: custom spawnpoints?
		return new Location(Bukkit.getWorld("Nube"), -549.5, 101, -552.5, 206.62F, 1.82F);
	}

	public PermissionAttachment getPermissionAttachment() {
		return permissionAttachment;
	}

	public void setPermissionAttachment(PermissionAttachment permissionAttachment) {
		this.permissionAttachment = permissionAttachment;
	}

	public Kits getKit() {
		return this.playerKit;
	}

	public void setKit(Kits k) {
		this.playerKit = k;
	}

	public Kits getChosenKit() {
		return chosenKit;
	}

	public void setChosenKit(Kits chosenKit) {
		this.chosenKit = chosenKit;
	}

	public boolean isSpectating() {
		return this.isSpectating;
	}

	public void setSpectating(boolean isSpectating) {
		this.isSpectating = isSpectating;
	}

	public boolean isModMode() {
		return this.isModMode;
	}

	public void setModMode(boolean isModMode) {
		this.isModMode = isModMode;
	}

	public boolean isStaffChat() {
		return this.isStaffChat;
	}

	public void setStaffChat(boolean isStaffChat) {
		this.isStaffChat = isStaffChat;
	}

	public boolean isInviteToggled() {
		return this.isInviteToggled;
	}

	public void setInviteToggle(boolean inviteToggled) {
		this.isInviteToggled = inviteToggled;
	}

	public PlayerModel getInvitedBy() {
		return invitedBy;
	}

	public void setInvitedBy(PlayerModel invitedBy) {
		this.invitedBy = invitedBy;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public ArrayList<PlayerModel> getWatchedBy() {
		return watchedBy;
	}

	public PlayerModel getWatching() {
		return watching;
	}

	public void setWatching(PlayerModel watching) {
		this.watching = watching;
	}

	public boolean isOnline() {
		return Bukkit.getPlayer(uuid) != null;
	}

	public boolean isInGame() {
		return game != null && !game.isOver(); // Game is not null and is not over
	}

	public void setGame(Game game) {
		this.game = game;
	}

	public Game getGame() {
		return this.game;
	}

	public boolean isInSquad() {
		return squad != null && !squad.isDissolved(); // Squad is not null and is not over
	}

	public boolean isSquadLeader() {
		return isInSquad() // Make sure they're in a squad
				&& squad.getLeader().getUUID().equals(getUUID());// Leader's UUID is the same as the player's UUID
	}

	public void setSquad(Squad squad) {
		this.squad = squad;
	}

	public Squad getSquad() {
		return this.squad;
	}

	public boolean sameSquad(PlayerModel otherPlayer) { // Make sure both players are in a squad, and it has the same id
		return otherPlayer.getSquad() != null && otherPlayer.getSquad() != null
				&& otherPlayer.getSquad().getId() == getSquad().getId();
	}

	public Player getPlayer() {
		return Bukkit.getPlayer(uuid);
	}

	public CraftPlayer getCraftPlayer() {
		return (CraftPlayer) Bukkit.getPlayer(uuid);
	}

	public String getName() {
		return getPlayer().getName();
	}

	public Economy getEconomy() {
		return economy;
	}

	public KitCustomizer getKitCustomizer() {
		return kitCustomizer;
	}

	public void sendToSpawn() {
		resetEnergy();
		setFullHealth();
		setKit(Kits.NONE);
		setSpectating(false);
		getPlayer().teleport(getSpawnPoint());
		getPlayer().setNoDamageTicks(0);
		boolean flying = getPlayer().hasPermission("Taken.FlyInLobby");
		getPlayer().setAllowFlight(flying);
		getPlayer().setFlying(flying);
		setDefaultInventory();

	}

	public void setFullHealth() {
		getPlayer().setMaxHealth(40.0);
		getPlayer().setHealth(40.0);
	}

	public void addEnergy(int energy) {
		Player p = getPlayer();
		if (p.getLevel() >= 100)
			return;
		if (p.getLevel() + energy > 100) {
			p.setLevel(100);
			sendAbilityMessage();
		} else
			p.giveExpLevels(energy);
	}

	public boolean isFullEnergy() {
		return getPlayer().getLevel() >= 100;
	}

	public void resetEnergy() {
		getPlayer().setLevel(0);
	}

	public void fixPlayer() {
		Player p = getPlayer();
		p.teleport(p.getLocation().add(0.0D, 1.1D, 0.0D));
		refreshPlayer();
	}

	@SuppressWarnings("deprecation")
	private void refreshPlayer() {
		final Player p = getPlayer();
		for (Player players : Bukkit.getOnlinePlayers()) {
			players.hidePlayer(p);
		}
		new BukkitRunnable() {
			public void run() {
				for (Player players : Bukkit.getOnlinePlayers()) {
					players.showPlayer(p);
				}
			}
		}.runTaskLater(Server.getInstance(), 2L);

	}

	@SuppressWarnings("deprecation")
	public Player getNearestPlayer() {
		Player p = getPlayer();
		Player nearestPlayer = p;
		double distanceSquared = Double.MAX_VALUE;
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			if (onlinePlayer.getWorld().getName().equalsIgnoreCase(p.getWorld().getName())) {
				if (p.getLocation().distanceSquared(onlinePlayer.getLocation()) != 0
						&& p.getLocation().distanceSquared(onlinePlayer.getLocation()) < distanceSquared) {
					distanceSquared = p.getLocation().distanceSquared(onlinePlayer.getLocation());
					nearestPlayer = onlinePlayer;
				}
			}
		}
		return nearestPlayer;
	}

	public boolean hasBooster() {
		return boosterTimeLeft > 0L;
	}

	public long getBoosterTime() {
		return boosterTimeLeft;
	}

	public void tickBoosterTime() {
		if (boosterTimeLeft > 0)
			boosterTimeLeft--;

	}

	public void addBooster(Booster booster) {
		boosterTimeLeft += booster.getTime();
		if (hasBooster()) {
			sendPrefixedMessage("§b" + booster.getTimeInHours()
					+ "§e hour(s) have been added to your current 2x coin booster time");
		} else {
			sendPrefixedMessage("§eYour §b" + booster.getTimeInHours() + "§e hour 2x coin booster has been activated");
		}
	}

	public void setQueuedInventory() {
		Player p = getPlayer();
		p.getInventory().clear();
		p.setLevel(0);
		p.setExp(0F);
		p.getInventory().setItem(0,
				new BetterItem().material(Material.REDSTONE).displayName("§cLeave Queue §7(Right Click)").grab());
		p.updateInventory();
	}

	public void setModModeInventory() {
		Player p = getPlayer();
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[4]);

		p.getInventory().setItem(4,
				new BetterItem().material(Material.EYE_OF_ENDER).displayName("§c§lLEAVE MOD MODE").grab());
		p.updateInventory();
	}

	public void setSpectatingInventory() {
		Player p = getPlayer();
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[4]);
		p.updateInventory();
	}

	public void setDefaultInventory() {
		setFullHealth();
		resetEnergy();

		Player p = getPlayer();
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[4]);

		BetterItem template = new BetterItem().displayName("§bVanity Armor");
		switch (VanityArmor.getSelectedVanityArmor(this))
		{
		default:
			break;
		case CHAINMAIL:
			p.getInventory().setChestplate(template.clone().material(Material.CHAINMAIL_CHESTPLATE).grab());
			p.getInventory().setLeggings(template.clone().material(Material.CHAINMAIL_LEGGINGS).grab());
			p.getInventory().setBoots(template.clone().material(Material.CHAINMAIL_BOOTS).grab());
			break;
		case IRON:
			p.getInventory().setChestplate(template.clone().material(Material.IRON_CHESTPLATE).grab());
			p.getInventory().setLeggings(template.clone().material(Material.IRON_LEGGINGS).grab());
			p.getInventory().setBoots(template.clone().material(Material.IRON_BOOTS).grab());
			break;
		case GOLD:
			p.getInventory().setChestplate(template.clone().material(Material.GOLD_CHESTPLATE).grab());
			p.getInventory().setLeggings(template.clone().material(Material.GOLD_LEGGINGS).grab());
			p.getInventory().setBoots(template.clone().material(Material.GOLD_BOOTS).grab());
			break;
		case DIAMOND:
			p.getInventory().setChestplate(template.clone().material(Material.DIAMOND_CHESTPLATE).grab());
			p.getInventory().setLeggings(template.clone().material(Material.DIAMOND_LEGGINGS).grab());
			p.getInventory().setBoots(template.clone().material(Material.DIAMOND_BOOTS).grab());
			break;
		}
		
		p.getInventory().setItem(0,
				new BetterItem().material(Material.FEATHER).displayName("§aFFA Class Selector §7(Right Click)").grab());
		p.getInventory().setItem(1, new BetterItem().material(Material.IRON_SWORD)
				.displayName("§bRanked 1v1 Queue §7(Right Click)").grab());
		p.getInventory().setItem(2, new BetterItem().material(Material.DIAMOND_SWORD)
				.displayName("§bRanked 2v2 Queue §7(Right Click)").grab());
		p.getInventory().setItem(3,
				new BetterItem().material(Material.GOLD_NUGGET).displayName("§aShop §7(Right Click)").grab());
		p.getInventory().setItem(4, new BetterItem().material(Material.NAME_TAG).displayName("§eCreate Squad").grab());

		if (p.hasPermission("Taken.staff")) {
			p.getInventory().setItem(8,
					new BetterItem().material(Material.EYE_OF_ENDER).displayName("§c§lMOD MODE").grab());
		}
		p.updateInventory();
	}

	public void addNoReductionEffect(PotionEffect effect) {
		Player p = getPlayer();
		for (PotionEffect activeEffect : p.getActivePotionEffects()) {
			if (activeEffect.getDuration() > 100000)
				continue;
			if (activeEffect.getType() == effect.getType()) {
				if (activeEffect.getAmplifier() > effect.getAmplifier()
						|| (activeEffect.getAmplifier() == effect.getAmplifier()
								&& activeEffect.getDuration() > effect.getDuration()))
					continue;
				p.removePotionEffect(effect.getType());
				p.addPotionEffect(effect);
				continue;
			} else
				continue;
		}
		p.addPotionEffect(effect);
	}

	public void damagePure(Double damage, Entity damager) {
		Player p = getPlayer();
		CraftPlayer cp = getCraftPlayer();
		// Make sure they're alive, in survival mode, and have a kit
		if (!p.isDead() && p.getGameMode() != GameMode.CREATIVE && getKit() != Kits.NONE) {
			if (damager instanceof Player) {
				if (this.sameSquad(PlayerModel.getPlayerModel((Player) damager))) {
					return; // **If the damager is a player, make sure they aren't in a squad**
				}
			}
			if (damage <= 0) // Don't apply negative damage
				return;

			p.damage(0.01, damager); // Damage the player using bukkit method to cause flinch and sound effect

			damage = MathUtil.capValue(damage, cp.getHealth()); // Make sure not to damage them for more than they have

			p.setHealth(cp.getHealth() - damage);
		}
	}

	public void damageImpure(Double damage, Entity damager) {
		Player p = getPlayer();
		CraftPlayer cp = getCraftPlayer();
		if (!p.isDead() && p.getGameMode() != GameMode.CREATIVE && getKit() != Kits.NONE) {
			if (damager instanceof Player) {
				if (this.sameSquad(PlayerModel.getPlayerModel((Player) damager))) {
					return; // **If the damager is a player, make sure they aren't in a squad**
				}
			}
			if (damage <= 0) // Don't apply negative damage
				return;
			double damageReduction = MathUtil.getArmorReductionPercentage(this); // How much armor guards against damage
			damage *= damageReduction; // Reduction will always be between 0.20 and 1.00;

			p.damage(0.01, damager); // Damage the player using bukkit method to cause flinch and sound effect

			damage = MathUtil.capValue(damage, cp.getHealth()); // Make sure not to damage them for more than they have

			p.setHealth(cp.getHealth() - damage);
		}
	}

	public Row getDatabaseRow() {
		return Server.getPlayerDataTable().getRow(getUUID());
	}

	public CellValue getDatabaseValue(String column) {
		return getDatabaseRow().getCell(column).getValue();
	}

	public Cell getDatabaseCell(String column) {
		return getDatabaseRow().getCell(column);
	}

	@SuppressWarnings("deprecation")
	public void updateScoreboard() {
		Player p = getPlayer();
		Scoreboard board = Server.getInstance().getScoreboardManager().getNewScoreboard();
		board.clearSlot(DisplaySlot.SIDEBAR);
		if (board.getObjective("PlayerStatistics") != null)
			board.getObjective("PlayerStatistics").unregister();
		Objective obj = board.registerNewObjective("PlaObjectistics", "PlayerStatistics");
		obj.setDisplayName("§bTaken Kits §c[BETA]");
		obj.setDisplaySlot(DisplaySlot.SIDEBAR);
		Objective health = board.getObjective(DisplaySlot.BELOW_NAME) == null
				? board.registerNewObjective("§c�?�", "health") : board.getObjective(DisplaySlot.BELOW_NAME);
		health.setDisplaySlot(DisplaySlot.BELOW_NAME);
		PlayerModel player;
		for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
			player = getPlayerModel(onlinePlayer);
			if (!player.getNickname().equalsIgnoreCase("")) {
				health.getScore(player.getNickname())
						.setScore(Math.toIntExact(Math.round(player.getCraftPlayer().getHealth())));
				if (health.getScore(player.getNickname()).getScore() == 0) {
					health.getScore(player.getNickname())
							.setScore(Math.toIntExact(Math.round(player.getCraftPlayer().getHealth())));
				}
			}

			if (health.getScore(player.getPlayer().getName()).getScore() == 0) {
				health.getScore(player.getPlayer().getName())
						.setScore(Math.toIntExact(Math.round(player.getCraftPlayer().getHealth())));
			}
		}
		obj.getScore("        ").setScore(13);
		obj.getScore("§a[1v1]").setScore(12);
		obj.getScore("Rating: §e" + getDatabaseValue("1v1Rating") + " ").setScore(11);
		obj.getScore("   ").setScore(10);
		obj.getScore("§a[2v2]").setScore(9);
		obj.getScore("Rating: §e" + getDatabaseValue("2v2Rating") + "  ").setScore(8);
		obj.getScore("  ").setScore(7);
		obj.getScore("§a[FFA]").setScore(6);
		obj.getScore("Rating: §e" + getDatabaseValue("FFARating") + "").setScore(5);
		obj.getScore("Kills: §e" + getDatabaseValue("FFAKills") + "").setScore(4);
		obj.getScore("Deaths: §e" + getDatabaseValue("FFADeaths") + "").setScore(3);
		obj.getScore("     ").setScore(2);
		int cap = getDatabaseValue("Coins").toString().length() > 15 ? 15
				: getDatabaseValue("Coins").toString().length() - 1;
		obj.getScore(("Coins: §e" + getDatabaseValue("Coins") + "").substring(0, cap)).setScore(1);
		obj.getScore("Spins: §b" + getDatabaseValue("Spins") + "").setScore(0);
		p.setScoreboard(board);
	}

	public void pushRowToDatabase() {
		Server.getPlayerDataTable().updateEntry(getDatabaseRow());
	}

	public void updateElo(GameType gameType, int delta) {
		getDatabaseCell(gameType.getRatingColumnName()).mutateValue(r -> r + delta); // Sum will also decrease if delta is negative
	}

	public double getElo(GameType gameType) {
		return getDatabaseValue(gameType.getRatingColumnName()).asDouble();
	}

	public void sendMessage(String message) {
		getPlayer().sendMessage(message);
	}

	public void sendPrefixedMessage(String message) {
		getPlayer().sendMessage(Chat.PREFIX + message);
	}

	public void sendAbilityMessage() {
		Kits kit = getKit();
		if (kit != Kits.NONE) {
			sendMessage("§aYour §b§l" + kit.getAbility() + "§a Skill is ready!");
			if (kit == Kits.SKELETON)
				sendMessage("§bLeft Click §awith any bow to activate your skill!");
			else
				sendMessage("§bRight Click §awith any sword to activate your skill!");
		}
	}

	public String getFormattedChatMessage(String message) {
		Group group;
		ChatColor msgColor = ChatColor.GRAY;
		String prefix = Group.DEFAULT.getPrefix();
		for (int i = Group.values().length - 1; i >= 0; i--) { // Start a loop at OWNER and loop downwards to get most important ranks first
			group = Group.values()[i];
			if (group.isInGroupExplicity(this) && group != Group.DEFAULT) { // If they're in the group and it's not default
				prefix = group.getPrefix();
				msgColor = ChatColor.WHITE;
				break;
			}
		} // Loop will give the highest ranking prefix that the user has
		return prefix + getName() + "§7:" + msgColor + message;
	}

	public static boolean playerExists(UUID uuid) {
		return getPlayerModel(uuid) == null;
	}

	public static PlayerModel loadPlayerModel(UUID uuid) {
		return new PlayerModel(uuid);
	}

	public static PlayerModel newPlayer(Player p) {

		Row playerRow = Server.getPlayerDataTable().getDefaultRow();
		Row customizerRow = Server.getKitCustomizerTable().getDefaultRow();

		playerRow.getPrimaryKey().setValue(p.getUniqueId()); // Set the dynamic default values and primary keys
		playerRow.getCell("PlayerName").setValue(p.getName());
		playerRow.getCell("IP").setValue(p.getAddress().getAddress().getHostAddress());
		customizerRow.getPrimaryKey().setValue(p.getUniqueId()); // Set primary key

		// Load this player model in as quick as possible so that other onJoin events can use it
		PlayerModel player = loadPlayerModel(p.getUniqueId());// Adds this player to the list of models

		Server.getPlayerDataTable().newEntry(playerRow); // Creates new row in contents and actual database

		Server.getKitCustomizerTable().newEntry(customizerRow); // Creates player's row in kit customizer and database

		return player;
	}

	public static PlayerModel getPlayerModel(Player p) {
		for (PlayerModel pm : Server.getAllPlayers()) {
			if (pm.getUUID() == p.getUniqueId()) {
				return pm;
			}
		}
		return null; // shouldn't be called because all players are registered during start up
	}

	public static PlayerModel getPlayerModel(UUID uuid) {
		for (PlayerModel pm : Server.getAllPlayers()) {
			if (pm.getUUID() == uuid) {
				return pm;
			}
		}
		return null; // shouldn't be called because all players are registered during start up
	}

	public boolean equals(PlayerModel p) {
		return (p.getUUID().equals(getUUID()));
	}

}
