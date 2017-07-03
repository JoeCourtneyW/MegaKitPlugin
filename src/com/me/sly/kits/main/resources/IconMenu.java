package com.me.sly.kits.main.resources;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

public class IconMenu implements Listener{

	  private boolean destroy = false;
	  private String name;
	  private int size;
	  private OptionClickEventHandler handler;
	  private Plugin plugin;
	  private String[] optionNames;
	  private ItemStack[] optionIcons;
	  
	  public IconMenu(String name, int size, OptionClickEventHandler handler, Plugin plugin)
	  {
	    this.name = name;
	    this.size = size;
	    this.handler = handler;
	    this.plugin = plugin;
	    this.optionNames = new String[size];
	    this.optionIcons = new ItemStack[size];
	    plugin.getServer().getPluginManager().registerEvents(this, plugin);
	  }
	  
	  public void setDestroy(boolean destroy)
	  {
	    this.destroy = destroy;
	  }
	  
	  public boolean willDestroy()
	  {
	    return this.destroy;
	  }
	  
	  public List<ItemStack> getItems()
	  {
	    List<ItemStack> items = new ArrayList<ItemStack>();
	    for (ItemStack i : this.optionIcons) {
	      if (i != null) {
	        items.add(i);
	      }
	    }
	    return items;
	  }
	  
	  public IconMenu setOption(int position, ItemStack icon)
	  {
	    this.optionNames[position] = this.name;
	    this.optionIcons[position] = icon;
	    return this;
	  }
	  
	  public void open(Player player)
	  {
	    Inventory inventory = Bukkit.createInventory(player, this.size, this.name);
	    for (int i = 0; i < this.optionIcons.length; i++) {
	      if (this.optionIcons[i] != null) {
	        inventory.setItem(i, this.optionIcons[i]);
	      }
	    }
	    player.openInventory(inventory);
	  }
	  
	  public void destroy()
	  {
	    HandlerList.unregisterAll(this);
	    this.handler = null;
	    this.plugin = null;
	    this.optionNames = null;
	    this.optionIcons = null;
	  }
	  
	  public int getSize()
	  {
	    return this.size;
	  }
	  
	  @EventHandler(priority=EventPriority.MONITOR)
	  void onInventoryClick(InventoryClickEvent event)
	  {
	    if (event.getInventory().getTitle().equals(this.name))
	    {
	      event.setCancelled(true);
	      int slot = event.getRawSlot();
	      if ((slot >= 0) && (slot < this.size) && (this.optionNames[slot] != null))
	      {
	        Plugin plugin = this.plugin;
	        OptionClickEvent e = new OptionClickEvent((Player)event.getWhoClicked(), this, slot, this.optionNames[slot], event);
	        this.handler.onOptionClick(e);
	        if (e.willClose())
	        {
	          final Player p = (Player)event.getWhoClicked();
	          Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
	          {
	            public void run()
	            {
	              p.closeInventory();
	            }
	          }, 1L);
	        }
	        if (this.destroy) {
	          destroy();
	        }
	      }
	    }
	  }
	  
	  @EventHandler(priority=EventPriority.MONITOR)
	  void onInventoryClose(InventoryCloseEvent event)
	  {
	    if (event.getInventory().getTitle().equals(this.name)) {
	      if (this.destroy) {
	        destroy();
	      }
	    }
	  }
	  
	  public class OptionClickEvent
	  {
	    private IconMenu menu;
	    private Player player;
	    private int position;
	    private String name;
	    private boolean close;
	    private InventoryClickEvent event;
	    
	    public OptionClickEvent(Player player, IconMenu menu, int position, String name, InventoryClickEvent event)
	    {
	      this.player = player;
	      this.position = position;
	      this.name = name;
	      this.close = true;
	      this.menu = menu;
	      this.event = event;
	    }
	    
	    public IconMenu getIconMenu()
	    {
	      return this.menu;
	    }
	    public InventoryClickEvent getEvent()
	    {
	    	return this.event;
	    }
	    
	    public Player getPlayer()
	    {
	      return this.player;
	    }
	    
	    public int getPosition()
	    {
	      return this.position;
	    }
	    
	    public String getName()
	    {
	      return this.name;
	    }
	    
	    public boolean willClose()
	    {
	      return this.close;
	    }
	    
	    public void setWillClose(boolean close)
	    {
	      this.close = close;
	    }
	  }
	  
	  public static ItemStack setItemNameAndLore(ItemStack item, String name, String[] lore)
	  {
	    ItemMeta im = item.getItemMeta();
	    im.setDisplayName(name);
	    im.setLore(Arrays.asList(lore));
	    item.setItemMeta(im);
	    return item;
	  }
	  
	  public static abstract interface OptionClickEventHandler
	  {
	    public abstract void onOptionClick(IconMenu.OptionClickEvent paramOptionClickEvent);
	  }
}
