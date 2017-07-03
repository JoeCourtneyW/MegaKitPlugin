package com.me.sly.kits.main.listeners;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.me.sly.kits.main.Chat;
import com.me.sly.kits.main.Corrupted;
import com.me.sly.kits.main.IconMenus;
import com.me.sly.kits.main.classes.Classes;
import com.me.sly.kits.main.enums.AccessMode;
import com.me.sly.kits.main.enums.CustomBlock;
import com.me.sly.kits.main.enums.Group;
import com.me.sly.kits.main.enums.Server;
import com.me.sly.kits.main.enums.Trail;
import com.me.sly.kits.main.enums.VanityArmor;
import com.me.sly.kits.main.gametype.Team;
import com.me.sly.kits.main.resources.BetterItem;
import com.me.sly.kits.main.resources.IconMenu;
import com.me.sly.kits.main.resources.LocationUtil;

@SuppressWarnings("deprecation")
public class ServerListeners implements Listener{

	@EventHandler
	public void clearClassOnDeath(PlayerDeathEvent event){
		event.getEntity().removeMetadata("Class", Corrupted.getInstance());
		event.setDroppedExp(0);
		event.getEntity().setLevel(0);
		event.getDrops().clear();
		event.setDeathMessage("");
	}
	@EventHandler
	public void itemsOnRespawn(PlayerRespawnEvent event){
		if(event.getPlayer().getWorld().getName().contains("2v2")){
			event.setRespawnLocation(Corrupted.getGames().get(event.getPlayer().getName()).getArena().getSpawn3());
			event.getPlayer().getInventory().clear();
		}else{
			Corrupted.getInstance().setDefaultInventory(event.getPlayer());
			event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ZOMBIE_UNFECT, 1, 1);
			Location l = new Location(Bukkit.getWorld("Nube"), -549.5, 101, -552.5, 206.62F, 1.82F);
			event.getPlayer().teleport(l);
			if(event.getPlayer().hasPermission("Taken.FlyInlobby")){
				event.getPlayer().setAllowFlight(true);
				event.getPlayer().setFlying(true);
			}
		}
	}
	@EventHandler
	public void clearClassOnJoin(PlayerJoinEvent event){
		event.getPlayer().removeMetadata("Class", Corrupted.getInstance());
	}
	@EventHandler
	public void clearClassOnLogout(PlayerQuitEvent event){
		event.getPlayer().removeMetadata("Class", Corrupted.getInstance());
	}
	@EventHandler
	public void onExpGain(EntityDeathEvent event){
		event.setDroppedExp(0);
	}
	@EventHandler
	public void disableDropItem(PlayerDropItemEvent event){
		event.setCancelled(true);
	}
	@EventHandler
	public void healthOnJoin(PlayerJoinEvent event){
		if(!event.getPlayer().isDead()){
		event.getPlayer().setMaxHealth(40D);
		event.getPlayer().setHealth(40D);
	      event.getPlayer().sendMessage("§f§l                                Patch " + Corrupted.getInstance().getDescription().getVersion() +" Beta");
	      event.getPlayer().sendMessage("§7§o                         http://takengaming.buycraft.net/");
	      event.getPlayer().sendMessage("§f§l                             Developed by Ayazo");
	      event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ORB_PICKUP, 1, (float) 0.5);
		} else {
			event.getPlayer().setMaxHealth(40D);
			event.getPlayer().setHealth(40D);
		      event.getPlayer().sendMessage("§f§l                                Patch " + Corrupted.getInstance().getDescription().getVersion() +" Beta");
		      event.getPlayer().sendMessage("§7§o                         http://takengaming.buycraft.net/");
		      event.getPlayer().sendMessage("§f§l                             Developed by Ayazo");
		      event.getPlayer().playSound(event.getPlayer().getLocation(), Sound.ORB_PICKUP, 1, (float) 0.5);
			event.getPlayer().teleport(Corrupted.SPAWN_POINT);
			Corrupted.getInstance().setDefaultInventory(event.getPlayer());
			event.getPlayer().setLevel(0);	
			if(event.getPlayer().hasPermission("Taken.FlyInlobby")){
				event.getPlayer().setAllowFlight(true);
				event.getPlayer().setFlying(true);
			}
		}
	}
	@EventHandler
	public void setScoreboard(PlayerJoinEvent event){
		Corrupted.getInstance().updateScoreboard(event.getPlayer());
	}
	@EventHandler
	public void onDamage(EntityDamageEvent event){
		if(event.getEntity() instanceof Player){
			
		}
	}
	@EventHandler
	public void onRegen(EntityRegainHealthEvent event){
		if(event.getEntity() instanceof Player){
			
		}
	}
	@EventHandler
	public void onTrailMove(PlayerMoveEvent event){
		if(event.getTo().getBlockX() != event.getFrom().getBlockX() || event.getTo().getBlockY() != event.getFrom().getBlockY() ||event.getTo().getBlockZ() != event.getFrom().getBlockZ()){
			if(event.getTo().getBlockY() < 2 && event.getTo().getWorld().getName().contains("Nube")){
				event.getPlayer().setFallDistance(0);
				event.setTo(Corrupted.SPAWN_POINT);
				return; 
			}
			if(Trail.getSelectedTrail(event.getPlayer()) != Trail.NONE){
				try {
					if(Corrupted.getModMode().containsKey(event.getPlayer().getName())) return;
					Trail.getSelectedTrail(event.getPlayer()).getParticleEffect().sendToAllPlayers(event.getPlayer().getLocation().clone().add(0, 0.2, 0), 0.13F, Trail.getSelectedTrail(event.getPlayer()).getParticleAmount());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void createConfigOnJoin(final PlayerJoinEvent event){
		final String ip = event.getPlayer().getAddress().getAddress().getHostAddress();
		final String name = event.getPlayer().getName();
		Bukkit.getScheduler().runTaskAsynchronously(Corrupted.getInstance(), new Runnable(){
			public void run(){
				PreparedStatement sql2;
				boolean hasStats = false;
				try {
					sql2 = Corrupted.getConnection().prepareStatement("SELECT * FROM `Players`");
					ResultSet rs = sql2.executeQuery();
					while(rs.next()){
						if(((String)rs.getObject(1)).equalsIgnoreCase(event.getPlayer().getUniqueId().toString())){
							hasStats = true;
						}
					}
				} catch (SQLException e1) {
					e1.printStackTrace(); 
				}
				if(!hasStats){
					Corrupted.getInstance().newDatabaseEntry(event.getPlayer());
					Corrupted.getDatabaseCache().get(event.getPlayer().getUniqueId().toString()).put("IP", ip);
					Corrupted.getDatabaseCache().get(event.getPlayer().getUniqueId().toString()).put("PlayerName", name);
					Corrupted.getInstance().updateDatabase(event.getPlayer());
					Bukkit.getScheduler().runTask(Corrupted.getInstance(), new Runnable(){
						public void run(){
						Corrupted.getInstance().updateScoreboard(event.getPlayer());	
						Chat.broadcastPrefixedMessage("§7It is §b" + event.getPlayer().getName()  + "§7's first time playing Taken Kits. Welcome them!");
						new BukkitRunnable(){
						public void run(){
							Corrupted.getInstance().setDefaultInventory(event.getPlayer());
							Location l = new Location(Bukkit.getWorld("Nube"), -549.5, 101, -552.5, 206.62F, 1.82F);
							event.getPlayer().teleport(l);
						}
						}.runTaskLater(Corrupted.getInstance(), 15);
						}
					});

				}else{
					Corrupted.getDatabaseCache().get(event.getPlayer().getUniqueId().toString()).put("IP", ip);
					Corrupted.getDatabaseCache().get(event.getPlayer().getUniqueId().toString()).put("PlayerName", name);
					Corrupted.getInstance().updateDatabase(event.getPlayer());
				}
			}
		});

	}
	@EventHandler
	public void onShopOpen(PlayerInteractEvent event){
		if(event.getItem() != null && event.getItem().getType() == Material.GOLD_NUGGET){
		IconMenus.mainShop.open(event.getPlayer());
		}
		if(event.getClickedBlock() != null && (event.getClickedBlock().getType() == Material.WOOD_BUTTON || event.getClickedBlock().getType() == Material.STONE_BUTTON)){
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void onAccessMode(PlayerPreLoginEvent event){
		if(Corrupted.getDatabaseCache().containsKey(event.getUniqueId().toString())){
		if(Corrupted.getMode().equals(AccessMode.DEV)){
			if(!Group.isPartOfGroupInheritance(event.getUniqueId(), Group.DEV)){
				event.setResult(Result.KICK_OTHER);
				event.setKickMessage("§eServer Access Mode is set to: §b" + Corrupted.getMode().name());
			}
		} else if(Corrupted.getMode().equals(AccessMode.BUILDER)){
			if((!Group.isPartOfGroupInheritance(event.getUniqueId(), Group.BUILDER)) && (!Group.isPartOfGroupInheritance(event.getUniqueId(), Group.DEV))){
				event.setResult(Result.KICK_OTHER);
				event.setKickMessage("§eServer Access Mode is set to: §b" + Corrupted.getMode().name());
			}
		}else if(Corrupted.getMode().equals(AccessMode.STAFF)){
			if((!Group.isPartOfGroupInheritance(event.getUniqueId(), Group.MOD)) && (!Group.isPartOfGroupInheritance(event.getUniqueId(), Group.BUILDER))){
				event.setResult(Result.KICK_OTHER);
				event.setKickMessage("§eServer Access Mode is set to: §b" + Corrupted.getMode().name());
			}
		}else if(Corrupted.getMode().equals(AccessMode.DONOR)){
			if((!Group.isPartOfGroupInheritance(event.getUniqueId(), Group.SUB))){
				event.setResult(Result.KICK_OTHER);
				event.setKickMessage("§eServer Access Mode is set to: §b" + Corrupted.getMode().name());
			}
		}else{
			return;
		}
		}else{
			if(!Corrupted.getMode().equals(AccessMode.ALL)){
				event.setResult(Result.KICK_OTHER);
				event.setKickMessage("§eServer Access Mode is set to: §b" + Corrupted.getMode().name());				
			}
		}
	}
	@EventHandler
	public void onPermissionLeave(PlayerQuitEvent event){
		Group.unregisterPlayerPermissions(event.getPlayer());
		event.setQuitMessage("§7[§c§l-§7] §c" + event.getPlayer().getName());
	}
	@EventHandler
	public void onPermissionKick(PlayerKickEvent event){
		Group.registerPlayerPermissions(event.getPlayer());
		event.setLeaveMessage("§7[§c§l-§7] §c" + event.getPlayer().getName());
	}
	@EventHandler
	public void onPermissionJoin(PlayerJoinEvent event){
		Group.registerPlayerPermissions(event.getPlayer());
		event.setJoinMessage("§7[§a§l+§7] §a" + event.getPlayer().getName());
	}
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event){
		if(event.getInventory().getTitle().contains("Slot Machine")){
			event.setCancelled(true);
		}
	}
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onChatFormat(AsyncPlayerChatEvent event){
		if(Corrupted.isSTFU()){
			if(!event.getPlayer().hasPermission("Taken.STFU.bypass")){
				Chat.sendPlayerPrefixedMessage(event.getPlayer(), "Chat is turned off");
				event.setCancelled(true);				
			}

		}
	if(Corrupted.getStaffChat().contains(event.getPlayer().getUniqueId().toString())){
		Chat.broadcastMessage("§7[§eSTAFF§7] " + Chat.getFormattedChatMessage(event.getPlayer(), event.getMessage().replaceAll("&", "§")), "Taken.SC");
		event.setCancelled(true);
	}else{

		event.setFormat(Chat.getFormattedChatMessage(event.getPlayer(), event.getMessage().replaceAll("%", "percent").length() > 100 ? event.getMessage().replaceAll("%", "percent").substring(0, 100) : event.getMessage().replaceAll("%", "percent")));
	}
	}
	@EventHandler
	public void onClassShopOpen(InventoryOpenEvent event){
		if(event.getInventory().getTitle().contains("Class Shop")){
			for(ItemStack i : event.getInventory().getContents()){
				if(i != null && i.hasItemMeta() && i.getType() != Material.ARROW){
				Classes c = Classes.valueOf(i.getItemMeta().getDisplayName().toUpperCase());
				ItemMeta im = i.getItemMeta();
				String name = "";
				List<String> lore = new ArrayList<String>();
				for(String r : c.getDescription()){
					lore.add(r);
				}
				String lorePrestige = "";
				String loreSpacer = "§1 ";
				String lorePrice = "";
				if(c.isPrestige((Player) event.getPlayer())) lorePrestige="§d§lPRESTIGED";
				if(c.hasKit((Player) event.getPlayer())){
					name = "§2" + c.getName();
					lorePrice = "§2§lPURCHASED";
				}
				else if(c.getPrice() < 0){
					lorePrice = "§7§lLOCKED";
					if(c.equals(Classes.SHAMAN)){
						name = "§7" + c.getName();
						lore.add("§bFound in the Slot Machine");
					}
					else if(c.equals(Classes.SPIDER)){
						name = "§7" + c.getName();
						lore.add("§bSubscriber+ Only");
					}else if(c.equals(Classes.PIGMAN)){
						name = "§7" + c.getName();
						lore.add("§bSubscriber++ Only");
					}
				}
				else if(c.hasEnoughCoins((Player) event.getPlayer())){
					name = "§2" + c.getName();
					lorePrice = "§7Price: §2" + c.getPrice() + " coins";
				}
				else{
					name = "§4" + c.getName();
					lorePrice = "§7Price: §4" + c.getPrice() + " coins";
				}
				if(!lorePrestige.equalsIgnoreCase(""))lore.add(lorePrestige);
				lore.add(loreSpacer);
				lore.add(lorePrice);
				String[] s = new String[lore.size()];
				for(int r = 0; r < lore.size(); r++){
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
	public void onSpinOpen(InventoryOpenEvent event){
		if(event.getInventory().getTitle().contains("Purchase Spins")){
			for(ItemStack i : event.getInventory().getContents()){
				if(i != null && i.hasItemMeta() && i.getType() != Material.ARROW){
				ItemMeta im = i.getItemMeta();
				List<String> lore = new ArrayList<String>();
				String loreSpacer = "§1 ";
				String lorePrice = "";
				switch(i.getAmount()){
				case 1:
					if((int)Corrupted.getDatabaseCache().get(event.getPlayer().getUniqueId().toString()).get("Coins") >= 500)lorePrice = "§7Price: §a500 coins";
					else lorePrice = "§7Price: §c500 coins";
					break;
				case 3:
					if((int)Corrupted.getDatabaseCache().get(event.getPlayer().getUniqueId().toString()).get("Coins") >= 1463)lorePrice = "§7Price: §a1463 coins §d(2.5% OFF)";
					else lorePrice = "§7Price: §c1463 coins §d(2.5% OFF)";
					break;
				case 5:
					if((int)Corrupted.getDatabaseCache().get(event.getPlayer().getUniqueId().toString()).get("Coins") >= 2375)lorePrice = "§7Price: §a2375 coins §d(5% OFF)";
					else lorePrice = "§7Price: §c2375 coins §d(5% OFF)";
					break;
				case 10:
					if((int)Corrupted.getDatabaseCache().get(event.getPlayer().getUniqueId().toString()).get("Coins") >= 4625)lorePrice = "§7Price: §a4625 coins §d(7.5% OFF)";
					else lorePrice = "§7Price: §c4625 coins §d(7.5% OFF)";
					break;
				case 25:
					if((int)Corrupted.getDatabaseCache().get(event.getPlayer().getUniqueId().toString()).get("Coins") >= 11250)lorePrice = "§7Price: §a11250 coins §d(10% OFF)";
					else lorePrice = "§7Price: §c11250 coins §d(10% OFF)";
					break;
				}
				lore.add(loreSpacer);
				lore.add(lorePrice);
				String[] s = new String[lore.size()];
				for(int r = 0; r < lore.size(); r++){
					s[r] = lore.get(r);
				}
				im.setLore(lore);
				i.setItemMeta(im);
			}
			}
		}
	}
	@EventHandler
	public void onBlockShopOpen(InventoryOpenEvent event){
		if(event.getInventory().getTitle().contains("Block Custom")){
			for(ItemStack i : event.getInventory().getContents()){
				if(i != null && i.hasItemMeta() && i.getType() != Material.ARROW){
				CustomBlock c = CustomBlock.fromInvName(i.getItemMeta().getDisplayName());
				ItemMeta im = i.getItemMeta();
				String name = "";
				List<String> lore = new ArrayList<String>();
				String loreSpacer = "§1 ";
				String lorePrice = "";
				if(c.hasBlock((Player) event.getPlayer())){
					name = "§a" + c.getDisplayName();
					if(CustomBlock.getSelectedBlock((Player) event.getPlayer()).name().equalsIgnoreCase(c.name())) lorePrice = "§aSELECTED";
					else lorePrice = "§aClick to Select!";

				}
				else if(c.getPrice() < 0){
					lorePrice = "§cLOCKED";
					if(c.getPrice() < -1){
						name = "§c" + c.getDisplayName();
						lore.add("§bPremium Only");
					}
					else{
						name = "§c" + c.getDisplayName();
						lore.add("§bFound in the Slot Machine");
					}
				}
				else if(c.hasEnoughCoins((Player) event.getPlayer())){
					name = "§a" + c.getDisplayName();
					lorePrice = "§7Price: §a" + c.getPrice() + " coins";
				}
				else{
					name = "§c" + c.getDisplayName();
					lorePrice = "§7Price: §c" + c.getPrice() + " coins";
				}
				lore.add(loreSpacer);
				lore.add(lorePrice);
				String[] s = new String[lore.size()];
				for(int r = 0; r < lore.size(); r++){
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
	public void onVanityShopOpen(InventoryOpenEvent event){
		if(event.getInventory().getTitle().contains("Vanity")){
			for(ItemStack i : event.getInventory().getContents()){
				if(i != null && i.hasItemMeta() && i.getType() != Material.ARROW){
				VanityArmor c = VanityArmor.fromInvName(i.getItemMeta().getDisplayName());
				ItemMeta im = i.getItemMeta();
				String name = "";
				List<String> lore = new ArrayList<String>();
				String loreSpacer = "§1 ";
				String lorePrice = "";
				if(c.hasVanityArmor((Player) event.getPlayer())){
					name = "§a" + c.getDisplayName();
					if(VanityArmor.getSelectedVanityArmor((Player) event.getPlayer()).name().equalsIgnoreCase(c.name())) lorePrice = "§aSELECTED";
					else lorePrice = "§aClick to Select!";

				}else{
					lorePrice = "§cLOCKED";		
					name = "§c" + c.getDisplayName();
				}
				lore.add(loreSpacer);
				lore.add(lorePrice);
				String[] s = new String[lore.size()];
				for(int r = 0; r < lore.size(); r++){
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
	public void onHunger(FoodLevelChangeEvent event){
		event.setFoodLevel(20);
	}
	@EventHandler
	public void onBlockChange(EntityChangeBlockEvent event){
		if(event.getEntityType() == EntityType.WITHER_SKULL){
			event.setCancelled(true);
		}
		if(event.getEntityType() == EntityType.FIREBALL){
			event.setCancelled(true);
		}
		if(event.getEntityType() == EntityType.SMALL_FIREBALL){
			event.setCancelled(true);
		}
	}
	public ArrayList<String> preModMode = new ArrayList<String>();
	@EventHandler
	public void onModMode(PlayerInteractEvent event){
		if(event.getItem() != null && event.getItem().getType() == Material.EYE_OF_ENDER && event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName() && event.getItem().getItemMeta().getDisplayName().contains("MOD MODE")){
			event.setCancelled(true);
			if(Corrupted.getModMode().containsKey(event.getPlayer().getName()) || preModMode.contains(event.getPlayer().getName())){
			Corrupted.getModMode().remove(event.getPlayer().getName());
			Location l = new Location(Bukkit.getWorld("Nube"), -549.5, 101, -552.5, 206.62F, 1.82F);
			event.getPlayer().teleport(l);
			Corrupted.getInstance().setDefaultInventory(event.getPlayer());
			preModMode.remove(event.getPlayer().getName());
			Chat.sendPlayerPrefixedMessage(event.getPlayer(), "§eYou have left Mod Mode");
			Chat.sendPlayerPrefixedMessage(event.getPlayer(), "§eIf you decided the player the player is hacking, use /tempban [player] [time] [reason]");
			for(Player p : Bukkit.getOnlinePlayers()){
				p.showPlayer(event.getPlayer());
			}
			}else{
				preModMode.add(event.getPlayer().getName());
				Chat.sendPlayerPrefixedMessage(event.getPlayer(), "§eYou are now in Mod Mode");		
				Chat.sendPlayerPrefixedMessage(event.getPlayer(), "§eType the player's name you want to spectate in chat");
				event.getPlayer().getInventory().clear();
				event.getPlayer().getInventory().setArmorContents(new ItemStack[4]);
				event.getPlayer().getInventory().setItem(4, new BetterItem(Material.EYE_OF_ENDER, 1, "§c§lLEAVE MOD MODE").grab());
				event.getPlayer().updateInventory();
			}
		}
	}
	@EventHandler
	public void onModmodeChat(final AsyncPlayerChatEvent event){
		if(preModMode.contains(event.getPlayer().getName())){
			if(Bukkit.getPlayer(event.getMessage()) != null){
				event.setCancelled(true);
				Bukkit.getScheduler().runTask(Corrupted.getInstance(), new Runnable(){
					public void run(){
				Player spectate = Bukkit.getPlayer(event.getMessage());
				event.getPlayer().teleport(LocationUtil.getSafeDestination(spectate.getLocation()));
				Corrupted.getModMode().put(event.getPlayer().getName(), spectate.getName());
				Chat.sendPlayerPrefixedMessage(event.getPlayer(), "§eYou are now spectating " + spectate.getName());
				for(String s : Corrupted.getModMode().keySet()){
					if(Corrupted.getModMode().get(s).equalsIgnoreCase(spectate.getName())){
						Chat.sendPlayerPrefixedMessage(event.getPlayer(), "§b" + s + " §eis also spectating §b" + spectate.getName());
					}
				}
				new BukkitRunnable(){
					public void run(){
						event.getPlayer().setGameMode(GameMode.CREATIVE);						
					}
				}.runTaskLater(Corrupted.getInstance(), 10);
				for(Player p : Bukkit.getOnlinePlayers()){
					p.hidePlayer(event.getPlayer());
				}
					}});
			}else{
				Chat.sendPlayerPrefixedMessage(event.getPlayer(), "§cPlayer not found!");	
				event.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onTeleportModMode(PlayerTeleportEvent event){
		if(Corrupted.getModMode().values().contains(event.getPlayer().getName())){
			String name = Corrupted.getKeyByValue(Corrupted.getModMode(), event.getPlayer().getName());
			Bukkit.getPlayer(name).teleport(event.getTo());
		}
	}
	@EventHandler
	public void onLeaveModMode(PlayerQuitEvent event){
		if(Corrupted.getModMode().values().contains(event.getPlayer().getName())){
			String name = Corrupted.getKeyByValue(Corrupted.getModMode(), event.getPlayer().getName());
			Corrupted.getModMode().remove(name);
			Chat.sendPlayerPrefixedMessage(Bukkit.getPlayer(name), "§cYour target has logged off");
			Corrupted.getModMode().remove(event.getPlayer().getName());
			Location l = new Location(Bukkit.getWorld("Nube"), -549.5, 101, -552.5, 206.62F, 1.82F);
			Bukkit.getPlayer(name).teleport(l);
			Corrupted.getInstance().setDefaultInventory(Bukkit.getPlayer(name));
			preModMode.remove(Bukkit.getPlayer(name).getName());
			Chat.sendPlayerPrefixedMessage(Bukkit.getPlayer(name), "§eYou have left Mod Mode");
			for(Player p : Bukkit.getOnlinePlayers()){
				p.showPlayer(event.getPlayer());
			}
		} else
		if( Corrupted.getModMode().keySet().contains(event.getPlayer().getName()) ){
			Corrupted.getModMode().remove(event.getPlayer().getName());	
		}
	}
	/*@EventHandler
	public void onNickRecieve(AsyncPlayerReceiveNameTagEvent event){
		if(Corrupted.getNickedPlayers().containsKey(event.getNamedPlayer().getName()) && event.getNamedPlayer().hasMetadata("Class") && event.getNamedPlayer().getWorld().getName().contains("Nube")){
			if(event.getPlayer().hasPermission("Taken.staff")) return;
			event.setTag(Corrupted.getNickedPlayers().get(event.getNamedPlayer().getName()));
		}
	}*/
	@EventHandler
	public void onHideJoin(PlayerJoinEvent event){
		for(Player p : Bukkit.getOnlinePlayers()){
		if(Corrupted.getModMode().containsKey(p.getName())){
			event.getPlayer().hidePlayer(p);
		}
		}
	}
	@EventHandler
	public void preventSpawnKill(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Player && event.getDamager() instanceof Player){
			Player killer = (Player) event.getDamager();
			Player killed = (Player) event.getEntity();
			if(!(killer.hasMetadata("Class")) || !(killed.hasMetadata("Class"))){
				event.setDamage(0);
				event.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onDeathModeMode(PlayerDeathEvent event){
		if(Corrupted.getModMode().values().contains(event.getEntity().getName())){
			String name = Corrupted.getKeyByValue(Corrupted.getModMode(), event.getEntity().getName());
			Chat.sendPlayerPrefixedMessage(Bukkit.getPlayer(name), "§cYour target has died");
			Location l = new Location(Bukkit.getWorld("Nube"), -549.5, 101, -552.5, 206.62F, 1.82F);
			Bukkit.getPlayer(name).teleport(l);
		}
	}
	@EventHandler
	public void onTrailShopOpen(InventoryOpenEvent event){
		if(event.getInventory().getTitle().contains("Trail")){
			for(ItemStack i : event.getInventory().getContents()){
				if(i != null && i.hasItemMeta() && i.getType() != Material.ARROW){
				Trail c = Trail.fromInvName(i.getItemMeta().getDisplayName());
				ItemMeta im = i.getItemMeta();
				String name = "";
				List<String> lore = new ArrayList<String>();
				String loreSpacer = "§1 ";
				String lorePrice = "";
				if(c.hasTrail((Player) event.getPlayer())){
					name = "§a" + c.getDisplayName();
					if(Trail.getSelectedTrail((Player) event.getPlayer()).name().equalsIgnoreCase(c.name())) lorePrice = "§aSELECTED";
					else lorePrice = "§aClick to Select!";

				}else{
					lorePrice = "§cLOCKED";		
					name = "§c" + c.getDisplayName();
				}
				lore.add(loreSpacer);
				lore.add(lorePrice);
				String[] s = new String[lore.size()];
				for(int r = 0; r < lore.size(); r++){
					s[r] = lore.get(r);
				}
				im.setDisplayName(name);
				im.setLore(lore);
				i.setItemMeta(im);
			}
			}
		}
	}
	private HashMap<String, Integer> customize = new HashMap<String, Integer>();
	@EventHandler
	public void onKitCustomizer(InventoryClickEvent event){
		if(event.getClickedInventory() == null) return;
		if(event.getInventory().getTitle().contains("Customize ")){ event.setCancelled(true);
		if(event.getClickedInventory().getTitle().contains("Customize ")){
			if(event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR){
					if(event.getRawSlot() < 9){
						if(customize.containsKey(event.getWhoClicked().getUniqueId().toString())){
							int oldSlot = customize.get(event.getWhoClicked().getUniqueId().toString());
							customize.remove(event.getWhoClicked().getUniqueId().toString());
							ItemStack clicked = event.getCurrentItem();
							event.getInventory().setItem(event.getRawSlot(), event.getInventory().getItem(oldSlot));
							event.getInventory().setItem(oldSlot, clicked);
							event.getClickedInventory().setItem(oldSlot+9, IconMenu.setItemNameAndLore(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15), " ", new String[0]));
						}else{
				customize.put(event.getWhoClicked().getUniqueId().toString(), event.getRawSlot());
				event.getClickedInventory().setItem(event.getRawSlot()+9, IconMenu.setItemNameAndLore(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 0), " ", new String[0]));
				Chat.sendPlayerPrefixedMessage(((Player)event.getWhoClicked()), "Select the slot where you want to move this item to!");
					}
					}else
					if(event.getRawSlot() > 45){
						if(event.getCurrentItem().getType() == Material.DIAMOND_BLOCK){
							String custom = "";
							for(int i = 0; i < 9; i++){
								if(i == 0){
									custom = custom + (Integer.parseInt(event.getInventory().getItem(i).getItemMeta().getLore().get(0))) ;
								}else{
									custom = custom + "|" + (Integer.parseInt(event.getInventory().getItem(i).getItemMeta().getLore().get(0)));
								}
							}
							Corrupted.getKitCustomizerCache().get(event.getWhoClicked().getUniqueId().toString()).put(event.getClickedInventory().getTitle().split(" ")[1], custom);
							Corrupted.getInstance().updateDatabase((Player) event.getWhoClicked());
							IconMenus.kitCustomizer.open((Player) event.getWhoClicked());
						}else
							if(event.getCurrentItem().getType() == Material.ARROW){
							IconMenus.kitCustomizer.open((Player) event.getWhoClicked());					
							}
					}
				}
			}
		}
	}

	/*@EventHandler
	public void spawnOnJoin(final PlayerJoinEvent event){
		//if(((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() < 47){
		Location l = new Location(Bukkit.getWorld("Nube"), -549.5, 101, -552.5, 206.62F, 1.82F);
		event.getPlayer().teleport(l);
		Corrupted.getInstance().setDefaultInventory(event.getPlayer());
		event.getPlayer().setLevel(0);
		//}else{
			/**
			 * Prevent people from joining the server using 1.8
			 * (Applicable on protocol version to limit compatability
			 * issues while still retaining plugin versions)
			 */
			/*new BukkitRunnable(){
				public void run(){
					event.getPlayer().kickPlayer("§cYou must be on 1.7 to play on this server!");					
				}
			}.runTaskLater(Corrupted.getInstance(), 30);

		}
	}*/
	@EventHandler
	public void onflyandtpjoin(final PlayerJoinEvent event) {
		new BukkitRunnable(){
			public void run(){
				Location l = new Location(Bukkit.getWorld("Nube"), -549.5, 101, -552.5, 206.62F, 1.82F);
				event.getPlayer().teleport(l);
				Corrupted.getInstance().setDefaultInventory(event.getPlayer());
				event.getPlayer().setLevel(0);	
				if(event.getPlayer().hasPermission("Taken.FlyInlobby")){
					event.getPlayer().setAllowFlight(true);
					event.getPlayer().setFlying(true);
				}
			}
		}.runTaskLater(Corrupted.getInstance(), 15);

		
	}
	@EventHandler
	public void preventCraft(CraftItemEvent event){
		event.setCancelled(true);
	}
	@EventHandler(priority = EventPriority.MONITOR)
	public void spawnOnJoin(PlayerRespawnEvent event){
		Location l = new Location(Bukkit.getWorld("Nube"), -549.5, 101, -552.5, 206.62F, 1.82F);
		event.getPlayer().teleport(l);
		Corrupted.getInstance().setDefaultInventory(event.getPlayer());
		event.getPlayer().setLevel(0);
	}
	@EventHandler
	public void onPing(ServerListPingEvent event){
		event.setMaxPlayers(90);
		if(Server.getServer(Bukkit.getIp()) == Server.GAME){
		event.setMotd("§eTaken Gaming §7| §f§lPatch " + Corrupted.getInstance().getDescription().getVersion());
		}else{
			event.setMotd("§eTaken Dev Server §7| §f§lPatch " + Corrupted.getInstance().getDescription().getVersion() + "\n§b§lMain Server: §c§nPlay.TakenGaming.Net");
		}
	}
	@EventHandler
	public void onNameTag(PlayerInteractEvent event){
		if(event.getItem() != null && event.getItem().getType() == Material.NAME_TAG){
			Player p = event.getPlayer();
			if (!Corrupted.getTeams().containsKey(p.getName())) {
				if (Classes.getClass(p) == Classes.NONE) {
					Team t = new Team(2, p.getName());
					Corrupted.getTeams().put(p.getName(), t);
					Chat.sendPlayerPrefixedMessage(p, "§eYou have created a team");
				} else {
					Chat.sendPlayerPrefixedMessage(p, "You must be at spawn to create a team");
				}
			} else {
				Chat.sendPlayerPrefixedMessage(p, "You are already in a team");
			}
		}
	}
}
