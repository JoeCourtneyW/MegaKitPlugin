package com.sly.main.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sly.main.Server;
import com.sly.main.enums.CustomBlock;
import com.sly.main.enums.Trail;
import com.sly.main.enums.VanityArmor;
import com.sly.main.guis.IconMenus;
import com.sly.main.kits.Kits;
import com.sly.main.player.KitCustomizer;
import com.sly.main.player.PlayerModel;
import com.sly.main.resources.BetterItem;
import com.sly.main.resources.InventoryUtil;

public class InventoryListeners implements Listener
{
	@EventHandler(priority = EventPriority.HIGH)
	public void disableShiftClick(InventoryClickEvent e) {
		if (e.isShiftClick()) {
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onShopOpen(PlayerInteractEvent event) {
		if (event.getItem() != null && event.getItem().getType() == Material.GOLD_NUGGET) {
			IconMenus.mainShop.open(event.getPlayer());
		}
		if (event.getClickedBlock() != null && (event.getClickedBlock().getType() == Material.WOOD_BUTTON
				|| event.getClickedBlock().getType() == Material.STONE_BUTTON)) {
			event.setCancelled(true);
		}
	}
	
	// TODO: Fix every single one of these inventory open events
		@EventHandler
		public void onClassShopOpen(InventoryOpenEvent event) {
			if (event.getInventory().getTitle().contains("Class Shop")) {
				for (ItemStack i : event.getInventory().getContents()) {
					if (i != null && i.hasItemMeta() && i.getType() != Material.ARROW) {
						PlayerModel player = PlayerModel.getPlayerModel((Player) event.getPlayer());
						Kits c = Kits.valueOf(i.getItemMeta().getDisplayName().toUpperCase());
						ItemMeta im = i.getItemMeta();
						String name = "";
						List<String> lore = new ArrayList<String>();
						for (String r : c.getDescription()) {
							lore.add(r);
						}
						String lorePrestige = "";
						String loreSpacer = "§1 ";
						String lorePrice = "";
						if (c.isPrestige(player))
							lorePrestige = "§d§lPRESTIGED";
						if (c.hasKit(player)) {
							name = "§2" + c.getName();
							lorePrice = "§2§lPURCHASED";
						} else if (c.getPrice() < 0) {
							lorePrice = "§7§lLOCKED";
							if (c.equals(Kits.SHAMAN)) {
								name = "§7" + c.getName();
								lore.add("§bFound in the Slot Machine");
							} else if (c.equals(Kits.SPIDER)) {
								name = "§7" + c.getName();
								lore.add("§bSubscriber+ Only");
							} else if (c.equals(Kits.PIGMAN)) {
								name = "§7" + c.getName();
								lore.add("§bSubscriber++ Only");
							}
						} else if (c.hasEnoughCoins((Player) event.getPlayer())) {
							name = "§2" + c.getName();
							lorePrice = "§7Price: §2" + c.getPrice() + " coins";
						} else {
							name = "§4" + c.getName();
							lorePrice = "§7Price: §4" + c.getPrice() + " coins";
						}
						if (!lorePrestige.equalsIgnoreCase(""))
							lore.add(lorePrestige);
						lore.add(loreSpacer);
						lore.add(lorePrice);
						String[] s = new String[lore.size()];
						for (int r = 0; r < lore.size(); r++) {
							s[r] = lore.get(r);
						}
						im.setDisplayName(name);
						im.setLore(lore);
						i.setItemMeta(im);
					}
				}
			}
		}
	
	@EventHandler
	public void onSpinOpen(InventoryOpenEvent event) {
		if (event.getInventory().getTitle().contains("Purchase Spins")) {
			for (ItemStack i : event.getInventory().getContents()) {
				if (InventoryUtil.hasItemMeta(i) && i.getType() != Material.ARROW) {
					BetterItem item = new BetterItem().material(i.getType()).amount(i.getAmount());
					switch (i.getAmount())
					{
					case 1:
						if ((int) Server.getDatabaseCache().get(event.getPlayer().getUniqueId().toString())
								.get("Coins") >= 500)
							lorePrice = "§7Price: §a500 coins";
						else
							lorePrice = "§7Price: §c500 coins";
						break;
					case 3:
						if ((int) Server.getDatabaseCache().get(event.getPlayer().getUniqueId().toString())
								.get("Coins") >= 1463)
							lorePrice = "§7Price: §a1463 coins §d(2.5% OFF)";
						else
							lorePrice = "§7Price: §c1463 coins §d(2.5% OFF)";
						break;
					case 5:
						if ((int) Server.getDatabaseCache().get(event.getPlayer().getUniqueId().toString())
								.get("Coins") >= 2375)
							lorePrice = "§7Price: §a2375 coins §d(5% OFF)";
						else
							lorePrice = "§7Price: §c2375 coins §d(5% OFF)";
						break;
					case 10:
						if ((int) Server.getDatabaseCache().get(event.getPlayer().getUniqueId().toString())
								.get("Coins") >= 4625)
							lorePrice = "§7Price: §a4625 coins §d(7.5% OFF)";
						else
							lorePrice = "§7Price: §c4625 coins §d(7.5% OFF)";
						break;
					case 25:
						if ((int) Server.getDatabaseCache().get(event.getPlayer().getUniqueId().toString())
								.get("Coins") >= 11250)
							lorePrice = "§7Price: §a11250 coins §d(10% OFF)";
						else
							lorePrice = "§7Price: §c11250 coins §d(10% OFF)";
						break;
					}
					lore.add(loreSpacer);
					lore.add(lorePrice);
					String[] s = new String[lore.size()];
					for (int r = 0; r < lore.size(); r++) {
						s[r] = lore.get(r);
					}
					im.setLore(lore);
					i.setItemMeta(im);
				}
			}
		}
	}

	@EventHandler
	public void onBlockShopOpen(InventoryOpenEvent event) {
		if (event.getInventory().getTitle().contains("Block Custom")) {
			for (ItemStack i : event.getInventory().getContents()) {
				if (i != null && i.hasItemMeta() && i.getType() != Material.ARROW) {
					CustomBlock c = CustomBlock.fromInvName(i.getItemMeta().getDisplayName());
					ItemMeta im = i.getItemMeta();
					String name = "";
					List<String> lore = new ArrayList<String>();
					String loreSpacer = "§1 ";
					String lorePrice = "";
					if (c.hasBlock((Player) event.getPlayer())) {
						name = "§a" + c.getDisplayName();
						if (CustomBlock.getSelectedBlock((Player) event.getPlayer()).name().equalsIgnoreCase(c.name()))
							lorePrice = "§aSELECTED";
						else
							lorePrice = "§aClick to Select!";

					} else if (c.getPrice() < 0) {
						lorePrice = "§cLOCKED";
						if (c.getPrice() < -1) {
							name = "§c" + c.getDisplayName();
							lore.add("§bPremium Only");
						} else {
							name = "§c" + c.getDisplayName();
							lore.add("§bFound in the Slot Machine");
						}
					} else if (c.hasEnoughCoins((Player) event.getPlayer())) {
						name = "§a" + c.getDisplayName();
						lorePrice = "§7Price: §a" + c.getPrice() + " coins";
					} else {
						name = "§c" + c.getDisplayName();
						lorePrice = "§7Price: §c" + c.getPrice() + " coins";
					}
					lore.add(loreSpacer);
					lore.add(lorePrice);
					String[] s = new String[lore.size()];
					for (int r = 0; r < lore.size(); r++) {
						s[r] = lore.get(r);
					}
					im.setDisplayName(name);
					im.setLore(lore);
					i.setItemMeta(im);
				}
			}
		}
	}

	@EventHandler
	public void onVanityShopOpen(InventoryOpenEvent event) {
		if (event.getInventory().getTitle().contains("Vanity")) {
			for (ItemStack i : event.getInventory().getContents()) {
				if (i != null && i.hasItemMeta() && i.getType() != Material.ARROW) {
					VanityArmor c = VanityArmor.fromInvName(i.getItemMeta().getDisplayName());
					ItemMeta im = i.getItemMeta();
					String name = "";
					List<String> lore = new ArrayList<String>();
					String loreSpacer = "§1 ";
					String lorePrice = "";
					if (c.hasVanityArmor((Player) event.getPlayer())) {
						name = "§a" + c.getDisplayName();
						if (VanityArmor.getSelectedVanityArmor((Player) event.getPlayer()).name()
								.equalsIgnoreCase(c.name()))
							lorePrice = "§aSELECTED";
						else
							lorePrice = "§aClick to Select!";

					} else {
						lorePrice = "§cLOCKED";
						name = "§c" + c.getDisplayName();
					}
					lore.add(loreSpacer);
					lore.add(lorePrice);
					String[] s = new String[lore.size()];
					for (int r = 0; r < lore.size(); r++) {
						s[r] = lore.get(r);
					}
					im.setDisplayName(name);
					im.setLore(lore);
					i.setItemMeta(im);
				}
			}
		}
	}
	@EventHandler
	public void onTrailShopOpen(InventoryOpenEvent event) {
		if (event.getInventory().getTitle().contains("Trail")) {
			for (ItemStack i : event.getInventory().getContents()) {
				if (i != null && i.hasItemMeta() && i.getType() != Material.ARROW) {
					Trail c = Trail.fromInvName(i.getItemMeta().getDisplayName());
					ItemMeta im = i.getItemMeta();
					String name = "";
					List<String> lore = new ArrayList<String>();
					String loreSpacer = "§1 ";
					String lorePrice = "";
					if (c.hasTrail((Player) event.getPlayer())) {
						name = "§a" + c.getDisplayName();
						if (Trail.getSelectedTrail((Player) event.getPlayer()).name().equalsIgnoreCase(c.name()))
							lorePrice = "§aSELECTED";
						else
							lorePrice = "§aClick to Select!";

					} else {
						lorePrice = "§cLOCKED";
						name = "§c" + c.getDisplayName();
					}
					lore.add(loreSpacer);
					lore.add(lorePrice);
					String[] s = new String[lore.size()];
					for (int r = 0; r < lore.size(); r++) {
						s[r] = lore.get(r);
					}
					im.setDisplayName(name);
					im.setLore(lore);
					i.setItemMeta(im);
				}
			}
		}
	}

	private HashMap<PlayerModel, Integer> customizing = new HashMap<PlayerModel, Integer>();

	@EventHandler
	public void onKitCustomizer(InventoryClickEvent event) {
		if (event.getClickedInventory() == null) // Make sure they actually clicked an inventory
			return;

		if (!event.getInventory().getTitle().contains("Customize "))
			return;
		else // If the player has the customizer inventory open, make sure to cancel
			event.setCancelled(true);

		if (!event.getClickedInventory().getTitle().contains("Customize ")) // If the player clicked the customizer inventory, check for events
			return;

		if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) // Make sure they clicked an item
			return;
		PlayerModel player = PlayerModel.getPlayerModel((Player) event.getWhoClicked());
		if (event.getRawSlot() < 9) { // They are selecting item spots
			if (customizing.containsKey(event.getWhoClicked().getUniqueId().toString())) { // If they are selecting the second position
				int oldSlot = customizing.get(event.getWhoClicked().getUniqueId().toString());
				customizing.remove(event.getWhoClicked().getUniqueId().toString());
				ItemStack clicked = event.getCurrentItem().clone();
				event.getInventory().setItem(event.getRawSlot(), event.getInventory().getItem(oldSlot));
				event.getInventory().setItem(oldSlot, clicked);
				event.getClickedInventory().setItem(oldSlot + 9, new BetterItem().material(Material.STAINED_GLASS_PANE)
						.data((short) 15).displayName(" ").grab());
			} else { // They are selecting the first item
				customizing.put(player, event.getRawSlot());
				event.getClickedInventory().setItem(event.getRawSlot() + 9, new BetterItem()
						.material(Material.STAINED_GLASS_PANE).data((short) 15).displayName(" ").grab());
				player.sendPrefixedMessage("§eSelect the slot where you want to move this item to");
			}
		} else if (event.getRawSlot() > 45) { // They are finalizing this change
			if (event.getCurrentItem().getType() == Material.DIAMOND_BLOCK) {
				Kits kit = Kits.getKitFromString(event.getClickedInventory().getTitle().split(" ")[1]);
				KitCustomizer customizer = player.getKitCustomizer();
				Integer[] spots = new Integer[9];
				for (int i = 0; i < spots.length; i++) {
					Integer newSpot = Integer.parseInt(event.getInventory().getItem(i).getItemMeta().getLore().get(0));
					spots[i] = newSpot;
				}
				customizer.setCustomization(kit, spots);
				customizer.pushToDatabase();
				IconMenus.kitCustomizer.open((Player) event.getWhoClicked());
			} else if (event.getCurrentItem().getType() == Material.ARROW) {
				IconMenus.kitCustomizer.open((Player) event.getWhoClicked());
			}
		}
	}

}
