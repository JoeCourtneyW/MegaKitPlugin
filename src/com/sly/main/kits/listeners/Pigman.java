package com.sly.main.kits.listeners;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.sly.main.Server;
import com.sly.main.gametype.Squad;
import com.sly.main.kits.Kit;
import com.sly.main.kits.Kits;
import com.sly.main.resources.BetterItem;
import com.sly.main.resources.EasyEnchant;

import de.slikey.effectlib.effect.ShieldEntityEffect;
import de.slikey.effectlib.util.ParticleEffect;

public class Pigman extends Kit implements Listener
{

	/**
	 * Burning Soul
	 */
	@SuppressWarnings("deprecation")
	@EventHandler
	public void BurningSoul(final PlayerInteractEvent event) {
		if (Kits.getClass(event.getPlayer()).equals(Kits.PIGMAN)) {
			if (Server.getInstance().isActivateAbilityEvent(event)) {
				// Create the Effect and attach it to the Player
				final Location lr = event.getPlayer().getLocation();
				final Location l = lr.clone();
				l.subtract(0, 1, 0);
				l.setPitch(-90);
				Location vloc = l.clone();
				vloc.add(0, 1.5, 0);
				final FallingBlock e = vloc.getWorld().spawnFallingBlock(vloc, Material.AIR, (byte) 0);
				ShieldEntityEffect vortexEffect = new ShieldEntityEffect(Server.getEffectManager(), e);
				vortexEffect.radius = 6;
				vortexEffect.sphere = true;
				vortexEffect.particle = ParticleEffect.FLAME;
				vortexEffect.iterations = 6 * 22;
				Server.getEffectManager().start(vortexEffect);

				final BukkitTask r = new BukkitRunnable() {
					public void run() {
						for (Entity p : e.getNearbyEntities(5, 5, 5)) {
							if (p instanceof Player) {
								if (((Player) p).getName().equalsIgnoreCase(event.getPlayer().getName()))
									continue;
								if (!Squad.sameTeam(event.getPlayer(), (Player) p)) {
									Server.getInstance().damagePure((Player) p, (double) 3, event.getPlayer());
									p.setVelocity(new Vector());
									Location playerLoc = p.getLocation();
									for (Player r : Bukkit.getOnlinePlayers()) {
										r.playEffect(playerLoc, Effect.MOBSPAWNER_FLAMES, (byte) 0);
									}
								}
							}
						}
					}
				}.runTaskTimer(Server.getInstance(), 20, 20);
				e.remove();
				new BukkitRunnable() {
					public void run() {
						r.cancel();
					}
				}.runTaskLater(Server.getInstance(), 22 * 5);
				// Take away levels
				event.getPlayer().setLevel(0);
			}
		}
	}

	/**
	 * Valor
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void Valor(EntityDamageByEntityEvent event) {
		if (event.isCancelled())
			return;
		if (event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER) {
			Player p = (Player) event.getEntity();
			if (Kits.getClass(p) == Kits.PIGMAN) {
				if (Server.getInstance().rand(1, 1000) <= 76) {
					Server.getInstance().addNoReductionEffect(p,
							new PotionEffect(PotionEffectType.REGENERATION, 8 * 20, 0));
					for (Entity e : p.getNearbyEntities(8, 8, 8)) {
						if (e instanceof Player) {
							if (Squad.sameTeam(p, (Player) e)) {
								Server.getInstance().addNoReductionEffect((Player) e,
										new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 8 * 20, 0));
								Server.getInstance().addNoReductionEffect((Player) e,
										new PotionEffect(PotionEffectType.REGENERATION, 8 * 20, 0));
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Endurance
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void Endurance(EntityDamageByEntityEvent event) {
		if (event.isCancelled())
			return;
		if (event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER) {
			Player p = (Player) event.getEntity();
			if (Kits.getClass(p) == Kits.PIGMAN) {
				if (((CraftPlayer) p).getHealth() < 10.0) {
					if (!p.isDead()) {
						Server.getInstance().addNoReductionEffect(p,
								new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 6 * 20, 1));
					}
				}
			}
		}
	}

	/**
	 * Kit Items
	 */
	public static HashMap<String, ItemStack> kitItems = new HashMap<String, ItemStack>();

	@Override
	public void fillKit() {
		kitItems.put("Helmet", new EasyEnchant(Material.IRON_HELMET, 1, "§bPigman Helmet").craftToItemStack());
		kitItems.put("Chestplate", new EasyEnchant(Material.DIAMOND_CHESTPLATE, 1, "§bPigman Chestplate")
				.addEnchant(Enchantment.DURABILITY, 1).craftToItemStack());
		kitItems.put("Leggings", new EasyEnchant(Material.IRON_LEGGINGS, 1, "§bPigman Leggings").craftToItemStack());
		kitItems.put("Boots", new EasyEnchant(Material.IRON_BOOTS, 1, "§bPigman Boots").craftToItemStack());
		kitItems.put("1", new EasyEnchant(Material.DIAMOND_SWORD, 1, "§bPigman Sword")
				.addEnchant(Enchantment.DURABILITY, 3).craftToItemStack());
		kitItems.put("2", new EasyEnchant(Material.GRILLED_PORK, 64, "§bPigman Porkchops").craftToItemStack());
		kitItems.put("3", new EasyEnchant(Material.POTION, 1, "§bPigman Health Potion (10�?�)").craftToItemStack());
		kitItems.put("4", new EasyEnchant(Material.POTION, 2, "§bPigman Speed Potion").craftToItemStack());
		kitItems.put("5", new BetterItem(Material.WOOD, 64, "§bPigman Blocks", (short) 3).grab());
		kitItems.put("6", new EasyEnchant(Material.COMPASS, 1, "§bPlayer Tracker").craftToItemStack());
		kitItems.put("7", new ItemStack(Material.AIR, 1));
		kitItems.put("8", new ItemStack(Material.AIR, 1));
		kitItems.put("9", new ItemStack(Material.AIR, 1));
	}

	@Override
	public HashMap<String, ItemStack> getKitItems() {
		return kitItems;
	}

	/**
	 * Energy
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void EnergyOnHit(EntityDamageByEntityEvent event) {
		if (event.isCancelled())
			return;
		if (event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER) {
			Player p = (Player) event.getDamager();
			if (Kits.getClass(p) == Kits.PIGMAN) {
				Server.getInstance().addEnergy(p, 8);
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void EnergyOnBowHit(EntityDamageByEntityEvent event) {
		if (event.isCancelled())
			return;
		if (event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.ARROW) {
			Arrow arrow = (Arrow) event.getDamager();
			if (!(event.getEntity() == arrow.getShooter())) {
				if (Kits.getClass(((Player) arrow.getShooter())) == Kits.PIGMAN) {
					Server.getInstance().addEnergy(((Player) arrow.getShooter()), 8);
				}
			}
		}
	}
}
