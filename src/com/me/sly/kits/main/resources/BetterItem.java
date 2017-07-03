package com.me.sly.kits.main.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BetterItem {

	private Material type;
	private int amount;
	private String displayname;
	private String[] lore;
	private short dura = 0;
	public BetterItem(Material type, int amount, String displayname, String... lore){
		this.type = type;
		this.amount = amount;
		this.displayname = displayname;
		this.lore = lore;
	}
	public BetterItem(Material type, int amount, String displayname){
		this.type = type;
		this.amount = amount;
		this.displayname = displayname;
	}
	public BetterItem(Material type, int amount, String displayname, short dura, String... lore){
		this.type = type;
		this.amount = amount;
		this.displayname = displayname;
		this.lore = lore;
		this.dura = dura;
	}
	public BetterItem(Material type, int amount, short dura, String displayname){
		this.type = type;
		this.amount = amount;
		this.displayname = displayname;
		this.dura = dura;
	}
	public void setDisplayName(String displayName){
		this.displayname = displayName;
	}
	
	public ItemStack grab(){
		ItemStack i = new ItemStack(type, amount);
		List<String> lore = new ArrayList<String>();
		ItemMeta im = i.getItemMeta();
		if(this.lore != null){
		lore =Arrays.asList(this.lore);
		}
		if(lore != null){
			im.setLore(lore);
		}
		if(displayname != null){
			im.setDisplayName(displayname);
		}
		if(dura != 0){
			i.setDurability(dura);
		}
		i.setItemMeta(im);
		return i;
	}
}
