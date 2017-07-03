package com.me.sly.kits.main.classes;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Creature;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.me.sly.kits.main.Corrupted;
import com.me.sly.kits.main.resources.MyMetadata;

public class Class { //Work on making this an interface because interfaces are cool

	public Class(){
		
	}
	
	public void fillKit(){
	}
	public HashMap<String, ItemStack> getKitItems(){
		return null;
	}
	public static void registerClassListeners(){
		for(Classes r : Classes.values()){
			if(r.equals(Classes.NONE)) continue;
			Bukkit.getPluginManager().registerEvents((Listener)r.getListenerClass(), Corrupted.getInstance());
		}
	}
	public void newMinion(Location l, Player owner, EntityType type){
		final Creature e = (Creature) l.getWorld().spawnEntity(l, type);
		e.setCustomName("§b" + owner.getName());
		e.setCustomNameVisible(true);
		e.setMetadata("owner", new MyMetadata(Corrupted.getInstance(), owner.getName()));
		e.setTarget(null);
		new BukkitRunnable(){
			public void run(){
				if(!e.isDead()){
				e.remove();
				}
			}
		}.runTaskLater(Corrupted.getInstance(), 10*20);
	}
	
}
