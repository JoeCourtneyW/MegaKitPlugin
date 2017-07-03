package com.me.sly.kits.main.classes.listeners;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
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

public class Spider extends Class implements Listener {
	/**
	 * Detonate
	 */
	@EventHandler
	public void HealingAbility(PlayerInteractEvent event){
		if(Classes.getClass(event.getPlayer()) == Classes.SPIDER){
			if(Corrupted.getInstance().isActivateAbilityEvent(event)){
				final Player p = event.getPlayer();
					p.setVelocity(p.getLocation().getDirection().setY(0.6).multiply(2.5));					
				Corrupted.getInstance().addNoReductionEffect(p, new PotionEffect(PotionEffectType.ABSORPTION, 5*20, 0));
				new BukkitRunnable(){
					@SuppressWarnings("deprecation")
					public void run(){
						if(p.isOnGround()){
							for(Entity e : p.getNearbyEntities(7, 7, 7)){
								if(e instanceof Player){
									if(!Team.sameTeam(p, (Player) e)){
								Corrupted.getInstance().addNoReductionEffect((Player)e, new PotionEffect(PotionEffectType.SLOW, 4*20, 2));
									}
								}
							}
							this.cancel();
						}
					}
				}.runTaskTimer(Corrupted.getInstance(), 9, 3);
				

			//Take away levels
			event.getPlayer().setLevel(0);
		}
		}
	}
	/**
	 * Nest Egg
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void NestEgg(PlayerDeathEvent event){
		if(event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player && !(event.getEntity().getName().equalsIgnoreCase(event.getEntity().getKiller().getName()))){
			Player p = (Player) event.getEntity();
			Location loc = p.getLocation();
			if(!p.getWorld().getName().contains("Nube")) return;
			if(Classes.getClass(p) == Classes.SPIDER){
				super.newMinion(loc, p, EntityType.SPIDER);
				super.newMinion(loc, p, EntityType.SPIDER);
				super.newMinion(loc, p, EntityType.SPIDER);
			}
		}
	}
	/**
	 * Drop Shock
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void DropShock(EntityDamageEvent event){
		if(event.isCancelled()) return;
		if(event.getEntity().getType() == EntityType.PLAYER && event.getCause() == DamageCause.FALL){
			Player p = (Player) event.getEntity();
			if(Classes.getClass(p) == Classes.SPIDER){
			if(event.getDamage() > 4.0){
				double dmg = 0.0;
				if(event.getDamage()*1.8 >= 21.0){
					dmg = 21.0;
				}else{
					dmg = event.getDamage()*1.8;
				}
				for(Entity e : p.getNearbyEntities(4.5, 4.5, 4.5)){
					if(e instanceof Player){
						if(((Player) e).getNoDamageTicks() < 19){
						double distance = e.getLocation().distance(p.getLocation());
						dmg -= Math.abs(distance*1.6);
						Corrupted.getInstance().damagePure((Player) e, dmg, p);
						((Player) e).setNoDamageTicks(20);
						}
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
	@Override
	public void fillKit(){
		kitItems.put("Helmet", new EasyEnchant(Material.IRON_HELMET, 1, "§bSpider Helmet").craftToItemStack());
		kitItems.put("Chestplate", new EasyEnchant(Material.IRON_CHESTPLATE, 1, "§bSpider Chestplate").craftToItemStack());
		kitItems.put("Leggings", new EasyEnchant(Material.IRON_LEGGINGS, 1, "§bSpider Leggings").craftToItemStack());
		kitItems.put("Boots", new EasyEnchant(Material.DIAMOND_BOOTS, 1, "§bSpider Boots").addEnchant(Enchantment.DURABILITY, 1).craftToItemStack());
		kitItems.put("1", new EasyEnchant(Material.DIAMOND_SWORD, 1, "§bSpider Sword").addEnchant(Enchantment.DURABILITY, 3).craftToItemStack());
		kitItems.put("2", new EasyEnchant(Material.COOKED_BEEF, 64, "§bSpider Steak").craftToItemStack());
		kitItems.put("3", new EasyEnchant(Material.POTION, 2, "§bSpider Health Potion (8❤)").craftToItemStack());
		kitItems.put("4", new EasyEnchant(Material.POTION, 2, "§bSpider Speed Potion").craftToItemStack());
		kitItems.put("5", new BetterItem(Material.WOOD, 64, "§bSpider Blocks", (short)3).grab());
		kitItems.put("6", new EasyEnchant(Material.COMPASS, 1, "§bPlayer Tracker").craftToItemStack());
		kitItems.put("7", new ItemStack(Material.AIR, 1));
		kitItems.put("8", new ItemStack(Material.AIR, 1));
		kitItems.put("9", new ItemStack(Material.AIR, 1));
	}
	@Override
	public HashMap<String, ItemStack> getKitItems(){
		return kitItems;
	}

}
