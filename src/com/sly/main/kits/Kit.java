package com.sly.main.kits;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.sly.main.Server;
import com.sly.main.player.PlayerModel;
import com.sly.main.resources.MyMetadata;

public abstract class Kit {
	
	public abstract void fillKit();
	public abstract HashMap<String, ItemStack> getKitItems();
	public abstract int getEnergyOnMeleeHit();
	public abstract int getEnergyOnBowHit();
	public abstract int getEnergyOverTime();
	public abstract void mainAbility(PlayerModel p);
	
	public static void registerKits(){
		for(Kits kit : Kits.getKits()){
			Bukkit.getPluginManager().registerEvents((Listener)kit.getListenerClass(), Server.getInstance());
			kit.getListenerClass().fillKit();
		}
	}
	public void newMinion(Location l, Player owner, EntityType type){
		final Creature e = (Creature) l.getWorld().spawnEntity(l, type);
		e.setCustomName("§b" + owner.getName());
		e.setCustomNameVisible(true);
		e.setMetadata("owner", new MyMetadata(Server.getInstance(), owner.getName()));
		e.setTarget(null);
		new BukkitRunnable(){
			public void run(){
				if(!e.isDead()){
				e.remove();
				}
			}
		}.runTaskLater(Server.getInstance(), 10*20);
	}
	
}
