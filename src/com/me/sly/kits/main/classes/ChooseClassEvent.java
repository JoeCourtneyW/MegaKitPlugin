package com.me.sly.kits.main.classes;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class ChooseClassEvent extends Event{

	private Classes classes;
	private Player player;
	private boolean isCancelled;
	public ChooseClassEvent(Player player, Classes classes){
		this.classes = classes;
		this.player = player;
	}
	@Override
	public HandlerList getHandlers() {
		return null;
	}
	public void setCancelled(boolean cancelled){
		this.isCancelled = cancelled;
	}
	public boolean isCancelled(){
		return this.isCancelled;
	}
	public Player getPlayer(){
		return this.player;
	}
	public Classes getClassChosen(){
		return this.classes;
	}

}
