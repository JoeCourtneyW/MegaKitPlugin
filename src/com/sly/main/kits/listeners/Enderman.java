package com.sly.main.kits.listeners;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sly.main.Server;
import com.sly.main.gametype.Squad;
import com.sly.main.kits.Kit;
import com.sly.main.kits.Kits;
import com.sly.main.resources.BetterItem;
import com.sly.main.resources.EasyEnchant;
import com.sly.main.resources.MathUtil;

public class Enderman extends Kit implements Listener
{

	/**
	 * Teleport
	 */
	@SuppressWarnings("deprecation")
	public void mainAbility(Player p) {
		for (Block b : p.getLineOfSight(null, 25)) {
			FallingBlock f = b.getWorld().spawnFallingBlock(b.getLocation(), Material.AIR, (byte) 0);
			List<Entity> players = f.getNearbyEntities(1.7, 1.7, 1.7);
			f.remove();
			if (players == null)
				continue;
			for (Entity e : players) {
				if (e.getType() == EntityType.PLAYER) {
					if (!((Player) e).getName().equalsIgnoreCase(p.getName())) {
						if (!Squad.sameTeam(p, (Player) e)) {
							Server.getInstance().addNoReductionEffect(p,
									new PotionEffect(PotionEffectType.SPEED, 4 * 20, 2));
							p.teleport((Player) e, TeleportCause.ENDER_PEARL);
							((Player) e).playSound(e.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 0.9F);
							p.playSound(e.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 0.9F);

							return;
						}
					}
				}
			}
		}
	}

	/**
	 * Anti Knockback
	 */
	@EventHandler
	public void AntiKB(PlayerVelocityEvent event) {
		final Player p = (Player) event.getPlayer();
		if (Kits.getClass(p) == Kits.ENDERMAN) {
			if (MathUtil.rand(1, 100) <= 90) {
				event.setCancelled(true);
			}
		}
	}

	/**
	 * Kit Items
	 */
	public static HashMap<String, ItemStack> kitItems = new HashMap<String, ItemStack>();

	@Override
	public void fillKit() {
		kitItems.put("Helmet", new EasyEnchant(Material.IRON_HELMET, 1, "§bEnderman Helmet").craftToItemStack());
		kitItems.put("Chestplate",
				new EasyEnchant(Material.IRON_CHESTPLATE, 1, "§bEnderman Chestplate").craftToItemStack());
		kitItems.put("Leggings", new EasyEnchant(Material.IRON_LEGGINGS, 1, "§bEnderman Leggings").craftToItemStack());
		kitItems.put("Boots", new EasyEnchant(Material.DIAMOND_BOOTS, 1, "§bEnderman Boots")
				.addEnchant(Enchantment.PROTECTION_FALL, 4).craftToItemStack());
		kitItems.put("1", new EasyEnchant(Material.IRON_SWORD, 1, "§bEnderman Sword")
				.addEnchant(Enchantment.DURABILITY, 3).craftToItemStack());
		kitItems.put("2", new EasyEnchant(Material.COOKED_BEEF, 64, "§bEnderman Steak").craftToItemStack());
		kitItems.put("3", new EasyEnchant(Material.POTION, 2, "§bEnderman Health Potion (8�?�)").craftToItemStack());
		kitItems.put("4", new EasyEnchant(Material.POTION, 2, "§bEnderman Speed Potion").craftToItemStack());
		kitItems.put("5", new BetterItem(Material.WOOD, 64, "§bEnderman Blocks", (short) 3).grab());
		kitItems.put("6", new EasyEnchant(Material.COMPASS, 1, "§bPlayer Tracker").craftToItemStack());
		kitItems.put("7", new ItemStack(Material.AIR, 1));
		kitItems.put("8", new ItemStack(Material.AIR, 1));
		kitItems.put("9", new ItemStack(Material.AIR, 1));
	}

	@Override
	public HashMap<String, ItemStack> getKitItems() {
		return kitItems;
	}

	public int getEnergyOnBowHit() {
		return 20;
	}

	public int getEnergyOnMeleeHit() {
		return 20;
	}

	public int getEnergyOverTime() {
		return 0;
	}

}
