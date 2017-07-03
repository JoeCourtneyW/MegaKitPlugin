package com.me.sly.kits.main.listeners;

import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.me.sly.kits.main.Corrupted;

public class TeleportFix implements Listener {
	
	
        // Attempt at fixing groundhogging
	
	public TeleportFix(Plugin plugin, Server server) {
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if(event.getCause() != TeleportCause.ENDER_PEARL && event.getCause() != TeleportCause.UNKNOWN){
		final Player player = event.getPlayer();
		if(event.getPlayer().isBlocking()){
			if(event.getPlayer().getItemInHand() != null){
			event.getPlayer().setItemInHand(event.getPlayer().getItemInHand());
			}
		}
		if(event.getPlayer().isSneaking()){
			event.getPlayer().setSneaking(false);
		}
		new BukkitRunnable(){
			public void run(){
				player.playSound(player.getLocation(), Sound.WITHER_SHOOT, 1, (float)0.5);				
			}
		}.runTaskLater(Corrupted.getInstance(), 4);
		}
		// Fix the visibility issue one tick later
	}
	
}
