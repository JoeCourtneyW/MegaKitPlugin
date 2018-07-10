package com.sly.main.guis.kitselector;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.sly.main.player.PlayerModel;

public class ClassSelectorGUI {

	private ArrayList<ItemStack> items;
	private PlayerModel player;
	@SuppressWarnings("unused")
	private KitSelector classSelectorInstance;
	@SuppressWarnings("unused")
	private int size;
	private Inventory inv;
	private int[] inventorySpots;
	public ClassSelectorGUI(ArrayList<ItemStack> items, PlayerModel player, KitSelector classSelectorInstance){
		this.player = player;
		this.items = items;
		this.classSelectorInstance = classSelectorInstance;
		
		inventorySpots = new int[15];
		
		inventorySpots[0] = 11;
		inventorySpots[1] = 12;
		inventorySpots[2] = 13;
		inventorySpots[3] = 14;
		inventorySpots[4] = 15;
		inventorySpots[5] = 20;
		inventorySpots[6] = 21;
		inventorySpots[7] = 22;
		inventorySpots[8] = 23;
		inventorySpots[9] = 24;
		inventorySpots[10] = 29;
		inventorySpots[11] = 30;
		inventorySpots[12] = 31;
		inventorySpots[13] = 32;
		inventorySpots[14] = 33;
		
		openGUI();
	}
	
	private void openGUI(){
		inv = Bukkit.createInventory(player.getPlayer(), 45, "Class Selection");
		ItemStack is;
		
		for(int i = 0; i < items.size(); i++){
			is = items.get(i);
			inv.setItem(inventorySpots[i], is);
		}

		player.getPlayer().openInventory(inv);
	}	
}
