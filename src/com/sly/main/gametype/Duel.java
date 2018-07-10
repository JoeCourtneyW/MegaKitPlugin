	package com.sly.main.gametype;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.sly.main.kits.Kits;

public class Duel {

	String p_name_one = "";
	String p_name_two = "";
	Kits p_class_one;
	Kits p_class_two;
	private boolean isOver;
	private UUID id;
	private Arena arena;
	
	public Duel(Arena arena, List<String> players, List<Kits> classes) {
		this.p_name_one = players.get(0);
		this.p_name_two = players.get(1);
		this.p_class_one = classes.get(0);
		this.p_class_two = classes.get(1);
		this.arena = arena;
		this.isOver = false;
		this.id = UUID.randomUUID();
		//startGame();
	}
	/*private void startGame(){
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
	}*/
	public List<String> getPlayers() {
		return Arrays.asList(p_name_one, p_name_two);
	}

	public String getPlayerNameOne() {
		return p_name_one;
	}

	public String getPlayerNameTwo() {
		return p_name_two;
	}

	public Kits getPlayerClassOne() {
		return p_class_one;
	}

	public Kits getPlayerClassTwo() {
		return p_class_two;
	}

	public List<Kits> getClasses() {
		return Arrays.asList(p_class_one, p_class_two);
	}
	public boolean isOver(){
		return isOver;
	}
	public UUID getID(){
		return id;
	}
	public Arena getArena(){
		return arena;
	}
	public void finish(){
		isOver = true;
		p_name_one = null;
		p_name_two = null;
		arena = null;
		id = null;
	}
}
