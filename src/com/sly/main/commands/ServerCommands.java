package com.sly.main.commands;

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

import com.sly.main.Server;
import com.sly.main.database.DatabaseColumn;
import com.sly.main.enums.AccessMode;
import com.sly.main.enums.CustomBlock;
import com.sly.main.enums.Group;
import com.sly.main.kits.Kits;
import com.sly.main.player.PlayerModel;
import com.sly.main.resources.MathUtil;
import com.sly.main.server.Chat;

public class ServerCommands implements CommandExecutor
{

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args) {
		if (label.equalsIgnoreCase("broadcast")) {
			if (sender.hasPermission("Taken.broadcast")) {
				if (args.length > 0) {
					String msg = "";
					for (String a : args) {
						msg = msg + " " + a;
					}

					Chat.broadcastPrefixedMessage(msg.substring(1).replace("&", "§"));
				} else {
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
						sql = Server.getConnection().prepareStatement("DELETE FROM `Players` WHERE UUID=?");
						sql.setString(1, p.getUniqueId().toString());
						sql.execute();
						sql.close();
						Server.getDatabaseCache().remove(p.getUniqueId().toString());
						Chat.sendPlayerPrefixedMessage((Player) sender,
								"You have wiped " + p.getName() + "'s statistics");
						p.kickPlayer("§bYour stats have been wiped.");
						Chat.broadcastPrefixedMessage(sender.getName() + " has wiped " + p.getName() + "'s statistics",
								"Taken.wipe");
						System.out.println(
								"[Taken Kits] " + sender.getName() + " has wiped " + p.getName() + "'s statistics");
					} catch (SQLException e) {
						e.printStackTrace();
					}

				}
			}
		} else if (label.equalsIgnoreCase("GCM")) {
			if (sender.hasPermission("Taken.GCM")) {
				if (args.length == 1) {
					if (MathUtil.isNumeric(args[0])) {
						int r = Integer.parseInt(args[0]);
						int oldMult = Server.getGlobalCoinMultiplier();
						Server.setGlobalCoinMultiplier(r);
						if (r == 1 && oldMult == 1) {
							return false;
						}
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
					Player receive = Bukkit.getPlayer(args[1]);
					PlayerModel receiveModel = PlayerModel.getPlayerModel(receive);
					if (Group.isGroup(args[0].toUpperCase())) {
						String oldGroup = (String) Server.getDatabaseCache().get(receive.getUniqueId().toString())
								.get("Rank");
						if (oldGroup.contains(",")) {
							Chat.sendPlayerPrefixedMessage(sender,
									"§b" + receive.getName() + "§e is already a member of 2 groups!");
						} else {
							oldGroup = Group.valueOf(args[0].toUpperCase()).name();
							Group rank = Group.valueOf(args[0].toUpperCase());
							Server.getDatabaseCache().get(receive.getUniqueId().toString()).put("Rank", oldGroup);
							switch (rank)
							{
							case SUB:
								Server.getDatabaseCache()
										.get(receive.getUniqueId()
												.toString())
										.put("Coins", ((int) Server.getDatabaseCache()
												.get(receive.getUniqueId().toString()).get("Coins")) + 2500);
								Server.getDatabaseCache()
										.get(receive.getUniqueId()
												.toString())
										.put("Spins", ((int) Server.getDatabaseCache()
												.get(receive.getUniqueId().toString()).get("Spins")) + 10);
								CustomBlock.SPONGE.setBlock(receive, true);
								CustomBlock.ICE.setBlock(receive, true);
								Chat.broadcastPrefixedMessage("§b" + receive.getName()
										+ " §ehas bought §bSubscriber §e@ §bTakenGaming.Buycraft.Net");
								Chat.sendPlayerPrefixedMessage(receive, "§b+2500 coins!");
								Chat.sendPlayerPrefixedMessage(receive, "§b+10 spins!");
								Chat.sendPlayerPrefixedMessage(receive, "§eThank you for donating, §b"
										+ receive.getName() + "§e. Your package has been credited to your account!");
								break;
							case SUBPLUS:
								Server.getDatabaseCache()
										.get(receive.getUniqueId()
												.toString())
										.put("Coins", ((int) Server.getDatabaseCache()
												.get(receive.getUniqueId().toString()).get("Coins")) + 5000);
								Server.getDatabaseCache()
										.get(receive.getUniqueId()
												.toString())
										.put("Spins", ((int) Server.getDatabaseCache()
												.get(receive.getUniqueId().toString()).get("Spins")) + 15);
								CustomBlock.SPONGE.setBlock(receive, true);
								CustomBlock.ICE.setBlock(receive, true);
								CustomBlock.BEDROCK.setBlock(receive, true);
								CustomBlock.MELON.setBlock(receive, true);
								Chat.broadcastPrefixedMessage("§b" + receive.getName()
										+ " §ehas bought §bSubscriber+ §e@ §bTakenGaming.Buycraft.Net");
								Kits.SPIDER.setKit(receive, true);
								Chat.sendPlayerPrefixedMessage(receive, "§b+3750 coins!");
								Chat.sendPlayerPrefixedMessage(receive, "§b+15 spins!");
								Chat.sendPlayerPrefixedMessage(receive, "§eThank you for donating, §b"
										+ receive.getName() + "§e. Your package has been credited to your account!");
								break;
							case SUBPLUSPLUS:
								Server.getDatabaseCache()
										.get(receive.getUniqueId()
												.toString())
										.put("Coins", ((int) Server.getDatabaseCache()
												.get(receive.getUniqueId().toString()).get("Coins")) + 5000);
								Server.getDatabaseCache()
										.get(receive.getUniqueId()
												.toString())
										.put("Spins", ((int) Server.getDatabaseCache()
												.get(receive.getUniqueId().toString()).get("Spins")) + 25);
								CustomBlock.SPONGE.setBlock(receive, true);
								CustomBlock.ICE.setBlock(receive, true);
								CustomBlock.BEDROCK.setBlock(receive, true);
								CustomBlock.MELON.setBlock(receive, true);
								CustomBlock.TNT.setBlock(receive, true);
								CustomBlock.RAINBOW_WOOL.setBlock(receive, true);
								Chat.broadcastPrefixedMessage("§b" + receive.getName()
										+ " §ehas bought §bSubscriber++ §e@ §bTakenGaming.Buycraft.Net");
								Kits.SPIDER.setKit(receive, true);
								Kits.PIGMAN.setKit(receive, true);
								Chat.sendPlayerPrefixedMessage(receive, "§b+5000 coins!");
								Chat.sendPlayerPrefixedMessage(receive, "§b+25 spins!");
								Chat.sendPlayerPrefixedMessage(receive, "§eThank you for donating, §b"
										+ receive.getName() + "§e. Your package has been credited to your account!");
								break;
							case PATRON:
								Server.getDatabaseCache()
										.get(receive.getUniqueId()
												.toString())
										.put("Coins", ((int) Server.getDatabaseCache()
												.get(receive.getUniqueId().toString()).get("Coins")) + 10000);
								Server.getDatabaseCache()
										.get(receive.getUniqueId()
												.toString())
										.put("Spins", ((int) Server.getDatabaseCache()
												.get(receive.getUniqueId().toString()).get("Spins")) + 50);
								CustomBlock.SPONGE.setBlock(receive, true);
								CustomBlock.ICE.setBlock(receive, true);
								CustomBlock.BEDROCK.setBlock(receive, true);
								CustomBlock.MELON.setBlock(receive, true);
								CustomBlock.TNT.setBlock(receive, true);
								CustomBlock.RAINBOW_WOOL.setBlock(receive, true);
								Chat.broadcastPrefixedMessage("§b" + receive.getName()
										+ " §ehas bought §dPatron §e@ §bTakenGaming.Buycraft.Net");
								Kits.SPIDER.setKit(receive, true);
								Kits.PIGMAN.setKit(receive, true);
								Chat.sendPlayerPrefixedMessage(receive, "§b+10000 coins!");
								Chat.sendPlayerPrefixedMessage(receive, "§b+50 spins!");
								Chat.sendPlayerPrefixedMessage(receive, "§eThank you for donating, §b"
										+ receive.getName() + "§e. Your package has been credited to your account!");
								break;
							default:
								break;
							}
							receiveModel.updateScoreboard();
							Server.getInstance().updateDatabase(receive);
							if (Bukkit.getPlayer(receive.getUniqueId()) != null) {
								Group.unregisterPlayerPermissions(Bukkit.getPlayer(receive.getUniqueId()));
								Group.registerPlayerPermissions(Bukkit.getPlayer(receive.getUniqueId()));
							}

						}
					} else {
						if (args[0].toUpperCase().equalsIgnoreCase("SUBtoSUBPLUS")) {
							String oldGroup = Group.valueOf("SUBPLUS").name();
							Server.getDatabaseCache().get(receive.getUniqueId().toString()).put("Rank", oldGroup);
							Server.getDatabaseCache()
									.get(receive.getUniqueId()
											.toString())
									.put("Coins", ((int) Server.getDatabaseCache()
											.get(receive.getUniqueId().toString()).get("Coins")) + 1250);
							Server
									.getDatabaseCache().get(
											receive.getUniqueId()
													.toString())
									.put("Spins", ((int) Server.getDatabaseCache()
											.get(receive.getUniqueId().toString()).get("Spins")) + 5);
							CustomBlock.SPONGE.setBlock(receive, true);
							CustomBlock.ICE.setBlock(receive, true);
							CustomBlock.BEDROCK.setBlock(receive, true);
							CustomBlock.MELON.setBlock(receive, true);
							Chat.broadcastPrefixedMessage("§b" + receive.getName()
									+ " §ehas upgraded from §bSubscriber §eto §bSubscriber+ §e@ §bTakenGaming.Buycraft.Net");
							Kits.SPIDER.setKit(receive, true);
							Chat.sendPlayerPrefixedMessage(receive, "§b+1250 coins!");
							Chat.sendPlayerPrefixedMessage(receive, "§b+5 spins!");
							Chat.sendPlayerPrefixedMessage(receive, "§eThank you for donating, §b" + receive.getName()
									+ "§e. Your package has been credited to your account!");
						} else if (args[0].toUpperCase().equalsIgnoreCase("SUBPLUStoSUBPLUSPLUS")) {
							String oldGroup = Group.valueOf("SUBPLUSPLUS").name();
							Server.getDatabaseCache().get(receive.getUniqueId().toString()).put("Group", oldGroup);
							Server.getDatabaseCache()
									.get(receive.getUniqueId()
											.toString())
									.put("Coins", ((int) Server.getDatabaseCache()
											.get(receive.getUniqueId().toString()).get("Coins")) + 1250);
							Server
									.getDatabaseCache().get(
											receive.getUniqueId()
													.toString())
									.put("Spins", ((int) Server.getDatabaseCache()
											.get(receive.getUniqueId().toString()).get("Spins")) + 10);
							CustomBlock.SPONGE.setBlock(receive, true);
							CustomBlock.ICE.setBlock(receive, true);
							CustomBlock.BEDROCK.setBlock(receive, true);
							CustomBlock.MELON.setBlock(receive, true);
							CustomBlock.TNT.setBlock(receive, true);
							CustomBlock.RAINBOW_WOOL.setBlock(receive, true);
							Chat.broadcastPrefixedMessage("§b" + receive.getName()
									+ " §ehas upgraded from §bSubscriber+ §eto §bSubscriber++ §e@ §bTakenGaming.Buycraft.Net");
							Kits.SPIDER.setKit(receive, true);
							Kits.PIGMAN.setKit(receive, true);
							Chat.sendPlayerPrefixedMessage(receive, "§b+1250 coins!");
							Chat.sendPlayerPrefixedMessage(receive, "§b+10 spins!");
							Chat.sendPlayerPrefixedMessage(receive, "§eThank you for donating, §b" + receive.getName()
									+ "§e. Your package has been credited to your account!");
						} else if (args[0].toUpperCase().equalsIgnoreCase("SUBPLUSPLUStoPATRON")) {
							String oldGroup = Group.valueOf("PATRON").name();
							Server.getDatabaseCache().get(receive.getUniqueId().toString()).put("Rank", oldGroup);
							Server.getDatabaseCache()
									.get(receive.getUniqueId()
											.toString())
									.put("Coins", ((int) Server.getDatabaseCache()
											.get(receive.getUniqueId().toString()).get("Coins")) + 5000);
							Server
									.getDatabaseCache().get(
											receive.getUniqueId()
													.toString())
									.put("Spins", ((int) Server.getDatabaseCache()
											.get(receive.getUniqueId().toString()).get("Spins")) + 25);
							Chat.broadcastPrefixedMessage("§b" + receive.getName()
									+ " §ehas upgraded from §bSubscriber++ §eto §dPatron §e@ §bTakenGaming.Buycraft.Net");
							Chat.sendPlayerPrefixedMessage(receive, "§b+5000 coins!");
							Chat.sendPlayerPrefixedMessage(receive, "§b+25 spins!");
							Chat.sendPlayerPrefixedMessage(receive, "§eThank you for donating, §b" + receive.getName()
									+ "§e. Your package has been credited to your account!");
						} else {
							Chat.sendPlayerPrefixedMessage(sender,
									"§eGroup §b" + args[2].toUpperCase() + "§e does not exist!");
						}
					}
				}
			}
		} else if (label.equalsIgnoreCase("setaccess")) {
			if (sender.hasPermission("Taken.setaccess")) {
				if (args.length == 1) {
					AccessMode[] modes = AccessMode.values();
					for (int i = 0; i < modes.length; i++) {
						if (modes[i].name().equalsIgnoreCase(args[0].toUpperCase())) {
							Server.setMode(modes[i]);
							if (sender instanceof Player) {
								Chat.sendPlayerPrefixedMessage(sender,
										"§eYou have set the Access Mode to: §b" + modes[i]);
							}
							break;
						} else if (i == modes.length - 1) {
							if (sender instanceof Player) {
								Chat.sendPlayerPrefixedMessage(sender, "§cError: Invalid access mode!");
							}
						}
					}
				} else {
					if (sender instanceof Player) {
						Chat.sendPlayerMessage(sender, "§cSyntax: /setaccess <access mode>");

					}
				}
			}
		} else if (label.equalsIgnoreCase("db")) {
			if (sender.hasPermission("Taken.db")) {
				if (args.length >= 3) {
					try {
						Integer.parseInt(args[2]);
					} catch (NumberFormatException e) {
						if (sender instanceof Player) {
							Chat.sendPlayerPrefixedMessage((Player) sender,
									"§c" + " " + args[2] + " is not a valid number!");
							return false;
						}
					}
					final ArrayList<UUID> ids = new ArrayList<UUID>();
					Bukkit.getScheduler().runTaskAsynchronously(Server.getInstance(), new Runnable() {
						public void run() {
							try {
								for (String s : Server.getDatabaseCache().keySet()) {
									if (((String) Server.getDatabaseCache().get(s).get("PlayerName"))
											.equalsIgnoreCase(args[1])) {
										ids.add(UUID.fromString(s));
									}
								}
								Bukkit.getScheduler().runTask(Server.getInstance(), new Runnable() {
									public void run() {
										if (!Server.getDatabaseCache().containsKey(ids.get(0).toString())) {
											Chat.sendPlayerPrefixedMessage(sender,
													"§eCould not find the player §b" + args[1]);
											return;
										}
										OfflinePlayer receive = Bukkit.getOfflinePlayer(ids.get(0));
										boolean isOnline = Bukkit.getPlayer(receive.getUniqueId()) != null;
										String columnName = args[0];
										if (DatabaseColumn.isColumn(columnName)) {
											Server.getDatabaseCache().get(ids.get(0).toString()).put(
													DatabaseColumn.getColumn(columnName).getDatabaseName(),
													Integer.parseInt(args[2]));
											Server.getInstance().updateDatabase(receive);
											Chat.sendPlayerPrefixedMessage(sender,
													"§eColumn §b" + columnName + "§e changed to §b" + args[2]
															+ " §efor §b" + (String) Server.getDatabaseCache()
																	.get(ids.get(0).toString()).get("PlayerName"));
											if (isOnline) {
												PlayerModel.getPlayerModel(Bukkit.getPlayer(receive.getUniqueId())).updateScoreboard();
											}
										} else {
											Chat.sendPlayerPrefixedMessage(sender,
													"§eColumn §b" + columnName + "§e does not exist!");
										}
									}
								});
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					});

				} else {
					Chat.sendPlayerMessage((Player) sender, "§cSyntax: /db <column> <player> <value>");
				}
			}
		}
		return false;
	}

}
