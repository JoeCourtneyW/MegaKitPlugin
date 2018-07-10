package com.sly.main.kits.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sly.main.Server;
import com.sly.main.gametype.Squad;
import com.sly.main.kits.Kit;
import com.sly.main.kits.Kits;
import com.sly.main.resources.BetterItem;
import com.sly.main.resources.EasyEnchant;

public class Herobrine extends Kit implements Listener
{

	/**
	 * Wrath
	 */
	public void mainAbility(Player p) {
		List<String> playersHit = new ArrayList<String>();
		// Loop through nearby entities, create visual effects and damage them with 4.0 pure
		for (Entity e : p.getNearbyEntities(3.5, 3.5, 3.5)) {
			if (e instanceof Player) {
				if (!Squad.sameTeam(p, (Player) e)) {
					if (((Player) e).getGameMode().equals(GameMode.CREATIVE))
						continue;
					playersHit.add(((Player) e).getName());
					Server.getInstance().damagePure((Player) e, 4.0, p);
					e.getWorld().strikeLightningEffect(e.getLocation());
				}
			}
		}

		if (playersHit.size() > 0) {
			StringBuilder playerList = new StringBuilder();
			for (String r : playersHit) {
				playerList.append(", ");
				playerList.append(r);
			}
			for (String r : playersHit) {
				Bukkit.getPlayer(r).sendMessage("§b" + p.getName() + "§e's Wrath skill struck " + playerList);
			}
			p.sendMessage("§b" + p.getName() + "§e's Wrath skill struck " + playerList);
		} else {
			p.sendMessage("§cNo player within range to target");
		}
	}

	/**
	 * Power
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void Power(PlayerDeathEvent event) {
		if (event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player) {
			Player p = (Player) event.getEntity().getKiller();
			if (Kits.getClass(p) == Kits.HEROBRINE) {
				Server.getInstance().addNoReductionEffect(p,
						new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 6 * 20, 0));
			}
		}
	}

	/**
	 * Flurry
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void Flurry(EntityDamageByEntityEvent event) {
		if (event.isCancelled())
			return;
		if (event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER) {
			Player p = (Player) event.getDamager();
			if (Kits.getClass(p) == Kits.HEROBRINE) {
				if (Server.getInstance().rand(1, 100) <= 85) {
					Server.getInstance().addNoReductionEffect(p, new PotionEffect(PotionEffectType.SPEED, 20, 1));
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
		kitItems.put("Helmet", new EasyEnchant(Material.IRON_HELMET, 1, "§bHerobrine Helmet")
				.addEnchant(Enchantment.WATER_WORKER, 1).addEnchant(Enchantment.DURABILITY, 3).craftToItemStack());
		kitItems.put("Chestplate",
				new EasyEnchant(Material.IRON_CHESTPLATE, 1, "§bHerobrine Chestplate").craftToItemStack());
		kitItems.put("Leggings", new EasyEnchant(Material.IRON_LEGGINGS, 1, "§bHerobrine Leggings").craftToItemStack());
		kitItems.put("Boots", new EasyEnchant(Material.IRON_BOOTS, 1, "§bHerobrine Boots").craftToItemStack());
		kitItems.put("1", new EasyEnchant(Material.DIAMOND_SWORD, 1, "§bHerobrine Sword")
				.addEnchant(Enchantment.DURABILITY, 3).craftToItemStack());
		kitItems.put("2", new EasyEnchant(Material.COOKED_BEEF, 64, "§bHerobrine Steak").craftToItemStack());
		kitItems.put("3", new EasyEnchant(Material.POTION, 2, "§bHerobrine Health Potion (7�?�)").craftToItemStack());
		kitItems.put("4", new EasyEnchant(Material.POTION, 2, "§bHerobrine Speed Potion").craftToItemStack());
		kitItems.put("5", new BetterItem(Material.WOOD, 64, "§bHerobrine Blocks", (short) 3).grab());
		kitItems.put("6", new EasyEnchant(Material.COMPASS, 1, "§bPlayer Tracker").craftToItemStack());
		kitItems.put("7", new ItemStack(Material.AIR, 1));
		kitItems.put("8", new ItemStack(Material.AIR, 1));
		kitItems.put("9", new ItemStack(Material.AIR, 1));
	}

	@Override
	public HashMap<String, ItemStack> getKitItems() {
		return kitItems;
	}

	public int getEnergyOnMeleeHit() {
		return 17;
	}

	public int getEnergyOnBowHit() {
		return 17;
	}

	public int getEnergyOverTime() {
		return 0;
	}
}
