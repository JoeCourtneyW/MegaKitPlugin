package com.sly.main.kits.listeners;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

import com.sly.main.Server;
import com.sly.main.kits.Kit;
import com.sly.main.kits.Kits;
import com.sly.main.resources.BetterItem;
import com.sly.main.resources.EasyEnchant;
public class Pirate extends Kit implements Listener{
	/**
	 * Kit Items
	 */
	public static HashMap<String, ItemStack> kitItems = new HashMap<String, ItemStack>();
	@Override
	public void fillKit(){
		kitItems.put("Helmet", new EasyEnchant(Material.IRON_HELMET, 1, "§bPirate Helmet").craftToItemStack());
		kitItems.put("Chestplate", new EasyEnchant(Material.IRON_CHESTPLATE, 1, "§bPirate Chestplate").craftToItemStack());
		kitItems.put("Leggings", new EasyEnchant(Material.IRON_LEGGINGS, 1, "§bPirate Leggings").craftToItemStack());
		kitItems.put("Boots", new EasyEnchant(Material.IRON_BOOTS, 1, "§bPirate Boots").craftToItemStack());
		kitItems.put("1", new EasyEnchant(Material.IRON_SWORD, 1, "§bPirate Sword").addEnchant(Enchantment.DURABILITY, 3).addEnchant(Enchantment.DAMAGE_ALL, 1).craftToItemStack());
		kitItems.put("2", new EasyEnchant(Material.BOW, 1, "§bPirate Bow").addEnchant(Enchantment.ARROW_DAMAGE, 3).addEnchant(Enchantment.DURABILITY, 3).craftToItemStack());
		kitItems.put("3", new EasyEnchant(Material.GOLDEN_APPLE, 3, "§bPirate Golden Apple ").craftToItemStack());
		kitItems.put("4", new EasyEnchant(Material.POTION, 2, "§bPirate Speed Potion").craftToItemStack());
		kitItems.put("5", new BetterItem(Material.WOOD, 64, "§bPirate Blocks", (short)3).grab());
		kitItems.put("6", new EasyEnchant(Material.COOKED_BEEF, 64, "§bPirate Steak").craftToItemStack());
		kitItems.put("7", new EasyEnchant(Material.COMPASS, 1, "§bPlayer Tracker").craftToItemStack());
		kitItems.put("8", new EasyEnchant(Material.ARROW, 64, "§bPirate Arrow").craftToItemStack());
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
			if(Kits.getClass(p) == Kits.PIRATE){
				Server.getInstance().addEnergy(p, 12);
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
			if(Kits.getClass(((Player)arrow.getShooter())) == Kits.PIRATE){
				Server.getInstance().addEnergy(((Player)arrow.getShooter()), 12);
			}
		}
		}
	}
}
