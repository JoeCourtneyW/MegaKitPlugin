package com.me.sly.kits.main.classes.listeners;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import com.me.sly.kits.main.Corrupted;
import com.me.sly.kits.main.classes.Class;
import com.me.sly.kits.main.classes.Classes;
import com.me.sly.kits.main.gametype.Team;
import com.me.sly.kits.main.resources.BetterItem;
import com.me.sly.kits.main.resources.EasyEnchant;

public class Arcanist extends Class implements Listener{
	
	/**
	 * Arcane Beam
	 */
	@SuppressWarnings("deprecation")
	@EventHandler
	public void ArcaneBeam(PlayerInteractEvent event){
		if(Classes.getClass(event.getPlayer()) == Classes.ARCANIST){
			if(Corrupted.getInstance().isActivateAbilityEvent(event)){
			Player p = event.getPlayer();
			for(Block b : p.getLineOfSight(null, 50)){
				if(b.getType() != Material.AIR) break;
				Firework f = (Firework) b.getWorld().spawnEntity(b.getLocation().add(0.5D, 0D, 0.5D),EntityType.FIREWORK);
				f.remove();
				List<Entity> players = f.getNearbyEntities(1.7, 1.7, 1.7);
				if(players == null) continue;
				for(Entity e : players){
					if(e.getType() == EntityType.PLAYER){
						if(!Team.sameTeam(p, (Player) e)){
						if(!((Player)e).getName().equalsIgnoreCase(event.getPlayer().getName())){
						Corrupted.getInstance().damageImpure(((Player)e), 3.3D, p);
						((Player) e).playSound(p.getLocation(), Sound.WITHER_SHOOT, 1, 2);
						p.playSound(p.getLocation(), Sound.WITHER_SHOOT, 1, 2);
						event.getPlayer().setLevel(0);
						return;
							}
						}
						}
					}
				}
		//Take away levels
		event.getPlayer().setLevel(0);
		}
	}
	}
	/**
	 * Tempest
	 */
	@EventHandler
	public void Tempest(PlayerDeathEvent event){
		if(event.getEntity().getKiller() != null && Classes.getClass(event.getEntity().getKiller()) == Classes.ARCANIST){
			Corrupted.getInstance().addNoReductionEffect(event.getEntity().getKiller(), new PotionEffect(PotionEffectType.SPEED, 6*20, 2));
			Corrupted.getInstance().addNoReductionEffect(event.getEntity().getKiller(), new PotionEffect(PotionEffectType.REGENERATION, 5*20, 1));
			Corrupted.getInstance().addEnergy(event.getEntity().getKiller(), 90);
		}
	}
	/**
	 * Arcane Explosion 
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void ArcaneExplosion(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		if(event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER){
			Player p = (Player) event.getEntity();
			if(Classes.getClass(p) == Classes.ARCANIST){
			if(Corrupted.getInstance().rand(1, 100) <= 20){
				p.playSound(p.getLocation(), Sound.EXPLODE, 1, 2);
				for(Entity e : p.getNearbyEntities(3, 3, 3)){
					if(e instanceof Player){
						Corrupted.getInstance().damagePure((Player) e, 2.0, p);
						((Player) e).playSound(p.getLocation(), Sound.EXPLODE, 1, 2);
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
		kitItems.put("Helmet", new EasyEnchant(Material.IRON_HELMET, 1, "§bArcanist Helmet").craftToItemStack());
		kitItems.put("Chestplate", new EasyEnchant(Material.IRON_CHESTPLATE, 1, "§bArcanist Chestplate").craftToItemStack());
		kitItems.put("Leggings", new EasyEnchant(Material.DIAMOND_LEGGINGS, 1, "§bArcanist Leggings").addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).addEnchant(Enchantment.PROTECTION_EXPLOSIONS, 2).craftToItemStack());
		kitItems.put("Boots", new EasyEnchant(Material.IRON_BOOTS, 1, "§bArcanist Boots").craftToItemStack());
		kitItems.put("1", new EasyEnchant(Material.DIAMOND_SWORD, 1, "§bArcanist Sword").addEnchant(Enchantment.DURABILITY, 3).craftToItemStack());
		kitItems.put("2", new EasyEnchant(Material.COOKED_BEEF, 64, "§bArcanist Steak").craftToItemStack());
		kitItems.put("3", new EasyEnchant(Material.POTION, 2, "§bArcanist Health Potion (8❤)").craftToItemStack());
		kitItems.put("4", new EasyEnchant(Material.POTION, 2, "§bArcanist Speed Potion").craftToItemStack());
		kitItems.put("5", new BetterItem(Material.WOOD, 64, "§bArcanist Blocks", (short)3).grab());
		kitItems.put("6", new EasyEnchant(Material.COMPASS, 1, "§bPlayer Tracker").craftToItemStack());
		kitItems.put("7", new ItemStack(Material.AIR, 1));
		kitItems.put("8", new ItemStack(Material.AIR, 1));
		kitItems.put("9", new ItemStack(Material.AIR, 1));
	}
	@Override
	public HashMap<String, ItemStack> getKitItems(){
		return kitItems;
	}
	public ItemStack getSlownessPot(){
		Potion p = new Potion(0);
		p.setType(PotionType.INSTANT_HEAL);
		p.getEffects().clear();
		ItemStack i =  p.toItemStack(2);
		PotionMeta im = (PotionMeta) i.getItemMeta();
		im.setDisplayName("§bArcanist Health Potion (8❤)");
		im.addCustomEffect(new PotionEffect(PotionEffectType.SLOW,  10*20, 2), true);
		i.setItemMeta(im);
		return i;
	}
	/**
	 * Energy On Hit
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void EnergyOnHit(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		if(event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER){
			Player p = (Player) event.getDamager();
			if(Classes.getClass(p) == Classes.ARCANIST){
				Corrupted.getInstance().addEnergy(p, 33);
			}
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void EnergyOnBowHit(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		if(event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.ARROW){
			Arrow arrow = (Arrow)event.getDamager();
			if(!(event.getEntity() == arrow.getShooter())){
			if(Classes.getClass(((Player)arrow.getShooter())) == Classes.ARCANIST){
				Corrupted.getInstance().addEnergy(((Player)arrow.getShooter()), 33);
			}
		}
		}
	}
}
