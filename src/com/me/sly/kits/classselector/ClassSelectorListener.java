package com.me.sly.kits.classselector;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.me.sly.kits.main.Chat;
import com.me.sly.kits.main.Corrupted;
import com.me.sly.kits.main.classes.Classes;
import com.me.sly.kits.main.resources.BetterItem;
import com.me.sly.kits.main.resources.LocationUtil;

public class ClassSelectorListener implements Listener{
	private ArrayList<BetterItem> itemsTest;
	
	public ClassSelectorListener(){
		itemsTest = new ArrayList<BetterItem>();

	}
	
	@EventHandler
	public void onSelectorOpen(PlayerInteractEvent e){
		
		if(e.getItem() != null && e.getItem().getType() == Material.FEATHER){
			if(!Corrupted.getTeams().containsKey(e.getPlayer().getName())){
			new ClassSelector(itemsTest, e.getPlayer());
			e.setCancelled(true);
			}else{
				Chat.sendPlayerPrefixedMessage(e.getPlayer(), "You may not join FFA when you are in a team");
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPurchase(InventoryClickEvent e){
		Player p = (Player)e.getWhoClicked();
		
		if(e.getClickedInventory() != null && e.getClickedInventory().getTitle().equals("Class Selection")){
			e.setCancelled(true);

			if((e.getCurrentItem()) != null && e.getCurrentItem().hasItemMeta() && e.getCurrentItem().getItemMeta().hasDisplayName()){
				if(Classes.valueOf(e.getCurrentItem().getItemMeta().getDisplayName().substring(2).toUpperCase()).hasKit(p) || p.isOp()){
					ClassSelector.setKit(e.getCurrentItem().getItemMeta().getDisplayName().substring(2), p);
				p.updateInventory();
				Chat.sendPlayerPrefixedMessage(p, "Teleporting...");
				Location l1 = new Location(Bukkit.getWorld("Nube"), -381.5, 42, 230.5, 0, 0);
				Location l2 = new Location(Bukkit.getWorld("Nube"), -346.5, 42, 193.5, 0, 0);
				Location l3 = new Location(Bukkit.getWorld("Nube"), -400.5, 39, 263.5, 0, 0);
				Location l4 = new Location(Bukkit.getWorld("Nube"), -401.5, 37, 186.5, 0, 0);
				int i = Corrupted.getInstance().rand(1, 4);
				if(i == 1){
					p.teleport(LocationUtil.getSafeDestination(l1.add(0, 1, 0)));
				}else if(i == 2){
					p.teleport(LocationUtil.getSafeDestination(l2.add(0, 1, 0)));
				}else if(i == 3){
					p.teleport(LocationUtil.getSafeDestination(l3.add(0, 1, 0)));
				}else{
					p.teleport(LocationUtil.getSafeDestination(l4.add(0, 1, 0)));
				}
				Corrupted.getInstance().refreshPlayer(p);
				Corrupted.getInstance().fixPlayer(p);
				}
				else{
				Chat.sendPlayerPrefixedMessage(p, "You do not own this kit!");	
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void disableShiftClick(InventoryClickEvent e){
		if((e.getWhoClicked().getOpenInventory().getTopInventory().getTitle().equals("Shop") || e.getWhoClicked().getOpenInventory().getTopInventory().getTitle().equals("Class Selection") || e.getWhoClicked().getOpenInventory().getTopInventory().getTitle().equals("1v1 Class Selection")) && e.isShiftClick()){
			e.setCancelled(true);
		}
	}
}