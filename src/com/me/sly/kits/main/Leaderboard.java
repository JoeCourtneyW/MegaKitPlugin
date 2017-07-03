package com.me.sly.kits.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Sign;

import com.me.sly.kits.main.classes.Classes;
import com.me.sly.kits.main.enums.DatabaseColumn;

public class Leaderboard {

	private DatabaseColumn column;
	private int players;
	private Location firstSign;
	public Leaderboard(DatabaseColumn column, int players, Location firstSign){
		this.column = column;
		this.players = players;
		this.firstSign = firstSign;
		update();
	}
	
	public DatabaseColumn getColumn(){
		return this.column;
	}
	public int getSize(){
		return this.players;
	}
	public Location getFirstSign(){
		return this.firstSign;
	}
	public void update(){
		if(players == 9){
			HashMap<String, Integer> values = new HashMap<String, Integer>();
			for(String id : Corrupted.getDatabaseCache().keySet()){
				HashMap<String, Object> columns = Corrupted.getDatabaseCache().get(id);
				int value = (int) columns.get(column.getDatabaseName());
				values.put(id, value);
			}
			List<Integer> list = new ArrayList<Integer>();
			list.addAll(values.values());
			Collections.sort(list);
			final List<Integer> finalList= list;
			Location l = getFirstSign().clone();
			if(!(Bukkit.getWorld("Nube").getBlockAt(l).getState() instanceof Sign)) return;
			for(int i = 0; i < 9; i++){
				final int rs = i;
				if(i < 3){
					final Sign s =  (Sign) Bukkit.getWorld("Nube").getBlockAt(l.clone().add(0, 0, i)).getState();
					String name = "";
					for(String r : values.keySet()){
						if(values.get(r) == list.get(list.size()-i-1)){
							name = r;
						}
					}
					Classes mostUsedClass = Classes.NONE;
					int mostKills = Integer.MIN_VALUE;
					for(Classes c : Classes.values()){
						if(c.equals(Classes.NONE)) continue;
						if(c.getKills(UUID.fromString(name)) > mostKills){
							mostKills = c.getKills(UUID.fromString(name));
							mostUsedClass = c;
						}
					}
					s.setLine(0, "#" + (rs+1));
					s.setLine(1, "§b§l" + Corrupted.getDatabaseCache().get(name).get("PlayerName"));
					s.setLine(2, "§1" + mostUsedClass.getName());
					s.setLine(3, "§l" + finalList.get(finalList.size()-rs-1));
					s.update();		
				}else if(i < 6){
					final Sign s = (Sign) Bukkit.getWorld("Nube").getBlockAt(l.clone().add(0, -1, i-3)).getState();
					String name = "";
					for(String r : values.keySet()){
						if(values.get(r) == list.get(list.size()-i-1)){
							name = r;
						}
					}
					Classes mostUsedClass = Classes.NONE;
					int mostKills = Integer.MIN_VALUE;
					for(Classes c : Classes.values()){
						if(c.equals(Classes.NONE)) continue;
						if(c.getKills(UUID.fromString(name)) > mostKills){
							mostKills = c.getKills(UUID.fromString(name));
							mostUsedClass = c;
						}
					}
					s.setLine(0, "#" + (rs+1));
					s.setLine(1, "§b§l" + Corrupted.getDatabaseCache().get(name).get("PlayerName"));
					s.setLine(2, "§1" + mostUsedClass.getName());
					s.setLine(3, "§l" + finalList.get(finalList.size()-rs-1));
					s.update();		
				}else{
					final Sign s =  (Sign) Bukkit.getWorld("Nube").getBlockAt(l.clone().add(0, -2, i-6)).getState();
					String name = "";
					for(String r : values.keySet()){
						if(values.get(r) == list.get(list.size()-i-1)){
							name = r;
						}
					}		
					Classes mostUsedClass = Classes.NONE;
					int mostKills = Integer.MIN_VALUE;
					for(Classes c : Classes.values()){
						if(c.equals(Classes.NONE)) continue;
						if(c.getKills(UUID.fromString(name)) > mostKills){
							mostKills = c.getKills(UUID.fromString(name));
							mostUsedClass = c;
						}
					}
						s.setLine(0, "#" + (rs+1));
						s.setLine(1, "§b§l" + Corrupted.getDatabaseCache().get(name).get("PlayerName"));
						s.setLine(2, "§1" + mostUsedClass.getName());
						s.setLine(3, "§l" + finalList.get(finalList.size()-rs-1));
						s.update();								
				}
			}
		}
	}
}
