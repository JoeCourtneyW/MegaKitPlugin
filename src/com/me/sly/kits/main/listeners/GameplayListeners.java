package com.me.sly.kits.main.listeners;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.me.sly.kits.main.Chat;
import com.me.sly.kits.main.Corrupted;
import com.me.sly.kits.main.SlotSpin;
import com.me.sly.kits.main.classes.Classes;
import com.me.sly.kits.main.enums.CustomBlock;
import com.me.sly.kits.main.resources.IconMenu;
import com.me.sly.kits.main.resources.MyMetadata;

public class GameplayListeners implements Listener{
	@EventHandler
	public void customPotDrink(PlayerItemConsumeEvent event){
		if(event.getItem() != null){
		if(event.getItem().hasItemMeta() && event.getItem().getItemMeta().hasDisplayName()){
		if(event.getItem().getItemMeta().getDisplayName().contains(" Health Potion")){
			String name = event.getItem().getItemMeta().getDisplayName();
			int health = 0;
			if(name.contains("6")){
				health = 12;
			}else if(name.contains("7")){
				health = 14;
			}else if(name.contains("8")){
				health = 16;
			}else if(name.contains("9")){
				health = 18;
			}else if(name.contains("10")){
				health = 20;
			}
			Player p = event.getPlayer();
			if (((CraftPlayer)p).getHealth() + health >= ((CraftPlayer)p).getMaxHealth()) p.setHealth(((CraftPlayer)p).getMaxHealth());
			else p.setHealth(((CraftPlayer)p).getHealth() + health);
		}else if(event.getItem().getItemMeta().getDisplayName().contains(" Speed Potion")){
			Player p = event.getPlayer();
			Corrupted.getInstance().addNoReductionEffect(p, new PotionEffect(PotionEffectType.SPEED, 15*20, 1));
		}else if(event.getItem().getItemMeta().getDisplayName().contains(" Regen Potion")){
			Player p = event.getPlayer();
			Corrupted.getInstance().addNoReductionEffect(p, new PotionEffect(PotionEffectType.REGENERATION, 11*20, 2));
		}
		}
		}
	}
	@EventHandler
	public void onVoidDamage(EntityDamageEvent event){
		if(event.getCause().equals(DamageCause.VOID)&&event.getEntity() instanceof Player){
			final Location l = new Location(Bukkit.getWorld("Nube"), -549.5, 101, -552.5, 206.62F, 1.82F);
			event.setCancelled(true);
			event.getEntity().teleport(l);
		}
	}
	@EventHandler
	public void onKillPots(PlayerDeathEvent event){
		if(!event.getEntity().getWorld().getName().equalsIgnoreCase("Nube")) return;
		if(event.getEntity().getKiller() != null && event.getEntity().getKiller() instanceof Player){
			Classes c = Classes.getClass(event.getEntity().getKiller());
			if(c.equals(Classes.NONE)) return;
			ItemStack one = Corrupted.getKitItems().get(c.getName()).get("3").clone();
			ItemStack two = Corrupted.getKitItems().get(c.getName()).get("4").clone();
				one.setAmount(1);
				two.setAmount(1);
				if(!c.equals(Classes.GOLEM)){
				boolean speedAdded = false;
				boolean healthAdded = false;
				for(ItemStack i : event.getEntity().getKiller().getInventory().getContents()){
					if(i != null && i.getType() == Material.POTION && i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().contains(" Health Potion")){
						i.setAmount(i.getAmount() + 1);
						healthAdded = true;
					}
					else if(i != null && i.getType() == Material.POTION && i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().contains(" Speed Potion")){
						i.setAmount(i.getAmount() + 1);
						speedAdded = true;
					}
				}
				if(!healthAdded){
					event.getEntity().getKiller().getInventory().addItem(one);		
				}
				if(!speedAdded){
					event.getEntity().getKiller().getInventory().addItem(two);			
				}
				}else{
				boolean regenAdded = false;
				boolean slowAdded = false;
				for(ItemStack i : event.getEntity().getKiller().getInventory().getContents()){
					if(i != null && i.getType() == Material.POTION && i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().contains(" Regen Potion")){
						i.setAmount(i.getAmount() + 1);
						regenAdded = true;
					}
					else if(i != null && i.getType() == Material.POTION && i.hasItemMeta() && i.getItemMeta().hasDisplayName() && i.getItemMeta().getDisplayName().contains(" Slowness Potion")){
						i.setAmount(i.getAmount() + 1);
						slowAdded = true;
					}
				}
				if(!regenAdded){
					event.getEntity().getKiller().getInventory().addItem(one);		
				}
				if(!slowAdded){
					event.getEntity().getKiller().getInventory().addItem(two);			
				}

				}
				if(c.equals(Classes.BLAZE) || c.equals(Classes.SKELETON)){
					event.getEntity().getKiller().getInventory().addItem(IconMenu.setItemNameAndLore(new ItemStack(Material.ARROW, 16), "§b" + c.getName() + " Arrow", new String[0]));
				}
				event.getEntity().getKiller().getInventory().addItem(new ItemStack(CustomBlock.getSelectedBlock(event.getEntity().getKiller()).getMaterial(), 32, CustomBlock.getSelectedBlock(event.getEntity().getKiller()).getDurability()));

		}
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		if(event.getPlayer().getGameMode() == GameMode.SURVIVAL) event.setCancelled(true);
	}
	@EventHandler(priority = EventPriority.LOWEST)
	public void onCompass(PlayerInteractEvent event){
		if(event.getPlayer().getGameMode() == GameMode.SURVIVAL && event.getItem() != null && event.getItem().getType() == Material.COMPASS) event.setCancelled(true);
	}
	@EventHandler
	public void onSlot(PlayerInteractEvent event){
		if(event.getClickedBlock() != null && event.getClickedBlock().getState() instanceof Sign && ((Sign)event.getClickedBlock().getState()).getLine(0).contains("[SLOTS]")){
			if(SlotSpin.spins.containsKey(event.getPlayer().getName())){
				event.getPlayer().openInventory(SlotSpin.spins.get(event.getPlayer().getName()).getInventory());
				return;
			}
			if((int)Corrupted.getDatabaseCache().get(event.getPlayer().getUniqueId().toString()).get("Spins") >= 1) new SlotSpin(event.getPlayer(), Corrupted.getInstance());
			else Chat.sendPlayerPrefixedMessage(event.getPlayer(), "You need a spin to use the Slot Machine!");
		}
	}

