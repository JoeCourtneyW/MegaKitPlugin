package com.me.sly.kits.main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.me.sly.kits.main.classes.Classes;
import com.me.sly.kits.main.enums.CustomBlock;
import com.me.sly.kits.main.enums.Trail;
import com.me.sly.kits.main.enums.VanityArmor;
import com.me.sly.kits.main.resources.IconMenu;

public class SlotSpin {

	private Player p;
	private HashMap<Integer, ItemStack> items;
	private Inventory inv;
	private Plugin plugin;
	public static HashMap<String, SlotSpin> spins = new HashMap<String, SlotSpin>();
	public SlotSpin(Player p, Plugin plugin){
		this.p = p;
		this.plugin = plugin;
		this.inv = Bukkit.createInventory(null, 27, "Slot Machine");
		this.items = new HashMap<Integer, ItemStack>();
		loadItems();
		loadInitialInv();
		this.p.openInventory(inv);
		Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Spins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Spins")-1);
		Corrupted.getInstance().updateScoreboard(p);
		spins.put(p.getName(), this);
		startRoll();
	}
	public Inventory getInventory(){
		return this.inv;
	}
	private void startRoll(){
		final BukkitTask r = new BukkitRunnable(){
			public void run(){
				nextItem();
				p.playSound(p.getLocation(), Sound.PISTON_EXTEND, 1,3);
			}
		}.runTaskTimer(plugin, 2, 2);
		new BukkitRunnable(){
			public void run(){
				r.cancel();
				nextItem();
				final BukkitTask r = new BukkitRunnable(){
					public void run(){
						nextItem();
						p.playSound(p.getLocation(), Sound.PISTON_EXTEND, 1,3);
					}
				}.runTaskTimer(plugin, 7, 7);
				new BukkitRunnable(){
					public void run(){
						r.cancel();
						nextItem();
						p.playSound(p.getLocation(), Sound.PISTON_EXTEND, 1,3);
						finalItem();
					}
				}.runTaskLater(plugin, (7*7));
			}
		}.runTaskLater(plugin, 15*2);
	}
	private void loadInitialInv(){
		for(int i = 9; i < 18; i++){
			inv.setItem(i, randomItem());
		}
		inv.setItem(22,IconMenu.setItemNameAndLore(new ItemStack(Material.NETHER_STAR, 1), "§b", new String[0]));
		inv.setItem(4,IconMenu.setItemNameAndLore(new ItemStack(Material.NETHER_STAR, 1), "§d", new String[0]));
		for(int i = 0; i < 27; i++){
			if(inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR){
				inv.setItem(i,IconMenu.setItemNameAndLore(new ItemStack(Material.STAINED_GLASS_PANE, 1, (short)7), "§d", new String[0]));
			}
		}
	}
	private void loadItems(){
		items.put(1, IconMenu.setItemNameAndLore(new ItemStack(Material.PAPER, 1), "§d$10 Buycraft Voucher", new String[0]));
		items.put(6, IconMenu.setItemNameAndLore(new ItemStack(Material.LAVA_BUCKET, 1), "§dMagma Trail", new String[0]));
		items.put(11, IconMenu.setItemNameAndLore(new ItemStack(Material.WATER_BUCKET, 1), "§dWater Trail", new String[0]));
		items.put(21, IconMenu.setItemNameAndLore(new ItemStack(Material.ENCHANTED_BOOK, 1), "§eMagic Trail", new String[0]));
		items.put(31, IconMenu.setItemNameAndLore(new ItemStack(Material.SULPHUR, 1), "§eSmoke Trail", new String[0]));
		items.put(41, IconMenu.setItemNameAndLore(new ItemStack(Material.BONE, 1), "§7Prestige: §bSkeleton", new String[0]));
		items.put(49, IconMenu.setItemNameAndLore(new ItemStack(Material.TNT, 1), "§7Prestige: §bCreeper", new String[0]));
		items.put(57, IconMenu.setItemNameAndLore(new ItemStack(Material.SKULL_ITEM, 1, (short)2), "§7Prestige: §bZombie", new String[0]));
		items.put(65, IconMenu.setItemNameAndLore(new ItemStack(Material.ENDER_PEARL, 1), "§7Prestige: §bEnderman", new String[0]));
		items.put(73, IconMenu.setItemNameAndLore(new ItemStack(Material.NETHER_STAR, 1), "§7Prestige: §bHerobrine", new String[0]));
		items.put(81, IconMenu.setItemNameAndLore(new ItemStack(Material.SKULL_ITEM, 1, (short)1), "§7Prestige: §bDreadlord", new String[0]));
		items.put(89, IconMenu.setItemNameAndLore(new ItemStack(Material.IRON_BLOCK, 1), "§7Prestige: §bGolem", new String[0]));
		items.put(97, IconMenu.setItemNameAndLore(new ItemStack(Material.FIREWORK, 1), "§7Prestige: §bArcanist", new String[0]));
		items.put(105, IconMenu.setItemNameAndLore(new ItemStack(Material.BLAZE_POWDER, 1), "§7Prestige: §bBlaze", new String[0]));
		items.put(113, IconMenu.setItemNameAndLore(new ItemStack(Material.MONSTER_EGG, 1, (short)95), "§7Prestige: §bShaman", new String[0]));
		items.put(121, IconMenu.setItemNameAndLore(new ItemStack(Material.WEB, 1), "§7Prestige: §bSpider", new String[0]));
		items.put(128, IconMenu.setItemNameAndLore(new ItemStack(Material.GRILLED_PORK, 1), "§7Prestige: §bPigman", new String[0]));
		items.put(136, IconMenu.setItemNameAndLore(new ItemStack(Material.STONE_AXE, 1), "§7Prestige: §bPirate", new String[0]));
		items.put(144, IconMenu.setItemNameAndLore(new ItemStack(Material.INK_SACK, 1), "§7Prestige: §bSquid", new String[0]));
		items.put(152, IconMenu.setItemNameAndLore(new ItemStack(Material.BOW, 1), "§7Prestige: §bHunter", new String[0]));
		items.put(176, IconMenu.setItemNameAndLore(new ItemStack(Material.SLIME_BALL, 1), "§bSlime Trail", new String[0]));
		items.put(201, IconMenu.setItemNameAndLore(new ItemStack(Material.SNOW_BALL, 1), "§bSnow Trail", new String[0]));
		items.put(230, IconMenu.setItemNameAndLore(new ItemStack(Material.DIAMOND_BLOCK, 1), "§6JackPot: 10,000 coins", new String[0]));
		items.put(280, IconMenu.setItemNameAndLore(new ItemStack(Material.DIAMOND_CHESTPLATE, 1), "§eDiamond Vanity Armor", new String[0]));
		items.put(330, IconMenu.setItemNameAndLore(new ItemStack(Material.ENCHANTMENT_TABLE, 1), "§aEnchanted Trail", new String[0]));
		items.put(380, IconMenu.setItemNameAndLore(new ItemStack(Material.BEDROCK, 1), "§aVoid Trail", new String[0]));
		items.put(430, IconMenu.setItemNameAndLore(new ItemStack(Material.PORTAL, 1), "§aPortal Trail", new String[0]));
		items.put(530, IconMenu.setItemNameAndLore(new ItemStack(Material.MOSSY_COBBLESTONE, 1), "§aMossy Stone Block", new String[0]));
		items.put(510, IconMenu.setItemNameAndLore(new ItemStack(Material.LEAVES, 1), "§aLeaves Block", new String[0]));
		items.put(730, IconMenu.setItemNameAndLore(new ItemStack(Material.BOOKSHELF, 1), "§aBookshelf Block", new String[0]));
		items.put(830, IconMenu.setItemNameAndLore(new ItemStack(Material.NETHER_BRICK, 1), "§aNether Brick Block", new String[0]));
		items.put(1930, IconMenu.setItemNameAndLore(new ItemStack(Material.OBSIDIAN, 1), "§aObsidian Block", new String[0]));
		items.put(1030, IconMenu.setItemNameAndLore(new ItemStack(Material.HAY_BLOCK, 1), "§aHay Bale Block", new String[0]));
		items.put(1130, IconMenu.setItemNameAndLore(new ItemStack(Material.GLOWSTONE, 1), "§eGlowstone Block", new String[0]));
		items.put(1280, IconMenu.setItemNameAndLore(new ItemStack(Material.GOLD_CHESTPLATE, 1), "§dGold Vanity Armor", new String[0]));
		items.put(1380, IconMenu.setItemNameAndLore(new ItemStack(Material.MONSTER_EGG, 1, (short)95), "§bShaman Kit", new String[0]));
		items.put(1580, IconMenu.setItemNameAndLore(new ItemStack(Material.DIAMOND, 1), "§6Big Win: 1,500 coins", new String[0]));
		items.put(1680, IconMenu.setItemNameAndLore(new ItemStack(Material.SUGAR, 1), "§e2 hour 2x Booster", new String[0]));
		items.put(2080, IconMenu.setItemNameAndLore(new ItemStack(Material.IRON_CHESTPLATE, 1), "§bIron Vanity Armor", new String[0]));
		items.put(2530, IconMenu.setItemNameAndLore(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1), "§aChainmail Vanity Armor", new String[0]));
		items.put(3030, IconMenu.setItemNameAndLore(new ItemStack(Material.TRIPWIRE_HOOK, 2), "§b2 Spin Pack", new String[0]));
		items.put(3530, IconMenu.setItemNameAndLore(new ItemStack(Material.GOLD_BLOCK, 1), "§6Payout: 750 coins", new String[0]));
		items.put(4530, IconMenu.setItemNameAndLore(new ItemStack(Material.GLOWSTONE_DUST, 1), "§e30 minute 2x Coins", new String[0]));
		items.put(5530, IconMenu.setItemNameAndLore(new ItemStack(Material.TRIPWIRE_HOOK, 1), "§bRespin", new String[0]));
		items.put(6530, IconMenu.setItemNameAndLore(new ItemStack(Material.GOLD_INGOT, 1), "§6Doubleup: 500 coins", new String[0]));
		items.put(10000, IconMenu.setItemNameAndLore(new ItemStack(Material.GOLD_NUGGET, 1), "§6Unlucky: 50 coins", new String[0]));
	}
	private void nextItem(){
		for(int i = 16; i > 8; i--){
			inv.setItem(i+1, inv.getItem(i));
		}
		inv.setItem(9, randomItem());
	}
	private void finalItem(){
		ItemStack Final = new ItemStack(Material.ANVIL);
		int rand = Corrupted.getInstance().rand(0, 9999);
		ArrayList<Integer> ints = new ArrayList<Integer>();
		for(int r : items.keySet()){
			ints.add(r);
		}
		Collections.sort(ints);
		for(int r : ints){
			if(rand < r){
				Final = items.get(r);
				break;
			}
		}
		for(int i = 16; i < 9; i--){
			inv.setItem(i+1, inv.getItem(i));
		}
		inv.setItem(9, Final);
		final ItemStack reward = Final;
		final BukkitTask r = new BukkitRunnable(){
			public void run(){
				p.playSound(p.getLocation(), Sound.PISTON_EXTEND, 1,3);
				nextItem();
			}
		}.runTaskTimer(plugin, 10, 15);
		new BukkitRunnable(){
			@SuppressWarnings("deprecation")
			public void run(){
				r.cancel();
				spins.remove(p.getName());
				Chat.sendPlayerPrefixedMessage(p, "§7You have found: " + reward.getItemMeta().getDisplayName());
				if (reward.getItemMeta().getDisplayName().contains("Unlucky")){
					Chat.sendPlayerPrefixedMessage(p, "§e+50 coins");
					p.playSound(p.getLocation(), Sound.WOLF_WHINE, 0.5F, 3);
					Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+50);
				} else if (reward.getItemMeta().getDisplayName().contains("Doubleup")){
					Chat.sendPlayerPrefixedMessage(p, "§e+500 coins");
					Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+500);				
				} else if (reward.getItemMeta().getDisplayName().contains("Payout")){
					Chat.sendPlayerPrefixedMessage(p, "§e+750 coins");
					Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+750);				
				}  else if (reward.getItemMeta().getDisplayName().contains("Big Win")){
					Chat.sendPlayerPrefixedMessage(p, "§e+1500 coins");
					Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+1500);				
				} else if (reward.getItemMeta().getDisplayName().contains("JackPot")){
					Chat.sendPlayerPrefixedMessage(p, "§e+10000 coins");
					Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+10000);				
				} else if (reward.getItemMeta().getDisplayName().contains("Respin")){
					Chat.sendPlayerPrefixedMessage(p, "§e+1 spin");
					Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Spins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Spins")+1);				
				} else if (reward.getItemMeta().getDisplayName().contains("Spin Pack")){
					Chat.sendPlayerPrefixedMessage(p, "§e+2 spins");
					Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Spins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Spins")+2);				
				} else if (reward.getItemMeta().getDisplayName().contains("Glowstone")){		
					if(!CustomBlock.GLOWSTONE.hasBlock(p)) CustomBlock.GLOWSTONE.setBlock(p, true);
					else{
						int coins = Corrupted.getInstance().rand(50, 100);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
					} else if (reward.getItemMeta().getDisplayName().contains("Hay Bale")){				
					if(!CustomBlock.HAY.hasBlock(p)) CustomBlock.HAY.setBlock(p, true);
					else{
						int coins = Corrupted.getInstance().rand(50, 100);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Obsidian")){				
					if(!CustomBlock.OBSIDIAN.hasBlock(p)) CustomBlock.OBSIDIAN.setBlock(p, true);
					else{
						int coins = Corrupted.getInstance().rand(50, 100);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Nether Brick")){				
					if(!CustomBlock.NETHER_BRICK.hasBlock(p)) CustomBlock.NETHER_BRICK.setBlock(p, true);
					else{
						int coins = Corrupted.getInstance().rand(50, 100);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Bookshelf")){				
					if(!CustomBlock.BOOKSHELF.hasBlock(p)) CustomBlock.BOOKSHELF.setBlock(p, true);
					else{
						int coins = Corrupted.getInstance().rand(50, 100);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Leaves")){				
					if(!CustomBlock.LEAF.hasBlock(p)) CustomBlock.LEAF.setBlock(p, true);
					else{
						int coins = Corrupted.getInstance().rand(50, 100);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Mossy")){				
					if(!CustomBlock.MOSS_STONE.hasBlock(p)) CustomBlock.MOSS_STONE.setBlock(p, true);
					else{
						int coins = Corrupted.getInstance().rand(50, 100);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Chainmail")){				
					if(!VanityArmor.CHAINMAIL.hasVanityArmor(p)) VanityArmor.CHAINMAIL.setVanityArmor(p, true);
					else{
						int coins = Corrupted.getInstance().rand(50, 100);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Iron")){				
					if(!VanityArmor.IRON.hasVanityArmor(p)) VanityArmor.IRON.setVanityArmor(p, true);
					else{
						int coins = Corrupted.getInstance().rand(50, 100);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Gold")){				
					if(!VanityArmor.GOLD.hasVanityArmor(p)) VanityArmor.GOLD.setVanityArmor(p, true);
					else{
						int coins = Corrupted.getInstance().rand(50, 100);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Diamond")){				
					if(!VanityArmor.DIAMOND.hasVanityArmor(p)) VanityArmor.DIAMOND.setVanityArmor(p, true);
					else{
						int coins = Corrupted.getInstance().rand(100, 200);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Magma")){
					if(!Trail.MAGMA.hasTrail(p)) Trail.MAGMA.setTrail(p, true);
					else{
						int coins = Corrupted.getInstance().rand(150, 250);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Water")){				
					if(!Trail.WATER.hasTrail(p)) Trail.WATER.setTrail(p, true);
					else{
						int coins = Corrupted.getInstance().rand(150, 250);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Magic")){				
					if(!Trail.MAGIC.hasTrail(p)) Trail.MAGIC.setTrail(p, true);
					else{
						int coins = Corrupted.getInstance().rand(100, 200);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Smoke")){				
					if(!Trail.SMOKE.hasTrail(p)) Trail.SMOKE.setTrail(p, true);
					else{
						int coins = Corrupted.getInstance().rand(100, 200);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Void")){				
					if(!Trail.VOID.hasTrail(p)) Trail.VOID.setTrail(p, true);
					else{
						int coins = Corrupted.getInstance().rand(50, 100);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Portal")){				
					if(!Trail.PORTAL.hasTrail(p)) Trail.PORTAL.setTrail(p, true);
					else{
						int coins = Corrupted.getInstance().rand(50, 100);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Enchanted")){				
					if(!Trail.ENCHANTED.hasTrail(p)) Trail.ENCHANTED.setTrail(p, true);
					else{
						int coins = Corrupted.getInstance().rand(50, 100);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Slime")){				
					if(!Trail.SLIME.hasTrail(p)) Trail.SLIME.setTrail(p, true);
					else{
						int coins = Corrupted.getInstance().rand(50, 100);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Snow")){				
					if(!Trail.SNOW.hasTrail(p)) Trail.SNOW.setTrail(p, true);
					else{
						int coins = Corrupted.getInstance().rand(50, 100);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Shaman Kit")){				
					if(!Classes.SHAMAN.hasKit(p)) Classes.SHAMAN.setKit(p, true);
					else{
						int coins = Corrupted.getInstance().rand(100, 150);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Shaman")){				
					if(!Classes.SHAMAN.isPrestige(p)) Classes.SHAMAN.setPrestiged(p, true);
					else{
						int coins = Corrupted.getInstance().rand(150, 200);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Skeleton")){				
					if(!Classes.SKELETON.isPrestige(p)) Classes.SKELETON.setPrestiged(p, true);
					else{
						int coins = Corrupted.getInstance().rand(150, 200);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Creeper")){				
					if(!Classes.CREEPER.isPrestige(p)) Classes.CREEPER.setPrestiged(p, true);
					else{
						int coins = Corrupted.getInstance().rand(150, 200);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Zombie")){				
					if(!Classes.ZOMBIE.isPrestige(p)) Classes.ZOMBIE.setPrestiged(p, true);
					else{
						int coins = Corrupted.getInstance().rand(150, 200);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Enderman")){				
					if(!Classes.ENDERMAN.isPrestige(p)) Classes.ENDERMAN.setPrestiged(p, true);
					else{
						int coins = Corrupted.getInstance().rand(150, 200);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Herobrine")){				
					if(!Classes.HEROBRINE.isPrestige(p)) Classes.HEROBRINE.setPrestiged(p, true);
					else{
						int coins = Corrupted.getInstance().rand(150, 200);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Dreadlord")){				
					if(!Classes.DREADLORD.isPrestige(p)) Classes.DREADLORD.setPrestiged(p, true);
					else{
						int coins = Corrupted.getInstance().rand(150, 200);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Arcanist")){				
					if(!Classes.ARCANIST.isPrestige(p)) Classes.ARCANIST.setPrestiged(p, true);
					else{
						int coins = Corrupted.getInstance().rand(150, 200);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Golem")){				
					if(!Classes.GOLEM.isPrestige(p)) Classes.GOLEM.setPrestiged(p, true);
					else{
						int coins = Corrupted.getInstance().rand(150, 200);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Pigman")){				
					if(!Classes.PIGMAN.isPrestige(p)) Classes.PIGMAN.setPrestiged(p, true);
					else{
						int coins = Corrupted.getInstance().rand(150, 200);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Blaze")){				
					if(!Classes.BLAZE.isPrestige(p)) Classes.BLAZE.setPrestiged(p, true);
					else{
						int coins = Corrupted.getInstance().rand(150, 200);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("Spider")){				
					if(!Classes.SPIDER.isPrestige(p)) Classes.SPIDER.setPrestiged(p, true);
					else{
						int coins = Corrupted.getInstance().rand(150, 200);
						Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).put("Coins", (int)Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Coins")+coins);	
					Chat.sendPlayerPrefixedMessage(p, "§eBut you already had it, so instead you found §b" + coins + " coins" );
					}
				} else if (reward.getItemMeta().getDisplayName().contains("2 hour")){	
					Corrupted.getInstance().addBooster(p, 7200);
				} else if (reward.getItemMeta().getDisplayName().contains("30 minute")){	
					Corrupted.getInstance().addBooster(p, 1800);
				} else if (reward.getItemMeta().getDisplayName().contains("$10 Buycraft")){	
					Chat.broadcastPrefixedMessage("§b" + p.getName() + " §ehas found a §d$10 Buycraft Voucher §ein the Slot Machine!");
					Chat.sendPlayerPrefixedMessage(p, "§eContact an admin to redeem this voucher");
					Corrupted.getInstance().getConfig().set("BUYCRAFTVOUCHER." + p.getName(), "" + new Date().getDay() + "" + new Date().getMonth());
				}
				Corrupted.getInstance().updateDatabase(p);
				Corrupted.getInstance().updateScoreboard(p);
			}
		}.runTaskLater(plugin, 20*3);
	}
	private ItemStack randomItem(){
		int rand = Corrupted.getInstance().rand(0, items.size()-1);
		return (ItemStack) items.values().toArray()[rand];
	}
}
