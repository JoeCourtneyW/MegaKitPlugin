package com.me.sly.kits.main.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.me.sly.kits.main.Chat;
import com.me.sly.kits.main.Corrupted;
import com.me.sly.kits.main.classes.Classes;

public class StatListeners implements Listener{
	
	@EventHandler(priority = EventPriority.HIGH)
	public void RatingOnKill(PlayerDeathEvent event){
		if(event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player && !(event.getEntity().getName().equalsIgnoreCase(event.getEntity().getKiller().getName()))){
			final Player p = (Player) event.getEntity().getKiller();
			final Player death = (Player) event.getEntity();
			if(p.hasMetadata("Class")){
				Classes.getClass(p).setKills(p, Classes.getClass(p).getKills(p)+1);
			}
			if(!p.getWorld().getName().contains("Nube")) return;
	        double killRating = 1.0 * (int) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("FFARating");
			double deathRating = 1.0 * (int)Corrupted.getDatabaseCache().get(death.getUniqueId().toString()).get("FFARating");
	        double killExpectedScore = (killRating) / (killRating + deathRating);
	        double deathExpectedScore = (deathRating) / (killRating + deathRating);
	        double killActualScore = 1.0;
	        double deathActualScore = 0.0;
	        double killScore = killActualScore-killExpectedScore;
	        double deathScore = deathActualScore-deathExpectedScore;
	        double killRatingToAdd = (32*(killScore) < 0 ? 0 : 32*(killScore));
	        double deathRatingToAdd = (32*(deathScore) > 32 ? 32 : 32*(deathScore));
	        /**
	         * Need to scale rating change
	         * scale = (1000/rating)
	         * 4000 = 0.25
	         * 2000 = 0.5
	         * 1000 = 1.0
	         * 500 = 2.0
	         */
	        double killScale = (1000/killRating);
	        double deathScale = (1000/deathRating);
	        killRatingToAdd *= killScale;
	        deathRatingToAdd *= deathScale;
	        double newKillRating = (killRating + killRatingToAdd);
	        double newDeathRating = (deathRating + deathRatingToAdd);
	        Chat.sendPlayerPrefixedMessage(p, "§e+" + Corrupted.getInstance().roundTwoDecimals(newKillRating-killRating) + " FFA rating §cfor killing §b" + death.getName() + "§c.");
	        Chat.sendPlayerPrefixedMessage(death, "§e-" + Corrupted.getInstance().roundTwoDecimals(Math.abs(newDeathRating-deathRating)) + " FFA rating §cfor dying to §b" + p.getName() + "§c.");
	        
	        Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).remove("FFARating");
	        Corrupted.getDatabaseCache().get(death.getUniqueId().toString()).remove("FFARating");
	        Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("FFARating", Math.toIntExact(Math.round(newKillRating)));
	        Corrupted.getDatabaseCache().get(death.getUniqueId().toString()).put("FFARating", Math.toIntExact(Math.round(newDeathRating)));
	        int killOldFFAKills = (int) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("FFAKills");
	        Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).remove("FFAKills");
	        Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("FFAKills", killOldFFAKills+1);
	        int deathOldFFADeaths = (int) Corrupted.getDatabaseCache().get(death.getUniqueId().toString()).get("FFADeaths");
	        Corrupted.getDatabaseCache().get(death.getUniqueId().toString()).remove("FFADeaths");
	        Corrupted.getDatabaseCache().get(death.getUniqueId().toString()).put("FFADeaths", deathOldFFADeaths+1);
	    	        Corrupted.getInstance().updateScoreboard(p);
	    	        Corrupted.getInstance().updateScoreboard(death);
	    	       Corrupted.getInstance().updateDatabase(p);
	    	       Corrupted.getInstance().updateDatabase(death);

		}
	}
	@EventHandler(priority = EventPriority.NORMAL)
	public void CoinsOnKill(PlayerDeathEvent event){
		if(event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player && !(event.getEntity().getName().equalsIgnoreCase(event.getEntity().getKiller().getName()))){
			Player p = (Player) event.getEntity().getKiller();
			Player death = (Player) event.getEntity();
			if (p.getWorld().getName().contains("Nube")){
	        Chat.sendPlayerPrefixedMessage(p, "§e+" + Corrupted.getInstance().getMultipliedCoins(p, 20) + " coins §cfor killing §b" + death.getName() + "§c.");
	        Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", ((int) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins"))+Corrupted.getInstance().getMultipliedCoins(p, 20));
			}
	        Corrupted.getInstance().updateScoreboard(p);
		}
	}

}
