package com.sly.main.kits.listeners;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.Potion;
import org.bukkit.potion.Potion.Tier;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.sly.main.Server;
import com.sly.main.gametype.Squad;
import com.sly.main.kits.Kit;
import com.sly.main.kits.Kits;
import com.sly.main.resources.AttributeAPI;
import com.sly.main.resources.BetterItem;
import com.sly.main.resources.EasyEnchant;
import com.sly.main.resources.ParticleEffects;

@SuppressWarnings("deprecation")
public class Golem extends Kit implements Listener {

	
	/**
	 * Iron Punch
	 */
	@EventHandler
	public void IronPunch(PlayerInteractEvent event){
		if(Kits.getClass(event.getPlayer()) == Kits.GOLEM){
			if(Server.getInstance().isActivateAbilityEvent(event)){
				Player p = event.getPlayer();
				Location l = p.getLocation().clone();
				FallingBlock b = p.getWorld().spawnFallingBlock(l.clone().add(3, 7, 0), Material.IRON_BLOCK, (byte) 0);
				b.setVelocity(new Vector(0, -1.3, 0));
				b.setDropItem(false);
				FallingBlock b1 = p.getWorld().spawnFallingBlock(l.clone().add(2, 7, 2), Material.IRON_BLOCK, (byte) 0);
				b1.setVelocity(new Vector(0, -1.3, 0));
				b1.setDropItem(false);
				FallingBlock b2 = p.getWorld().spawnFallingBlock(l.clone().add(2, 7, -2), Material.IRON_BLOCK, (byte) 0);
				b2.setVelocity(new Vector(0, -1.3, 0));
				b2.setDropItem(false);
				FallingBlock b3 = p.getWorld().spawnFallingBlock(l.clone().add(-3, 7, 0), Material.IRON_BLOCK, (byte) 0);
				b3.setVelocity(new Vector(0, -1.3, 0));
				b3.setDropItem(false);
				FallingBlock b4 = p.getWorld().spawnFallingBlock(l.clone().add(-2, 7, 2), Material.IRON_BLOCK, (byte) 0);
				b4.setVelocity(new Vector(0, -1.3, 0));
				b4.setDropItem(false);
				FallingBlock b5 = p.getWorld().spawnFallingBlock(l.clone().add(-2, 7, -2), Material.IRON_BLOCK, (byte) 0);
				b5.setVelocity(new Vector(0, -1.3, 0));
				b5.setDropItem(false);
        		for(Entity e : p.getNearbyEntities(4, 2.5, 4)){
        			if(e instanceof Player){
        				if(((Player) e).getName().equalsIgnoreCase(p.getName())) continue;
						if(!Squad.sameTeam(p, (Player) e)){
        				Server.getInstance().damagePure(((Player) e), 5.0, p);
        				Location playerLoc = e.getLocation();
        				Location vecloc = l.clone().subtract(playerLoc);
        				if(Math.abs(vecloc.getX()) < 0.7 && Math.abs(vecloc.getZ()) < 0.7) e.setVelocity(new Vector());
        				else{
        				Vector v = new Vector(vecloc.getX()/2.7, 0, vecloc.getZ()/2.7);
        				e.setVelocity(v);
        				}
        				}
        			}
        		}
			//Take away levels
			p.setLevel(0);
		}
		}
	}
	@EventHandler
	public void onEntityChangeBlockEvent(EntityChangeBlockEvent event){
		if(event.getEntity() instanceof FallingBlock && ((FallingBlock)event.getEntity()).getMaterial() == Material.IRON_BLOCK){
			event.setCancelled(true);
    		for(Entity e : event.getEntity().getNearbyEntities(4, 2.5, 4)){
    			if(e instanceof Player){
    				((Player) e).playSound(e.getLocation(), Sound.ZOMBIE_METAL, 1, 0.5F);
    			}
    		}
			try {
				ParticleEffects.LARGE_EXPLODE.sendToAllPlayers(event.getBlock().getLocation().add(0, 1, 0), 0.13F, 5);
			} catch (Exception e) {
			}
		}
	}
	/**
	 * Iron Heart
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void IronHeart(final PlayerDeathEvent event){
		if(event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player){
			Player p = (Player) event.getEntity().getKiller();
			if(Kits.getClass(p) == Kits.GOLEM){
				if(Server.getCooldowns().containsKey(event.getEntity().getName()) && Server.getCooldowns().get(event.getEntity().getName()).equalsIgnoreCase("GOLEM")){
					return;
				}
				Server.getCooldowns().put(event.getEntity().getName(), "GOLEM");
				new BukkitRunnable(){
					public void run(){
						Server.getCooldowns().remove(event.getEntity().getName());
					}
				}.runTaskLater(Server.getInstance(), 30*20);
				Server.getInstance().addNoReductionEffect(p, new PotionEffect(PotionEffectType.ABSORPTION, 12*20, 1));
		}
	}
	}
	/**
	 * Iron Constitution
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void IronConstitution(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		if(event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.ARROW){
			Player p = (Player) event.getEntity();
			if(Kits.getClass(p) == Kits.GOLEM){
				Server.getInstance().addNoReductionEffect(p, new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 10*20, 0));
			}
		}
	}
	/**
	 * Kit Items
	 */
	public static HashMap<String, ItemStack> kitItems = new HashMap<String, ItemStack>();
	@Override
	public void fillKit(){
	    ItemStack slowPot = new ItemStack(Material.POTION, 1, (short)8266);
	    AttributeAPI.removeAttributesPotion(slowPot);
		kitItems.put("Helmet", new EasyEnchant(Material.IRON_HELMET, 1, "§bGolem Helmet").craftToItemStack());
		kitItems.put("Chestplate", new EasyEnchant(Material.DIAMOND_CHESTPLATE, 1, "§bGolem Chestplate").addEnchant(Enchantment.DURABILITY, 3).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).craftToItemStack());
		kitItems.put("Leggings", new EasyEnchant(Material.IRON_LEGGINGS, 1, "§bGolem Leggings").craftToItemStack());
		kitItems.put("Boots", new EasyEnchant(Material.DIAMOND_BOOTS, 1, "§bGolem Boots").addEnchant(Enchantment.DURABILITY, 3).addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1).craftToItemStack());
		kitItems.put("1", new EasyEnchant(Material.IRON_SWORD, 1, "§bGolem Sword").addEnchant(Enchantment.DURABILITY, 3).craftToItemStack());
		kitItems.put("2", new EasyEnchant(Material.COOKED_BEEF, 64, "§bGolem Steak").craftToItemStack());
		kitItems.put("3", new BetterItem(Material.POTION, 2, "§bGolem Regen Potion (9�?�)").grab());
		kitItems.put("4", getSlownessPot());
		kitItems.put("5", new BetterItem(Material.WOOD, 64, "§bGolem Blocks", (short)3).grab());
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
		p.setType(PotionType.SLOWNESS);
		p.setTier(Tier.ONE);
		p.setSplash(true);
		ItemStack i =  p.toItemStack(1);
		PotionMeta im = (PotionMeta) i.getItemMeta();
		im.setDisplayName("§bGolem Slowness Potion");
		im.addCustomEffect(new PotionEffect(PotionEffectType.SLOW,  10*20, 2), true);
		i.setItemMeta(im);
		return i;
	}
	/**
	 * Energy
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void EnergyOnHit(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		if(event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER){
			Player p = (Player) event.getDamager();
			if(Kits.getClass(p) == Kits.GOLEM){
				Server.getInstance().addEnergy(p, 9);
			}
		}
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void EnergyOnBowHit(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		if(event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.ARROW){
			Arrow arrow = (Arrow)event.getDamager();
			if(!(event.getEntity() == arrow.getShooter())){
			if(Kits.getClass(((Player)arrow.getShooter())) == Kits.GOLEM){
				Server.getInstance().addEnergy(((Player)arrow.getShooter()), 9);
			}
		}
		}
	}
}
