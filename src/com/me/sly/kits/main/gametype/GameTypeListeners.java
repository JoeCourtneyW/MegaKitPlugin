package com.me.sly.kits.main.gametype;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.me.sly.kits.main.Chat;
import com.me.sly.kits.main.Corrupted;
import com.me.sly.kits.main.classes.Classes;
import com.me.sly.kits.main.resources.BetterItem;
import com.me.sly.kits.main.resources.IconMenu;

public class GameTypeListeners implements Listener{
  
	@EventHandler(priority = EventPriority.HIGH)
	public void RatingOnKill1v1(PlayerDeathEvent event){
		if(Corrupted.getGames().containsKey(event.getEntity().getName())){
			if(!event.getEntity().getWorld().getName().contains("1v1")) return;
			final Game g = Corrupted.getGames().get(event.getEntity().getName());
			final Player p = g.getTeam1().contains(event.getEntity().getName()) ? Bukkit.getPlayer(g.getTeam2().get(0)) : Bukkit.getPlayer(g.getTeam1().get(0));
			final Player death = g.getTeam1().contains(event.getEntity().getName()) ? Bukkit.getPlayer(g.getTeam1().get(0)) : Bukkit.getPlayer(g.getTeam2().get(0));
			final Location l = new Location(Bukkit.getWorld("Nube"), -549.5, 101, -552.5, 206.62F, 1.82F);
	        double killRating = 1.0 * (int) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("1v1Rating");
			double deathRating = 1.0 * (int) Corrupted.getDatabaseCache().get(death.getUniqueId().toString()).get("1v1Rating");
	        double killExpectedScore = (killRating) / (killRating + deathRating);
	        double deathExpectedScore = (deathRating) / (killRating + deathRating);
	        double killActualScore = 1.0;
	        double deathActualScore = 0.0;
	        double killScore = killActualScore-killExpectedScore;
	        double deathScore = deathActualScore-deathExpectedScore;
	        double newKillRating = (killRating + (32*(killScore) < 0 ? 0 : 32*(killScore)));
	        double newDeathRating = (deathRating + (32*(deathScore) > 32 ? 32 : 32*(deathScore)));
	        Chat.sendPlayerPrefixedMessage(p, "§e+" + Corrupted.getInstance().roundTwoDecimals(newKillRating-killRating) + " 1v1 rating §cfor beating §b" + death.getName() + "§c.");
	        Chat.sendPlayerPrefixedMessage(death, "§e-" + Corrupted.getInstance().roundTwoDecimals(Math.abs(newDeathRating-deathRating)) +  " 1v1 rating §cfor losing to §b" + p.getName() + "§c.");
	        Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).remove("1v1Rating");
	        Corrupted.getDatabaseCache().get(death.getUniqueId().toString()).remove("1v1Rating");
	        Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("1v1Rating", Math.toIntExact(Math.round(newKillRating)));
	        Corrupted.getDatabaseCache().get(death.getUniqueId().toString()).put("1v1Rating", Math.toIntExact(Math.round(newDeathRating)));
		        Chat.sendPlayerPrefixedMessage(p, "§e+" + Corrupted.getInstance().getMultipliedCoins(p, 30) + " coins §cfor beating §b" + death.getName() + "§c.");
		        Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", ((int) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins"))+Corrupted.getInstance().getMultipliedCoins(p, 30));				
		        Chat.sendPlayerPrefixedMessage(death, "§e+" + Corrupted.getInstance().getMultipliedCoins(death, 15) + " coins §cfor losing to §b" + p.getName() + "§c.");
		        Corrupted.getDatabaseCache().get(death.getUniqueId().toString()).put("Coins", ((int) Corrupted.getDatabaseCache().get(death.getUniqueId().toString()).get("Coins"))+Corrupted.getInstance().getMultipliedCoins(death, 15));		 
		        Corrupted.getInstance().updateScoreboard(p);
	    	        Corrupted.getInstance().updateScoreboard(death);
	    	       Corrupted.getInstance().updateDatabase(p);
	    	       Corrupted.getInstance().updateDatabase(death);
	    	       p.setNoDamageTicks(100000);
	    	       p.setAllowFlight(true);
	    	       p.setFlying(true);
	    	       Corrupted.getGames().remove(g.getTeam1().get(0));
	    	       Corrupted.getGames().remove(g.getTeam2().get(0));
	    	       final Arena ag = g.getArena();
	    	       g.finish();
	    	       new BukkitRunnable(){
					public void run(){
	    	   	        p.teleport(l);
	    	   	        p.setHealth(40.0);
	    	   	     p.setAllowFlight(false);
		    	       p.setFlying(false);
	    	   	     p.setNoDamageTicks(0);
	    	   	     p.setFlying(false);
	    	    	   ag.setIsInUse(false);
	    	    	   Corrupted.getInstance().setDefaultInventory(p);
	    	    	   }
	    	       }.runTaskLater(Corrupted.getInstance(), 60);
		}
	}
	@EventHandler(priority = EventPriority.HIGH)
	public void RatingOnKill2v2(PlayerDeathEvent event){
		if(Corrupted.getGames().containsKey(event.getEntity().getName())){
			if(!event.getEntity().getWorld().getName().contains("2v2")) return;
			final Game g = Corrupted.getGames().get(event.getEntity().getName());
			final Player p;
			final Player p2;
			final Player death = event.getEntity();
			final Player death2;
			if(g.isOver()) return;
			if(g.getTeam1().contains(event.getEntity().getName())){
					if(g.getTeam1().get(0).equalsIgnoreCase(event.getEntity().getName())) death2 = Bukkit.getPlayer(g.getTeam1().get(1));
					else death2 = Bukkit.getPlayer(g.getTeam1().get(0));	
					p = Bukkit.getPlayer(g.getTeam2().get(0));
					p2 = Bukkit.getPlayer(g.getTeam2().get(1));
			}else{
				if(g.getTeam2().get(0).equalsIgnoreCase(event.getEntity().getName())) death2 = Bukkit.getPlayer(g.getTeam2().get(1));
				else death2 = Bukkit.getPlayer(g.getTeam2().get(0));	
				p = Bukkit.getPlayer(g.getTeam1().get(0));
				p2 = Bukkit.getPlayer(g.getTeam1().get(1));			
			}
			if(!(Corrupted.getSpectators().get(g).contains(death2.getName())) || Corrupted.getSpectators().get(g).isEmpty()){
				Corrupted.getSpectators().get(g).add(death.getName());
				death.setHealth(40.0);
				death.teleport(g.getArena().getSpawn3());
				return;
			}
			Corrupted.getSpectators().remove(death2.getName());
			death.setHealth(40.0);
			death.teleport(g.getArena().getSpawn3());
			final Location l = new Location(Bukkit.getWorld("Nube"), -549.5, 101, -552.5, 206.62F, 1.82F);
			double pRating = 1.0 * (int) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("2v2Rating");
			double p2Rating = 1.0 * (int) Corrupted.getDatabaseCache().get(p2.getUniqueId().toString()).get("2v2Rating");
			double deathRating = 1.0 * (int) Corrupted.getDatabaseCache().get(death.getUniqueId().toString()).get("2v2Rating");
			double death2Rating = 1.0 * (int) Corrupted.getDatabaseCache().get(death2.getUniqueId().toString()).get("2v2Rating");
	        double deathAVGRating = (deathRating + death2Rating)/2.0;
	        double killAVGRating = (pRating + p2Rating)/2.0;
	        double pExpectedScore = (pRating) / (pRating + deathAVGRating);
	        double p2ExpectedScore = (p2Rating) / (p2Rating + deathAVGRating);
	        double deathExpectedScore = (deathRating) / (deathRating + killAVGRating);
	        double death2ExpectedScore = (death2Rating) / (death2Rating + killAVGRating);
	        double killActualScore = 1.0;
	        double deathActualScore = 0.0;
	        double pScore = killActualScore-pExpectedScore;
	        double p2Score = killActualScore-p2ExpectedScore;
	        double deathScore = deathActualScore-deathExpectedScore;
	        double death2Score = deathActualScore-death2ExpectedScore;
	        double newpRating = (pRating + (32*(pScore) < 0 ? 0 : 32*(pScore)));
	        double newp2Rating = (p2Rating + (32*(p2Score) < 0 ? 0 : 32*(p2Score)));
	        double newdeathRating = (deathRating +  (32*(deathScore) > 32 ? 32 : 32*(deathScore)));
	        double newdeath2Rating = (death2Rating +  (32*(death2Score) > 32 ? 32 : 32*(death2Score)));
	        Chat.sendPlayerPrefixedMessage(p, "§e+" + Corrupted.getInstance().roundTwoDecimals(newpRating-pRating) + " 2v2 rating §cfor beating §b" + death.getName() + " §cand §b" + death2.getName() + "§c.");
	        Chat.sendPlayerPrefixedMessage(p2, "§e+" + Corrupted.getInstance().roundTwoDecimals(newp2Rating-p2Rating) + " 2v2 rating §cfor beating §b" + death.getName() + " §cand §b" + death2.getName() + "§c.");
	        Chat.sendPlayerPrefixedMessage(death, "§e-" + Corrupted.getInstance().roundTwoDecimals(Math.abs(newdeathRating-deathRating)) +  " 2v2 rating §cfor losing to §b" + p.getName() + " §cand §b" + p2.getName() + "§c.");
	        Chat.sendPlayerPrefixedMessage(death2, "§e-" + Corrupted.getInstance().roundTwoDecimals(Math.abs(newdeath2Rating-death2Rating)) +  " 2v2 rating §cfor losing to §b" + p.getName() + " §cand §b" + p2.getName() + "§c.");
	        Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("2v2Rating", Math.toIntExact(Math.round(newpRating)));
	        Corrupted.getDatabaseCache().get(p2.getUniqueId().toString()).put("2v2Rating", Math.toIntExact(Math.round(newp2Rating)));
	        Corrupted.getDatabaseCache().get(death.getUniqueId().toString()).put("2v2Rating", Math.toIntExact(Math.round(newdeathRating)));
	        Corrupted.getDatabaseCache().get(death2.getUniqueId().toString()).put("2v2Rating", Math.toIntExact(Math.round(newdeath2Rating)));
		        Chat.sendPlayerPrefixedMessage(p, "§e+" + Corrupted.getInstance().getMultipliedCoins(p, 40) + " coins §cfor beating §b" + death.getName() + " §cand§b " + death2.getName() + "§c.");
		        Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", ((int) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins"))+Corrupted.getInstance().getMultipliedCoins(p, 40));	
		        Chat.sendPlayerPrefixedMessage(p2, "§e+" + Corrupted.getInstance().getMultipliedCoins(p2, 40) + " coins §cfor beating §b" + death.getName() + " §cand§b " + death2.getName() + "§c.");
		        Corrupted.getDatabaseCache().get(p2.getUniqueId().toString()).put("Coins", ((int) Corrupted.getDatabaseCache().get(p2.getUniqueId().toString()).get("Coins"))+Corrupted.getInstance().getMultipliedCoins(p2, 40));	
		        Chat.sendPlayerPrefixedMessage(death, "§e+" + Corrupted.getInstance().getMultipliedCoins(death, 20) + " coins §cfor losing to §b" + p.getName() + " §cand§b " + p2.getName() + "§c.");
		        Corrupted.getDatabaseCache().get(death.getUniqueId().toString()).put("Coins", ((int) Corrupted.getDatabaseCache().get(death.getUniqueId().toString()).get("Coins"))+Corrupted.getInstance().getMultipliedCoins(death, 20));	
		        Chat.sendPlayerPrefixedMessage(death2, "§e+" + Corrupted.getInstance().getMultipliedCoins(death2, 20) + " coins §cfor losing to §b" + p.getName() + " §cand§b " + p2.getName() + "§c.");
		        Corrupted.getDatabaseCache().get(death2.getUniqueId().toString()).put("Coins", ((int) Corrupted.getDatabaseCache().get(death2.getUniqueId().toString()).get("Coins"))+Corrupted.getInstance().getMultipliedCoins(death2, 20));	
		        Corrupted.getInstance().updateDatabase(p);
 	       Corrupted.getInstance().updateDatabase(death);
	       Corrupted.getInstance().updateDatabase(p2);
	       Corrupted.getInstance().updateDatabase(death2);
	       Corrupted.getInstance().updateScoreboard(p);
	       Corrupted.getInstance().updateScoreboard(death);
	       Corrupted.getInstance().updateScoreboard(p2);
	       Corrupted.getInstance().updateScoreboard(death2);
	    	       p.setNoDamageTicks(100000);
	    	       p2.setNoDamageTicks(100000);
	    	       Corrupted.getGames().remove(g.getTeam1().get(0));
	    	       Corrupted.getGames().remove(g.getTeam2().get(0));
	    	       Corrupted.getGames().remove(g.getTeam1().get(1));
	    	       Corrupted.getGames().remove(g.getTeam2().get(1));
	    	       final Arena ag = g.getArena();
	    	       g.finish();
	    	       new BukkitRunnable(){
					public void run(){
	    	   	        p.teleport(l);
	    	   	        p.setHealth(40.0);
	    	   	     p.setNoDamageTicks(0);
	    	   	        p2.teleport(l);
	    	   	        p2.setHealth(40.0);
	    	   	     p2.setNoDamageTicks(0);
	    	   	        death.teleport(l);
	    	   	        death.setHealth(40.0);
	    	   	     death.setNoDamageTicks(0);
	    	   	        death2.teleport(l);
	    	   	        death2.setHealth(40.0);
	    	   	     death2.setNoDamageTicks(0);
	    	    	   ag.setIsInUse(false);
	    	    	   Corrupted.getInstance().setDefaultInventory(p);
	    	    	   Corrupted.getInstance().setDefaultInventory(p2);
	    	    	   Corrupted.getInstance().setDefaultInventory(death);
	    	    	   Corrupted.getInstance().setDefaultInventory(death2);
	    	    	   }
	    	       }.runTaskLater(Corrupted.getInstance(), 60);
		}
	}
	@SuppressWarnings("deprecation")
	@EventHandler
	public void noDamageTeammates(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Player && event.getDamager() instanceof Player){
			if(Team.sameTeam((Player)event.getEntity(), (Player) event.getDamager())){
				event.setCancelled(true);
				event.setDamage(0.0);
			}
		}
		if(event.getEntity() instanceof Player && event.getDamager() instanceof Projectile){
			if(((Projectile)event.getDamager()).getShooter() instanceof Player){
				if(Team.sameTeam((Player)((Projectile)event.getDamager()).getShooter(), (Player) event.getEntity())){
					event.setCancelled(true);
					event.setDamage(0.0);					
				}
			}
		}
	}
	@EventHandler
	public void clickSignForTeleport(PlayerInteractEvent event){
			if(event.getItem()  != null && event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName() && event.getItem().getItemMeta().getDisplayName().contains("Ranked 1v1 Queue")){
			if(!(Corrupted.getTeams().containsKey(event.getPlayer().getName()))){
				Player p = event.getPlayer();
			openGUI(p, formatItemArray(p));
			}else{
				Chat.sendPlayerPrefixedMessage(event.getPlayer(), "You can not 1v1 queue when you are in a team. Use /team disband");			
			}
		} else if (event.getItem()  != null && event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName() && event.getItem().getItemMeta().getDisplayName().contains("Ranked 2v2 Queue")){
			if(Corrupted.getTeams().containsKey(event.getPlayer().getName()) && Corrupted.getTeams().get(event.getPlayer().getName()).getMembers().size() == 2 && Corrupted.getTeams().get(event.getPlayer().getName()).getOwner().equalsIgnoreCase(event.getPlayer().getName())){
			for(String name : Corrupted.getTeams().get(event.getPlayer().getName()).getMembers()){
				openGUI2v2(Bukkit.getPlayer(name), formatItemArray(Bukkit.getPlayer(name)));	
			}
			} else {
				Chat.sendPlayerPrefixedMessage(event.getPlayer(), "You are not the owner of a team of 2");
			}
		}
	}
	@EventHandler
	public void leaveQueue(PlayerInteractEvent event){
		if(event.getItem() != null && event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName() && event.getItem().getItemMeta().getDisplayName().contains("Leave Queue")){
			Player p = event.getPlayer();
			Corrupted.getQueue().removeFromQueue1v1(p.getName());
			if(Corrupted.getTeams().containsKey(p.getName())){
				for(String sr : Corrupted.getTeams().get(p.getName()).getMembers()){
					Corrupted.getInstance().setDefaultInventory(Bukkit.getPlayer(sr));					
				}
				Corrupted.getQueue().removeFromQueue2v2(Corrupted.getTeams().get(p.getName()).getMembers());
			}

			Corrupted.getInstance().setDefaultInventory(p);
		}
	}
	@EventHandler
	public void onLeave(PlayerQuitEvent event){
		if(Corrupted.getGames().containsKey(event.getPlayer().getName())){
			if(!Corrupted.getGames().get(event.getPlayer().getName()).isOver()){
			final Game g = Corrupted.getGames().get(event.getPlayer().getName());
			if(g.getArena().getGameType() == GameType.TWOvTWO){
			final Player p;
			final Player p2;
			final Player death = event.getPlayer();
			final Player death2;
			if(g.isOver()) return;
			if(g.getTeam1().contains(event.getPlayer().getName())){
					if(g.getTeam1().get(0).equalsIgnoreCase(event.getPlayer().getName())) death2 = Bukkit.getPlayer(g.getTeam1().get(1));
					else death2 = Bukkit.getPlayer(g.getTeam1().get(0));	
					p = Bukkit.getPlayer(g.getTeam2().get(0));
					p2 = Bukkit.getPlayer(g.getTeam2().get(1));
			}else{
				if(g.getTeam2().get(0).equalsIgnoreCase(event.getPlayer().getName())) death2 = Bukkit.getPlayer(g.getTeam2().get(1));
				else death2 = Bukkit.getPlayer(g.getTeam2().get(0));	
				p = Bukkit.getPlayer(g.getTeam1().get(0));
				p2 = Bukkit.getPlayer(g.getTeam1().get(1));			
			}
			Corrupted.getSpectators().remove(death2.getName());
			Corrupted.getSpectators().remove(death.getName());
			final Location l = new Location(Bukkit.getWorld("Nube"), -549.5, 101, -552.5, 206.62F, 1.82F);
			double pRating = 1.0 * (int) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("2v2Rating");
			double p2Rating = 1.0 * (int) Corrupted.getDatabaseCache().get(p2.getUniqueId().toString()).get("2v2Rating");
			double deathRating = 1.0 * (int) Corrupted.getDatabaseCache().get(death.getUniqueId().toString()).get("2v2Rating");
			double death2Rating = 1.0 * (int) Corrupted.getDatabaseCache().get(death2.getUniqueId().toString()).get("2v2Rating");
	        double deathAVGRating = (deathRating + death2Rating)/2.0;
	        double killAVGRating = (pRating + p2Rating)/2.0;
	        double pExpectedScore = (pRating) / (pRating + deathAVGRating);
	        double p2ExpectedScore = (p2Rating) / (p2Rating + deathAVGRating);
	        double deathExpectedScore = (deathRating) / (deathRating + killAVGRating);
	        double death2ExpectedScore = (death2Rating) / (death2Rating + killAVGRating);
	        double killActualScore = 1.0;
	        double deathActualScore = 0.0;
	        double pScore = killActualScore-pExpectedScore;
	        double p2Score = killActualScore-p2ExpectedScore;
	        double deathScore = deathActualScore-deathExpectedScore;
	        double death2Score = deathActualScore-death2ExpectedScore;
	        double newpRating = (pRating + (32*(pScore) < 0 ? 0 : 32*(pScore)));
	        double newp2Rating = (p2Rating + (32*(p2Score) < 0 ? 0 : 32*(p2Score)));
	        double newdeathRating = (deathRating +  (32*(deathScore) > 32 ? 32 : 32*(deathScore)));
	        double newdeath2Rating = (death2Rating +  (32*(death2Score) > 32 ? 32 : 32*(death2Score)));
	        Chat.sendPlayerPrefixedMessage(p, "§e+" + Corrupted.getInstance().roundTwoDecimals(newpRating-pRating) + " 2v2 rating §cfor beating §b" + death.getName() + " §cand §b" + death2.getName() + "§c.");
	        Chat.sendPlayerPrefixedMessage(p2, "§e+" + Corrupted.getInstance().roundTwoDecimals(newp2Rating-p2Rating) + " 2v2 rating §cfor beating §b" + death.getName() + " §cand §b" + death2.getName() + "§c.");
	        Chat.sendPlayerPrefixedMessage(death2, "§e-" + Corrupted.getInstance().roundTwoDecimals(Math.abs(newdeath2Rating-death2Rating)) +  " 2v2 rating §cfor losing to §b" + p.getName() + " §cand §b" + p2.getName() + "§c.");
	        Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("2v2Rating", Math.toIntExact(Math.round(newpRating)));
	        Corrupted.getDatabaseCache().get(p2.getUniqueId().toString()).put("2v2Rating", Math.toIntExact(Math.round(newp2Rating)));
	        Corrupted.getDatabaseCache().get(death.getUniqueId().toString()).put("2v2Rating", Math.toIntExact(Math.round(newdeathRating)));
	        Corrupted.getDatabaseCache().get(death2.getUniqueId().toString()).put("2v2Rating", Math.toIntExact(Math.round(newdeath2Rating)));
		        Chat.sendPlayerPrefixedMessage(p, "§e+" + Corrupted.getInstance().getMultipliedCoins(p, 40) + " coins §cfor beating §b" + death.getName() + " §cand§b " + death2.getName() + "§c.");
		        Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", ((int) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins"))+Corrupted.getInstance().getMultipliedCoins(p, 40));	
		        Chat.sendPlayerPrefixedMessage(p2, "§e+" + Corrupted.getInstance().getMultipliedCoins(p2, 40) + " coins §cfor beating §b" + death.getName() + " §cand§b " + death2.getName() + "§c.");
		        Corrupted.getDatabaseCache().get(p2.getUniqueId().toString()).put("Coins", ((int) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins"))+Corrupted.getInstance().getMultipliedCoins(p2, 40));	
		        Chat.sendPlayerPrefixedMessage(death2, "§e+" + Corrupted.getInstance().getMultipliedCoins(death2, 20) + " coins §cfor losing to §b" + p.getName() + " §cand§b " + p2.getName() + "§c.");
		        Corrupted.getDatabaseCache().get(death2.getUniqueId().toString()).put("Coins", ((int) Corrupted.getDatabaseCache().get(death2.getUniqueId().toString()).get("Coins"))+Corrupted.getInstance().getMultipliedCoins(death2, 20));
		        Corrupted.getInstance().updateDatabase(p);
 	       Corrupted.getInstance().updateDatabase(death);
	       Corrupted.getInstance().updateDatabase(p2);
	       Corrupted.getInstance().updateDatabase(death2);
	       Corrupted.getInstance().updateScoreboard(p);
	       Corrupted.getInstance().updateScoreboard(death);
	       Corrupted.getInstance().updateScoreboard(p2);
	       Corrupted.getInstance().updateScoreboard(death2);
	    	       p.setNoDamageTicks(100000);
	    	       p2.setNoDamageTicks(100000);
	    	       Corrupted.getGames().remove(g.getTeam1().get(0));
	    	       Corrupted.getGames().remove(g.getTeam2().get(0));
	    	       Corrupted.getGames().remove(g.getTeam1().get(1));
	    	       Corrupted.getGames().remove(g.getTeam2().get(1));
	    	       final Arena ag = g.getArena();
	    	       g.finish();
	    	       new BukkitRunnable(){
					public void run(){
	    	   	        p.teleport(l);
	    	   	        p.setHealth(40.0);
	    	   	     p.setNoDamageTicks(0);
	    	   	        p2.teleport(l);
	    	   	        p2.setHealth(40.0);
	    	   	     p2.setNoDamageTicks(0);
	    	   	        death2.teleport(l);
	    	   	        death2.setHealth(40.0);
	    	   	     death2.setNoDamageTicks(0);
	    	    	   ag.setIsInUse(false);
	    	    	   Corrupted.getInstance().setDefaultInventory(p);
	    	    	   Corrupted.getInstance().setDefaultInventory(p2);
	    	    	   Corrupted.getInstance().setDefaultInventory(death2);
	    	    	   }
	    	       }.runTaskLater(Corrupted.getInstance(), 60);
			}else if(g.getArena().getGameType() == GameType.ONEvONE){
				final Player p = g.getTeam1().contains(event.getPlayer().getName()) ? Bukkit.getPlayer(g.getTeam2().get(0)) : Bukkit.getPlayer(g.getTeam1().get(0));
				final Player death = event.getPlayer();
				final Location l = new Location(Bukkit.getWorld("Nube"), -549.5, 101, -552.5, 206.62F, 1.82F);
		        double killRating = 1.0 * (int) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("1v1Rating");
				double deathRating = 1.0 * (int) Corrupted.getDatabaseCache().get(death.getUniqueId().toString()).get("1v1Rating");
		        double killExpectedScore = (killRating) / (killRating + deathRating);
		        double deathExpectedScore = (deathRating) / (killRating + deathRating);
		        double killActualScore = 1.0;
		        double deathActualScore = 0.0;
		        double killScore = killActualScore-killExpectedScore;
		        double deathScore = deathActualScore-deathExpectedScore;
		        double newKillRating = (killRating + (32*(killScore) < 0 ? 0 : 32*(killScore)));
		        double newDeathRating = (deathRating + (32*(deathScore) > 32 ? 32 : 32*(deathScore)));
		        Chat.sendPlayerPrefixedMessage(p, "§e+" + Corrupted.getInstance().roundTwoDecimals(newKillRating-killRating) + " 1v1 rating §cfor beating §b" + death.getName() + "§c.");
		        Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).remove("1v1Rating");
		        Corrupted.getDatabaseCache().get(death.getUniqueId().toString()).remove("1v1Rating");
		        Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("1v1Rating", Math.toIntExact(Math.round(newKillRating)));
		        Corrupted.getDatabaseCache().get(death.getUniqueId().toString()).put("1v1Rating", Math.toIntExact(Math.round(newDeathRating)));
		        Chat.sendPlayerPrefixedMessage(p, "§e+" + Corrupted.getInstance().getMultipliedCoins(p, 30) + " coins §cfor beating §b" + death.getName() + "§c.");
		        Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", ((int) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins"))+Corrupted.getInstance().getMultipliedCoins(p, 30));	
		    	        Corrupted.getInstance().updateScoreboard(p);
		    	        Corrupted.getInstance().updateScoreboard(death);
		    	       Corrupted.getInstance().updateDatabase(p);
		    	       Corrupted.getInstance().updateDatabase(death);
		    	       p.setNoDamageTicks(100000);
		    	       p.setAllowFlight(true);
		    	       p.setFlying(true);
		    	       Corrupted.getGames().remove(g.getTeam1().get(0));
		    	       Corrupted.getGames().remove(g.getTeam2().get(0));
		    	       final Arena ag = g.getArena();
		    	       g.finish();
		    	       new BukkitRunnable(){
						public void run(){
		    	   	        p.teleport(l);
		    	   	        p.setHealth(40.0);
		    	   	     p.setAllowFlight(false);
			    	       p.setFlying(false);
		    	   	     p.setNoDamageTicks(0);
		    	   	     p.setFlying(false);
		    	    	   ag.setIsInUse(false);
		    	    	   Corrupted.getInstance().setDefaultInventory(p);
		    	    	   }
		    	       }.runTaskLater(Corrupted.getInstance(), 60);
				}
			}else{
				Corrupted.getGames().remove(event.getPlayer().getName());
			}
		}
		if(Corrupted.getTeams().containsKey(event.getPlayer().getName())){
			if(Corrupted.getQueue().isInQueue2v2(Corrupted.getTeams().get(event.getPlayer().getName()).getMembers())){
				Corrupted.getQueue().removeFromQueue2v2(Corrupted.getTeams().get(event.getPlayer().getName()).getMembers());

			}
			for(String s : Corrupted.getTeams().get(event.getPlayer().getName()).getMembers()){
				Corrupted.getTeams().remove(s);		
				Chat.sendPlayerPrefixedMessage(Bukkit.getPlayer(s), "§eYour team has been dissolved because §b" + event.getPlayer().getName() + " §ehas logged out");
			}
		}
		if(Corrupted.getQueue().isInQueue1v1(event.getPlayer().getName())){
			Corrupted.getQueue().removeFromQueue1v1(event.getPlayer().getName());
		}

	}
	@EventHandler
	public void onInvitation(InventoryClickEvent event){
		if(event.getInventory() != null && event.getInventory().getTitle().contains("Invitation")){
			if(event.getCurrentItem() != null){
				if(event.getCurrentItem().getType() == Material.WOOL && event.getCurrentItem().getDurability() == (short) 5){
					if(Bukkit.getPlayer(event.getInventory().getItem(0).getItemMeta().getDisplayName().substring(2)) != null){
							Player p = Bukkit.getPlayer(event.getClickedInventory().getItem(0).getItemMeta().getDisplayName().substring(2));
							if(Corrupted.getTeams().containsKey(p.getName()) && Corrupted.getTeams().get(p.getName()).getOwner().equalsIgnoreCase(p.getName())){
						if(Corrupted.getTeams().get(p.getName()).getMembers().size() + 1 <= Corrupted.getTeams().get(p.getName()).getMaxSize()){
								event.getWhoClicked().closeInventory();
								Chat.sendPlayerPrefixedMessage(p, "§b" + event.getWhoClicked().getName() + " §ehas joined your team!");
						Chat.sendPlayerPrefixedMessage((Player) event.getWhoClicked(), "§eYou have joined §b" + event.getInventory().getItem(0).getItemMeta().getDisplayName().substring(2) + "§e's team!");						
						Corrupted.getTeams().put(event.getWhoClicked().getName(), Corrupted.getTeams().get(p.getName()));
						Corrupted.getTeams().get(p.getName()).getMembers().add(event.getWhoClicked().getName());
						} else {
							event.getWhoClicked().closeInventory();
							Chat.sendPlayerPrefixedMessage((Player) event.getWhoClicked(), "The person's team is full");
						}
						} else {
							event.getWhoClicked().closeInventory();
							Chat.sendPlayerPrefixedMessage((Player) event.getWhoClicked(), "The person who invited you does not own a team!");							
						}
						} else {
						event.getWhoClicked().closeInventory();
						Chat.sendPlayerPrefixedMessage((Player) event.getWhoClicked(), "The person who invited you has logged offline!");
					}
				}else if(event.getCurrentItem().getType() == Material.WOOL && event.getCurrentItem().getDurability() == (short) 14){
					event.getWhoClicked().closeInventory();
				}
			}
		}
	}
	@EventHandler
	public void queueClassSelector(InventoryClickEvent event){
		if(event.getClickedInventory() != null && event.getClickedInventory().getTitle().equals("1v1 Class Selection")){
			event.setCancelled(true);
			Player p = (Player)event.getWhoClicked();
			if((event.getCurrentItem()) != null && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()){
				if(Classes.valueOf(event.getCurrentItem().getItemMeta().getDisplayName().substring(2).toUpperCase()).hasKit(p) || p.isOp()){
				Chat.sendPlayerPrefixedMessage(p, "§eQueued for Ranked 1v1");
				Corrupted.getQueue().addToQueue1v1(p.getName(), Classes.valueOf(event.getCurrentItem().getItemMeta().getDisplayName().substring(2).toUpperCase()));
				p.closeInventory();
				Corrupted.getInstance().setQueuedInventory(p);
				}
				else{
				Chat.sendPlayerPrefixedMessage(p, "You do not own this kit!");	
				}
			}
		}
	}
	@EventHandler
	public void queueClassSelector2v2(InventoryClickEvent event){
		if(event.getClickedInventory() != null && event.getClickedInventory().getTitle().equals("2v2 Class Selection")){
			event.setCancelled(true);
			Player p = (Player)event.getWhoClicked();
			if((event.getCurrentItem()) != null && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()){
				if(Classes.valueOf(event.getCurrentItem().getItemMeta().getDisplayName().substring(2).toUpperCase()).hasKit(p) || p.isOp()){
				if(event.getInventory().getItem(45) != null && event.getInventory().getItem(45).getType() == Material.REDSTONE_BLOCK){
					Chat.sendPlayerPrefixedMessage(p, "§eYou have readied up. Waiting for teammate.");
					for(String s : Corrupted.getTeams().get(p.getName()).getMembers()){
						if(!s.equalsIgnoreCase(p.getName())){
							if(Bukkit.getPlayer(s).getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase("2v2 Class Selection")){
								String[] lore = new String[1];
								lore[0] = "§7" + event.getCurrentItem().getItemMeta().getDisplayName().substring(2);
							Bukkit.getPlayer(s).getOpenInventory().getTopInventory().setItem(45, IconMenu.setItemNameAndLore(new ItemStack(Material.EMERALD_BLOCK, 1), "§aTeammate is ready!", lore));
							}else{
								p.closeInventory();
							}
						}
					}
				}else{
					List<Classes> classes = new ArrayList<Classes>();
					if(event.getWhoClicked().getName().equals(Corrupted.getTeams().get(event.getWhoClicked().getName()).getOwner())){
					classes.add(Classes.valueOf(event.getCurrentItem().getItemMeta().getDisplayName().substring(2).toUpperCase()));
					classes.add(Classes.valueOf(event.getInventory().getItem(45).getItemMeta().getLore().get(0).substring(2).toUpperCase()));
					}else{
						classes.add(Classes.valueOf(event.getInventory().getItem(45).getItemMeta().getLore().get(0).substring(2).toUpperCase()));	
						classes.add(Classes.valueOf(event.getCurrentItem().getItemMeta().getDisplayName().substring(2).toUpperCase()));
					}
					Corrupted.getQueue().addToQueue2v2(Corrupted.getTeams().get(p.getName()).getMembers(), classes);

					for(String s : Corrupted.getTeams().get(p.getName()).getMembers()){
						Bukkit.getPlayer(s).closeInventory();
						Chat.sendPlayerPrefixedMessage(Bukkit.getPlayer(s), "§eQueued for Ranked 2v2");
						Corrupted.getInstance().setQueuedInventory(Bukkit.getPlayer(s));
					}
				}
				
				}	else{
					Chat.sendPlayerPrefixedMessage(p, "You do not own this kit!");	
					}
				}
			}
	}
		

	
	private void openGUI(Player player, ArrayList<BetterItem> items){
		
		Inventory inv = Bukkit.createInventory(player, 45, "1v1 Class Selection");
		ItemStack is;
		
		for(int i = 0; i < items.size(); i++){
			is = items.get(i).grab();
			inv.setItem(Corrupted.getInventorySpots()[i], is);
		}

		player.openInventory(inv);
	}
	private void openGUI2v2(Player player, ArrayList<BetterItem> items){
		
		Inventory inv = Bukkit.createInventory(player, 54, "2v2 Class Selection");
		ItemStack is;
		
		for(int i = 0; i < items.size(); i++){
			is = items.get(i).grab();
			inv.setItem(Corrupted.getInventorySpots()[i], is);
		}
		inv.setItem(45, IconMenu.setItemNameAndLore(new ItemStack(Material.REDSTONE_BLOCK, 1), "§cTeammate is not ready", new String[0]));
		player.openInventory(inv);
	}
	private ArrayList<BetterItem> formatItemArray(Player player){
		ArrayList<BetterItem> items = new ArrayList<BetterItem>();
		BetterItem b;
		
		for(Classes c : Classes.values()){
			if(c.equals(Classes.NONE)) continue;
			Material displayMaterial = c.getDisplayMaterial();
			String name;
			short durability = c.getDisplayDurability();
			List<String> lore = new ArrayList<String>();
			for(String r : c.getDescription()){
				lore.add(r);
			}
			if(c.isPrestige(player)){
				lore.add("§d§lPRESTIGED");
			}
			if(c.hasKit(player) || player.isOp()){
				name = "§2" + c.getName();
			}
			else{
				name = "§4" + c.getName();
			}
			String[] s = new String[lore.size()];
			for(int i = 0; i < lore.size(); i++){
				s[i] = lore.get(i);
			}
			b = new BetterItem(displayMaterial, 1, name, durability, s);
			items.add(b);
		}
		return items;
	}

	
}
