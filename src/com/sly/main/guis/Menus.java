package com.sly.main.guis;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import com.sly.main.kits.Kits;
import com.sly.main.player.PlayerModel;
import com.sly.main.resources.BetterItem;

public class Menus
{

	private Plugin plugin;

	public Menus(Plugin plugin) {
		this.plugin = plugin;
	}

	public GUI mainMenu() {
		return new MainMenu();
	}

	public GUI kitShop() {
		return new KitShop();
	}

	public GUI kitCustomizer() {
		return new MainMenu();
	}

	public GUI blockCustomizer() {
		return new MainMenu();
	}

	public GUI vanityArmor() {
		return new MainMenu();
	}

	public GUI trailCustomizer() {
		return new MainMenu();
	}

	public GUI spinPurchaser() {
		return new MainMenu();
	}

	public class MainMenu extends GUI
	{

		public MainMenu() {
			super(54, "Main Menu", plugin);
		}

		@Override
		public void show(PlayerModel player) {
			Inventory inventory = getInventory();
			inventory.setItem(11, new BetterItem().material(Material.GOLD_INGOT).displayName("§eKit Shop").grab());
			inventory.setItem(13,
					new BetterItem().material(Material.ANVIL).displayName("§eCustomize your Kit layout").grab());
			inventory.setItem(15,
					new BetterItem().material(Material.WOOD).displayName("§eCustomize your block material").grab());
			inventory.setItem(30,
					new BetterItem().material(Material.BLAZE_POWDER).displayName("§eTrail Customizer").grab());
			inventory.setItem(32, new BetterItem().material(Material.DIAMOND_CHESTPLATE)
					.displayName("§bCustomize your lobby armor").grab());
			inventory.setItem(34,
					new BetterItem().material(Material.TRIPWIRE_HOOK).displayName("§eSlot Machine Spins").grab());
			player.getPlayer().openInventory(inventory);
		}

		@Override
		public void handle(ClickEvent event) {
			PlayerModel p = event.getPlayer();
			event.setCloseOnClick(true);
			switch (event.getItemClicked().getType())
			{
			case GOLD_INGOT:
				kitShop().show(p);
				break;
			case ANVIL:
				kitCustomizer().show(p);
				break;
			case WOOD:
				blockCustomizer().show(p);
				break;
			case DIAMOND_CHESTPLATE:
				vanityArmor().show(p);
				break;
			case BLAZE_POWDER:
				trailCustomizer().show(p);
				break;
			case TRIPWIRE_HOOK:
				spinPurchaser().show(p);
				break;
			default:
				break;
			}
		}

	}

	public class KitShop extends GUI
	{

		public KitShop() {
			super(54, "Kit Shop", plugin);
		}

		@Override
		public void show(PlayerModel player) {
			Inventory inventory = getInventory();
			int[] spots = GUI.getDisplaySlots(getSize());
			for (int i = 0; i < Kits.getKits().length; i++) {
				Kits kit = Kits.getKits()[i];
				int spot = spots[i];
				BetterItem item = new BetterItem().material(kit.getDisplayMaterial()).amount(1)
						.data(kit.getDisplayData());
				for (String line : kit.getDescription())
					item.addLoreLine(line);

				if (kit.isPrestige(player))
					item.addLoreLine("§d§lPRESTIGED");

				item.addLoreSpacer();

				if (kit.hasKit(player) || player.getPlayer().isOp()) {
					item.displayName("§2" + kit.getName());

					item.addLoreLine("§2§lPURCHASED"); // Price
				} else if (kit.getPrice() < 0) {
					item.displayName("§7" + kit.getName());
					if (kit.equals(Kits.SHAMAN))
						item.addLoreLine("§bFound in the Slot Machine");
					else if (kit.equals(Kits.SPIDER))
						item.addLoreLine("§bSubscriber+ Only");
					else if (kit.equals(Kits.PIGMAN))
						item.addLoreLine("§bSubscriber++ Only");
					item.addLoreLine("§7§lLOCKED"); // Price

				} else if (kit.hasEnoughCoins(player)) {
					item.displayName("§2" + kit.getName());
					item.addLoreLine("§7Price: §2" + kit.getPrice() + " coins"); // Price
				} else {
					item.displayName("§4" + kit.getName());
					item.addLoreLine("§7Price: §4" + kit.getPrice() + " coins"); // Price
				}

				inventory.setItem(spot, item.grab());
			}
			inventory.setItem(48, new BetterItem().material(Material.ARROW).displayName("§bGo back").grab());
			player.getPlayer().openInventory(inventory);
		}

		@Override
		public void handle(ClickEvent event) {
			PlayerModel p = event.getPlayer();
			if (event.getItemClicked().getType() == Material.ARROW) {
				p.getPlayer().closeInventory();
				mainMenu().show(p);
			}
			if (event.getItemClicked().hasItemMeta()
					&& Kits.isKit(event.getItemClicked().getItemMeta().getDisplayName())) {
				Kits kit = Kits.getKitFromString(event.getItemClicked().getItemMeta().getDisplayName());
				if (!kit.hasKit(p)) { // Do they own the kit
					if (kit.getPrice() > 0) { // Is the kit special unlock
						if (kit.hasEnoughCoins(p)) { // Can they afford it?
							kit.onPurchase(p);
							p.sendPrefixedMessage("You purchased §b" + kit.getName() + "§c!");
							p.getDatabaseCell("COINS").mutateValue(bal -> bal - kit.getPrice());
							p.updateScoreboard();
						} else {
							p.sendPrefixedMessage("You don't have enough coins for §b" + kit.getName() + "§c.");
						}
					} else {
						p.sendPrefixedMessage("§b" + kit.getName() + " §ccan not be purchased in the kit shop.");
					}
				} else {
					p.sendPrefixedMessage("You already own §b" + kit.getName() + "§c.");
				}
			}

		}

	}
}
