package com.sly.main.kits.listeners;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.sly.main.Server;
import com.sly.main.gametype.Squad;
import com.sly.main.kits.Kit;
import com.sly.main.kits.Kits;
import com.sly.main.resources.BetterItem;
import com.sly.main.resources.EasyEnchant;
import com.sly.main.resources.MathUtil;
import com.sly.main.resources.MyMetadata;

public class Dreadlord extends Kit implements Listener
{

	
	/**
	 * Wither Blast 
	 */
	@SuppressWarnings("deprecation")
	public void mainAbility(Player p) {
		WitherSkull skull;
		for(int i = 0; i < 3; i++){
			skull = (WitherSkull) p.launchProjectile(WitherSkull.class);
			skull.setDirection(p.getLocation().clone().add(0, 0.5, 0).getDirection().multiply(1.2));
			skull.setIsIncendiary(false);
			skull.setVelocity(p.getLocation().getDirection().add(new Vector(-0.4F + (0.4F)*i, 0, 0)));
			skull.setShooter(p);
			skull.setMetadata("dread", new MyMetadata(Server.getInstance(), ""));
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void WitherExplosion(ProjectileHitEvent event) {
		if (event.getEntity().hasMetadata("dread")) {
			event.getEntity().getWorld().createExplosion(event.getEntity().getLocation().getX(),
					event.getEntity().getLocation().getY(), event.getEntity().getLocation().getZ(), 0F, false, false);
			for (Entity e : event.getEntity().getNearbyEntities(1.9, 1.9, 1.9)) {
				if (e.getType() == EntityType.PLAYER) {
					Player p = (Player) e;
					if (!p.getName().equalsIgnoreCase(((Player) event.getEntity().getShooter()).getName())) {
						if (p.getNoDamageTicks() <= 18) {
							if (!Squad.sameTeam((Player) event.getEntity().getShooter(), p)) {
								Server.getInstance().damagePure(p, 7.2, event.getEntity().getShooter());
								p.setNoDamageTicks(20);
							}
						}
						event.getEntity().remove();
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void WitherExplosionI(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player && event.getDamager().getType() == EntityType.WITHER_SKULL) {
			event.setCancelled(true);
			if (!((Player) event.getEntity()).getName()
					.equalsIgnoreCase(((Player) ((Projectile) event.getDamager()).getShooter()).getName())) {

				if (((Player) event.getEntity()).getNoDamageTicks() <= 18) { //TODO: Dude, idek what this does, someone care to tell me?
					if (!Squad.sameTeam((Player) ((Projectile) event.getDamager()).getShooter(),
							(Player) event.getEntity())) {
						Server.getInstance().damagePure((Player) event.getEntity(), 8.0,
								((Projectile) event.getDamager()).getShooter());
						((Player) event.getEntity()).setNoDamageTicks(20);
					}
				}
				event.getDamager().remove();
			}
		}
	}

	/**
	 * Soul Siphon
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void SoulSiphon(PlayerDeathEvent event) {
		if (event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player) {
			Player p = (Player) event.getEntity().getKiller();
			if (Kits.getClass(p) == Kits.DREADLORD) {
				Server.getInstance().addNoReductionEffect(p,
						new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 5 * 20, 0));
				Server.getInstance().addNoReductionEffect(p,
						new PotionEffect(PotionEffectType.REGENERATION, 6 * 20, 0));
			}
		}
	}

	/**
	 * Soul Eater
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void SoulEater(EntityDamageByEntityEvent event) {
		if (event.isCancelled())
			return;
		if (event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER) {
			Player p = (Player) event.getDamager();
			if (Kits.getClass(p) == Kits.DREADLORD) {
				if (MathUtil.rand(1, 100) <= 25) {
					if (((CraftPlayer) p).getHealth() + 1 >= ((CraftPlayer) p).getMaxHealth())
						p.setHealth(((CraftPlayer) p).getMaxHealth());
					else
						p.setHealth(((CraftPlayer) p).getHealth() + 1);
					if (p.getFoodLevel() + 1 >= 20)
						p.setFoodLevel(20);
					else
						p.setFoodLevel(p.getFoodLevel() + 1);
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
		kitItems.put("Helmet", new EasyEnchant(Material.DIAMOND_HELMET, 1, "§bDreadlord Helmet")
				.addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 2).craftToItemStack());
		kitItems.put("Chestplate",
				new EasyEnchant(Material.IRON_CHESTPLATE, 1, "§bDreadlord Chestplate").craftToItemStack());
		kitItems.put("Leggings", new EasyEnchant(Material.IRON_LEGGINGS, 1, "§bDreadlord Leggings").craftToItemStack());
		kitItems.put("Boots", new EasyEnchant(Material.IRON_BOOTS, 1, "§bDreadlord Boots").craftToItemStack());
		kitItems.put("1", new EasyEnchant(Material.DIAMOND_SWORD, 1, "§bDreadlord Sword")
				.addEnchant(Enchantment.DURABILITY, 3).addEnchant(Enchantment.DAMAGE_UNDEAD, 1).craftToItemStack());
		kitItems.put("2", new EasyEnchant(Material.COOKED_BEEF, 64, "§bDreadlord Steak").craftToItemStack());
		kitItems.put("3", new EasyEnchant(Material.POTION, 2, "§bDreadlord Health Potion (8�?�)").craftToItemStack());
		kitItems.put("4", new EasyEnchant(Material.POTION, 2, "§bDreadlord Speed Potion").craftToItemStack());
		kitItems.put("5", new BetterItem(Material.WOOD, 64, "§bDreadlord Blocks", (short) 3).grab());
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
		return 10;
	}

	public int getEnergyOnBowHit() {
		return 10;
	}

	public int getEnergyOverTime() {
		return 0;
	}

}