	@EventHandler
	public void onMinion(EntityTargetEvent event){
		if(event.getTarget() instanceof Player){
			if(event.getEntity().hasMetadata("owner") && ((String)event.getEntity().getMetadata("owner").get(0).value()).equalsIgnoreCase(((Player)event.getTarget()).getName())){
				event.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void click(InventoryClickEvent event){
		if(event.getInventory().getName().contains("crafting") && event.getWhoClicked().hasMetadata("Class") && event.getCurrentItem() != null && Corrupted.getGames().containsKey(event.getWhoClicked().getName())){
			Chat.sendPlayerPrefixedMessage(event.getWhoClicked(), "§eIf you want to change your class setup, do so in the 'Class Customizer'");
			event.setCancelled(true);
		}else if(event.getInventory().getName().contains("crafting") && event.getCurrentItem() != null && event.getCurrentItem().hasItemMeta() && event.getCurrentItem().getItemMeta().getDisplayName().contains("Vanity")){
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void blockPlacing(final BlockPlaceEvent event)
	{
		if(event.getPlayer().getGameMode() != GameMode.SURVIVAL) return;
		
			if(Corrupted.getBlocks().contains(event.getBlockPlaced().getLocation().clone().subtract(0, 3, 0))){
				event.setCancelled(true);
				return;
			}
			final BlockState oldb = event.getBlockReplacedState();
			if(oldb.getType() == Material.WATER || oldb.getType() == Material.LAVA || oldb.getType() == Material.STATIONARY_WATER || oldb.getType() == Material.STATIONARY_LAVA){
				event.setCancelled(true);
				return;
			}

			final Block b = event.getBlockPlaced();
			if(event.getBlockAgainst().equals(b.getRelative(BlockFace.NORTH)) || event.getBlockAgainst().equals(b.getRelative(BlockFace.SOUTH)) || event.getBlockAgainst().equals(b.getRelative(BlockFace.EAST)) || event.getBlockAgainst().equals(b.getRelative(BlockFace.WEST))){
				event.setCancelled(true);
				return;
			}
			if(b.getRelative(BlockFace.NORTH).getType() == Material.CACTUS ||b.getRelative(BlockFace.SOUTH).getType() == Material.CACTUS || b.getRelative(BlockFace.EAST).getType() == Material.CACTUS ||b.getRelative(BlockFace.WEST).getType() == Material.CACTUS){
				event.setCancelled(true);
				return;
			}
			if(b.getRelative(BlockFace.NORTH).getType() == Material.LADDER ||b.getRelative(BlockFace.SOUTH).getType() == Material.LADDER ||
					b.getRelative(BlockFace.EAST).getType() == Material.LADDER ||b.getRelative(BlockFace.WEST).getType() == Material.LADDER
					||b.getRelative(BlockFace.UP).getType() == Material.LADDER ||b.getRelative(BlockFace.DOWN).getType() == Material.LADDER){
				event.setCancelled(true);
				return;
			}
			if(event.getBlockPlaced().getType() == Material.WOOL){
				Corrupted.getBlocks().add(b.getLocation());
			final BukkitTask r = new BukkitRunnable(){
					@SuppressWarnings("deprecation")
					public void run(){
						event.getBlockPlaced().setData((byte) Corrupted.getInstance().rand(1, 15));
					}
				}.runTaskTimer(Corrupted.getInstance(), 5, 10);
				new BukkitRunnable(){
					public void run(){
						r.cancel();
						b.getWorld().loadChunk(b.getWorld().getChunkAt(b));
						b.setType(Material.AIR);
						Corrupted.getInstance().removeBlock(b.getLocation());
						if(Corrupted.getBlocks().contains(event.getBlockPlaced().getLocation().clone().add(0, 1, 0))){
							Corrupted.getInstance().removeBlock(event.getBlockPlaced().getLocation().clone().add(0, 1, 0));
							event.getBlockPlaced().getWorld().getBlockAt(event.getBlockPlaced().getLocation().clone().add(0, 1, 0)).setType(Material.AIR);		
						}
						if(Corrupted.getBlocks().contains(event.getBlockPlaced().getLocation().clone().add(0, 2, 0))){
							Corrupted.getInstance().removeBlock(event.getBlockPlaced().getLocation().clone().add(0, 2, 0));
							event.getBlockPlaced().getWorld().getBlockAt(event.getBlockPlaced().getLocation().clone().add(0, 2, 0)).setType(Material.AIR);		
						}
						if(Corrupted.getBlocks().contains(event.getBlockPlaced().getLocation().clone().add(0, 3, 0))){
							Corrupted.getInstance().removeBlock(event.getBlockPlaced().getLocation().clone().add(0, 3, 0));
							event.getBlockPlaced().getWorld().getBlockAt(event.getBlockPlaced().getLocation().clone().add(0, 3, 0)).setType(Material.AIR);		
						}
					}
				}.runTaskLater(Corrupted.getInstance(), 5*20);
				return;
			}
			Corrupted.getBlocks().add(b.getLocation());
			new BukkitRunnable(){
				public void run(){
					b.getWorld().loadChunk(b.getWorld().getChunkAt(b));
					b.setType(Material.AIR);
					Corrupted.getInstance().removeBlock(b.getLocation());
					if(Corrupted.getBlocks().contains(event.getBlockPlaced().getLocation().clone().add(0, 1, 0))){
						Corrupted.getInstance().removeBlock(event.getBlockPlaced().getLocation().clone().add(0, 1, 0));
						event.getBlockPlaced().getWorld().getBlockAt(event.getBlockPlaced().getLocation().clone().add(0, 1, 0)).setType(Material.AIR);		
					}
					if(Corrupted.getBlocks().contains(event.getBlockPlaced().getLocation().clone().add(0, 2, 0))){
						Corrupted.getInstance().removeBlock(event.getBlockPlaced().getLocation().clone().add(0, 2, 0));
						event.getBlockPlaced().getWorld().getBlockAt(event.getBlockPlaced().getLocation().clone().add(0, 2, 0)).setType(Material.AIR);		
					}
					if(Corrupted.getBlocks().contains(event.getBlockPlaced().getLocation().clone().add(0, 3, 0))){
						Corrupted.getInstance().removeBlock(event.getBlockPlaced().getLocation().clone().add(0, 3, 0));
						event.getBlockPlaced().getWorld().getBlockAt(event.getBlockPlaced().getLocation().clone().add(0, 3, 0)).setType(Material.AIR);		
					}
				}
			}.runTaskLater(Corrupted.getInstance(), 5*20);
	}
	public static ConcurrentHashMap<String, BukkitTask> tagged = new ConcurrentHashMap<String, BukkitTask>();
	/**
	 * When a playre is tagged initially, set their metadata to their damager
	 * Start a task, 30 seconds later remove the metadata
	 * If said player is hit by the same player, cancel the old task and create a new one
	 * If said player is hit by a different player, cancel the old task, change the entity's metadata to the new name, and create a new one
	 */
	@EventHandler(priority = EventPriority.MONITOR)
	public void onLastPlayerDamage(EntityDamageByEntityEvent event){
		if(event.isCancelled()) return;
		if(event.getEntity() instanceof Player && event.getDamager() instanceof Player){
			final Player entity = (Player) event.getEntity();
			Player damager = (Player) event.getDamager();
			if(entity.hasMetadata("lastPlayerDamage") && ((String)entity.getMetadata("lastPlayerDamage").get(0).value()).equalsIgnoreCase(damager.getUniqueId().toString())){
				//Same Attacker as last hit
				tagged.get(entity.getUniqueId().toString()).cancel();
				tagged.remove(entity.getUniqueId().toString());
				entity.removeMetadata("lastPlayerDamage", Corrupted.getInstance());
				entity.setMetadata("lastPlayerDamage", new MyMetadata(Corrupted.getInstance(), damager.getUniqueId().toString()));
				final String value = damager.getUniqueId().toString();
				tagged.put(entity.getUniqueId().toString(), new BukkitRunnable(){
					public void run(){
						if(entity.hasMetadata("lastPlayerDamage") && ((String)entity.getMetadata("lastPlayerDamage").get(0).value()).equalsIgnoreCase(value)){
							entity.removeMetadata("lastPlayerDamage", Corrupted.getInstance());
							tagged.remove(entity.getUniqueId().toString());
						}

					}
				}.runTaskLater(Corrupted.getInstance(), 30*20));
				return;
			}else if(entity.hasMetadata("lastPlayerDamage")){
				tagged.get(entity.getUniqueId().toString()).cancel();
				tagged.remove(entity.getUniqueId().toString());
				entity.removeMetadata("lastPlayerDamage", Corrupted.getInstance());
				entity.setMetadata("lastPlayerDamage", new MyMetadata(Corrupted.getInstance(), damager.getUniqueId().toString()));
				final String value = damager.getUniqueId().toString();
				tagged.put(entity.getUniqueId().toString(), new BukkitRunnable(){
					public void run(){
						if(entity.hasMetadata("lastPlayerDamage") && ((String)entity.getMetadata("lastPlayerDamage").get(0).value()).equalsIgnoreCase(value)){
							entity.removeMetadata("lastPlayerDamage", Corrupted.getInstance());
							tagged.remove(entity.getUniqueId().toString());
						}

					}
				}.runTaskLater(Corrupted.getInstance(), 30*20));
				//New Attacker
			}
			//Initial Tag
			entity.setMetadata("lastPlayerDamage", new MyMetadata(Corrupted.getInstance(), damager.getUniqueId().toString()));
			final String value = damager.getUniqueId().toString();
			tagged.put(entity.getUniqueId().toString(), new BukkitRunnable(){
				public void run(){
					if(entity.hasMetadata("lastPlayerDamage") && ((String)entity.getMetadata("lastPlayerDamage").get(0).value()).equalsIgnoreCase(value)){
						entity.removeMetadata("lastPlayerDamage", Corrupted.getInstance());
						tagged.remove(entity.getUniqueId().toString());
					}

				}
			}.runTaskLater(Corrupted.getInstance(), 30*20));
		}
	}
	@EventHandler
	public void onLogoutKill(PlayerQuitEvent event){
		if(event.getPlayer().hasMetadata("lastPlayerDamage")){
		Player p = Bukkit.getPlayer(UUID.fromString((String) event.getPlayer().getMetadata("lastPlayerDamage").get(0).value()));
		if(p != null){
		event.getPlayer().damage(1000.0, p);
		if(tagged.containsKey(event.getPlayer().getUniqueId().toString())){
		tagged.get(event.getPlayer().getUniqueId().toString()).cancel();
		tagged.remove(event.getPlayer().getUniqueId().toString());
		}
		event.getPlayer().removeMetadata("lastPlayerDamage", Corrupted.getInstance());
		}
	}	
	}
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDeathKill(PlayerDeathEvent event){
		if(!event.getEntity().getWorld().getName().contains("Nube")) return;
		if(event.getEntity().hasMetadata("lastPlayerDamage")){
		Player p = Bukkit.getPlayer(UUID.fromString((String) event.getEntity().getMetadata("lastPlayerDamage").get(0).value()));
		if(p != null){
			if(event.getEntity().getKiller() == null){
				event.getEntity().damage(1000.0, p);
			}
		event.getEntity().setLastDamageCause(new EntityDamageByEntityEvent(p, event.getEntity(), DamageCause.ENTITY_ATTACK, 0.0001));
		tagged.get(event.getEntity().getUniqueId().toString()).cancel();
		tagged.remove(event.getEntity().getUniqueId().toString());
		event.getEntity().removeMetadata("lastPlayerDamage", Corrupted.getInstance());
		}
	}
	}
}
