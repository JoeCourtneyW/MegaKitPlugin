package com.sly.main.guis;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import com.sly.main.Server;
import com.sly.main.enums.CustomBlock;
import com.sly.main.enums.Trail;
import com.sly.main.enums.VanityArmor;
import com.sly.main.kits.Kits;
import com.sly.main.player.PlayerModel;
import com.sly.main.resources.BetterItem;
import com.sly.main.resources.IconMenu;
import com.sly.main.resources.IconMenu.OptionClickEvent;
import com.sly.main.server.Chat;

public class IconMenus
{

	private Plugin p;
	public static IconMenu mainShop;
	public static IconMenu classShop;
	public static IconMenu kitCustomizer;
	public static IconMenu blockCustomizer;
	public static IconMenu spinsPurchaser;
	public static IconMenu trailCustomizer;
	public static IconMenu vanityArmorCustomizer;

	public IconMenus(Plugin p) {
		this.p = p;
		classShop = registerClassShop();
		kitCustomizer = registerKitCustomizer();
		blockCustomizer = registerBlockCustomizer();
		spinsPurchaser = registerSpinsPurchaser();
		trailCustomizer = registerTrailCustomizer();
		vanityArmorCustomizer = registerVanityArmorCustomizer();
	}

	private IconMenu registerClassShop() {
		IconMenu i = new IconMenu("Class Shop", 54, new IconMenu.OptionClickEventHandler() {
			@Override
			public void onOptionClick(OptionClickEvent e) {
				InventoryClickEvent event = e.getEvent();
				PlayerModel p = PlayerModel.getPlayerModel((Player) event.getWhoClicked());
				e.setWillClose(false);
				if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta()) {
					if (event.getCurrentItem().getType() == Material.ARROW) {
						mainShop.open(p.getPlayer());
						return;
					}
					String kitName = event.getCurrentItem().getItemMeta().getDisplayName();
					if (Kits.valueOf(kitName.substring(2).toUpperCase()) != null) {
						Kits c = Kits.valueOf(kitName.substring(2).toUpperCase());
						if (!c.hasKit(p.getPlayer())) {
							if (c == Kits.PIGMAN || c == Kits.SHAMAN || c == Kits.SPIDER) {
								Chat.sendPlayerPrefixedMessage(p.getPlayer(),
										"You don't have " + c.getName() + " unlocked.");
							} else {
								if (c.hasEnoughCoins(p.getPlayer())) {
									c.setKit(p.getPlayer(), true);
									Chat.sendPlayerPrefixedMessage(p.getPlayer(), "You bought " + c.getName() + "!");
									classShop.open(p.getPlayer());
									int bal = (int) Server.getDatabaseCache().get(p.getUUID().toString())
											.get("Coins");
									Server.getDatabaseCache().get(p.getUUID().toString()).remove("Coins");
									Server.getDatabaseCache().get(p.getUUID().toString()).put("Coins",
											bal - c.getPrice());
									p.updateScoreboard();
								} else {
									Chat.sendPlayerPrefixedMessage(p.getPlayer(),
											"You don't have enough coins for " + c.getName() + ".");
								}
							}
						} else {
							Chat.sendPlayerPrefixedMessage(p.getPlayer(), "You already have " + c.getName() + ".");
						}
					}
				}
			}

		}, p);
		BetterItem b;
		for (int r = 0; r < Kits.values().length; r++) {
			Kits c = Kits.values()[r];
			if (c.equals(Kits.NONE))
				continue;
			Material displayMaterial = c.getDisplayMaterial();
			String name = c.getName();
			short durability = c.getDisplayDurability();

			b = new BetterItem(displayMaterial, 1, name, durability);
			i.setOption(Server.getInventorySpots()[r], b.grab());
		}
		i.setOption(48, IconMenu.setItemNameAndLore(new ItemStack(Material.ARROW, 1), "§bGo back", new String[0]));
		return i;
	}

	private IconMenu registerKitCustomizer() {
		IconMenu i = new IconMenu("Class Customizer", 54, new IconMenu.OptionClickEventHandler() {
			@Override
			public void onOptionClick(OptionClickEvent e) {
				InventoryClickEvent event = e.getEvent();
				e.setWillClose(false);
				if (event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta()) {
					String kitName = event.getCurrentItem().getItemMeta().getDisplayName();
					if (event.getCurrentItem().getType() == Material.ARROW) {
						mainShop.open((Player) event.getWhoClicked());
						return;
					}
					if (Kits.valueOf(kitName.substring(2).toUpperCase()) != null) {
						Kits c = Kits.valueOf(kitName.substring(2).toUpperCase());
						Player player = (Player) event.getWhoClicked();
						if (c.hasKit(player)) {
							Inventory i = Bukkit.createInventory(null, 54, "Customize " + c.getName());
							@SuppressWarnings("unchecked")
							HashMap<String, ItemStack> rItems = (HashMap<String, ItemStack>) Server.getKitItems()
									.get(kitName.substring(2)).clone();
							String custom = Server.getKitCustomizerCache()
									.get(event.getWhoClicked().getUniqueId().toString()).get(c.getName());
							for (int r = 5; r < rItems.size() + 1; r++) {
								int customSlot = Integer.parseInt(custom.split("\\|")[r - 5]);
								int baseSlot = r - 5;
								if (r == (rItems.size() + 1)) {
									ItemStack ir = IconMenu.setItemNameAndLore(
											new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 6), " ",
											new String[0]);
									ItemMeta im = ir.getItemMeta();
									im.setLore(Arrays.asList("" + customSlot));
									ir.setItemMeta(im);
									i.setItem(baseSlot, ir);
									continue;
								}

								if (rItems.get(customSlot + "") != null
										&& rItems.get(customSlot + "").getType() != Material.AIR) {
									ItemStack ir = rItems.get(customSlot + "").clone();
									ItemMeta im = ir.getItemMeta();
									im.setLore(Arrays.asList("" + (customSlot)));
									ir.setItemMeta(im);
									i.addItem(ir);
								} else {
									ItemStack ir = IconMenu.setItemNameAndLore(
											new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 6), " ",
											new String[0]);
									ItemMeta im = ir.getItemMeta();
									im.setLore(Arrays.asList("" + customSlot));
									ir.setItemMeta(im);
									i.setItem(baseSlot, ir);
								}
							}
							for (int se = 0; se < 9; se++) {
								if (i.getItem(se) != null && i.getItem(se).getType() != Material.AIR)
									;
								else {
									ItemStack ir = IconMenu.setItemNameAndLore(
											new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 6), " ",
											new String[0]);
									ItemMeta im = ir.getItemMeta();
									im.setLore(Arrays.asList("" + (se)));
									ir.setItemMeta(im);
									i.setItem(se, ir);
								}
							}
							for (int r = 9; r < 45; r++) {
								i.setItem(r, IconMenu.setItemNameAndLore(
										new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), " ", new String[0]));
							}
							i.setItem(48, IconMenu.setItemNameAndLore(new ItemStack(Material.ARROW, 1), "§bGo back",
									new String[0]));
							i.setItem(50, IconMenu.setItemNameAndLore(new ItemStack(Material.DIAMOND_BLOCK, 1),
									"§bSave changes", new String[0]));
							event.getWhoClicked().openInventory(i);
						} else {
							Chat.sendPlayerPrefixedMessage(player, "You dont own " + c.getName() + "!");
						}
					}
				}
			}

		}, p);
		BetterItem b;
		for (int r = 0; r < Kits.values().length; r++) {
			Kits c = Kits.values()[r];
			if (c.equals(Kits.NONE))
				continue;
			Material displayMaterial = c.getDisplayMaterial();
			String name = "§b" + c.getName();
			short durability = c.getDisplayDurability();

			b = new BetterItem(displayMaterial, 1, name, durability);
			i.setOption(Server.getInventorySpots()[r], b.grab());
		}
		i.setOption(48, IconMenu.setItemNameAndLore(new ItemStack(Material.ARROW, 1), "§bGo back", new String[0]));
		return i;
	}

	private IconMenu registerBlockCustomizer() {
		final int positions[] = { 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33 };
		IconMenu i = new IconMenu("Block Customizer", 54, new IconMenu.OptionClickEventHandler() {
			@Override
			public void onOptionClick(OptionClickEvent e) {
				e.setWillClose(false);
				InventoryClickEvent event = e.getEvent();
				PlayerModel p = PlayerModel.getPlayerModel((Player) event.getWhoClicked());
				if (event.getCurrentItem() != null) {
					if (event.getCurrentItem().getType() == Material.ARROW) {
						mainShop.open(p.getPlayer());
						return;
					}
					CustomBlock cb = CustomBlock
							.fromInvName(event.getCurrentItem().getItemMeta().getDisplayName().substring(2));
					if (cb.hasBlock(p.getPlayer())) {
						cb.setSelectedBlock(p.getPlayer());
						Chat.sendPlayerPrefixedMessage(p.getPlayer(),
								"§eYou have changed your block to §b" + cb.getDisplayName());
						blockCustomizer.open(p.getPlayer());
					} else if (cb.hasEnoughCoins(p.getPlayer())) {
						if (cb.getPrice() > 0) {
							cb.setSelectedBlock(p.getPlayer());
							cb.setBlock(p.getPlayer(), true);
							Chat.sendPlayerPrefixedMessage(p.getPlayer(),
									"§eYou have bought the §b" + cb.getDisplayName() + " §eblock");
							Server.getDatabaseCache().get(p.getPlayer().getUniqueId().toString()).put(
									"Coins",
									(int) Server.getDatabaseCache()
											.get(p.getPlayer().getUniqueId().toString()).get("Coins")
											- cb.getPrice());
							p.updateScoreboard();
							blockCustomizer.open(p.getPlayer());
						} else {
							Chat.sendPlayerPrefixedMessage(p.getPlayer(),
									"§eYou don't have §b" + cb.getDisplayName() + " §eunlocked");
						}
					} else {
						Chat.sendPlayerPrefixedMessage(p.getPlayer(),
								"§eYou don't have §b" + cb.getDisplayName() + " §eunlocked");
					}
				}
			}

		}, p);
		for (int i2 = 0; i2 < CustomBlock.values().length; i2++) {
			CustomBlock cb = CustomBlock.values()[i2];
			i.setOption(positions[i2], IconMenu.setItemNameAndLore(
					new ItemStack(cb.getMaterial(), 1, cb.getDurability()), cb.getDisplayName(), new String[0]));
		}
		i.setOption(48, IconMenu.setItemNameAndLore(new ItemStack(Material.ARROW, 1), "§bGo back", new String[0]));
		return i;
	}

	private IconMenu registerSpinsPurchaser() {
		IconMenu i = new IconMenu("Purchase Spins", 54, new IconMenu.OptionClickEventHandler() {
			@Override
			public void onOptionClick(OptionClickEvent e) {
				e.setWillClose(false);
				InventoryClickEvent event = e.getEvent();
				PlayerModel p = PlayerModel.getPlayerModel((Player) event.getWhoClicked());
				if (event.getCurrentItem() != null) {
					if (event.getCurrentItem().getType() == Material.ARROW) {
						mainShop.open((Player) event.getWhoClicked());
						return;
					}
					switch (event.getCurrentItem().getAmount())
					{
					case 1:
						if ((int) Server.getDatabaseCache().get(event.getWhoClicked().getUniqueId().toString())
								.get("Coins") >= 500) {
							Chat.sendPlayerPrefixedMessage(event.getWhoClicked(), "§eYou have bought §b1 Spin");
							Server.getDatabaseCache().get(event.getWhoClicked().getUniqueId().toString())
									.put("Spins",
											(int) Server.getDatabaseCache()
													.get(event.getWhoClicked().getUniqueId().toString()).get("Spins")
													+ 1);
							Server.getDatabaseCache().get(event.getWhoClicked().getUniqueId().toString())
									.put("Coins",
											(int) Server.getDatabaseCache()
													.get(event.getWhoClicked().getUniqueId().toString()).get("Coins")
													- 500);
						} else {
							Chat.sendPlayerPrefixedMessage(event.getWhoClicked(),
									"§eYou dont have enough coins to buy 1 Spin");
						}
						break;
					case 3:
						if ((int) Server.getDatabaseCache().get(event.getWhoClicked().getUniqueId().toString())
								.get("Coins") >= 1463) {
							Chat.sendPlayerPrefixedMessage(event.getWhoClicked(), "§eYou have bought §b3 Spins");
							Server.getDatabaseCache().get(event.getWhoClicked().getUniqueId().toString())
									.put("Spins",
											(int) Server.getDatabaseCache()
													.get(event.getWhoClicked().getUniqueId().toString()).get("Spins")
													+ 3);
							Server.getDatabaseCache().get(event.getWhoClicked().getUniqueId().toString())
									.put("Coins",
											(int) Server.getDatabaseCache()
													.get(event.getWhoClicked().getUniqueId().toString()).get("Coins")
													- 1463);
						} else {
							Chat.sendPlayerPrefixedMessage(event.getWhoClicked(),
									"§eYou dont have enough coins to buy 3 Spins");
						}
						break;
					case 5:
						if ((int) Server.getDatabaseCache().get(event.getWhoClicked().getUniqueId().toString())
								.get("Coins") >= 2375) {
							Chat.sendPlayerPrefixedMessage(event.getWhoClicked(), "§eYou have bought §b5 Spins");
							Server.getDatabaseCache().get(event.getWhoClicked().getUniqueId().toString())
									.put("Spins",
											(int) Server.getDatabaseCache()
													.get(event.getWhoClicked().getUniqueId().toString()).get("Spins")
													+ 5);
							Server.getDatabaseCache().get(event.getWhoClicked().getUniqueId().toString())
									.put("Coins",
											(int) Server.getDatabaseCache()
													.get(event.getWhoClicked().getUniqueId().toString()).get("Coins")
													- 2375);
						} else {
							Chat.sendPlayerPrefixedMessage(event.getWhoClicked(),
									"§eYou dont have enough coins to buy 5 Spins");
						}
						break;
					case 10:
						if ((int) Server.getDatabaseCache().get(event.getWhoClicked().getUniqueId().toString())
								.get("Coins") >= 4625) {
							Chat.sendPlayerPrefixedMessage(event.getWhoClicked(), "§eYou have bought §b10 Spin");
							Server.getDatabaseCache().get(event.getWhoClicked().getUniqueId().toString())
									.put("Spins",
											(int) Server.getDatabaseCache()
													.get(event.getWhoClicked().getUniqueId().toString()).get("Spins")
													+ 10);
							Server.getDatabaseCache().get(event.getWhoClicked().getUniqueId().toString())
									.put("Coins",
											(int) Server.getDatabaseCache()
													.get(event.getWhoClicked().getUniqueId().toString()).get("Coins")
													- 4625);
						} else {
							Chat.sendPlayerPrefixedMessage(event.getWhoClicked(),
									"§eYou dont have enough coins to buy 10 Spins");
						}
						break;
					case 25:
						if ((int) Server.getDatabaseCache().get(event.getWhoClicked().getUniqueId().toString())
								.get("Coins") >= 11250) {
							Chat.sendPlayerPrefixedMessage(event.getWhoClicked(), "§eYou have bought §b25 Spins");
							Server.getDatabaseCache().get(event.getWhoClicked().getUniqueId().toString())
									.put("Spins",
											(int) Server.getDatabaseCache()
													.get(event.getWhoClicked().getUniqueId().toString()).get("Spins")
													+ 25);
							Server.getDatabaseCache().get(event.getWhoClicked().getUniqueId().toString())
									.put("Coins",
											(int) Server.getDatabaseCache()
													.get(event.getWhoClicked().getUniqueId().toString()).get("Coins")
													- 11250);
						} else {
							Chat.sendPlayerPrefixedMessage(event.getWhoClicked(),
									"§eYou dont have enough coins to buy 25 Spins");
						}
						break;
					default:
						break;
					}
					Server.getInstance().updateDatabase((Player) event.getWhoClicked());
					p.updateScoreboard();
					spinsPurchaser.open((Player) event.getWhoClicked());
				}
			}

		}, p);
		i.setOption(11, IconMenu.setItemNameAndLore(new ItemStack(Material.TRIPWIRE_HOOK, 1), "§b1 Spin",
				(String[]) Arrays.asList("§7Price: §c250").toArray()));
		i.setOption(13, IconMenu.setItemNameAndLore(new ItemStack(Material.TRIPWIRE_HOOK, 3), "§b3 Spins",
				(String[]) Arrays.asList("§7Price: §c731").toArray()));
		i.setOption(15, IconMenu.setItemNameAndLore(new ItemStack(Material.TRIPWIRE_HOOK, 5), "§b5 Spins",
				(String[]) Arrays.asList("§7Price: §c1187").toArray()));
		i.setOption(30, IconMenu.setItemNameAndLore(new ItemStack(Material.TRIPWIRE_HOOK, 10), "§b10 Spins",
				(String[]) Arrays.asList("§7Price: §c2312").toArray()));
		i.setOption(32, IconMenu.setItemNameAndLore(new ItemStack(Material.TRIPWIRE_HOOK, 25), "§b25 Spins",
				(String[]) Arrays.asList("§7Price: §c5625").toArray()));
		i.setOption(48, IconMenu.setItemNameAndLore(new ItemStack(Material.ARROW, 1), "§bGo back", new String[0]));
		return i;
	}

	private IconMenu registerTrailCustomizer() {
		IconMenu i = new IconMenu("Trail Customizer", 54, new IconMenu.OptionClickEventHandler() {
			@Override
			public void onOptionClick(OptionClickEvent e) {
				e.setWillClose(false);
				InventoryClickEvent event = e.getEvent();
				PlayerModel p = PlayerModel.getPlayerModel((Player) event.getWhoClicked());
				if (event.getCurrentItem() != null) {
					if (event.getCurrentItem().getType() == Material.ARROW) {
						mainShop.open((Player) event.getWhoClicked());
						return;
					}
					Trail cb = Trail.fromInvName(event.getCurrentItem().getItemMeta().getDisplayName().substring(2));
					if (cb.hasTrail((Player) event.getWhoClicked())) {
						cb.setSelectedTrail((Player) event.getWhoClicked());
						Chat.sendPlayerPrefixedMessage(event.getWhoClicked(),
								"§eYou have changed your trail to §b" + cb.getDisplayName());
						trailCustomizer.open((Player) event.getWhoClicked());
					} else {
						Chat.sendPlayerPrefixedMessage(event.getWhoClicked(),
								"§eYou don't have §b" + cb.getDisplayName() + " §eunlocked");
					}
				}
			}

		}, p);
		for (int i2 = 0; i2 < Trail.values().length; i2++) {
			Trail cb = Trail.values()[i2];
			i.setOption(Server.getInventorySpots()[i2], IconMenu
					.setItemNameAndLore(new ItemStack(cb.getMaterial(), 1), cb.getDisplayName(), new String[0]));
		}
		i.setOption(48, IconMenu.setItemNameAndLore(new ItemStack(Material.ARROW, 1), "§bGo back", new String[0]));
		return i;
	}

	private IconMenu registerVanityArmorCustomizer() {
		final int positions[] = { 20, 21, 22, 23, 24 };
		IconMenu i = new IconMenu("Vanity Armor Customizer", 54, new IconMenu.OptionClickEventHandler() {
			@Override
			public void onOptionClick(OptionClickEvent e) {
				e.setWillClose(false);
				InventoryClickEvent event = e.getEvent();
				PlayerModel p = PlayerModel.getPlayerModel((Player) event.getWhoClicked());
				if (event.getCurrentItem() != null) {
					if (event.getCurrentItem().getType() == Material.ARROW) {
						mainShop.open((Player) event.getWhoClicked());
						return;
					}
					VanityArmor cb = VanityArmor
							.fromInvName(event.getCurrentItem().getItemMeta().getDisplayName().substring(2));
					if (cb.hasVanityArmor((Player) event.getWhoClicked())) {
						cb.setSelectedVanityArmor((Player) event.getWhoClicked());
						Chat.sendPlayerPrefixedMessage(event.getWhoClicked(),
								"§eYou have changed your vanity armor to §b" + cb.getDisplayName());
						vanityArmorCustomizer.open((Player) event.getWhoClicked());
						switch (VanityArmor.getSelectedVanityArmor((Player) event.getWhoClicked()))
						{
						case NONE:
							((Player) event.getWhoClicked()).getInventory().setArmorContents(new ItemStack[4]);
							break;
						case CHAINMAIL:
							((Player) event.getWhoClicked()).getInventory().setChestplate(IconMenu.setItemNameAndLore(
									new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1), "§bVanity Armor", new String[0]));
							((Player) event.getWhoClicked()).getInventory().setLeggings(IconMenu.setItemNameAndLore(
									new ItemStack(Material.CHAINMAIL_LEGGINGS, 1), "§bVanity Armor", new String[0]));
							((Player) event.getWhoClicked()).getInventory().setBoots(IconMenu.setItemNameAndLore(
									new ItemStack(Material.CHAINMAIL_BOOTS, 1), "§bVanity Armor", new String[0]));
							break;
						case IRON:
							((Player) event.getWhoClicked()).getInventory().setChestplate(IconMenu.setItemNameAndLore(
									new ItemStack(Material.IRON_CHESTPLATE, 1), "§bVanity Armor", new String[0]));
							((Player) event.getWhoClicked()).getInventory().setLeggings(IconMenu.setItemNameAndLore(
									new ItemStack(Material.IRON_LEGGINGS, 1), "§bVanity Armor", new String[0]));
							((Player) event.getWhoClicked()).getInventory().setBoots(IconMenu.setItemNameAndLore(
									new ItemStack(Material.IRON_BOOTS, 1), "§bVanity Armor", new String[0]));
							break;
						case GOLD:
							((Player) event.getWhoClicked()).getInventory().setChestplate(IconMenu.setItemNameAndLore(
									new ItemStack(Material.GOLD_CHESTPLATE, 1), "§bVanity Armor", new String[0]));
							((Player) event.getWhoClicked()).getInventory().setLeggings(IconMenu.setItemNameAndLore(
									new ItemStack(Material.GOLD_LEGGINGS, 1), "§bVanity Armor", new String[0]));
							((Player) event.getWhoClicked()).getInventory().setBoots(IconMenu.setItemNameAndLore(
									new ItemStack(Material.GOLD_BOOTS, 1), "§bVanity Armor", new String[0]));
							break;
						case DIAMOND:
							((Player) event.getWhoClicked()).getInventory().setChestplate(IconMenu.setItemNameAndLore(
									new ItemStack(Material.DIAMOND_CHESTPLATE, 1), "§bVanity Armor", new String[0]));
							((Player) event.getWhoClicked()).getInventory().setLeggings(IconMenu.setItemNameAndLore(
									new ItemStack(Material.DIAMOND_LEGGINGS, 1), "§bVanity Armor", new String[0]));
							((Player) event.getWhoClicked()).getInventory().setBoots(IconMenu.setItemNameAndLore(
									new ItemStack(Material.DIAMOND_BOOTS, 1), "§bVanity Armor", new String[0]));
							break;
						}
					} else {
						Chat.sendPlayerPrefixedMessage(event.getWhoClicked(),
								"§eYou don't have §b" + cb.getDisplayName() + " §eunlocked");
					}
				}
			}

		}, p);
		for (int i2 = 0; i2 < VanityArmor.values().length; i2++) {
			VanityArmor cb = VanityArmor.values()[i2];
			i.setOption(positions[i2], IconMenu.setItemNameAndLore(new ItemStack(cb.getMaterial(), 1),
					cb.getDisplayName(), new String[0]));
		}
		i.setOption(48, IconMenu.setItemNameAndLore(new ItemStack(Material.ARROW, 1), "§bGo back", new String[0]));
		return i;
	}

	@SuppressWarnings("unused")
	private boolean isSameItemStack(ItemStack I, ItemStack II) {
		if (I.getType() == II.getType() && I.getAmount() == II.getAmount()) {
			if (I.hasItemMeta() && II.hasItemMeta()) {
				if (I.getItemMeta().getDisplayName().equalsIgnoreCase(II.getItemMeta().getDisplayName())) {
					return true;
				}
			} else if (!(I.hasItemMeta() && II.hasItemMeta())) {
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
}
