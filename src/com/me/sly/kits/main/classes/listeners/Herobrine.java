package com.me.sly.kits.main.classes.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.me.sly.kits.main.Corrupted;
import com.me.sly.kits.main.classes.Class;
import com.me.sly.kits.main.classes.Classes;
import com.me.sly.kits.main.gametype.Team;
import com.me.sly.kits.main.resources.BetterItem;
import com.me.sly.kits.main.resources.EasyEnchant;

public class Herobrine extends Class implements Listener {
	/**
	 * Wrath
	 */
	@EventHandler
    public void Wrath(final PlayerInteractEvent event) {
    	if(Classes.getClass(event.getPlayer()).equals(Classes.HEROBRINE)){
    	if(Corrupted.getInstance().isActivateAbilityEvent(event)){
			List<String> playersHit = new ArrayList<String>();
    		for(Entity e : event.getPlayer().getNearbyEntities(3.5, 3.5, 3.5)){
    			if(e instanceof Player){
					if(!Team.sameTeam(event.getPlayer(), (Player) e)){
						if(((Player) e).getGameMode().equals(GameMode.CREATIVE)) continue;
    				playersHit.add(((Player) e).getName());
    				Corrupted.getInstance().damagePure((Player) e, 4.0, event.getPlayer());
    				e.getWorld().strikeLightningEffect(e.getLocation());
					}
    			}
    		}

    		if(playersHit.size() != 0){
		//Take away levels
		event.getPlayer().setLevel(0);
		String playerList = "";
		for(String r : playersHit){
			playerList = playerList + ", " + r;
		}
		playerList = playerList.substring(2);
		for(String r : playersHit){
			Bukkit.getPlayer(r).sendMessage("§b" + event.getPlayer().getName() + "§e's Wrath skill struck " + playerList);
		}
		event.getPlayer().sendMessage("§b" + event.getPlayer().getName() + "§e's Wrath skill struck " + playerList);
    	}else{
    		event.getPlayer().sendMessage("§cNo player within range to target");
    	}
    	}
    }
    }
    
    /**
     * Power
     */
	@EventHandler(priority = EventPriority.HIGH)
	public void Power(PlayerDeathEvent event){
		if(event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player){
			Player p = (Player) event.getEntity().getKiller();
			if(Classes.getClass(p) == Classes.HEROBRINE){
				Corrupted.getInstance().addNoReductionEffect(p, new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 6*20, 0));
		}
	}
	}
	/**
	 * Flurry
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void Flurry(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		if(event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER){
			Player p = (Player) event.getDamager();
			if(Classes.getClass(p) == Classes.HEROBRINE){
			if(Corrupted.getInstance().rand(1, 100) <= 85){
				Corrupted.getInstance().addNoReductionEffect(p, new PotionEffect(PotionEffectType.SPEED, 20, 1));
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
		kitItems.put("Helmet", new EasyEnchant(Material.IRON_HELMET, 1, "§bHerobrine Helmet").addEnchant(Enchantment.WATER_WORKER, 1).addEnchant(Enchantment.DURABILITY, 3).craftToItemStack());
		kitItems.put("Chestplate", new EasyEnchant(Material.IRON_CHESTPLATE, 1, "§bHerobrine Chestplate").craftToItemStack());
		kitItems.put("Leggings", new EasyEnchant(Material.IRON_LEGGINGS, 1, "§bHerobrine Leggings").craftToItemStack());
		kitItems.put("Boots", new EasyEnchant(Material.IRON_BOOTS, 1, "§bHerobrine Boots").craftToItemStack());
		kitItems.put("1", new EasyEnchant(Material.DIAMOND_SWORD, 1, "§bHerobrine Sword").addEnchant(Enchantment.DURABILITY, 3).craftToItemStack());
		kitItems.put("2", new EasyEnchant(Material.COOKED_BEEF, 64, "§bHerobrine Steak").craftToItemStack());
		kitItems.put("3", new EasyEnchant(Material.POTION, 2, "§bHerobrine Health Potion (7❤)").craftToItemStack());
		kitItems.put("4", new EasyEnchant(Material.POTION, 2, "§bHerobrine Speed Potion").craftToItemStack());
		kitItems.put("5", new BetterItem(Material.WOOD, 64, "§bHerobrine Blocks", (short)3).grab());
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
			if(Classes.getClass(p) == Classes.HEROBRINE){
				Corrupted.getInstance().addEnergy(p, 17);
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
			if(Classes.getClass(((Player)arrow.getShooter())) == Classes.HEROBRINE){
				Corrupted.getInstance().addEnergy(((Player)arrow.getShooter()), 17);
			}
		}
		}
	}
}
