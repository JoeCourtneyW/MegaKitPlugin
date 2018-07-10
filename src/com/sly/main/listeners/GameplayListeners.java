package com.sly.main.listeners;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sly.main.Server;
import com.sly.main.enums.CustomBlock;
import com.sly.main.kits.Kits;
import com.sly.main.player.PlayerModel;
import com.sly.main.resources.MathUtil;
import com.sly.main.resources.MyMetadata;
import com.sly.main.slots.SlotSpin;

public class GameplayListeners implements Listener
{
	@EventHandler
	public void customPotDrink(PlayerItemConsumeEvent event) {
		PlayerModel p = PlayerModel.getPlayerModel(event.getPlayer());
		if (event.getItem() != null) {
			if (event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()) {
				if (event.getItem().getItemMeta().getDisplayName().contains(" Health Potion")) {
					String name = event.getItem().getItemMeta().getDisplayName();
					int health = 0;
					if (name.contains("6")) {
						health = 12;
					} else if (name.contains("7")) {
						health = 14;
					} else if (name.contains("8")) {
						health = 16;
					} else if (name.contains("9")) {
						health = 18;
					} else if (name.contains("10")) {
						health = 20;
					}
					if (p.getCraftPlayer().getHealth() + health >= p.getCraftPlayer().getMaxHealth()) {
						p.getPlayer().setHealth(p.getCraftPlayer().getMaxHealth());
					} else {
						p.getPlayer().setHealth(p.getCraftPlayer().getHealth() + health);
					}
				} else if (event.getItem().getItemMeta().getDisplayName().contains(" Speed Potion")) {
					p.addNoReductionEffect(new PotionEffect(PotionEffectType.SPEED, 15 * 20, 1));
				} else if (event.getItem().getItemMeta().getDisplayName().contains(" Regen Potion")) {
					p.addNoReductionEffect(new PotionEffect(PotionEffectType.REGENERATION, 11 * 20, 2));
				}
			}
		}
	}

