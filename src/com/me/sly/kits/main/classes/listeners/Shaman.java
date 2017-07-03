package com.me.sly.kits.main.classes.listeners;

import java.util.HashMap;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import com.me.sly.kits.main.Corrupted;
import com.me.sly.kits.main.classes.Class;
import com.me.sly.kits.main.classes.Classes;
import com.me.sly.kits.main.gametype.Team;
import com.me.sly.kits.main.resources.BetterItem;
import com.me.sly.kits.main.resources.EasyEnchant;
import com.me.sly.kits.main.resources.MyMetadata;

import de.slikey.effectlib.effect.ConeLocationEffect;
import de.slikey.effectlib.util.ParticleEffect;

public class Shaman extends Class implements Listener{
	
	/**
	 * Tornado
	 */
    @SuppressWarnings("deprecation")
	@EventHandler
    public void Tornado(final PlayerInteractEvent event) {
    	if(Classes.getClass(event.getPlayer()).equals(Classes.SHAMAN)){
    	if(Corrupted.getInstance().isActivateAbilityEvent(event)){
        // Create the Effect and attach it to the Player
    		final Location lr = event.getPlayer().getLocation();
    		final Location l = lr.clone();
    		l.subtract(0, 1, 0);
    		l.setPitch(-90);
        ConeLocationEffect vortexEffect= new ConeLocationEffect(Corrupted.getEffectManager(), l);
        vortexEffect.radiusGrow = 0.019F;
        vortexEffect.lenghtGrow = 0.04F;
        vortexEffect.particle = ParticleEffect.CLOUD;
        vortexEffect.iterations = 5 * 22;
        Corrupted.getEffectManager().start(vortexEffect);
        Location vloc = l.clone();
        vloc.add(0, 1.5, 0);
        final FallingBlock e = vloc.getWorld().spawnFallingBlock(vloc, Material.AIR, (byte) 0);
        final BukkitTask  r = new BukkitRunnable(){
        	public void run(){
        		for(Entity p : e.getNearbyEntities(4, 2.5, 4)){
        			if(p instanceof Player){
        				if(((Player) p).getName().equalsIgnoreCase(event.getPlayer().getName())) continue;
        				if(((Player) p).getGameMode().equals(GameMode.CREATIVE)) continue;
        				if(event.getPlayer().isDead()) break;
        				if(((Player) p).isDead()) continue;
        				if(!event.getPlayer().hasMetadata("Class")) break;
        				if(!((Player) p).hasMetadata("Class")) continue;
						if(!Team.sameTeam(event.getPlayer(), (Player) p)){
        				Corrupted.getInstance().damageImpure((Player) p, 3.5D, event.getPlayer());
        				Location playerLoc = p.getLocation();
        				Location vecloc = l.clone().subtract(playerLoc);
        				if(Math.abs(vecloc.getX()) < 0.7 && Math.abs(vecloc.getZ()) < 0.7) p.setVelocity(new Vector());
        				else{
        				Vector v = new Vector(vecloc.getX()/10, 0, vecloc.getZ()/10);
        				p.setVelocity(v);
        				}
						}
        				for(int i = 0; i < 5; i++){
        					((Player) p).playSound(l.clone(), Sound.ENDERDRAGON_WINGS, 2, 0.5F);
        					event.getPlayer().playSound(l.clone(), Sound.ENDERDRAGON_WINGS, 2, 0.5F);
        				}
        			}
        		}
        	}
        }.runTaskTimer(Corrupted.getInstance(), 22, 22);
        e.remove();
        new BukkitRunnable(){
        	public void run(){
        		r.cancel();
        	}
        }.runTaskLater(Corrupted.getInstance(), 22*5);
		//Take away levels
		event.getPlayer().setLevel(0);
    	}
    }
    }
    
    /**
     * Heroism
     */
	@EventHandler(priority = EventPriority.HIGH)
	public void Heroism(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		if(event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER){
			Player p = (Player) event.getEntity();
			if(Classes.getClass(p) == Classes.SHAMAN){
			if(Corrupted.getInstance().rand(1, 100) <= 17){
				Corrupted.getInstance().addNoReductionEffect((Player) event.getDamager(), new PotionEffect(PotionEffectType.WEAKNESS, 6*20, 0));
				Corrupted.getInstance().addNoReductionEffect(p, new PotionEffect(PotionEffectType.SPEED, 6*20, 1));
			}
		}
		}
	}
	/**
	 * Wolf Pack
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void WolfPack(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		if(event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER){
			Player p = (Player) event.getEntity();
			if(Classes.getClass(p) == Classes.SHAMAN){
			if(Corrupted.getInstance().rand(1, 100) <= 10){
				final Wolf w = (Wolf) p.getWorld().spawnEntity(p.getLocation(), EntityType.WOLF);
				w.setMetadata("owner", new MyMetadata(Corrupted.getInstance(), p.getName()));
				w.setAngry(true);
				w.setTarget((LivingEntity) event.getDamager());
				new BukkitRunnable(){
					public void run(){
						if(!w.isDead()){
						w.remove();
						}
					}
				}.runTaskLater(Corrupted.getInstance(), 10*20);
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
		kitItems.put("Helmet", new EasyEnchant(Material.IRON_HELMET, 1, "§bShaman Helmet").craftToItemStack());
		kitItems.put("Chestplate", new EasyEnchant(Material.IRON_CHESTPLATE, 1, "§bShaman Chestplate").craftToItemStack());
		kitItems.put("Leggings", new EasyEnchant(Material.IRON_LEGGINGS, 1, "§bShaman Leggings").craftToItemStack());
		kitItems.put("Boots", new EasyEnchant(Material.DIAMOND_BOOTS, 1, "§bShaman Boots").addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 2).addEnchant(Enchantment.PROTECTION_FALL, 2).craftToItemStack());
		kitItems.put("1", new EasyEnchant(Material.DIAMOND_SWORD, 1, "§bShaman Sword").addEnchant(Enchantment.DURABILITY, 3).craftToItemStack());
		kitItems.put("2", new EasyEnchant(Material.COOKED_BEEF, 64, "§bShaman Steak").craftToItemStack());
		kitItems.put("3", new EasyEnchant(Material.POTION, 2, "§bShaman Health Potion (8❤)").craftToItemStack());
		kitItems.put("4", new EasyEnchant(Material.POTION, 2, "§bShaman Speed Potion").craftToItemStack());
		kitItems.put("5", new BetterItem(Material.WOOD, 64, "§bShaman Blocks", (short)3).grab());
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
			if(Classes.getClass(p) == Classes.SHAMAN){
				Corrupted.getInstance().addEnergy(p, 9);
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
			if(Classes.getClass(((Player)arrow.getShooter())) == Classes.SHAMAN){
				Corrupted.getInstance().addEnergy(((Player)arrow.getShooter()), 9);
			}
		}
		}
	}
}
