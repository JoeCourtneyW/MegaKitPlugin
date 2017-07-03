package com.me.sly.kits.main.classes.listeners;

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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import com.me.sly.kits.main.Corrupted;
import com.me.sly.kits.main.classes.Class;
import com.me.sly.kits.main.classes.Classes;
import com.me.sly.kits.main.gametype.Team;
import com.me.sly.kits.main.resources.BetterItem;
import com.me.sly.kits.main.resources.EasyEnchant;
import com.me.sly.kits.main.resources.MyMetadata;

public class Blaze extends Class implements Listener {
	/**
	 * Fireball
	 */
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void BlazeFireball(PlayerInteractEvent event) {
		if (Classes.getClass(event.getPlayer()) == Classes.BLAZE) {
			if (Corrupted.getInstance().isActivateAbilityEvent(event)) {
				final Player p = event.getPlayer();
				p.playSound(p.getLocation(), Sound.BLAZE_BREATH, 1, 0.75F);
					new BukkitRunnable(){
						public void run(){
							Fireball f = (Fireball) p.launchProjectile(Fireball.class);
							f.setDirection(p.getLocation().clone().add(0, 0.5, 0).getDirection().multiply(1.5));
							f.setIsIncendiary(false);
							f.setShooter(p);
							f.setMetadata("blaze", new MyMetadata(Corrupted.getInstance(), ""));	
						}
					}.runTaskLater(Corrupted.getInstance(), 10);
				new BukkitRunnable(){
					public void run(){
						Fireball f1 = (Fireball) p.launchProjectile(Fireball.class);
						f1.setDirection(p.getLocation().clone().add(0, 0.5, 0).getDirection().multiply(1.5));
						f1.setIsIncendiary(false);
						f1.setShooter(p);
						f1.setMetadata("blaze", new MyMetadata(Corrupted.getInstance(), ""));
					}
				}.runTaskLater(Corrupted.getInstance(), 22);
				new BukkitRunnable(){
					public void run(){
						Fireball f2 = (Fireball) p.launchProjectile(Fireball.class);
						f2.setDirection(p.getLocation().clone().add(0, 0.5, 0).getDirection().multiply(1.5));
						f2.setIsIncendiary(false);
						f2.setShooter(p);	
						f2.setMetadata("blaze", new MyMetadata(Corrupted.getInstance(), ""));
					}
				}.runTaskLater(Corrupted.getInstance(), 34);
				event.getPlayer().setLevel(0);
				return;
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void FireballExplosionI(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Player && event.getDamager().getType() == EntityType.FIREBALL){
			event.setCancelled(true);
			if(((Player) ((Projectile)event.getDamager()).getShooter()).getName().equalsIgnoreCase(((Player) event.getEntity()).getName()))
			if(!Team.sameTeam((Player) event.getEntity(), (Player) ((Projectile)event.getDamager()).getShooter())){
			Corrupted.getInstance().damagePure((Player) event.getEntity(), 5.6, ((Projectile)event.getDamager()).getShooter());
			}
			event.getDamager().remove();
		}
	}
	/**
	 * Call of the Blazes
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	public void CallOfTheBlazes(PlayerDeathEvent event) {
		if (event.getEntity().getType() == EntityType.PLAYER && event.getEntity().getKiller() != null && event.getEntity().getKiller().getType() == EntityType.PLAYER) {
			Player p = (Player) event.getEntity().getKiller();
			Player death = (Player) event.getEntity();
			if (Classes.getClass(p) == Classes.BLAZE) {
				if(p.getWorld().getName().contains("Nube")){
				super.newMinion(death.getLocation(), p, EntityType.BLAZE);
				}
			}
		}
	}
	/**
	 * Fire Arrows
	 */
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void FireArrows(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		if(event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.ARROW && ((Arrow)event.getDamager()).getShooter() instanceof Player){
			Player damaged = (Player) event.getEntity();
			Player damager = (Player) ((Arrow)event.getDamager()).getShooter();
			if(Classes.getClass(damager) == Classes.BLAZE){
			if(Corrupted.getInstance().rand(1, 100) <= 30){
				Corrupted.getInstance().addNoReductionEffect(damaged, new PotionEffect(PotionEffectType.SLOW, 5*20, 1));
				damaged.setFireTicks(3*20);
			}
			}
		}
	}
	
	
	/**
	 * Kit Items
	 */
	public static HashMap<String, ItemStack> kitItems = new HashMap<String, ItemStack>();
	@Override
	public void fillKit(){
		kitItems.put("Helmet", new EasyEnchant(Material.IRON_HELMET, 1, "§bBlaze Helmet").craftToItemStack());
		kitItems.put("Chestplate", new EasyEnchant(Material.IRON_CHESTPLATE, 1, "§bBlaze Chestplate").craftToItemStack());
		kitItems.put("Leggings", new EasyEnchant(Material.IRON_LEGGINGS, 1, "§bBlaze Leggings").craftToItemStack());
		kitItems.put("Boots", new EasyEnchant(Material.IRON_BOOTS, 1, "§bBlaze Boots").craftToItemStack());
		kitItems.put("1", new EasyEnchant(Material.DIAMOND_SWORD, 1, "§bBlaze Sword").addEnchant(Enchantment.DURABILITY, 3).craftToItemStack());
		kitItems.put("2", new EasyEnchant(Material.BOW, 1, "§bBlaze Bow").addEnchant(Enchantment.ARROW_DAMAGE, 3).addEnchant(Enchantment.DURABILITY, 3).craftToItemStack());
		kitItems.put("3", new EasyEnchant(Material.POTION, 2, "§bBlaze Health Potion (8❤)").craftToItemStack());
		kitItems.put("4", new EasyEnchant(Material.POTION, 2, "§bBlaze Speed Potion").craftToItemStack());
		kitItems.put("5", new BetterItem(Material.WOOD, 64, "§bBlaze Blocks", (short)3).grab());
		kitItems.put("6", new EasyEnchant(Material.COOKED_BEEF, 64, "§bBlaze Steak").craftToItemStack());
		kitItems.put("7", new EasyEnchant(Material.COMPASS, 1, "§bPlayer Tracker").craftToItemStack());
		kitItems.put("8", new EasyEnchant(Material.ARROW, 64, "§bBlaze Arrow").craftToItemStack());
		kitItems.put("9", new ItemStack(Material.AIR, 1));
	}
	@Override
	public HashMap<String, ItemStack> getKitItems(){
		return kitItems;
	}
}
