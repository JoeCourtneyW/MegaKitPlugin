package com.sly.main.guis.kitselector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.sly.main.Server;
import com.sly.main.enums.CustomBlock;
import com.sly.main.gametype.GameType;
import com.sly.main.kits.Kits;
import com.sly.main.player.PlayerModel;
import com.sly.main.resources.BetterItem;
import com.sly.main.resources.IconMenu;
import com.sly.main.resources.InventoryUtil;
import com.sly.main.resources.MathUtil;

public class KitSelector
{
	private PlayerModel player;
	private ArrayList<ItemStack> items;

	public KitSelector(PlayerModel player) {
		this.player = player;

		populateKitDisplays();

	}

	// TODO: If price is too high change what the item is to the unavailable icon
	private void populateKitDisplays() {
		items = new ArrayList<ItemStack>();
		for (Kits kit : Kits.getKits()) {
			items.add(kit.getDisplayItemStack(player));
		}
	}


	public void setKit(Kits kit) {
		player.getPlayer().getInventory().clear();
		player.getPlayer().getInventory().setArmorContents(new ItemStack[4]);
		player.getPlayer().closeInventory();

		HashMap<String, ItemStack> kitItems = kit.getListenerClass().getKitItems();

		Integer[] spots = player.getKitCustomizer().getCustomization(kit);

		for (int i = 0; i < 9; i++) {
			ItemStack item = kitItems.get(i + "");
			if (item == null || item.getType() == Material.AIR) // If it's a blank space, don't have to add it
				return;
			if (item.getType() == Material.WOOD)
				player.getPlayer().getInventory().setItem(spots[i],
						CustomBlock.getSelectedBlock(player.getPlayer()).getItemStack());
			else
				player.getPlayer().getInventory().setItem(spots[i], item);

		}

		if (kitItems.get("Boots") != null)
			player.getPlayer().getInventory().setBoots(kitItems.get("Boots"));
		if (kitItems.get("Leggings") != null)
			player.getPlayer().getInventory().setLeggings(kitItems.get("Leggings"));
		if (kitItems.get("Chestplate") != null)
			player.getPlayer().getInventory().setChestplate(kitItems.get("Chestplate"));
		if (kitItems.get("Helmet") != null)
			player.getPlayer().getInventory().setHelmet(kitItems.get("Helmet"));

		player.getPlayer().updateInventory();

		player.sendPrefixedMessage("§aYou are playing as: §b" + kit);

		player.setKit(kit);
		player.setChosenKit(Kits.NONE);

		if (kit.isPrestige(player)) {
			player.getPlayer().setMaxHealth(44.0);
			player.getPlayer().setHealth(44.0);
		} else {
			player.setFullHealth();
		}

		player.getPlayer().setFallDistance(0);
		player.getPlayer().setAllowFlight(false);
		player.getPlayer().setFlying(false);
		// TagAPI.refreshPlayer(p); Used for nicknames TODO
	}

	@EventHandler
	public void kitSelectorListener1v1(InventoryClickEvent event) {
		if (event.getClickedInventory() == null // Check if it's the right inventory
				|| !event.getClickedInventory().getTitle().equals("1v1 Class Selection"))
			return;
		event.setCancelled(true);
		PlayerModel player = PlayerModel.getPlayerModel((Player) event.getWhoClicked());
		if (InventoryUtil.hasItemMeta(event.getCurrentItem())) { // Clicked on a class item
			Kits kit = Kits.getKitFromString(event.getCurrentItem().getItemMeta().getDisplayName().substring(2));
			if (kit.hasKit(player) || player.getPlayer().isOp()) { // Make sure they own the kit

				player.setChosenKit(kit);
				Server.getQueue(GameType.ONEvONE).addToQueue(Arrays.asList(player));
				player.sendPrefixedMessage("§eQueued for Ranked 1v1");

				player.getPlayer().closeInventory();
				player.setQueuedInventory();
			} else {
				player.sendPrefixedMessage("You do not own this kit");
			}
		}
	}

	@EventHandler
	public void kitSelectorListener2v2(InventoryClickEvent event) {
		if (event.getClickedInventory() == null
				|| !event.getClickedInventory().getTitle().equals("2v2 Class Selection"))
			return; // Make sure they're actually clicking in this inventory
		event.setCancelled(true);
		PlayerModel player = PlayerModel.getPlayerModel((Player) event.getWhoClicked());
		if (InventoryUtil.hasItemMeta(event.getCurrentItem())) { // If the player selected a kit item, duh

			Kits kit = Kits.getKitFromString(event.getCurrentItem().getItemMeta().getDisplayName().substring(2));
			if (kit.hasKit(player) || player.getPlayer().isOp()) { // Has permission for this kit

				if (event.getInventory().getItem(45) != null // Check if their teammate has readied up yet
						&& event.getInventory().getItem(45).getType() == Material.REDSTONE_BLOCK) {
					player.sendPrefixedMessage("§eYou have readied up, waiting for teammate...");
					PlayerModel squadmate = player.getSquad().getOtherPlayer(player);// Grab the other player in the squad

					if (squadmate.getPlayer().getOpenInventory().getTopInventory().getTitle()
							.equalsIgnoreCase("2v2 Class Selection")) { // If they have the class selector open still
						ItemStack emeraldBlock = new BetterItem().material(Material.EMERALD_BLOCK)
								.addLoreLine("§7" + kit.getName()).displayName("§aTeammate is ready!").grab();
						squadmate.getPlayer().getOpenInventory().getTopInventory().setItem(45, emeraldBlock);

						player.setChosenKit(kit);
					} else { // Just close the queue inventory if the other person closed it
						player.getPlayer().closeInventory();
					}
				} else {
					for (PlayerModel squadMember : player.getSquad().getSquadMembers()) {
						squadMember.getPlayer().closeInventory();
						squadMember.sendMessage("§eYour squad is queued for Ranked 2v2");
						squadMember.setQueuedInventory();
					}
					Server.getQueue(GameType.TWOvTWO).addToQueue(player.getSquad().getSquadMembers());
				}

			} else {
				player.sendPrefixedMessage("You do not own this kit");
			}
		}
	}

	public void openGUI(GameType gameType) {
		Inventory inv = Bukkit.createInventory(player.getPlayer(), 54, gameType.getDisplayName() + " Class Selection");

		for (int i = 0; i < Kits.getKits().length; i++)
			inv.setItem(getDisplaySlots(54)[i], Kits.getKits()[i].getDisplayItemStack(player));

		if (gameType == GameType.TWOvTWO)
			inv.setItem(45, IconMenu.setItemNameAndLore(new ItemStack(Material.REDSTONE_BLOCK, 1),
					"§cTeammate is not ready", new String[0]));
		player.getPlayer().openInventory(inv);
	}

}
