package com.sly.main.guis;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.sly.main.player.PlayerModel;
import com.sly.main.resources.MathUtil;

public abstract class GUI implements Listener
{

	private int size;
	private String title;
	private Plugin plugin;

	public GUI(int size, String title, Plugin plugin) {
		this.size = size;
		this.title = title;
		this.plugin = plugin;
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	// Some stupid unneccesary math in order to grab specific inventory slots
	protected static int[] getDisplaySlots(int inventorySize) {
		int arraySize = MathUtil.limitValue(((inventorySize / 9) - 1) * 5, 5, 20); // ((18/9)-1) * 5 == 5
		int[] slots = new int[arraySize];
		int arrayIndex = 0;
		for (int i = 9; i < inventorySize - 9; i++) { // Use only the middle rows
			if (9 % i > 1 && 9 % i < 7) { // Is within the middle 5 slots, 1 < i < 7
				slots[arrayIndex] = i;
				arrayIndex++;
			}
		}
		return slots;
	}

	public int getSize() {
		return size;
	}

	public String getTitle() {
		return title;
	}
	
	public Inventory getInventory() {
		return Bukkit.createInventory(null, size, title);
	}

	public abstract void show(PlayerModel player);

	public abstract void handle(ClickEvent event);

	@EventHandler(priority = EventPriority.HIGHEST)
	void onInventoryClick(InventoryClickEvent event) {
		if (event.getInventory().getTitle().equals(title)) {
			if (event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) { // Make sure the item is not null
				event.setCancelled(true); // Cancel it if it even involves on of our inventories
				if (event.getClickedInventory().equals(event.getInventory())) { // Make sure the inventory they have open is the one they clicked in
					if (event.getRawSlot() >= 0 && event.getRawSlot() < size) {
						ClickEvent clickEvent = new ClickEvent(event);
						this.handle(clickEvent);
						if (clickEvent.isCloseOnClick()) {
							new BukkitRunnable() {
								public void run() {
									event.getWhoClicked().closeInventory();
								}
							}.runTaskLater(plugin, 1L);
						}
					}
				}
			}
		}
	}

	public class ClickEvent
	{
		private PlayerModel player;
		private ClickType clickType;
		private ItemStack itemClicked;
		private Inventory inventoryClicked;
		private int rawPosition;
		private boolean closeOnClick;

		public ClickEvent(InventoryClickEvent event) {
			this.player = PlayerModel.getPlayerModel((Player) event.getWhoClicked());
			this.clickType = event.getClick();
			this.itemClicked = event.getCurrentItem();
			this.inventoryClicked = event.getClickedInventory();
			this.rawPosition = event.getRawSlot();
			this.closeOnClick = false;
		}

		public PlayerModel getPlayer() {
			return player;
		}

		public ClickType getClickType() {
			return clickType;
		}

		public ItemStack getItemClicked() {
			return itemClicked;
		}

		public Inventory getInventoryClicked() {
			return inventoryClicked;
		}

		public int getRawPosition() {
			return rawPosition;
		}

		public boolean isCloseOnClick() {
			return closeOnClick;
		}

		public void setCloseOnClick(boolean closeOnClick) {
			this.closeOnClick = closeOnClick;
		}
	}
}