	@EventHandler
	public void onVoidDamage(EntityDamageEvent event) {
		if (event.getCause().equals(DamageCause.VOID) && event.getEntity() instanceof Player) {
			PlayerModel p = PlayerModel.getPlayerModel((Player) event.getEntity());
			p.getPlayer().teleport(p.getSpawnPoint());
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onKillPots(PlayerDeathEvent event) {
		PlayerModel p = PlayerModel.getPlayerModel(event.getEntity().getKiller());
		if (!p.getPlayer().getWorld().getName().equalsIgnoreCase("Nube"))
			return;
		if (p.getPlayer() != null && p.getPlayer() instanceof Player) {
			Kits kit = p.getKit();
			if (kit == Kits.NONE)
				return;
			ItemStack one = kit.getListenerClass().getKitItems().get("3").clone();
			ItemStack two = kit.getListenerClass().getKitItems().get("4").clone();
			one.setAmount(1);
			two.setAmount(1);
			if (kit != Kits.GOLEM) {
				boolean speedAdded = false;// TODO: Make this a lot cleaner, there has to be a better way to do it
				boolean healthAdded = false;
				for (ItemStack i : p.getPlayer().getInventory().getContents()) {
					if (i != null && i.getType() == Material.POTION && i.hasItemMeta()
							&& i.getItemMeta().hasDisplayName()
							&& i.getItemMeta().getDisplayName().contains(" Health Potion")) {
						i.setAmount(i.getAmount() + 1);
						healthAdded = true;
					} else if (i != null && i.getType() == Material.POTION && i.hasItemMeta()
							&& i.getItemMeta().hasDisplayName()
							&& i.getItemMeta().getDisplayName().contains(" Speed Potion")) {
						i.setAmount(i.getAmount() + 1);
						speedAdded = true;
					}
				}
				if (!healthAdded) {
					p.getPlayer().getInventory().addItem(one);
				}
				if (!speedAdded) {
					p.getPlayer().getInventory().addItem(two);
				}
			} else {
				boolean regenAdded = false;
				boolean slowAdded = false;
				for (ItemStack i : p.getPlayer().getInventory().getContents()) {
					if (i != null && i.getType() == Material.POTION && i.hasItemMeta()
							&& i.getItemMeta().hasDisplayName()
							&& i.getItemMeta().getDisplayName().contains(" Regen Potion")) {
						i.setAmount(i.getAmount() + 1);
						regenAdded = true;
					} else if (i != null && i.getType() == Material.POTION && i.hasItemMeta()
							&& i.getItemMeta().hasDisplayName()
							&& i.getItemMeta().getDisplayName().contains(" Slowness Potion")) {
						i.setAmount(i.getAmount() + 1);
						slowAdded = true;
					}
				}
				if (!regenAdded) {
					p.getPlayer().getInventory().addItem(one);
				}
				if (!slowAdded) {
					p.getPlayer().getInventory().addItem(two);
				}

			}
			if (kit == Kits.BLAZE || kit == Kits.SKELETON) {
				ItemStack arrows = kit.getListenerClass().getKitItems().get("8"); //Get the arrow itemstack
				arrows.setAmount(16);
				p.getPlayer().getInventory().addItem(arrows);
			}
			p.getPlayer().getInventory()
					.addItem(new ItemStack(CustomBlock.getSelectedBlock(p).getMaterial(), 32,
							CustomBlock.getSelectedBlock(p).getData()));

		}
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.SURVIVAL)
			event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onCompass(PlayerInteractEvent event) {
		if (event.getPlayer().getGameMode() == GameMode.SURVIVAL && event.getItem() != null
				&& event.getItem().getType() == Material.COMPASS)
			event.setCancelled(true);
	}

	@EventHandler
	public void onSlot(PlayerInteractEvent event) {
		PlayerModel player = PlayerModel.getPlayerModel(event.getPlayer());
		if (event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof Sign
				&& ((Sign) event.getClickedBlock().getState()).getLine(0).contains("[SLOTS]")) {
			if (SlotSpin.spins.containsKey(event.getPlayer().getName())) {
				event.getPlayer().openInventory(SlotSpin.spins.get(event.getPlayer().getName()).getInventory());
				return;
			}
			if (player.getDatabaseValue("SPINS").asInt() > 0) {
				new SlotSpin(event.getPlayer(), Server.getInstance());
			} else
				player.sendPrefixedMessage("You need a spin to use the Slot Machine!");
		}
	}

	//TODO: Migrate this away from metadata
	@EventHandler
	public void onMinion(EntityTargetEvent event) {
		if (event.getTarget() instanceof Player) {
			if (event.getEntity().hasMetadata("owner")
					&& ((String) event.getEntity().getMetadata("owner").get(0).value())
							.equalsIgnoreCase(((Player) event.getTarget()).getName())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void click(InventoryClickEvent event) {
		PlayerModel player = PlayerModel.getPlayerModel((Player) event.getWhoClicked());

		if (event.getInventory().getName().contains("crafting") && player.getKit() != Kits.NONE) { // If they are in their inventory
			player.sendPrefixedMessage("§eIf you want to change your class setup, do so in the 'Class Customizer'");
			event.setCancelled(true);
		} else if (event.getInventory().getName().contains("crafting") && event.getCurrentItem() != null
				&& event.getCurrentItem().hasItemMeta()
				&& event.getCurrentItem().getItemMeta().getDisplayName().contains("Vanity")) {
			event.setCancelled(true); // Don't let them change their vanity armor placement
		}
	}

	@EventHandler
	public void blockPlacing(final BlockPlaceEvent event) {
		if (event.getPlayer().getGameMode() != GameMode.SURVIVAL)
			return;

		if (Server.getBlocks().contains(event.getBlockPlaced().getLocation().clone().subtract(0, 3, 0))) {
			event.setCancelled(true);
			return;
		}
		final BlockState oldb = event.getBlockReplacedState();
		if (oldb.getType() == Material.WATER || oldb.getType() == Material.LAVA
				|| oldb.getType() == Material.STATIONARY_WATER || oldb.getType() == Material.STATIONARY_LAVA) {
			event.setCancelled(true);
			return;
		}

		final Block b = event.getBlockPlaced();
		final Block north = b.getRelative(BlockFace.NORTH);
		final Block south = b.getRelative(BlockFace.SOUTH);
		final Block east = b.getRelative(BlockFace.EAST);
		final Block west = b.getRelative(BlockFace.WEST);
		if (event.getBlockAgainst().equals(north) || event.getBlockAgainst().equals(south)
				|| event.getBlockAgainst().equals(east) || event.getBlockAgainst().equals(west)) {
			event.setCancelled(true);
			return;
		}
		if (north.getType() == Material.CACTUS || south.getType() == Material.CACTUS
				|| east.getType() == Material.CACTUS || west.getType() == Material.CACTUS) {
			event.setCancelled(true);
			return;
		}
		if (north.getType() == Material.LADDER || south.getType() == Material.LADDER
				|| east.getType() == Material.LADDER || west.getType() == Material.LADDER
				|| b.getRelative(BlockFace.UP).getType() == Material.LADDER
				|| b.getRelative(BlockFace.DOWN).getType() == Material.LADDER) {
			event.setCancelled(true);
			return;
		}
		if (event.getBlockPlaced().getType() == Material.WOOL) {
			Server.getBlocks().add(b.getLocation());
			final BukkitTask r = new BukkitRunnable() {
				@SuppressWarnings("deprecation")
				public void run() {
					event.getBlockPlaced().setData((byte) MathUtil.rand(1, 15));
				}
			}.runTaskTimer(Server.getInstance(), 5, 10);
			new BukkitRunnable() {
				public void run() {
					r.cancel();
					b.getWorld().loadChunk(b.getWorld().getChunkAt(b));
					b.setType(Material.AIR);
					Server.getInstance().removeBlock(b.getLocation());
					for (int i = 0; i < 4; i++) {
						if (Server.getBlocks()
								.contains(event.getBlockPlaced().getLocation().clone().add(0, 1 + i, 0))) {
							Server.getInstance()
									.removeBlock(event.getBlockPlaced().getLocation().clone().add(0, 1 + i, 0));
							event.getBlockPlaced().getWorld()
									.getBlockAt(event.getBlockPlaced().getLocation().clone().add(0, 1 + i, 0))
									.setType(Material.AIR);
						}
					}
				}
			}.runTaskLater(Server.getInstance(), 5 * 20);
			return;
		}
		Server.getBlocks().add(b.getLocation());
	}

	public static ConcurrentHashMap<String, BukkitTask> tagged = new ConcurrentHashMap<String, BukkitTask>();

	/**
	 * When a playre is tagged initially, set their metadata to their damager Start a task, 30 seconds later remove the metadata If said player is hit by the same player, cancel the old task and
	 * create a new one If said player is hit by a different player, cancel the old task, change the entity's metadata to the new name, and create a new one
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onLastPlayerDamage(EntityDamageByEntityEvent event) {
		if (event.isCancelled())
			return;
		if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
			final Player entity = (Player) event.getEntity();
			Player damager = (Player) event.getDamager();
			if (entity.hasMetadata("lastPlayerDamage")
					&& ((String) entity.getMetadata("lastPlayerDamage").get(0).value())
							.equalsIgnoreCase(damager.getUniqueId().toString())) {
				// Same Attacker as last hit
				tagged.get(entity.getUniqueId().toString()).cancel();
				tagged.remove(entity.getUniqueId().toString());
				entity.removeMetadata("lastPlayerDamage", Server.getInstance());
				entity.setMetadata("lastPlayerDamage",
						new MyMetadata(Server.getInstance(), damager.getUniqueId().toString()));
				final String value = damager.getUniqueId().toString();
				tagged.put(entity.getUniqueId().toString(), new BukkitRunnable() {
					public void run() {
						if (entity.hasMetadata("lastPlayerDamage")
								&& ((String) entity.getMetadata("lastPlayerDamage").get(0).value())
										.equalsIgnoreCase(value)) {
							entity.removeMetadata("lastPlayerDamage", Server.getInstance());
							tagged.remove(entity.getUniqueId().toString());
						}

					}
				}.runTaskLater(Server.getInstance(), 30 * 20));
				return;
			} else if (entity.hasMetadata("lastPlayerDamage")) {
				tagged.get(entity.getUniqueId().toString()).cancel();
				tagged.remove(entity.getUniqueId().toString());
				entity.removeMetadata("lastPlayerDamage", Server.getInstance());
				entity.setMetadata("lastPlayerDamage",
						new MyMetadata(Server.getInstance(), damager.getUniqueId().toString()));
				final String value = damager.getUniqueId().toString();
				tagged.put(entity.getUniqueId().toString(), new BukkitRunnable() {
					public void run() {
						if (entity.hasMetadata("lastPlayerDamage")
								&& ((String) entity.getMetadata("lastPlayerDamage").get(0).value())
										.equalsIgnoreCase(value)) {
							entity.removeMetadata("lastPlayerDamage", Server.getInstance());
							tagged.remove(entity.getUniqueId().toString());
						}

					}
				}.runTaskLater(Server.getInstance(), 30 * 20));
				// New Attacker
			}
			// Initial Tag
			entity.setMetadata("lastPlayerDamage",
					new MyMetadata(Server.getInstance(), damager.getUniqueId().toString()));
			final String value = damager.getUniqueId().toString();
			tagged.put(entity.getUniqueId().toString(), new BukkitRunnable() {
				public void run() {
					if (entity.hasMetadata("lastPlayerDamage")
							&& ((String) entity.getMetadata("lastPlayerDamage").get(0).value())
									.equalsIgnoreCase(value)) {
						entity.removeMetadata("lastPlayerDamage", Server.getInstance());
						tagged.remove(entity.getUniqueId().toString());
					}

				}
			}.runTaskLater(Server.getInstance(), 30 * 20));
		}
	}

	@EventHandler
	public void onLogoutKill(PlayerQuitEvent event) {
		if (event.getPlayer().hasMetadata("lastPlayerDamage")) {
			Player p = Bukkit.getPlayer(
					UUID.fromString((String) event.getPlayer().getMetadata("lastPlayerDamage").get(0).value()));
			if (p != null) {
				event.getPlayer().damage(1000.0, p);
				if (tagged.containsKey(event.getPlayer().getUniqueId().toString())) {
					tagged.get(event.getPlayer().getUniqueId().toString()).cancel();
					tagged.remove(event.getPlayer().getUniqueId().toString());
				}
				event.getPlayer().removeMetadata("lastPlayerDamage", Server.getInstance());
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeathKill(PlayerDeathEvent event) {
		if (!event.getEntity().getWorld().getName().contains("Nube"))
			return;
		if (event.getEntity().hasMetadata("lastPlayerDamage")) {
			Player p = Bukkit.getPlayer(
					UUID.fromString((String) event.getEntity().getMetadata("lastPlayerDamage").get(0).value()));
			if (p != null) {
				if (event.getEntity().getKiller() == null) {
					event.getEntity().damage(1000.0, p);
				}
				event.getEntity().setLastDamageCause(
						new EntityDamageByEntityEvent(p, event.getEntity(), DamageCause.ENTITY_ATTACK, 0.0001));
				tagged.get(event.getEntity().getUniqueId().toString()).cancel();
				tagged.remove(event.getEntity().getUniqueId().toString());
				event.getEntity().removeMetadata("lastPlayerDamage", Server.getInstance());
			}
		}
	}
}
