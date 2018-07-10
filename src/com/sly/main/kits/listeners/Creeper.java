package com.sly.main.kits.listeners;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sly.main.Server;
import com.sly.main.gametype.Squad;
import com.sly.main.kits.Kit;
import com.sly.main.kits.Kits;
import com.sly.main.resources.BetterItem;
import com.sly.main.resources.EasyEnchant;
import com.sly.main.resources.ParticleEffects;

public class Creeper extends Kit implements Listener
{

	public void mainAbility(final Player p) {
		final BukkitTask r = new BukkitRunnable() {
			public void run() {
				ParticleEffects.LARGE_SMOKE.sendToAllPlayers(p.getLocation(), 1F, 15);
				for (Entity e : p.getNearbyEntities(6, 2.5, 6)) {
					if (e instanceof Player) {
						((Player) e).playSound(p.getLocation(), Sound.CREEPER_HISS, 1, 0.65F);
					}
				}
				p.playSound(p.getLocation(), Sound.CREEPER_HISS, 1, 0.65F);
			}
		}.runTaskTimer(Server.getInstance(), 0, 21);
		
		new BukkitRunnable() {
			public void run() {
				r.cancel();
				for (Entity e : p.getNearbyEntities(3.7, 2.5, 3.7)) {
					if (e instanceof Player) {
						if (!Squad.sameTeam(p, (Player) e)) {
							if (p.isDead())
								return;
							Server.getInstance().damagePure((Player) e,
									10.0 - (e.getLocation().distanceSquared(p.getLocation())), p);
						}
						((Player) e).playSound(p.getLocation(), Sound.EXPLODE, 1, 1);
					}
				}
				p.playSound(p.getLocation(), Sound.EXPLODE, 1, 1);
				ParticleEffects.LARGE_EXPLODE.sendToAllPlayers(p.getLocation(), 1F, 10);
			}
		}.runTaskLater(Server.getInstance(), 3 * 20);

	}

	/**
	 * Minions
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void Minions(PlayerDeathEvent event) {
		if (event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player
				&& !(event.getEntity().getName().equalsIgnoreCase(event.getEntity().getKiller().getName()))) {
			Player p = (Player) event.getEntity();
			Location loc = p.getLocation();
			if (Kits.getClass(p) == Kits.CREEPER) {
				if (p.getWorld().getName().contains("Nube")) { // TODO: Why is this only in Nube world?
					super.newMinion(loc, p, EntityType.CREEPER);
				}
			}
		}
	}

	/**
	 * Willpower
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void Willpower(EntityDamageEvent event) {
		if (event.isCancelled())
			return;
		if (event.getEntity().getType() == EntityType.PLAYER) {
			Player p = (Player) event.getEntity();
			if (Kits.getClass(p) == Kits.CREEPER) {
				if (((CraftPlayer) p).getHealth() < 14.0) {
					if (!p.isDead()) {
						Server.getInstance().addNoReductionEffect(p,
								new PotionEffect(PotionEffectType.SPEED, 8 * 20, 1));
					}
				}
			}
		}
	}

	/**
	 * Kit Items
	 */
	public static HashMap<String, ItemStack> kitItems = new HashMap<String, ItemStack>();

	public void fillKit() {
		kitItems.put("Helmet", new EasyEnchant(Material.IRON_HELMET, 1, "§bCreeper Helmet").craftToItemStack());
		kitItems.put("Chestplate",
				new EasyEnchant(Material.IRON_CHESTPLATE, 1, "§bCreeper Chestplate").craftToItemStack());
		kitItems.put("Leggings", new EasyEnchant(Material.DIAMOND_LEGGINGS, 1, "§bCreeper Leggings")
				.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 4).craftToItemStack());
		kitItems.put("Boots", new EasyEnchant(Material.IRON_BOOTS, 1, "§bCreeper Boots").craftToItemStack());
		kitItems.put("1", new EasyEnchant(Material.IRON_SWORD, 1, "§bCreeper Sword")
				.addEnchant(Enchantment.DURABILITY, 3).craftToItemStack());
		kitItems.put("2", new EasyEnchant(Material.COOKED_BEEF, 64, "§bCreeper Steak").craftToItemStack());
		kitItems.put("3", new EasyEnchant(Material.POTION, 2, "§bCreeper Health Potion (8�?�)").craftToItemStack());
		kitItems.put("4", new EasyEnchant(Material.POTION, 2, "§bCreeper Speed Potion").craftToItemStack());
		kitItems.put("5", new BetterItem(Material.WOOD, 64, "§bCreeper Blocks", (short) 3).grab());
		kitItems.put("6", new EasyEnchant(Material.COMPASS, 1, "§bPlayer Tracker").craftToItemStack());
		kitItems.put("7", new ItemStack(Material.AIR, 1));
		kitItems.put("8", new ItemStack(Material.AIR, 1));
		kitItems.put("9", new ItemStack(Material.AIR, 1));
	}

	public HashMap<String, ItemStack> getKitItems() {
		return kitItems;
	}

	public int getEnergyOnBowHit() {
		return 20;
	}

	public int getEnergyOverTime() {
		return 0;
	}

	public int getEnergyOnMeleeHit() {
		return 20;
	}

}
