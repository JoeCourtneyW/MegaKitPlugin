package com.me.sly.kits.main.gametype;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;

import com.me.sly.kits.classselector.ClassSelector;
import com.me.sly.kits.main.Corrupted;
import com.me.sly.kits.main.classes.Classes;

public class Queue {

	private HashMap<String, Classes> queue1v1;
	private HashMap<List<String>, List<Classes>> queue2v2;
	public Queue(){
		this.queue1v1 = new HashMap<String, Classes>();
		this.queue2v2 = new HashMap<List<String>, List<Classes>>();
	}

	public void addToQueue1v1(String player, Classes c){
		queue1v1.put(player, c);
	}
	public void addToQueue2v2(List<String> players, List<Classes> c){
		queue2v2.put(players, c);
	}
	public void removeFromQueue1v1(String player){
		queue1v1.remove(player);
	}
	public void removeFromQueue2v2(List<String> players){
		queue2v2.remove(players);
	}
	public boolean isInQueue1v1(String player){
		return queue1v1.containsKey(player);
	}
	public boolean isInQueue2v2(List<String> players){
		return queue2v2.containsKey(players);
	}
	public void processQueue(){
		if(queue1v1.size() > 1){
			List<String> shuffleKeys = new ArrayList<String>();
			HashMap<String, Classes> newMap = new HashMap<String, Classes>();
			shuffleKeys.addAll(queue1v1.keySet());
			Collections.shuffle(shuffleKeys);
			for(String list : shuffleKeys){
				newMap.put(list, queue1v1.get(list));
			}
					for(int i = 0; i < newMap.size()-1; i+=2){
						Object[] keys =  newMap.keySet().toArray();
						String team1 = (String) keys[i];
						String team2 = (String) keys[i+1];
						for(Arena a : Corrupted.getArenas1v1()){
							if(!a.isInUse()){
								a.setIsInUse(true);
								Game g = new Game(a, Arrays.asList(team1), Arrays.asList(team2));
								Corrupted.getGames().put(team1, g);
								Corrupted.getGames().put(team2, g);
								ClassSelector.setKitGameType(newMap.get(team1).getName(), Bukkit.getPlayer(team1));
								ClassSelector.setKitGameType(newMap.get(team2).getName(), Bukkit.getPlayer(team2));
								queue1v1.remove(team1);
								queue1v1.remove(team2);
								break;
							}
						}
			}
		}
		if(queue2v2.size() > 1){
			List<List<String>> shuffleKeys = new ArrayList<List<String>>();
			HashMap<List<String>, List<Classes>> newMap = new HashMap<List<String>, List<Classes>>();
			shuffleKeys.addAll(queue2v2.keySet());
			Collections.shuffle(shuffleKeys);
			for(List<String> list : shuffleKeys){
				newMap.put(list, queue2v2.get(list));
			}
			for(int i = 0; i < newMap.size()-1; i+=2){
				Set<List<String>> keys = newMap.keySet();
				ArrayList<List<String>> keysArray = new ArrayList<List<String>>();
				for(List<String> strings : keys){
					keysArray.add(strings);
				}
				List<String> team1 = keysArray.get(i);
				List<String> team2 = keysArray.get(i+1);
				for(Arena a : Corrupted.getArenas2v2()){
					if(!a.isInUse()){
						a.setIsInUse(true);
						Game g = new Game(a, team1, team2);
						Corrupted.getGames().put(team1.get(0), g);
						Corrupted.getGames().put(team2.get(0), g);
						Corrupted.getGames().put(team1.get(1), g);
						Corrupted.getGames().put(team2.get(1), g);
						ClassSelector.setKitGameType(newMap.get(team1).get(0).getName(), Bukkit.getPlayer(team1.get(0)));
						ClassSelector.setKitGameType(newMap.get(team2).get(0).getName(), Bukkit.getPlayer(team2.get(0)));
						ClassSelector.setKitGameType(newMap.get(team1).get(1).getName(), Bukkit.getPlayer(team1.get(1)));
						ClassSelector.setKitGameType(newMap.get(team2).get(1).getName(), Bukkit.getPlayer(team2.get(1)));
						queue2v2.remove(team1);
						queue2v2.remove(team2);
						break;
					}
				}
	}
		}
	}
}
