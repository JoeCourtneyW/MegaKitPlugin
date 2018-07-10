package com.sly.main.resources;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BetterItem
{

	private Material type = Material.AIR;
	private int amount = 1;
	private String displayname = "";
	private HashMap<Enchantment, Integer> enchantments = new HashMap<Enchantment, Integer>();
	private String[] lore = new String[0];
	private short data = 0;

	public BetterItem() {

	}

	public BetterItem material(Material material) {
		this.type = material;
		return this;
	}

	public BetterItem amount(int amount) {
		this.amount = amount;
		return this;
	}

	public BetterItem displayName(String displayName) {
		this.displayname = displayName;
		return this;
	}

	public BetterItem data(short data) {
		this.data = data;
		return this;
	}

	public BetterItem enchant(Enchantment enchant, int level) {
		enchantments.put(enchant, level);
		return this;
	}

	public BetterItem enchant(HashMap<Enchantment, Integer> enchants) {
		enchantments.putAll(enchants);
		return this;
	}

	public BetterItem lore(String... lore) {
		this.lore = lore;
		return this;
	}

	public BetterItem addLoreLine(String line) {
		String[] lore = new String[this.lore.length + 1];

		for (int i = 0; i < this.lore.length; i++)
			lore[i] = this.lore[i];

		lore[lore.length - 1] = line;

		this.lore = lore;
		return this;
	}

	public BetterItem addLoreSpacer() {
		String[] lore = new String[this.lore.length + 1];

		for (int i = 0; i < this.lore.length; i++)
			lore[i] = this.lore[i];

		lore[lore.length - 1] = "§k ";

		this.lore = lore;
		return this;
	}

	public ItemStack grab() {
		ItemStack i = new ItemStack(type, amount);

		ItemMeta itemMeta = i.getItemMeta();

		if (this.lore.length > 0) {
			itemMeta.setLore(Arrays.asList(this.lore));
		}

		if (displayname.length() > 0) {
			itemMeta.setDisplayName(displayname);
		}
		if (data != 0) {
			i.setDurability(data);
		}
		for (Enchantment e : enchantments.keySet())
			if (e.getMaxLevel() < enchantments.get(e))
				i.addUnsafeEnchantment(e, enchantments.get(e));
			else
				i.addEnchantment(e, enchantments.get(e));

		i.setItemMeta(itemMeta);
		return i;
	}

	public BetterItem clone() {
		return new BetterItem().material(type).amount(amount).data(data).displayName(displayname).lore(this.lore)
				.enchant(enchantments);
	}
}
