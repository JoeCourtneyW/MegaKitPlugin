package com.me.sly.kits.main.classes.listeners;

import java.util.HashMap;

import org.bukkit.Material;
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
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.me.sly.kits.main.Chat;
import com.me.sly.kits.main.Corrupted;
import com.me.sly.kits.main.classes.Class;
import com.me.sly.kits.main.classes.Classes;
import com.me.sly.kits.main.gametype.Team;
import com.me.sly.kits.main.resources.BetterItem;
import com.me.sly.kits.main.resources.EasyEnchant;

public class Zombie extends Class implements Listener{

	
	/**
	 * Healing Ability
	 */
	@EventHandler
	public void HealingAbility(PlayerInteractEvent event){
		if(Classes.getClass(event.getPlayer()) == Classes.ZOMBIE){
			if(Corrupted.getInstance().isActivateAbilityEvent(event)){
				Player p = event.getPlayer();
				Chat.sendPlayerPrefixedMessage(p, "§e§lYou were healed by your Heal Skill");
				if (((CraftPlayer)p).getHealth() + 5.0 >= ((CraftPlayer)p).getMaxHealth()) p.setHealth(((CraftPlayer)p).getMaxHealth());
				else p.setHealth(((CraftPlayer)p).getHealth() + 5.0);
				for(Entity e : p.getNearbyEntities(3, 3, 3)){
					if(e instanceof Player){
						Player pe = (Player) e;
						if(Team.sameTeam(pe, p)){
						Chat.sendPlayerPrefixedMessage(pe, "§e§lYou were healed by §b" + p.getName() +"§e's Heal Skill");
						if (((CraftPlayer)pe).getHealth() + 3.5 >= ((CraftPlayer)pe).getMaxHealth()) pe.setHealth(((CraftPlayer)pe).getMaxHealth());
						else pe.setHealth(((CraftPlayer)pe).getHealth() + 3.5);
						}
					}
				}
			//Take away levels
			event.getPlayer().setLevel(0);
		}
		}
	}
	/**
	 * Toughness
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void Toughness(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		if(event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER){
			Player p = (Player) event.getEntity();
			if(Classes.getClass(p) == Classes.ZOMBIE){
			if(Corrupted.getInstance().rand(1, 100) <= 40){
				Corrupted.getInstance().addNoReductionEffect(p, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 40, 0));
			}
		}
		}
	}
	/**
	 * Berserk
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void Berserk(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		if(event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.ARROW){
			Player p = (Player) event.getEntity();
			if(Classes.getClass(p) == Classes.ZOMBIE){
			if(Corrupted.getInstance().rand(1, 100) <= 32){
				Corrupted.getInstance().addNoReductionEffect(p, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 3*20, 0));
				Corrupted.getInstance().addNoReductionEffect(p, new PotionEffect(PotionEffectType.SPEED, 3*20, 1));
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
		kitItems.put("Helmet", new EasyEnchant(Material.IRON_HELMET, 1, "§bZombie Helmet").craftToItemStack());
		kitItems.put("Chestplate", new EasyEnchant(Material.DIAMOND_CHESTPLATE, 1, "§bZombie Chestplate").addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).craftToItemStack());
		kitItems.put("Leggings", new EasyEnchant(Material.IRON_LEGGINGS, 1, "§bZombie Leggings").craftToItemStack());
		kitItems.put("Boots", new EasyEnchant(Material.IRON_BOOTS, 1, "§bZombie Boots").craftToItemStack());
		kitItems.put("1", new EasyEnchant(Material.IRON_SWORD, 1, "§bZombie Sword").addEnchant(Enchantment.DURABILITY, 3).craftToItemStack());
		kitItems.put("2", new EasyEnchant(Material.COOKED_BEEF, 64, "§bZombie Steak").craftToItemStack());
		kitItems.put("3", new EasyEnchant(Material.POTION, 1, "§bZombie Health Potion (10❤)").craftToItemStack());
		kitItems.put("4", new EasyEnchant(Material.POTION, 2, "§bZombie Speed Potion").craftToItemStack());
		kitItems.put("5", new BetterItem(Material.WOOD, 64, "§bZombie Blocks", (short)3).grab());
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
	 * Energy
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void EnergyOnHit(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		if(event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER){
			Player p = (Player) event.getDamager();
			if(Classes.getClass(p) == Classes.ZOMBIE){
				Corrupted.getInstance().addEnergy(p, 11);
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
			if(Classes.getClass(((Player)arrow.getShooter())) == Classes.ZOMBIE){
				Corrupted.getInstance().addEnergy(((Player)arrow.getShooter()), 11);
			}
		}
		}
	}
}
