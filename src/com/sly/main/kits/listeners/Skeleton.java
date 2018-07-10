package com.sly.main.kits.listeners;


import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.sly.main.Server;
import com.sly.main.gametype.Squad;
import com.sly.main.kits.Kit;
import com.sly.main.kits.Kits;
import com.sly.main.resources.BetterItem;
import com.sly.main.resources.EasyEnchant;
import com.sly.main.resources.IconMenu;
import com.sly.main.resources.MyMetadata;
import com.sly.main.server.Chat;

public class Skeleton extends Kit implements Listener{

	@SuppressWarnings("deprecation")
	public void mainAbility(Player p) {
		Arrow e = p.shootArrow();
		e.setVelocity(e.getVelocity().multiply(1.2));
		e.setShooter(p);
		e.setMetadata("Explosive", new MyMetadata(Server.getInstance(), true));	
	}
	
	//Arrow Explosion
	@SuppressWarnings("deprecation")
	@EventHandler
	public void ArrowExplosion(ProjectileHitEvent event){
		if(event.getEntity().hasMetadata("Explosive")){
			event.getEntity().getWorld().createExplosion(event.getEntity().getLocation().getX(), event.getEntity().getLocation().getY(), event.getEntity().getLocation().getZ(), 0F, false, false);
			for(Entity e : event.getEntity().getNearbyEntities(3, 3, 3)){
				if(e.getType() == EntityType.PLAYER){
					Player p = (Player) e;
					if(p.getName().equalsIgnoreCase(((Player) event.getEntity().getShooter()).getName())) return;
					if(!Squad.sameTeam(p,  (Player) event.getEntity().getShooter())){
					Server.getInstance().damagePure(p, 5.0, event.getEntity().getShooter());
					}
					event.getEntity().remove();
				}
			}
		}
	}
	/**
	 * Arrow Recovery
	 */
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void ArrowRecovery(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		if(event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.ARROW){
			Arrow arrow = (Arrow)event.getDamager();
			if(!(event.getEntity() == arrow.getShooter())){
			if(Kits.getClass(((Player)arrow.getShooter())) == Kits.SKELETON){
				if(Server.getInstance().rand(1, 9) != 5){
					((Player)arrow.getShooter()).getInventory().addItem(IconMenu.setItemNameAndLore(new ItemStack(Material.ARROW, 2), "§bSkeleton Arrow", new String[0]));
					Chat.sendPlayerMessage(((Player)arrow.getShooter()), "§eYour Salvaging Skill returned 2 arrows to you!");
				}
			}
			}
		}
	}
	
	
	/**
	 * Agile Bonus
	 */
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void AgileBonus(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		if(event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.ARROW){
			Arrow arrow = (Arrow)event.getDamager();
			if(!(event.getEntity() == arrow.getShooter())){
			if(Kits.getClass(((Player)arrow.getShooter())) == Kits.SKELETON){
				Server.getInstance().addNoReductionEffect(((Player)arrow.getShooter()), new PotionEffect(PotionEffectType.SPEED, 5*20, 1));
				Server.getInstance().addNoReductionEffect(((Player)arrow.getShooter()), new PotionEffect(PotionEffectType.REGENERATION, 4*20, 0));
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
		kitItems.put("Helmet", new EasyEnchant(Material.DIAMOND_HELMET, 1, "§bSkeleton Helmet").addEnchant(Enchantment.PROTECTION_PROJECTILE, 4).craftToItemStack());
		kitItems.put("Chestplate", new EasyEnchant(Material.IRON_CHESTPLATE, 1, "§bSkeleton Chestplate").craftToItemStack());
		kitItems.put("Leggings", new EasyEnchant(Material.IRON_LEGGINGS, 1, "§bSkeleton Leggings").craftToItemStack());
		kitItems.put("Boots", new EasyEnchant(Material.IRON_BOOTS, 1, "§bSkeleton Boots").craftToItemStack());
		kitItems.put("1", new EasyEnchant(Material.IRON_AXE, 1, "§bSkeleton Axe").addEnchant(Enchantment.DIG_SPEED, 1).addEnchant(Enchantment.DURABILITY, 3).craftToItemStack());
		kitItems.put("2", new EasyEnchant(Material.BOW, 1, "§bSkeleton Bow").addEnchant(Enchantment.ARROW_DAMAGE, 3).addEnchant(Enchantment.DURABILITY, 3).craftToItemStack());
		kitItems.put("3", new EasyEnchant(Material.POTION, 2, "§bSkeleton Health Potion (8�?�)").craftToItemStack());
		kitItems.put("4", new EasyEnchant(Material.POTION, 2, "§bSkeleton Speed Potion").craftToItemStack());
		kitItems.put("5", new BetterItem(Material.WOOD, 64, "§bSkeleton Blocks", (short)3).grab());
		kitItems.put("6", new EasyEnchant(Material.COOKED_BEEF, 64, "§bSkeleton Steak").craftToItemStack());
		kitItems.put("7", new EasyEnchant(Material.COMPASS, 1, "§bPlayer Tracker").craftToItemStack());
		kitItems.put("8", new EasyEnchant(Material.ARROW, 64, "§bSkeleton Arrow").craftToItemStack());
		kitItems.put("9", new ItemStack(Material.AIR, 1));
	}
	
	public HashMap<String, ItemStack> getKitItems(){
		return kitItems;
	}
	
	public int getEnergyOnBowHit() {
		return 25;
	}
	
	public int getEnergyOnMeleeHit() {
		return 0;
	}
	
	public int getEnergyOverTime() {
		return 1;
	}
}
