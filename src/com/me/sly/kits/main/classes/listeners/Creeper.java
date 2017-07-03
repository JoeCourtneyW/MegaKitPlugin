package com.me.sly.kits.main.classes.listeners;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.me.sly.kits.main.Corrupted;
import com.me.sly.kits.main.classes.Class;
import com.me.sly.kits.main.classes.Classes;
import com.me.sly.kits.main.gametype.Team;
import com.me.sly.kits.main.resources.BetterItem;
import com.me.sly.kits.main.resources.EasyEnchant;
import com.me.sly.kits.main.resources.ParticleEffects;
public class Creeper extends Class implements Listener {
	/**
	 * Detonate
	 */
	@EventHandler
	public void HealingAbility(PlayerInteractEvent event){
		if(Classes.getClass(event.getPlayer()) == Classes.CREEPER){
			if(Corrupted.getInstance().isActivateAbilityEvent(event)){
				final Player p = event.getPlayer();
				final BukkitTask r = new BukkitRunnable(){
					public void run(){
						try {
							ParticleEffects.LARGE_SMOKE.sendToAllPlayers(p.getLocation(), 1F, 15);
						} catch (Exception e) {
						}
						for(Entity e : p.getNearbyEntities(6, 2.5, 6)){
							if(e instanceof Player){
								((Player) e).playSound(p.getLocation(), Sound.CREEPER_HISS, 1, 0.65F);
							}
						}	
						p.playSound(p.getLocation(), Sound.CREEPER_HISS, 1, 0.65F);
					}
				}.runTaskTimer(Corrupted.getInstance(), 0, 21);
				new BukkitRunnable(){
					public void run(){
						r.cancel();
						for(Entity e : p.getNearbyEntities(3.7, 2.5, 3.7)){
							if(e instanceof Player){
								if(!Team.sameTeam(p, (Player) e)){
									if(p.isDead()) return;
									if(e.isDead()) return;
									if(!p.hasMetadata("Class")) return;
									if(!e.hasMetadata("Class")) return;
							Corrupted.getInstance().damagePure((Player) e, 10.0-(e.getLocation().distance(p.getLocation())*1.7), p);
								}
								((Player) e).playSound(p.getLocation(), Sound.EXPLODE, 1, 1);
							}

						}	
						p.playSound(p.getLocation(), Sound.EXPLODE, 1, 1);
						try {
							ParticleEffects.LARGE_EXPLODE.sendToAllPlayers(p.getLocation(), 1F, 10);
						} catch (Exception e) {
						}

					}
				}.runTaskLater(Corrupted.getInstance(), 3*20);
				

			//Take away levels
			event.getPlayer().setLevel(0);
		}
		}
	}
	/**
	 * Minions
	 */
	@EventHandler(priority = EventPriority.LOW)
	public void Minions(PlayerDeathEvent event){
		if(event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player && !(event.getEntity().getName().equalsIgnoreCase(event.getEntity().getKiller().getName()))){
			Player p = (Player) event.getEntity();
			Location loc = p.getLocation();
			if(Classes.getClass(p) == Classes.CREEPER){
				if(p.getWorld().getName().contains("Nube")){
				super.newMinion(loc, p, EntityType.CREEPER);
				}
			}
		}
	}
	/**
	 * Willpower
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void Willpower(EntityDamageEvent event){
		if(event.isCancelled()) return;
		if(event.getEntity().getType() == EntityType.PLAYER){
			Player p = (Player) event.getEntity();
			if(Classes.getClass(p) == Classes.CREEPER){
			if(((CraftPlayer)p).getHealth() < 14.0){
				if(!p.isDead()){
					Corrupted.getInstance().addNoReductionEffect(p, new PotionEffect(PotionEffectType.SPEED, 8*20, 1));
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
		kitItems.put("Helmet", new EasyEnchant(Material.IRON_HELMET, 1, "§bCreeper Helmet").craftToItemStack());
		kitItems.put("Chestplate", new EasyEnchant(Material.IRON_CHESTPLATE, 1, "§bCreeper Chestplate").craftToItemStack());
		kitItems.put("Leggings", new EasyEnchant(Material.DIAMOND_LEGGINGS, 1, "§bCreeper Leggings").addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 4).craftToItemStack());
		kitItems.put("Boots", new EasyEnchant(Material.IRON_BOOTS, 1, "§bCreeper Boots").craftToItemStack());
		kitItems.put("1", new EasyEnchant(Material.IRON_SWORD, 1, "§bCreeper Sword").addEnchant(Enchantment.DURABILITY, 3).craftToItemStack());
		kitItems.put("2", new EasyEnchant(Material.COOKED_BEEF, 64, "§bCreeper Steak").craftToItemStack());
		kitItems.put("3", new EasyEnchant(Material.POTION, 2, "§bCreeper Health Potion (8❤)").craftToItemStack());
		kitItems.put("4", new EasyEnchant(Material.POTION, 2, "§bCreeper Speed Potion").craftToItemStack());
		kitItems.put("5", new BetterItem(Material.WOOD, 64, "§bCreeper Blocks", (short)3).grab());
		kitItems.put("6", new EasyEnchant(Material.COMPASS, 1, "§bPlayer Tracker").craftToItemStack());
		kitItems.put("7", new ItemStack(Material.AIR, 1));
		kitItems.put("8", new ItemStack(Material.AIR, 1));
		kitItems.put("9", new ItemStack(Material.AIR, 1));
	}
	@Override
	public HashMap<String, ItemStack> getKitItems(){
		return kitItems;
	}
	
	/**
	 * Energy On Hit
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void EnergyOnHit(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		if(event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER){
			Player p = (Player) event.getDamager();
			if(Classes.getClass(p) == Classes.CREEPER){
				Corrupted.getInstance().addEnergy(p, 20);
			}
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.MONITOR)
	public void EnergyOnBowHit(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		if(event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.ARROW){
			Arrow arrow = (Arrow)event.getDamager();
			if(!(event.getEntity() == arrow.getShooter())){
			if(Classes.getClass(((Player)arrow.getShooter())) == Classes.CREEPER){
				Corrupted.getInstance().addEnergy(((Player)arrow.getShooter()), 20);
			}
		}
		}
	}

}
