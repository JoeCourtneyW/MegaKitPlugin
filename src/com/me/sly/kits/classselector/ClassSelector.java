package com.me.sly.kits.classselector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.kitteh.tag.TagAPI;

import com.me.sly.kits.main.Corrupted;
import com.me.sly.kits.main.classes.Classes;
import com.me.sly.kits.main.enums.CustomBlock;
import com.me.sly.kits.main.resources.BetterItem;
import com.me.sly.kits.main.resources.MyMetadata;

public class ClassSelector {
	private Player player;
	private ArrayList<BetterItem> items;
	@SuppressWarnings("unused")
	private ClassSelectorGUI gui;
	
	public ClassSelector(ArrayList<BetterItem> items, Player player){
		this.player = player;
		
		formatItemArray();
		
		gui = new ClassSelectorGUI(this.items, player, this);
		
	}
	
	//If price is too high change what the item is to the unavailable icon
	private void formatItemArray(){
		items = new ArrayList<BetterItem>();
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
	}
	
	public static void setKit(String kit, Player p){
		if(Corrupted.getKitItems().containsKey(kit)){	
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[4]);
		p.closeInventory();
		HashMap<String, ItemStack> rItems = Corrupted.getKitItems().get(kit);
		 String custom = Corrupted.getKitCustomizerCache().get(p.getUniqueId().toString()).get(kit);
		 for(int r = 5; r < rItems.size()+1; r++){
			if(rItems.get(custom.split("\\|")[r-5]) != null && rItems.get(custom.split("\\|")[r-5]).getType() != Material.AIR){
				if(rItems.get(custom.split("\\|")[r-5]).getType() == Material.WOOD){
					p.getInventory().setItem(r-5, new ItemStack(CustomBlock.getSelectedBlock(p).getMaterial(), 64, CustomBlock.getSelectedBlock(p).getDurability()));
					continue;
				}
			p.getInventory().setItem(r-5, rItems.get(custom.split("\\|")[r-5]));
			}
		}
		if(Corrupted.getKitItems().get(kit).get("Boots") != null)p.getInventory().setBoots(Corrupted.getKitItems().get(kit).get("Boots"));
		if(Corrupted.getKitItems().get(kit).get("Leggings") != null)p.getInventory().setLeggings(Corrupted.getKitItems().get(kit).get("Leggings"));
		if(Corrupted.getKitItems().get(kit).get("Chestplate") != null)p.getInventory().setChestplate(Corrupted.getKitItems().get(kit).get("Chestplate"));
		if(Corrupted.getKitItems().get(kit).get("Helmet") != null)p.getInventory().setHelmet(Corrupted.getKitItems().get(kit).get("Helmet"));
		p.updateInventory();
		p.sendMessage("§aYou chose the §b" + kit + " §akit!");
		p.setMetadata("Class", new MyMetadata(Corrupted.getInstance(), kit));
		p.setFoodLevel(20);
		if(Classes.valueOf(kit.toUpperCase()).isPrestige(p)){
			p.setMaxHealth(44.0);
			p.setHealth(44.0);
		}else{
			p.setMaxHealth(40.0);
			p.setHealth(40.0);
		}
		p.setFallDistance(0);
		p.setAllowFlight(false);
		p.setFlying(false);
		TagAPI.refreshPlayer(p);
	}
	}
	public static void setKitGameType(String kit, Player p){
		if(Corrupted.getKitItems().containsKey(kit)){	
		p.getInventory().clear();
		p.getInventory().setArmorContents(new ItemStack[4]);
		p.closeInventory();
		HashMap<String, ItemStack> rItems = Corrupted.getKitItems().get(kit);
		 String custom = Corrupted.getKitCustomizerCache().get(p.getUniqueId().toString()).get(kit);
		 for(int r = 5; r < rItems.size()+1; r++){
			if(rItems.get(custom.split("\\|")[r-5]) != null && rItems.get(custom.split("\\|")[r-5]).getType() != Material.AIR){
				if(rItems.get(custom.split("\\|")[r-5]).getType() == Material.WOOD){
					p.getInventory().setItem(r-5, new ItemStack(CustomBlock.getSelectedBlock(p).getMaterial(), 64, CustomBlock.getSelectedBlock(p).getDurability()));
					continue;
				}
			p.getInventory().setItem(r-5, rItems.get(custom.split("\\|")[r-5]));
			}
		}
		if(Corrupted.getKitItems().get(kit).get("Boots") != null)p.getInventory().setBoots(Corrupted.getKitItems().get(kit).get("Boots"));
		if(Corrupted.getKitItems().get(kit).get("Leggings") != null)p.getInventory().setLeggings(Corrupted.getKitItems().get(kit).get("Leggings"));
		if(Corrupted.getKitItems().get(kit).get("Chestplate") != null)p.getInventory().setChestplate(Corrupted.getKitItems().get(kit).get("Chestplate"));
		if(Corrupted.getKitItems().get(kit).get("Helmet") != null)p.getInventory().setHelmet(Corrupted.getKitItems().get(kit).get("Helmet"));
		p.updateInventory();
		p.setMetadata("Class", new MyMetadata(Corrupted.getInstance(), kit));
		p.setFoodLevel(20);
		if(Classes.valueOf(kit.toUpperCase()).isPrestige(p)){
			p.setMaxHealth(44.0);
			p.setHealth(44.0);
		}else{
			p.setMaxHealth(40.0);
			p.setHealth(40.0);
		}
		p.setFallDistance(0);
		p.setAllowFlight(false);
		p.setFlying(false);
		TagAPI.refreshPlayer(p);
	}
	}
}

