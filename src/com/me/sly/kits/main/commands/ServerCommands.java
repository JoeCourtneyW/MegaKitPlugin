 package com.me.sly.kits.main.commands;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.me.sly.kits.main.Chat;
import com.me.sly.kits.main.Corrupted;
import com.me.sly.kits.main.classes.Classes;
import com.me.sly.kits.main.enums.AccessMode;
import com.me.sly.kits.main.enums.CustomBlock;
import com.me.sly.kits.main.enums.DatabaseColumn;
import com.me.sly.kits.main.enums.Group;

public class ServerCommands implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args) {
		if (label.equalsIgnoreCase("broadcast")) {
			if(sender.hasPermission("Taken.broadcast")){
				if(args.length > 0){
			String msg = "";
			for (String a : args) {
				msg = msg + " " + a;
			}
			
			Chat.broadcastPrefixedMessage(msg.substring(1).replace("&", "§"));
				}else{
					Chat.sendPlayerMessage(sender, "§cSyntax: /broadcast <msg>");
				}
			}
		} else if (label.equalsIgnoreCase("cleardb")) {
			if (args.length == 1) {
				Player p = Bukkit.getPlayer(args[0]);
				if (p == null)
					return false;
				if (sender instanceof Player && sender.getName().equalsIgnoreCase("SpotifyPremium")) {
					PreparedStatement sql;
					try {
						sql = Corrupted.getConnection().prepareStatement("DELETE FROM `Players` WHERE UUID=?");
						sql.setString(1, p.getUniqueId().toString());
						sql.execute();
						sql.close();
						Corrupted.getDatabaseCache().remove(p.getUniqueId().toString());
						Chat.sendPlayerPrefixedMessage((Player) sender, "You have wiped " + p.getName() + "'s statistics");
						p.kickPlayer("§bYour stats have been wiped.");
						Chat.broadcastPrefixedMessage(sender.getName() + " has wiped " + p.getName() + "'s statistics", "Taken.wipe");
						System.out.println("[Taken Kits] " + sender.getName() + " has wiped " + p.getName() + "'s statistics");
					} catch (SQLException e) {
						e.printStackTrace();
					}

				}
			}
		} else if (label.equalsIgnoreCase("GCM")) {
			if (sender.hasPermission("Taken.GCM")) {
				if (args.length == 1) {
					if (Corrupted.getInstance().isNumeric(args[0])) {
						int r = Integer.parseInt(args[0]);
						int oldMult = Corrupted.getGlobalCoinMultiplier();
						Corrupted.setGlobalCoinMultiplier(r);
						if (r == 1 && oldMult == 1){
							return false;						}
						if (r < 1) {
							sender.sendMessage("§cMultiplier must be greater than 1!");
							return false;
						}
						if (r == 1) {
							if (oldMult == 2) {
								Chat.broadcastPrefixedMessage("§6Double Coins §cdeactivated!");
							} else if (oldMult == 3) {
								Chat.broadcastPrefixedMessage("§6Triple Coins §cdeactivated!");
							} else {
								Chat.broadcastPrefixedMessage("§6" + oldMult + "x Coins §cdeactivated!");
							}
						} else if (r == 2) {
							Chat.broadcastPrefixedMessage("§6Double Coins §aactivated!");
						} else if (r == 3) {
							Chat.broadcastPrefixedMessage("§6Triple Coins §aactivated!");
						} else {
							Chat.broadcastPrefixedMessage("§6" + r + "x Coins §aactivated!");
						}
					}
				}
			}
		} else if (label.equalsIgnoreCase("Package")) {
			if (sender instanceof ConsoleCommandSender) {
				if (args.length > 1) {
										if (!Bukkit.getOfflinePlayer(args[1]).isOnline()) {
											Chat.sendPlayerPrefixedMessage(sender, "§eCould not find the player §b" + args[1]);
											return false;
										}
										Player recieve = Bukkit.getPlayer(args[1]);
										if (Group.isGroup(args[0].toUpperCase())) {
											String oldGroup = (String) Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).get("Rank");
											if (oldGroup.contains(",")) {
												Chat.sendPlayerPrefixedMessage(sender, "§b" + recieve.getName() + "§e is already a member of 2 groups!");
											} else {
												oldGroup = Group.valueOf(args[0].toUpperCase()).name();
												Group rank = Group.valueOf(args[0].toUpperCase());
												Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Rank", oldGroup);
												switch(rank){
												case SUB:
													Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Coins", ((int)Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).get("Coins")) + 2500);
													Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Spins", ((int)Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).get("Spins")) + 10);
													CustomBlock.SPONGE.setBlock(recieve, true);
													CustomBlock.ICE.setBlock(recieve, true);
													Chat.broadcastPrefixedMessage("§b" + recieve.getName() + " §ehas bought §bSubscriber §e@ §bTakenGaming.Buycraft.Net" );
														Chat.sendPlayerPrefixedMessage(recieve, "§b+2500 coins!");
														Chat.sendPlayerPrefixedMessage(recieve, "§b+10 spins!");
														Chat.sendPlayerPrefixedMessage(recieve, "§eThank you for donating, §b" + recieve.getName() + "§e. Your package has been credited to your account!");
													break;
												case SUBPLUS:
													Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Coins", ((int)Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).get("Coins")) + 5000);
													Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Spins", ((int)Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).get("Spins")) + 15);
													CustomBlock.SPONGE.setBlock(recieve, true);
													CustomBlock.ICE.setBlock(recieve, true);
													CustomBlock.BEDROCK.setBlock(recieve, true);
													CustomBlock.MELON.setBlock(recieve, true);
													Chat.broadcastPrefixedMessage("§b" + recieve.getName() + " §ehas bought §bSubscriber+ §e@ §bTakenGaming.Buycraft.Net" );
													Classes.SPIDER.setKit(recieve, true);
													Chat.sendPlayerPrefixedMessage(recieve, "§b+3750 coins!");
													Chat.sendPlayerPrefixedMessage(recieve, "§b+15 spins!");
													Chat.sendPlayerPrefixedMessage(recieve, "§eThank you for donating, §b" + recieve.getName() + "§e. Your package has been credited to your account!");
													break;
												case SUBPLUSPLUS:
													Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Coins", ((int)Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).get("Coins")) + 5000);
													Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Spins", ((int)Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).get("Spins")) + 25);
													CustomBlock.SPONGE.setBlock(recieve, true);
													CustomBlock.ICE.setBlock(recieve, true);
													CustomBlock.BEDROCK.setBlock(recieve, true);
													CustomBlock.MELON.setBlock(recieve, true);
													CustomBlock.TNT.setBlock(recieve, true);
													CustomBlock.RAINBOW_WOOL.setBlock(recieve, true);
													Chat.broadcastPrefixedMessage("§b" + recieve.getName() + " §ehas bought §bSubscriber++ §e@ §bTakenGaming.Buycraft.Net" );
													Classes.SPIDER.setKit(recieve, true);
													Classes.PIGMAN.setKit(recieve, true);
													Chat.sendPlayerPrefixedMessage(recieve, "§b+5000 coins!");
													Chat.sendPlayerPrefixedMessage(recieve, "§b+25 spins!");
													Chat.sendPlayerPrefixedMessage(recieve, "§eThank you for donating, §b" + recieve.getName() + "§e. Your package has been credited to your account!");
													break;
												case PATRON:
													Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Coins", ((int)Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).get("Coins")) + 10000);
													Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Spins", ((int)Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).get("Spins")) + 50);
													CustomBlock.SPONGE.setBlock(recieve, true);
													CustomBlock.ICE.setBlock(recieve, true);
													CustomBlock.BEDROCK.setBlock(recieve, true);
													CustomBlock.MELON.setBlock(recieve, true);
													CustomBlock.TNT.setBlock(recieve, true);
													CustomBlock.RAINBOW_WOOL.setBlock(recieve, true);
													Chat.broadcastPrefixedMessage("§b" + recieve.getName() + " §ehas bought §dPatron §e@ §bTakenGaming.Buycraft.Net" );
													Classes.SPIDER.setKit(recieve, true);
													Classes.PIGMAN.setKit(recieve, true);
													Chat.sendPlayerPrefixedMessage(recieve, "§b+10000 coins!");
													Chat.sendPlayerPrefixedMessage(recieve, "§b+50 spins!");
													Chat.sendPlayerPrefixedMessage(recieve, "§eThank you for donating, §b" + recieve.getName() + "§e. Your package has been credited to your account!");
													break;
												default:
													break;
												}
												Corrupted.getInstance().updateScoreboard(recieve);
												Corrupted.getInstance().updateDatabase(recieve);
												if (Bukkit.getPlayer(recieve.getUniqueId()) != null) {
													Group.unregisterPlayerPermissions(Bukkit.getPlayer(recieve.getUniqueId()));
													Group.registerPlayerPermissions(Bukkit.getPlayer(recieve.getUniqueId()));
												}

											}
										} else {
											if(args[0].toUpperCase().equalsIgnoreCase("SUBtoSUBPLUS")){
												String oldGroup = Group.valueOf("SUBPLUS").name();
												Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Rank", oldGroup);
												Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Coins", ((int)Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).get("Coins")) + 1250);
												Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Spins", ((int)Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).get("Spins")) + 5);
												CustomBlock.SPONGE.setBlock(recieve, true);
												CustomBlock.ICE.setBlock(recieve, true);
												CustomBlock.BEDROCK.setBlock(recieve, true);
												CustomBlock.MELON.setBlock(recieve, true);
												Chat.broadcastPrefixedMessage("§b" + recieve.getName() + " §ehas upgraded from §bSubscriber §eto §bSubscriber+ §e@ §bTakenGaming.Buycraft.Net" );
												Classes.SPIDER.setKit(recieve, true);
												Chat.sendPlayerPrefixedMessage(recieve, "§b+1250 coins!");
												Chat.sendPlayerPrefixedMessage(recieve, "§b+5 spins!");
												Chat.sendPlayerPrefixedMessage(recieve, "§eThank you for donating, §b" + recieve.getName() + "§e. Your package has been credited to your account!");
											}else if(args[0].toUpperCase().equalsIgnoreCase("SUBPLUStoSUBPLUSPLUS")){
												String oldGroup = Group.valueOf("SUBPLUSPLUS").name();
												Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Group", oldGroup);
												Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Coins", ((int)Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).get("Coins")) + 1250);
												Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Spins", ((int)Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).get("Spins")) + 10);
												CustomBlock.SPONGE.setBlock(recieve, true);
												CustomBlock.ICE.setBlock(recieve, true);
												CustomBlock.BEDROCK.setBlock(recieve, true);
												CustomBlock.MELON.setBlock(recieve, true);
												CustomBlock.TNT.setBlock(recieve, true);
												CustomBlock.RAINBOW_WOOL.setBlock(recieve, true);
												Chat.broadcastPrefixedMessage("§b" + recieve.getName() + " §ehas upgraded from §bSubscriber+ §eto §bSubscriber++ §e@ §bTakenGaming.Buycraft.Net" );
												Classes.SPIDER.setKit(recieve, true);
												Classes.PIGMAN.setKit(recieve, true);
												Chat.sendPlayerPrefixedMessage(recieve, "§b+1250 coins!");
												Chat.sendPlayerPrefixedMessage(recieve, "§b+10 spins!");
												Chat.sendPlayerPrefixedMessage(recieve, "§eThank you for donating, §b" + recieve.getName() + "§e. Your package has been credited to your account!");
											}else if(args[0].toUpperCase().equalsIgnoreCase("SUBPLUSPLUStoPATRON")){
												String oldGroup = Group.valueOf("PATRON").name();
												Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Rank", oldGroup);
												Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Coins", ((int)Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).get("Coins")) + 5000);
												Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Spins", ((int)Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).get("Spins")) + 25);
												Chat.broadcastPrefixedMessage("§b" + recieve.getName() + " §ehas upgraded from §bSubscriber++ §eto §dPatron §e@ §bTakenGaming.Buycraft.Net" );
												Chat.sendPlayerPrefixedMessage(recieve, "§b+5000 coins!");
												Chat.sendPlayerPrefixedMessage(recieve, "§b+25 spins!");
												Chat.sendPlayerPrefixedMessage(recieve, "§eThank you for donating, §b" + recieve.getName() + "§e. Your package has been credited to your account!");
											}else{
											Chat.sendPlayerPrefixedMessage(sender, "§eGroup §b" + args[2].toUpperCase() + "§e does not exist!");
											}
											}
				}
			}
		} else if (label.equalsIgnoreCase("setaccess")){
			if(sender.hasPermission("Taken.setaccess")){
				if(args.length == 1){
					AccessMode[] modes = AccessMode.values();
					for(int i = 0; i < modes.length; i++){
						if(modes[i].name().equalsIgnoreCase(args[0].toUpperCase())){
							Corrupted.setMode(modes[i]);
							if(sender instanceof Player){
							Chat.sendPlayerPrefixedMessage(sender, "§eYou have set the Access Mode to: §b" + modes[i]);
							}
							break;
						}else if (i == modes.length - 1){
							if(sender instanceof Player){
								Chat.sendPlayerPrefixedMessage(sender, "§cError: Invalid access mode!");
							}
						}
					}
				}else{
					if(sender instanceof Player){
						Chat.sendPlayerMessage(sender, "§cSyntax: /setaccess <access mode>");

					}
				}
			}
		}else if (label.equalsIgnoreCase("db")){
			if(sender.hasPermission("Taken.db")){
				if(args.length >= 3){
					try{
						Integer.parseInt(args[2]);
					}catch(NumberFormatException e){
						if(sender instanceof Player){
							Chat.sendPlayerPrefixedMessage((Player)sender, "§c" + " " + args[2] + " is not a valid number!");
							return false;
						}
					}
					final ArrayList<UUID> ids = new ArrayList<UUID>();
					Bukkit.getScheduler().runTaskAsynchronously(Corrupted.getInstance(), new Runnable() {
						public void run() {
							try {
								for (String s : Corrupted.getDatabaseCache().keySet()) {
									if (((String) Corrupted.getDatabaseCache().get(s).get("PlayerName")).equalsIgnoreCase(args[1])) {
										ids.add(UUID.fromString(s));
									}
								}
								Bukkit.getScheduler().runTask(Corrupted.getInstance(), new Runnable() {
									public void run() {
										if (!Corrupted.getDatabaseCache().containsKey(ids.get(0).toString())) {
											Chat.sendPlayerPrefixedMessage(sender, "§eCould not find the player §b" + args[1]);
											return;
										}
										OfflinePlayer recieve = Bukkit.getOfflinePlayer(ids.get(0));
										boolean isOnline = Bukkit.getPlayer(recieve.getUniqueId()) != null;
										String columnName = args[0];
										if(DatabaseColumn.isColumn(columnName)){
											Corrupted.getDatabaseCache().get(ids.get(0).toString()).put(DatabaseColumn.getColumn(columnName).getDatabaseName(), Integer.parseInt(args[2]));
											Corrupted.getInstance().updateDatabase(recieve);
											Chat.sendPlayerPrefixedMessage(sender, "§eColumn §b" + columnName + "§e changed to §b" + args[2] + " §efor §b" + (String) Corrupted.getDatabaseCache().get(ids.get(0).toString()).get("PlayerName"));
											if(isOnline){
												Corrupted.getInstance().updateScoreboard(Bukkit.getPlayer(recieve.getUniqueId()));
											}
										}else{
											Chat.sendPlayerPrefixedMessage(sender, "§eColumn §b" + columnName + "§e does not exist!");
										}
									}
									});
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						});

				}else{
					Chat.sendPlayerMessage((Player)sender, "§cSyntax: /db <column> <player> <value>");
				}
			}
		}
		return false;
	}

}
