package com.me.sly.kits.main.gametype;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.me.sly.kits.main.Chat;
import com.me.sly.kits.main.Corrupted;

public class Team {
	private int maxSize;
	private String owner;
	private List<String> members;
	public Team(int maxSize, String owner){
		this.maxSize = maxSize;
		this.owner = owner;
		this.members = new ArrayList<String>();
		this.members.add(owner);
	}
	public int getMaxSize(){
		return this.maxSize;
	}
	public String getOwner(){
		return this.owner;
	}
	public List<String> getMembers(){
		return this.members;
	}
	public void dissolve(){
		for(String id : members){
			if(Bukkit.getPlayer(id) != null){
				Chat.sendPlayerPrefixedMessage(Bukkit.getPlayer(id), "§eThe team you were on has been dissolved!");
				Bukkit.getPlayer(id).closeInventory();
				Corrupted.getTeams().remove(id.toString());
			}
		}
		this.maxSize = 0;
		this.owner = null;
		this.members = null;
	}
	public static boolean sameTeam(Player p, Player p1){
		if(Corrupted.getTeams().containsKey(p.getName()) && Corrupted.getTeams().containsKey(p1.getName())){
			if(Corrupted.getTeams().get(p.getName()).getMembers().contains(p1.getName())){
			return true;	
			}
		}
		return false;
	}
}
