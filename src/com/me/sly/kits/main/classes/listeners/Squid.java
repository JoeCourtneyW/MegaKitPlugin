package com.me.sly.kits.main.classes.listeners;
import java.util.HashMap;

import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.me.sly.kits.main.Corrupted;
import com.me.sly.kits.main.classes.Class;
import com.me.sly.kits.main.classes.Classes;
import com.me.sly.kits.main.resources.BetterItem;
import com.me.sly.kits.main.resources.EasyEnchant;

import de.slikey.effectlib.effect.WarpEntityEffect;
import de.slikey.effectlib.util.ParticleEffect;

public class Squid extends Class implements Listener{
	public static HashMap<String, Long> cooldowns = new HashMap<String, Long>();
	/**
	 * Squid Splash
	 */
	@SuppressWarnings("deprecation")
	@EventHandler
	public void SquidSplash(PlayerInteractEvent event){
		if(Classes.getClass(event.getPlayer()) == Classes.SQUID){
			if(Corrupted.getInstance().isActivateAbilityEvent(event)){
				final Player p = event.getPlayer();
				int damage = 0;
				for(Entity e : p.getNearbyEntities(6, 2.5, 6)){
					if(e instanceof Player){
						((Player) e).playSound(p.getLocation(), Sound.SPLASH, 1, 0.65F);
					}
				}	
				p.playSound(p.getLocation(), Sound.SPLASH, 1, 0.65F);
	            final FallingBlock ef = p.getWorld().spawnFallingBlock(p.getLocation().clone().subtract(0, 0.2, 0), Material.AIR, (byte) 0);
				WarpEntityEffect we = new WarpEntityEffect(Corrupted.getEffectManager(), ef);
				we.radius = 2;
				we.particle = ParticleEffect.SMOKE;
				we.rings = 1;
				we.period = 2;
				we.iterations = 1;
				we.start();
				for(Entity e : p.getNearbyEntities(2.5, 2.5, 2.5)){
					if(e instanceof Player){
						Player pl = (Player) e;
						damage += 3;
						Corrupted.getInstance().damagePure(pl, 3.0, p);
					}
				}
				if(damage > 0){
					if(damage > 8) damage = 8;
					CraftPlayer cp = (CraftPlayer) p;
					if(cp.getHealth() + damage > cp.getMaxHealth()) cp.setHealth(cp.getMaxHealth());
					else{
						cp.setHealth(cp.getHealth() + damage);
					}
				}
			//Take away levels
			event.getPlayer().setLevel(0);
		}
		}
	}
	/**
	 * Rejuvenate
	 */
	@EventHandler
	public void Rejuvenate(EntityDamageEvent event){
		if(event.getEntity() instanceof Player){
			Player p = (Player) event.getEntity();
		if(Classes.getClass((Player) event.getEntity()) == Classes.SQUID){
			if(((CraftPlayer)p).getHealth() < 10){
			if(!(cooldowns.containsKey(p.getName()))){
				((CraftPlayer)p).setHealth(((CraftPlayer)p).getHealth() + 9);
			cooldowns.put(p.getName(), System.currentTimeMillis());
			}else if(System.currentTimeMillis() - cooldowns.get(p.getName()) > 60000){
				//Able to use ability again
				((CraftPlayer)p).setHealth(((CraftPlayer)p).getHealth() + 9);
				cooldowns.put(p.getName(), System.currentTimeMillis());
				p.playEffect(EntityEffect.VILLAGER_HAPPY);
			}else if(System.currentTimeMillis() - cooldowns.get(p.getName()) < 60000){
				//Unable to use ability again
			}
		}
		}
		}
	}
	/**
	 * Inner Ink
	 */
	@EventHandler
	public void InnerInk(PlayerItemConsumeEvent event){
		if(Classes.getClass(event.getPlayer()) == Classes.SQUID){
		if(event.getItem().getType() == Material.POTION){
			for(Entity e : event.getPlayer().getNearbyEntities(2.5, 2.5, 2.5)){
				if(e instanceof Player){
					Player p = (Player) e;
					Corrupted.getInstance().addNoReductionEffect(p, new PotionEffect(PotionEffectType.BLINDNESS, 60, 2));
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
		kitItems.put("Helmet", new EasyEnchant(Material.IRON_HELMET, 1, "§bSquid Helmet").craftToItemStack());
		kitItems.put("Chestplate", new EasyEnchant(Material.IRON_CHESTPLATE, 1, "§bSquid Chestplate").craftToItemStack());
		kitItems.put("Leggings", new EasyEnchant(Material.IRON_LEGGINGS, 1, "§bSquid Leggings").craftToItemStack());
		kitItems.put("Boots", new EasyEnchant(Material.DIAMOND_BOOTS, 1, "§bSquid Boots").addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).craftToItemStack());
		kitItems.put("1", new EasyEnchant(Material.IRON_SWORD, 1, "§bSquid Sword").addEnchant(Enchantment.DURABILITY, 3).craftToItemStack());
		kitItems.put("2", new EasyEnchant(Material.COOKED_FISH, 64, "§bSquid Salmom").craftToItemStack());
		kitItems.put("3", new EasyEnchant(Material.POTION, 3, "§bSquid Health Potion (6❤)").craftToItemStack());
		kitItems.put("4", new EasyEnchant(Material.POTION, 1, "§bSquid Speed Potion").craftToItemStack());
		kitItems.put("5", new BetterItem(Material.WOOD, 64, "§bSquid Blocks", (short)3).grab());
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
			if(Classes.getClass(p) == Classes.SQUID){
				Corrupted.getInstance().addEnergy(p, 12);
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
			if(Classes.getClass(((Player)arrow.getShooter())) == Classes.SQUID){
				Corrupted.getInstance().addEnergy(((Player)arrow.getShooter()), 12);
			}
		}
		}
	}
}
