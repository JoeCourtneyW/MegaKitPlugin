package com.me.sly.kits.main.listeners;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.me.sly.kits.main.Chat;
import com.me.sly.kits.main.Corrupted;
import com.me.sly.kits.main.classes.Classes;
import com.me.sly.kits.main.gametype.Duel;
import com.me.sly.kits.main.resources.BetterItem;
import com.me.sly.kits.main.resources.IconMenu;

public class DuelListeners implements Listener {

	@EventHandler
	public void queueClassSelector2v2(InventoryClickEvent event) {
		if (event.getClickedInventory() != null && event.getClickedInventory().getTitle().equals("Duel Class Selection")) {
			List<String> players = new ArrayList<String>(Arrays.asList(event.getWhoClicked().getName(), event.getInventory().getItem(45).getItemMeta().getDisplayName().split(" ")[0].substring(2)));
			event.setCancelled(true);
			Player p = (Player) event.getWhoClicked();
			if ((event.getCurrentItem()) != null && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().hasDisplayName()) {
				if (Classes.valueOf(event.getCurrentItem().getItemMeta().getDisplayName().substring(2).toUpperCase()).hasKit(p) || p.isOp()) {
					if (event.getInventory().getItem(45) != null && event.getInventory().getItem(45).getType() == Material.REDSTONE_BLOCK) {
						Chat.sendPlayerPrefixedMessage(p, "§eYou have readied up. Waiting for opponent.");
						for (String s : players) {
							if (!s.equalsIgnoreCase(p.getName())) {
								if (Bukkit.getPlayer(s).getOpenInventory().getTopInventory().getTitle().equalsIgnoreCase("Duel Class Selection")) {
									String[] lore = new String[1];
									lore[0] = "§7" + event.getCurrentItem().getItemMeta().getDisplayName().substring(2);
									Bukkit.getPlayer(s).getOpenInventory().getTopInventory().setItem(45, IconMenu.setItemNameAndLore(new ItemStack(Material.EMERALD_BLOCK, 1), "§a" + players.get(1) + " is ready!", lore));
								} else {
									p.closeInventory();
								}
							}
						}
					} else {
						List<Classes> classes = new ArrayList<Classes>();
						classes.add(Classes.valueOf(event.getCurrentItem().getItemMeta().getDisplayName().substring(2).toUpperCase()));
						classes.add(Classes.valueOf(event.getInventory().getItem(45).getItemMeta().getLore().get(0).substring(2).toUpperCase()));
						// INITIATE DUEL
						new Duel(Corrupted.getArenasDuel().get(0), players, classes);
						for (String s : players) {
							Bukkit.getPlayer(s).closeInventory();
							Chat.sendPlayerPrefixedMessage(Bukkit.getPlayer(s), "§eYou have been warped into a duel arena");
							Corrupted.getInstance().setQueuedInventory(Bukkit.getPlayer(s));
						}
					}

				} else {
					Chat.sendPlayerPrefixedMessage(p, "You do not own this kit!");
				}
			}
		}
	}

	public static void openGUIDuel(Player player, ArrayList<BetterItem> items, Player opponent) {

		Inventory inv = Bukkit.createInventory(player, 54, "Duel Class Selection");
		ItemStack is;

		for (int i = 0; i < items.size(); i++) {
			is = items.get(i).grab();
			inv.setItem(Corrupted.getInventorySpots()[i], is);
		}
		inv.setItem(45, IconMenu.setItemNameAndLore(new ItemStack(Material.REDSTONE_BLOCK, 1), "§c" + opponent.getName() + " is not ready", new String[0]));
		player.openInventory(inv);
	}

	public static ArrayList<BetterItem> formatItemArray(Player player) {
		ArrayList<BetterItem> items = new ArrayList<BetterItem>();
		BetterItem b;

		for (Classes c : Classes.values()) {
			if (c.equals(Classes.NONE))
				continue;
			Material displayMaterial = c.getDisplayMaterial();
			String name;
			short durability = c.getDisplayDurability();
			List<String> lore = new ArrayList<String>();
			for (String r : c.getDescription()) {
				lore.add(r);
			}
			if (c.isPrestige(player)) {
				lore.add("§d§lPRESTIGED");
			}
			if (c.hasKit(player) || player.isOp()) {
				name = "§2" + c.getName();
			} else {
				name = "§4" + c.getName();
			}
			String[] s = new String[lore.size()];
			for (int i = 0; i < lore.size(); i++) {
				s[i] = lore.get(i);
			}
			b = new BetterItem(displayMaterial, 1, name, durability, s);
			items.add(b);
		}
		return items;
	}
}
