package com.sly.main.kits.listeners;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sly.main.kits.Kit;
import com.sly.main.kits.Kits;
import com.sly.main.player.PlayerModel;
import com.sly.main.resources.BetterItem;
import com.sly.main.resources.MathUtil;

public class Arcanist extends Kit implements Listener
{

	@SuppressWarnings("deprecation")
	public void mainAbility(PlayerModel p) {
		for (Block b : p.getPlayer().getLineOfSight(null, 50)) {
			if (b.getType() != Material.AIR) // make sure the LOS isn't blocked
				break;
			Firework f = (Firework) b.getWorld().spawnEntity(b.getLocation().add(0.5D, 0D, 0.5D), EntityType.FIREWORK);
			f.remove(); // Creates a small trail effect while we iterate
			List<Entity> players = f.getNearbyEntities(1.7, 1.7, 1.7);
			if (players == null)
				continue;
			for (Entity e : players) { // Loop through all nearby players
				if (e.getType() == EntityType.PLAYER) {// Make sure nearby entity is a player
					PlayerModel entity = PlayerModel.getPlayerModel((Player) e);
					if (!entity.sameSquad(p)) { // Make sure they are not on the same team
						if (!entity.equals(p)) { // Male sure they aren't hitting themselves
							entity.damageImpure(3.3D, p.getPlayer()); // Damage the player, doesn't ignore armor
							entity.getPlayer().playSound(p.getPlayer().getLocation(), Sound.WITHER_SHOOT, 1, 2);
							p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.WITHER_SHOOT, 1, 2);
							return; // break out of entire loop because we found a player to damage
						}
					}
				}
			}
		}
	};

	/**
	 * Tempest
	 */
	@EventHandler
	public void Tempest(PlayerDeathEvent event) {
		if (event.getEntity().getKiller() == null)
			return;
		PlayerModel p = PlayerModel.getPlayerModel(event.getEntity().getKiller());
		if (p.getKit() == Kits.ARCANIST) {
			p.addNoReductionEffect(new PotionEffect(PotionEffectType.SPEED, 6 * 20, 2));
			p.addNoReductionEffect(new PotionEffect(PotionEffectType.REGENERATION, 5 * 20, 1));
			p.addEnergy(90);
		}
	}

	/**
	 * Arcane Explosion
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void ArcaneExplosion(EntityDamageByEntityEvent event) {
		if (event.isCancelled())
			return;
		if (event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER) {
			PlayerModel p = PlayerModel.getPlayerModel((Player) event.getEntity());
			if (p.getKit() == Kits.ARCANIST) {
				if (MathUtil.rand(1, 100) <= 20) {
					p.getPlayer().playSound(p.getPlayer().getLocation(), Sound.EXPLODE, 1, 2);
					for (Entity e : p.getPlayer().getNearbyEntities(3, 3, 3)) {
						if (e instanceof Player) {
							PlayerModel entity = PlayerModel.getPlayerModel((Player) e);
							entity.damagePure(2.0, p.getPlayer());
							entity.getPlayer().playSound(p.getPlayer().getLocation(), Sound.EXPLODE, 1, 2);
						}
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

		kitItems.put("Helmet", new BetterItem().material(Material.IRON_HELMET).displayName("§Arcanist Helmet").grab());
		kitItems.put("Chestplate",
				new BetterItem().material(Material.IRON_CHESTPLATE).displayName("§Arcanist Chestplate").grab());
		kitItems.put("Leggings", new BetterItem().material(Material.DIAMOND_LEGGINGS).displayName("§Arcanist Leggings")
				.enchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).enchant(Enchantment.PROTECTION_EXPLOSIONS, 2).grab());
		kitItems.put("Boots", new BetterItem().material(Material.IRON_BOOTS).displayName("§Arcanist Boots").grab());
		kitItems.put("1", new BetterItem().material(Material.DIAMOND_SWORD).displayName("§Arcanist Sword")
				.enchant(Enchantment.DURABILITY, 3).grab());
		kitItems.put("2", new BetterItem().material(Material.COOKED_BEEF).displayName("§bArcanist Steak").grab());
		kitItems.put("3",
				new BetterItem().material(Material.POTION).displayName("§bArcanist Health Potion (8�?�)").grab());
		kitItems.put("4", new BetterItem().material(Material.POTION).displayName("§bArcanist Speed Potion").grab());
		kitItems.put("5",
				new BetterItem().material(Material.WOOD).displayName("§bArcanist Blocks").data((short) 3).grab());
		kitItems.put("6", new BetterItem().material(Material.COMPASS).displayName("§bPlayer Tracker").grab());
		kitItems.put("7", new ItemStack(Material.AIR, 1));
		kitItems.put("8", new ItemStack(Material.AIR, 1));
		kitItems.put("9", new ItemStack(Material.AIR, 1));
	}

	public HashMap<String, ItemStack> getKitItems() {
		return kitItems;
	}

	public int getEnergyOnMeleeHit() {
		return 33;
	}

	public int getEnergyOnBowHit() {
		return 33;
	}

	public int getEnergyOverTime() {
		return 0;
	}

}
