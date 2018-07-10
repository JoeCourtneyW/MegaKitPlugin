package com.sly.main.guis.kitselector;

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

import com.sly.main.Server;
import com.sly.main.kits.Kits;
import com.sly.main.resources.BetterItem;
import com.sly.main.resources.LocationUtil;
import com.sly.main.resources.MathUtil;
import com.sly.main.server.Chat;

public class KitSelectorListener implements Listener
{
	private ArrayList<BetterItem> itemsTest;

	public KitSelectorListener() {
		itemsTest = new ArrayList<BetterItem>();

	}

	@EventHandler
	public void onSelectorOpen(PlayerInteractEvent e) {

		if (e.getItem() != null && e.getItem().getType() == Material.FEATHER) {
			if (!Server.getTeams().containsKey(e.getPlayer().getName())) {
				new KitSelector(itemsTest, e.getPlayer());
				e.setCancelled(true);
			} else {
				Chat.sendPlayerPrefixedMessage(e.getPlayer(), "You may not join FFA when you are in a team");
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPurchase(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();

		if (e.getClickedInventory() != null && e.getClickedInventory().getTitle().equals("Class Selection")) {
			e.setCancelled(true);

			if ((e.getCurrentItem()) != null && e.getCurrentItem().hasItemMeta()
					&& e.getCurrentItem().getItemMeta().hasDisplayName()) {
				if (Kits.valueOf(e.getCurrentItem().getItemMeta().getDisplayName().substring(2).toUpperCase()).hasKit(p)
						|| p.isOp()) {
					KitSelector.setKit(e.getCurrentItem().getItemMeta().getDisplayName().substring(2), p);
					p.updateInventory();
					Chat.sendPlayerPrefixedMessage(p, "Teleporting...");
					Location l1 = new Location(Bukkit.getWorld("Nube"), -381.5, 42, 230.5, 0, 0);
					Location l2 = new Location(Bukkit.getWorld("Nube"), -346.5, 42, 193.5, 0, 0);
					Location l3 = new Location(Bukkit.getWorld("Nube"), -400.5, 39, 263.5, 0, 0);
					Location l4 = new Location(Bukkit.getWorld("Nube"), -401.5, 37, 186.5, 0, 0);
					int i = MathUtil.rand(1, 4);
					if (i == 1) {
						p.teleport(LocationUtil.getSafeDestination(l1.add(0, 1, 0)));
					} else if (i == 2) {
						p.teleport(LocationUtil.getSafeDestination(l2.add(0, 1, 0)));
					} else if (i == 3) {
						p.teleport(LocationUtil.getSafeDestination(l3.add(0, 1, 0)));
					} else {
						p.teleport(LocationUtil.getSafeDestination(l4.add(0, 1, 0)));
					}
				} else {
					Chat.sendPlayerPrefixedMessage(p, "You do not own this kit!");
				}
			}
		}
	}
}