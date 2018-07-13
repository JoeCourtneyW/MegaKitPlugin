package com.sly.main.kits.listeners;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.sly.main.Server;
import com.sly.main.gametype.Squad;
import com.sly.main.kits.Kit;
import com.sly.main.kits.Kits;
import com.sly.main.player.PlayerModel;
import com.sly.main.resources.BetterItem;
import com.sly.main.resources.MathUtil;
import com.sly.main.resources.MyMetadata;

public class Blaze extends Kit implements Listener
{

	/**
	 * Fireball
	 */
	public void mainAbility(final PlayerModel player) {
		Player p = player.getPlayer();
		p.playSound(p.getLocation(), Sound.ENTITY_BLAZE_BURN, 1, 0.75F); //Blaze breath sound, maybe not the right one
		// For loop to spawn 3 fireballs 12 ticks after one another
		for (int i = 0; i < 3; i++) {
			if (p.isDead())
				break;
			new BukkitRunnable() {
				public void run() {
					Fireball f = (Fireball) p.launchProjectile(Fireball.class);
					f.setDirection(p.getLocation().clone().add(0, 0.5, 0).getDirection().multiply(1.5));
					f.setIsIncendiary(false);
					f.setShooter(p);
					f.setMetadata("blaze", new MyMetadata(Server.getInstance(), ""));
				}
			}.runTaskLater(Server.getInstance(), 10 + 12 * i);
		}
	}

	// Listener for main ability damage event to increase damage done by normal fireballs
	@EventHandler
	public void FireballExplosionI(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager().getType() == EntityType.FIREBALL) {
			Projectile fireball = (Projectile) event.getDamager();
			PlayerModel blaze = PlayerModel.getPlayerModel((Player) fireball.getShooter());
			event.setDamage(0.0);
			event.setCancelled(true);
			if (blaze.getPlayer().getName().equalsIgnoreCase(event.getEntity().getName())) {
                if (blaze.sameSquad(PlayerModel.getPlayerModel((Player) fireball.getShooter()))){
                    blaze.damagePure(5.6, ((Projectile) event.getDamager()).getShooter());
                }
            }
			event.getDamager().remove();
		}
	}

	/**
	 * Call of the Blazes
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void CallOfTheBlazes(PlayerDeathEvent event) {
		if (event.getEntity().getType() == EntityType.PLAYER && event.getEntity().getKiller() != null
				&& event.getEntity().getKiller().getType() == EntityType.PLAYER) {
			PlayerModel p = PlayerModel.getPlayerModel(event.getEntity().getKiller());
			Player death = event.getEntity();
			if (p.getKit() == Kits.BLAZE) {
				if (p.getPlayer().getWorld().getName().contains("Nube")) {
					super.newMinion(death.getLocation(), p.getPlayer(), EntityType.BLAZE);
				}
			}
		}
	}

	/**
	 * Fire Arrows
	 */
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void FireArrows(EntityDamageByEntityEvent event) {
		if (event.isCancelled())
			return;
		if (event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.ARROW
				&& ((Arrow) event.getDamager()).getShooter() instanceof Player) {
			PlayerModel damaged = PlayerModel.getPlayerModel((Player) event.getEntity());
			PlayerModel damager = PlayerModel.getPlayerModel((Player) ((Arrow) event.getDamager()).getShooter());
			if (damager.getKit() == Kits.BLAZE) {
				if (MathUtil.rand(1, 100) <= 30) {
					damaged.addNoReductionEffect(new PotionEffect(PotionEffectType.SLOW, 5 * 20, 1));
					damaged.getPlayer().setFireTicks(3 * 20);
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
		kitItems.put("Helmet", new EasyEnchant(Material.IRON_HELMET, 1, "§bBlaze Helmet").craftToItemStack());
		kitItems.put("Chestplate",
				new EasyEnchant(Material.IRON_CHESTPLATE, 1, "§bBlaze Chestplate").craftToItemStack());
		kitItems.put("Leggings", new EasyEnchant(Material.IRON_LEGGINGS, 1, "§bBlaze Leggings").craftToItemStack());
		kitItems.put("Boots", new EasyEnchant(Material.IRON_BOOTS, 1, "§bBlaze Boots").craftToItemStack());
		kitItems.put("1", new EasyEnchant(Material.DIAMOND_SWORD, 1, "§bBlaze Sword")
				.addEnchant(Enchantment.DURABILITY, 3).craftToItemStack());
		kitItems.put("2", new EasyEnchant(Material.BOW, 1, "§bBlaze Bow").addEnchant(Enchantment.ARROW_DAMAGE, 3)
				.addEnchant(Enchantment.DURABILITY, 3).craftToItemStack());
		kitItems.put("3", new EasyEnchant(Material.POTION, 2, "§bBlaze Health Potion (8�?�)").craftToItemStack());
		kitItems.put("4", new EasyEnchant(Material.POTION, 2, "§bBlaze Speed Potion").craftToItemStack());
		kitItems.put("5", new BetterItem(Material.WOOD, 64, "§bBlaze Blocks", (short) 3).grab());
		kitItems.put("6", new EasyEnchant(Material.COOKED_BEEF, 64, "§bBlaze Steak").craftToItemStack());
		kitItems.put("7", new EasyEnchant(Material.COMPASS, 1, "§bPlayer Tracker").craftToItemStack());
		kitItems.put("8", new EasyEnchant(Material.ARROW, 64, "§bBlaze Arrow").craftToItemStack());
		kitItems.put("9", new ItemStack(Material.AIR, 1));
	}

	@Override
	public HashMap<String, ItemStack> getKitItems() {
		return kitItems;
	}

	@Override
	public int getEnergyOnBowHit() {
		return 0;
	}

	@Override
	public int getEnergyOnMeleeHit() {
		return 0;
	}

	@Override
	public int getEnergyOverTime() {
		return 6;
	}
}
