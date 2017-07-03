package com.me.sly.kits.main.gametype;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.me.sly.kits.main.Chat;
import com.me.sly.kits.main.Corrupted;

public class Game {

	private Arena arena;
	private List<String> team1;
	private List<String> team2;
	private boolean isOver;
	private UUID id;
	public Game(Arena arena, List<String> team1, List<String> team2){
		this.arena = arena;
		this.team1 = team1;
		this.team2 = team2;
		this.isOver = false;
		this.id = UUID.randomUUID();
		startGame();
	}
	private void startGame(){
		Corrupted.getSpectators().put(this, new ArrayList<String>());
		for(String s : team1){
			Player p = Bukkit.getPlayer(s);
			if(p != null){
				Location l = getArena().getSpawn1();
				p.setFallDistance(0);
				p.teleport((l.clone().add(0, 1, 0)));
				p.setHealth(((CraftPlayer)p).getMaxHealth());
				String gameType = l.getWorld().getName().contains("2v2") ? "2v2" : "1v1";
				if(team2.size() == 1){
					int rating = (int) Corrupted.getDatabaseCache().get(Bukkit.getPlayer(team2.get(0)).getUniqueId().toString()).get(gameType + "Rating");
					Chat.sendPlayerPrefixedMessage(p, "§eYou have been matched up against §b" + team2.get(0) + "§e (" + rating + ")");					
				}else{
				int rating1 = (int) Corrupted.getDatabaseCache().get(Bukkit.getPlayer(team2.get(0)).getUniqueId().toString()).get(gameType + "Rating");
				int rating2 = (int) Corrupted.getDatabaseCache().get(Bukkit.getPlayer(team2.get(1)).getUniqueId().toString()).get(gameType + "Rating");
				Chat.sendPlayerPrefixedMessage(p, "§eYou have been matched up against §b" + team2.get(0) + "§e and §b" + team2.get(1) + "§e(" + rating1 + ", " + rating2 + ")");
				}
			}
		}

		for(String s : team2){
			Player p = Bukkit.getPlayer(s);
			if(p != null){
				Location l = getArena().getSpawn2();
				p.setFallDistance(0);
				p.teleport((l.clone().add(0, 1, 0)));
				p.setHealth(((CraftPlayer)p).getMaxHealth());
				String gameType = l.getWorld().getName().contains("2v2") ? "2v2" : "1v1";
				if(team1.size() == 1){
					int rating = (int) Corrupted.getDatabaseCache().get(Bukkit.getPlayer(team1.get(0)).getUniqueId().toString()).get(gameType + "Rating");
					Chat.sendPlayerPrefixedMessage(p, "You have been matched up against " + team1.get(0) + "§e (" + rating + ")");					
				}else{
					int rating1 = (int) Corrupted.getDatabaseCache().get(Bukkit.getPlayer(team1.get(0)).getUniqueId().toString()).get(gameType + "Rating");
					int rating2 = (int) Corrupted.getDatabaseCache().get(Bukkit.getPlayer(team1.get(1)).getUniqueId().toString()).get(gameType + "Rating");
					Chat.sendPlayerPrefixedMessage(p, "§eYou have been matched up against §b" + team1.get(0) + "§e and §b" + team1.get(1) + "§e(" + rating1 + ", " + rating2 + ")");
				}
				}
		}
		new BukkitRunnable(){
		public void run(){
			for(String s : team2){
				Player p = Bukkit.getPlayer(s);
				if(p != null){
					Corrupted.getInstance().refreshPlayer(p);
					Corrupted.getInstance().fixPlayer(p);
				}
			}
			for(String s : team1){
				Player p = Bukkit.getPlayer(s);
				if(p != null){
					Corrupted.getInstance().refreshPlayer(p);
					Corrupted.getInstance().fixPlayer(p);
				}
			}
		}
		}.runTaskLater(Corrupted.getInstance(), 20);
	}
	public Arena getArena(){
		return arena;
	}
	public List<String> getTeam1(){
		return team1;
	}
	public List<String> getTeam2(){
		return team2;
	}
	public boolean isOver(){
		return isOver;
	}
	public UUID getID(){
		return id;
	}
	public void finish(){
		isOver = true;
		team1 = null;
		team2 = null;
		arena = null;
		id = null;
	}
}
