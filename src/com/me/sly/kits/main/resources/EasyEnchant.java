package com.me.sly.kits.main.resources;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class EasyEnchant{

	private Material m;
	private int amount = 1;
	private String disp = null;
	private int dura = 0;
	private HashMap<Enchantment, String> enchants = new HashMap<Enchantment, String>();
	private HashMap<String, Enchantment> backenchants = new HashMap<String, Enchantment>();
	EasyEnchant(Material m, int amount){
		this.m = m;
		this.amount = amount;
	}
	public EasyEnchant(Material m, int amount, String disp){
		this.m = m;
		this.amount = amount;
		this.disp = disp;
	}
	public EasyEnchant(Material m, int amount, int dura, String disp){
		this.m = m;
		this.amount = amount;
		this.disp = disp;
		this.dura = dura;
	}
	public EasyEnchant addEnchant(Enchantment e, int level){
			enchants.put(e, String.valueOf(level));
			backenchants.put(String.valueOf(level), e);
		return this;
	}
	public ItemStack craftToItemStack(){
		ItemStack is = new ItemStack(m, amount);
		is.setDurability(((short)dura));
		if(disp != null){
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(disp);
		is.setItemMeta(im);
		}
		for(Enchantment e : enchants.keySet()){
			if(e.getMaxLevel() < Integer.parseInt(enchants.get(e))){
				is.addUnsafeEnchantment(e, Integer.parseInt(enchants.get(e)));
			}else{
				is.addEnchantment(e, Integer.parseInt(enchants.get(e)));
			}
		}
		return is;
	}
}
