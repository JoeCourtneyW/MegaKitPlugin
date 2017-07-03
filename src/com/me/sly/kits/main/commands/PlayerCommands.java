package com.me.sly.kits.main.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.kitteh.tag.TagAPI;

import com.me.sly.kits.main.Chat;
import com.me.sly.kits.main.Corrupted;
import com.me.sly.kits.main.classes.Classes;
import com.me.sly.kits.main.enums.CustomBlock;
import com.me.sly.kits.main.enums.Group;
import com.me.sly.kits.main.enums.Trail;
import com.me.sly.kits.main.enums.VanityArmor;
import com.me.sly.kits.main.gametype.GameType;
import com.me.sly.kits.main.gametype.Team;
import com.me.sly.kits.main.listeners.DuelListeners;
import com.me.sly.kits.main.resources.IconMenu;

public class PlayerCommands implements CommandExecutor {

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (sender instanceof Player) {
			final Player p = (Player) sender;
			if (label.equalsIgnoreCase("Kys")) {
				if (p.hasPermission("Taken.kill")) {
					p.setHealth(0.0);
				}
			} else if (label.equalsIgnoreCase("TpAllClass")) {
				if (p.hasPermission("Taken.TpAllClass")) {
					for (Player b : Bukkit.getOnlinePlayers()) {
						if (b.hasMetadata("Class")) {
							b.teleport(p);
						}
					}
				}
			} else if (label.equalsIgnoreCase("Spawn")) {
				if (p.hasPermission("Taken.spawn")) {
					if (Corrupted.getGames().containsKey(p.getName()))
						return false;
					final Location l = new Location(Bukkit.getWorld("Nube"), -549.5, 101, -552.5, 206.62F, 1.82F);

					if (p.hasPermission("Taken.instanttp")) {
						Chat.sendPlayerPrefixedMessage(p, "�eTeleporting...");
						p.teleport(l, TeleportCause.COMMAND);
						p.removeMetadata("Class", Corrupted.getInstance());
						Corrupted.getInstance().setDefaultInventory(p);
						if(p.hasPermission("Taken.FlyInlobby")){
							p.setAllowFlight(true);
							p.setFlying(true);
						}
						TagAPI.refreshPlayer(p);
					} else {
						Chat.sendPlayerPrefixedMessage(p, "�eYou'll be teleported to spawn in 5 seconds!");
						final Location loc = p.getLocation().clone();
						final BukkitTask k = new BukkitRunnable() {
							public void run() {

								Chat.sendPlayerPrefixedMessage(p, "�eTeleporting...");
								p.removeMetadata("Class", Corrupted.getInstance());
								Corrupted.getInstance().setDefaultInventory(p);
								p.teleport(l);
								if(p.hasPermission("Taken.FlyInlobby")){
									p.setAllowFlight(true);
									p.setFlying(true);
								}
								TagAPI.refreshPlayer(p);
							}
						}.runTaskLater(Corrupted.getInstance(), 5 * 20);
						final BukkitTask r = new BukkitRunnable() {
							public void run() {
								if (loc.getBlockX() != p.getLocation().getBlockX() && loc.getBlockX() != p.getLocation().getBlockZ()) {
									Chat.sendPlayerPrefixedMessage(p, "Teleport cancelled.");
									k.cancel();
									this.cancel();
								}
							}
						}.runTaskTimer(Corrupted.getInstance(), 19, 20);
						new BukkitRunnable() {
							public void run() {
								r.cancel();
							}
						}.runTaskLater(Corrupted.getInstance(), 5 * 20);
					}
				}
			} else if (label.equalsIgnoreCase("Arena")) {
				if (p.hasPermission("Taken.Arena")) {
					if (args.length != 2) {
						Chat.sendPlayerPrefixedMessage(p, "/Arena [GameType] [Team #]");
						return true;
					} else {
						String gameType = args[0];
						if (GameType.TWOvTWO.getDatabaseName().equalsIgnoreCase(gameType) || GameType.ONEvONE.getDatabaseName().equalsIgnoreCase(gameType)) {
							if (Corrupted.getInstance().isNumeric(args[1])) {
								int team = Integer.parseInt(args[1]);
								if (team == 1 || team == 2 || team == 3) {
									int id = 0;
									for (int i = 0; i < 1000; i++) {
										if (Corrupted.getInstance().getConfig().contains("Arenas." + gameType + "." + i + "." + team + ".x")) {
											continue;
										} else {
											id = i;
											break;
										}
									}

									Chat.sendPlayerPrefixedMessage(p, gameType + " Arena #" + id + " registered");
									System.out.println("[Taken Kits] " + gameType + " Arena #" + id + " registered by " + p.getName());
									Corrupted.getInstance().getConfig().set("Arenas." + gameType + "." + id + "." + team + ".x", p.getLocation().getX());
									Corrupted.getInstance().getConfig().set("Arenas." + gameType + "." + id + "." + team + ".y", p.getLocation().getY());
									Corrupted.getInstance().getConfig().set("Arenas." + gameType + "." + id + "." + team + ".z", p.getLocation().getZ());
									Corrupted.getInstance().getConfig().set("Arenas." + gameType + "." + id + "." + team + ".yaw", p.getLocation().getYaw());
									Corrupted.getInstance().getConfig().set("Arenas." + gameType + "." + id + "." + team + ".pitch", p.getLocation().getPitch());
									Corrupted.getInstance().getConfig().set("Arenas." + gameType + "." + id + "." + team + ".world", p.getLocation().getWorld().getName());
									Corrupted.getInstance().saveConfig();
								} else {
									Chat.sendPlayerPrefixedMessage(p, "Team # must be 1, 2, or 3");
								}
							} else {
								Chat.sendPlayerPrefixedMessage(p, "Team # must be numeric");
							}
						} else if (args[0].equalsIgnoreCase("remove")) {

						} else {
							Chat.sendPlayerPrefixedMessage(p, "Game type must be 1v1 or 2v2");
						}
					}
				}
			} else if (label.equalsIgnoreCase("Team")) {
				if (p.hasPermission("Taken.Team")) {
					if (Corrupted.getGames().containsKey(p.getName())) {
						Chat.sendPlayerPrefixedMessage(p, "You can not use the team command in game! If you are not in a game, relog.");
						return false;
					}
					if (args.length >= 1) {
						if (args[0].equalsIgnoreCase("invite")) {
							if (Corrupted.getCooldowns().containsKey(p.getName()) && Corrupted.getCooldowns().get(p.getName()).equalsIgnoreCase("INVITE")) {
								// invite cooldown
								Chat.sendPlayerPrefixedMessage(p, "Slow down! Wait a few seconds and try again");
								return false;
							}
							if (args.length == 2) {
								if (Corrupted.getTeams().containsKey(p.getName()) && Corrupted.getTeams().get(p.getName()).getOwner().equalsIgnoreCase(p.getName())) {
									if (Corrupted.getTeams().get(p.getName()).getMembers().size() + 1 <= Corrupted.getTeams().get(p.getName()).getMaxSize()) {
										Player recieve = Bukkit.getPlayer(args[1]);
										if (recieve.getName().equalsIgnoreCase(p.getName())) {
											Chat.sendPlayerPrefixedMessage(p, "You can not invite yourself!");
											return true;
										}
										if (Classes.getClass(recieve) == Classes.NONE) {
											if (!Corrupted.getTeams().containsKey(recieve.getName())) {
												if (!recieve.getOpenInventory().getTopInventory().getTitle().contains("Select") && !recieve.getOpenInventory().getTopInventory().getTitle().contains("Shop") && !recieve.getOpenInventory().getTopInventory().getTitle().contains("Invitation")) {
													if (Corrupted.getInvitesToggled().contains(recieve.getName())) {
														Chat.sendPlayerPrefixedMessage(p, "�cYou can't invite �b" + recieve.getName() + " �cto your team because they have invites toggled");
														return false;
													}
													Chat.sendPlayerPrefixedMessage(p, "�eYou have invited �b" + recieve.getName() + " �eto your team");
													Inventory toOpen = Bukkit.createInventory(null, 9, "Invitation");
													toOpen.setItem(0, IconMenu.setItemNameAndLore(new ItemStack(Material.SKULL_ITEM, 1, (short) 3), "�7" + p.getName(), new String[0]));
													toOpen.setItem(3, IconMenu.setItemNameAndLore(new ItemStack(Material.WOOL, 1, (short) 5), "�aAccept Invitation", new String[0]));
													toOpen.setItem(5, IconMenu.setItemNameAndLore(new ItemStack(Material.WOOL, 1, (short) 14), "�cDecline Invitation", new String[0]));
													recieve.openInventory(toOpen);
													Corrupted.getCooldowns().put(p.getName(), "INVITE");
													new BukkitRunnable() {
														public void run() {
															Corrupted.getCooldowns().remove(p.getName());
														}
													}.runTaskLater(Corrupted.getInstance(), 7 * 20);
												} else {
													Chat.sendPlayerPrefixedMessage(p, "Player has a pending invitation or is in the shop");
												}
											} else {
												Chat.sendPlayerPrefixedMessage(p, "Player is already in a team");
											}
										} else {
											Chat.sendPlayerPrefixedMessage(p, "Player is in an arena or in a ranked match");
										}

									} else {
										Chat.sendPlayerPrefixedMessage(p, "Your team is already full");
									}
								} else {
									Chat.sendPlayerPrefixedMessage(p, "You do not own a team");
								}
							} else {
								Chat.sendPlayerMessage(p, "�cSyntax: /team <invite|remove|create|disband|leave|list> [player]");
							}
						} else if (args[0].equalsIgnoreCase("remove")) {
							if (args.length == 2) {
								if (Corrupted.getTeams().containsKey(p.getName()) && Corrupted.getTeams().get(p.getName()).getOwner().equalsIgnoreCase(p.getName())) {
									Player recieve = Bukkit.getPlayer(args[1]);
									if (recieve.getName().equalsIgnoreCase(p.getName())) {
										Chat.sendPlayerPrefixedMessage(p, "You can not remove yourself!");
										return true;
									}
									if (Corrupted.getTeams().containsKey(recieve.getName()) && Corrupted.getTeams().get(recieve.getName()).getOwner().equalsIgnoreCase(p.getName())) {
										Corrupted.getTeams().remove(recieve.getName());
										Corrupted.getTeams().get(p.getName()).getMembers().remove(recieve.getName());
										Chat.sendPlayerPrefixedMessage(p, "�eYou have removed �b" + recieve.getName() + " �efrom your team");
									} else {
										Chat.sendPlayerPrefixedMessage(p, "Player is not in your team");
									}

								} else {
									Chat.sendPlayerPrefixedMessage(p, "You do not own a team");
								}
							} else {
								Chat.sendPlayerMessage(p, "�cSyntax: /team <invite|remove|create|disband|leave|list> [player]");
							}
						} else if (args[0].equalsIgnoreCase("create")) {
							if (args.length == 1) {
								if (!Corrupted.getTeams().containsKey(p.getName())) {
									if (Classes.getClass(p) == Classes.NONE) {
										Team t = new Team(2, p.getName());
										Corrupted.getTeams().put(p.getName(), t);
										Chat.sendPlayerPrefixedMessage(p, "�eYou have created a team");
									} else {
										Chat.sendPlayerPrefixedMessage(p, "You must be at spawn to create a team");
									}
								} else {
									Chat.sendPlayerPrefixedMessage(p, "You are already in a team");
								}
							} else {
								Chat.sendPlayerMessage(p, "�cSyntax: /team <invite|remove|create|disband|leave|list> [player]");
							}
						} else if (args[0].equalsIgnoreCase("disband")) {
							if (args.length == 1) {
								if (Corrupted.getTeams().containsKey(p.getName()) && Corrupted.getTeams().get(p.getName()).getOwner().equalsIgnoreCase(p.getName())) {
									Team t = Corrupted.getTeams().get(p.getName());
									for (String r : Corrupted.getTeams().get(p.getName()).getMembers()) {
										Corrupted.getTeams().remove(r);
									}
									t.dissolve();
									Chat.sendPlayerPrefixedMessage(p, "�eYou have disbanded your team!");
								} else {
									Chat.sendPlayerPrefixedMessage(p, "You are not the owner of a team");
								}
							} else {
								Chat.sendPlayerMessage(p, "�cSyntax: /team <invite|remove|create|disband|leave|list> [player]");
							}
						} else if (args[0].equalsIgnoreCase("list")) {
							if (args.length == 1) {
								if (Corrupted.getTeams().containsKey(p.getName())) {
									StringBuilder sb = new StringBuilder("�eMembers (" + Corrupted.getTeams().get(p.getName()).getMembers().size() + "): ");
									for (String s : Corrupted.getTeams().get(p.getName()).getMembers()) {
										sb.append("�b" + s + ", ");
									}
									Chat.sendPlayerMessage(p, sb.toString().substring(0, sb.toString().length() - 2));
								} else {
									Chat.sendPlayerPrefixedMessage(p, "You are not in a team");
								}
							} else {
								Chat.sendPlayerMessage(p, "�cSyntax: /team <invite|remove|create|disband|leave|list> [player]");
							}
						} else if (args[0].equalsIgnoreCase("leave")) {
							if (args.length == 1) {
								if (Corrupted.getTeams().containsKey(p.getName())) {
									if (!Corrupted.getTeams().get(p.getName()).getOwner().equalsIgnoreCase(p.getName())) {
										Team t = Corrupted.getTeams().get(p.getName());
										t.getMembers().remove(p.getName());
										Corrupted.getTeams().remove(p.getName());
										Chat.sendPlayerPrefixedMessage(p, "You have left your team!");
									} else {
										Chat.sendPlayerPrefixedMessage(p, "You are the owner of a team, use '/team disband' to leave your team");
									}
								} else {
									Chat.sendPlayerPrefixedMessage(p, "You are not in a team");
								}
							} else {
								Chat.sendPlayerMessage(p, "�cSyntax: /team <invite|remove|create|disband|leave|list> [player]");
							}
						}
					} else {
						Chat.sendPlayerMessage(p, "�cSyntax: /team <invite|remove|create|disband|leave|list> [player]");
					}
				} else {
					Chat.sendPlayerPrefixedMessage(p, "No permission");
				}
			} else if (label.equalsIgnoreCase("Bug")) {
				if (p.hasPermission("Taken.Bug")) {
					if (args.length >= 1) {
						if (args[0].equalsIgnoreCase("Add")) {
							String msg = "";
							for (int i = 1; i < args.length; i++) {
								msg = msg + " " + args[i];
							}
							Chat.broadcastPrefixedMessage("�eAdded �c'" + msg.substring(1).replace("&", "�") + "' �e(BUG)", "Taken.Bug");
							List<String> values = Corrupted.getInstance().getConfig().getStringList("Bugs");
							values.add(msg.substring(1).replace("&", "�"));
							Corrupted.getInstance().getConfig().set("Bugs", values);
						} else if (args[0].equalsIgnoreCase("Remove")) {
							if (args.length == 2 && Corrupted.getInstance().isNumeric(args[1])) {
								List<String> values = Corrupted.getInstance().getConfig().getStringList("Bugs");
								String value = values.get(Integer.parseInt(args[1]));
								values.remove(Integer.parseInt(args[1]));
								Corrupted.getInstance().getConfig().set("Bugs", values);
								Chat.broadcastPrefixedMessage("�eRemoved �c'" + value + "' �e(BUG)", "Taken.Bug");
							} else {
								Chat.sendPlayerMessage(p, "�cSyntax: /bug remove <id>");
							}
						} else if (args[0].equalsIgnoreCase("List")) {
							List<String> values = Corrupted.getInstance().getConfig().getStringList("Bugs");
							Chat.sendPlayerMessage(p, "             �cBugs");
							for (int i = 0; i < values.size(); i++) {
								Chat.sendPlayerMessage(p, "�e" + i + ". �b" + values.get(i));
							}
						}
						Corrupted.getInstance().saveConfig();
					} else {
						Chat.sendPlayerMessage(p, "�cSyntax: /bug <add|remove|list> [bug]");
					}
				}
			} else if (label.equalsIgnoreCase("Idea")) {
				if (p.hasPermission("Taken.Idea")) {
					if (args.length >= 1) {
						if (args[0].equalsIgnoreCase("Add")) {
							String msg = "";
							for (int i = 1; i < args.length; i++) {
								msg = msg + " " + args[i];
							}
							Chat.broadcastPrefixedMessage("�eAdded �c'" + msg.substring(1).replace("&", "�") + "' �e(IDEA)", "Taken.Idea");
							List<String> values = Corrupted.getInstance().getConfig().getStringList("Ideas");
							values.add(msg.substring(1).replace("&", "�"));
							Corrupted.getInstance().getConfig().set("Ideas", values);
						} else if (args[0].equalsIgnoreCase("Remove")) {
							if (args.length == 2 && Corrupted.getInstance().isNumeric(args[1])) {
								List<String> values = Corrupted.getInstance().getConfig().getStringList("Ideas");
								String value = values.get(Integer.parseInt(args[1]));
								values.remove(Integer.parseInt(args[1]));
								Corrupted.getInstance().getConfig().set("Ideas", values);
								Chat.broadcastPrefixedMessage("�eRemoved �c'" + value + "' �e(IDEA)", "Taken.Idea");
							} else {
								Chat.sendPlayerMessage(p, "�cSyntax: /idea remove <id>");
							}
						} else if (args[0].equalsIgnoreCase("List")) {
							List<String> values = Corrupted.getInstance().getConfig().getStringList("Ideas");
							Chat.sendPlayerMessage(p, "             �cIdeas");
							for (int i = 0; i < values.size(); i++) {
								Chat.sendPlayerMessage(p, "�e" + i + ". �b" + values.get(i));
							}
						}
						Corrupted.getInstance().saveConfig();
					} else {
						Chat.sendPlayerMessage(p, "�cSyntax: /idea <add|remove|list> [idea]");
					}
				}
			} else if (label.equalsIgnoreCase("SC")) {
				if (p.hasPermission("Taken.SC")) {
					if (args.length == 0) {
						if (Corrupted.getStaffChat().contains(p.getUniqueId().toString())) {
							Chat.sendPlayerPrefixedMessage(p, "You have switched your chat to: " + "�eALL");
							Corrupted.getStaffChat().remove(p.getUniqueId().toString());
						} else {
							Chat.sendPlayerPrefixedMessage(p, "You have switched your chat to: " + "�eSTAFF");
							Corrupted.getStaffChat().add(p.getUniqueId().toString());
						}
					} else {
						String msg = "";
						for (int i = 0; i < args.length; i++) {
							msg = msg + " " + args[i];
						}
						Chat.broadcastMessage("�7[�eStaff�7] " + Chat.getFormattedChatMessage(p, msg.substring(1).replace("&", "�")), "Taken.SC");
					}
				}
			} else if (label.equalsIgnoreCase("Group")) {
				if (p.hasPermission("Taken.Group")) {
					if (args.length > 1) {
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
											if (ids.isEmpty()) {
												Chat.sendPlayerPrefixedMessage(p, "�eCould not find the player �b" + args[1]);
												return;
											}
											OfflinePlayer recieve = Bukkit.getOfflinePlayer(ids.get(0));
											if (args[0].equalsIgnoreCase("Add")) {
												if (args.length == 3) {
													if (Group.isGroup(args[2].toUpperCase())) {
														String oldGroup = (String) Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).get("Group");
														if (oldGroup.contains(",")) {
															Chat.sendPlayerPrefixedMessage(p, "�b" + recieve.getName() + "�e is already a member of 2 groups!");
														} else {
															oldGroup = oldGroup + "," + Group.valueOf(args[2].toUpperCase()).name();
															Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Group", oldGroup);
															Corrupted.getInstance().updateDatabase(recieve);
															if (Bukkit.getPlayer(recieve.getUniqueId()) != null) {
																Group.unregisterPlayerPermissions(Bukkit.getPlayer(recieve.getUniqueId()));
																Group.registerPlayerPermissions(Bukkit.getPlayer(recieve.getUniqueId()));
															}

														}
													} else {
														Chat.sendPlayerPrefixedMessage(p, "�eGroup �b" + args[2].toUpperCase() + "�e does not exist!");
													}
												} else {
													Chat.sendPlayerMessage(p, "�cSyntax: /group <add|set|show|remove> <player> [group]");
												}
											} else if (args[0].equalsIgnoreCase("Show")) {
												if (args.length == 2) {
													String group = (String) Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).get("Group");
													Chat.sendPlayerPrefixedMessage(p, "�b" + recieve.getName() + " �eis a member of �b" + group);
												} else {
													Chat.sendPlayerMessage(p, "�cSyntax: /group <add|set|show|remove> <player> [group]");
												}
											} else if (args[0].equalsIgnoreCase("Set")) {
												if (args.length == 3) {
													if (Group.isGroup(args[2].toUpperCase())) {
														String newGroup = "";
														newGroup = Group.valueOf(args[2].toUpperCase()).name();
														Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Group", newGroup);
														Corrupted.getInstance().updateDatabase(recieve);
														Chat.sendPlayerPrefixedMessage(p, "�eYou have set �b" + recieve.getName() + "�e's group to �b" + newGroup);
													} else {
														Chat.sendPlayerPrefixedMessage(p, "�eGroup �b" + args[2].toUpperCase() + "�e does not exist!");
													}
												} else {
													Chat.sendPlayerMessage(p, "�cSyntax: /group <add|set|show|remove> <player> [group]");
												}
											} else if (args[0].equalsIgnoreCase("Remove")) {

												if (args.length == 3) {
													if (Group.isGroup(args[2].toUpperCase())) {
														String oldGroup = (String) Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).get("Group");
														Group toRemove = Group.valueOf(args[2].toUpperCase());
														if (oldGroup.contains(",")) {
															if (oldGroup.split(",")[0].contains(toRemove.name())) {
																Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Group", oldGroup.split(",")[1]);
																Corrupted.getInstance().updateDatabase(recieve);
																Chat.sendPlayerPrefixedMessage(p, "�eYou have removed �b" + recieve.getName() + "�efrom �b" + toRemove);
															} else if (oldGroup.split(",")[1].contains(toRemove.name())) {
																Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Group", oldGroup.split(",")[0]);
																Corrupted.getInstance().updateDatabase(recieve);
																Chat.sendPlayerPrefixedMessage(p, "�eYou have removed �b" + recieve.getName() + "�efrom �b" + toRemove);
															} else {
																Chat.sendPlayerPrefixedMessage(p, "�b" + recieve.getName() + " �eis not a member of �b" + toRemove.name());
															}
														} else {
															Corrupted.getDatabaseCache().get(recieve.getUniqueId().toString()).put("Group", "DEFAULT");
															Corrupted.getInstance().updateDatabase(recieve);
															Chat.sendPlayerPrefixedMessage(p, "�eYou have removed �b" + recieve.getName() + "�efrom �b" + toRemove);
														}
													} else {
														Chat.sendPlayerPrefixedMessage(p, "Group " + args[2].toUpperCase() + " does not exist!");
													}
												} else {
													Chat.sendPlayerMessage(p, "�cSyntax: /group <add|set|show|remove> <player> [group]");
												}
											} else {
												Chat.sendPlayerMessage(p, "�cSyntax: /group <add|set|show|remove> <player> [group]");
											}
										}
									});
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						});
					} else {
						Chat.sendPlayerMessage(p, "�cSyntax: /group <add|set|show|remove> <player> [group]");
					}
				}
			} else if (label.equalsIgnoreCase("STFU")) {
				if (p.hasPermission("Taken.STFU")) {
					Corrupted.setSTFU(!Corrupted.isSTFU());
					if (Corrupted.isSTFU()) {
						Chat.sendPlayerPrefixedMessage(p, "You have enabled STFU mode");
					} else {
						Chat.sendPlayerPrefixedMessage(p, "You have disabled STFU mode");
					}
				}
			} else if (label.equalsIgnoreCase("ToggleInvite")) {
				if (Corrupted.getInvitesToggled().contains(p.getUniqueId().toString())) {
					Chat.sendPlayerPrefixedMessage(p, "�eYou have enabled player invites");
					Corrupted.getInvitesToggled().remove(p.getUniqueId().toString());
				} else {
					Chat.sendPlayerPrefixedMessage(p, "�eYou have disabled player invites");
					Corrupted.getInvitesToggled().add(p.getUniqueId().toString());
				}
			} else if (label.equalsIgnoreCase("Booster")) {
				if (Corrupted.getInstance().getActiveBoosters() != null) {
					if (Corrupted.getInstance().getActiveBoosters().containsKey(p.getUniqueId())) {
						for (UUID g : Corrupted.getInstance().getActiveBoosters().keySet()) {
							if (g.toString().equalsIgnoreCase(p.getUniqueId().toString())) {
								Chat.sendPlayerPrefixedMessage(p, "You have �b" + (Corrupted.getInstance().roundTwoDecimals((Corrupted.getInstance().getConfig().getInt("Boosters." + g.toString() + ".timeLeft") * 1.0) / 60 / 60)) + " hour(s) �eleft on your 2x Coin Booster");
								break;
							}
						}
					} else {
						Chat.sendPlayerPrefixedMessage(p, "�eYou don't have a Coin Booster active!");
					}
				} else {
					Chat.sendPlayerPrefixedMessage(p, "�eYou don't have a Coin Booster active!");
				}
			} else if (label.equalsIgnoreCase("messages")) {
				List<String> messages = Corrupted.getInstance().getConfig().getStringList("BroadcastMessages");
				if (sender.hasPermission("Taken.messages")) {
					String message = "";
					if (args.length > 1) {
						if (args[0].equalsIgnoreCase("add")) {
							String msg = "";
							for (String a : args) {
								if(a.equalsIgnoreCase(args[0])) continue;
								msg = msg + " " + a;
							}
							message = msg;
							Corrupted.getInstance().getBroadcaster().addMessage(message);
							Chat.sendPlayerPrefixedMessage(sender, "�eMessage added: " + message + "!");
						} else if (args[0].equalsIgnoreCase("remove")) {
							int state = -1;
							int messageIndex = 0;
							if (messages.size() != 0) {
								state = Corrupted.getInstance().getBroadcaster().removeMessage(args[1]);
								if (state == 0) {
									Chat.sendPlayerPrefixedMessage(sender, "�eSucessfully removed message: �r�o" + messages.get(Integer.parseInt(args[1])) + "�c�r.");
								} else if (state == 1) {
									Chat.sendPlayerPrefixedMessage(sender, "�cMessage not found with index of: �7�o" + args[1] + "�c�r!");
								} else if (state == 2){
									Chat.sendPlayerPrefixedMessage(sender, "�c�o" + args[1] + "�r�c is not a number!");
								}else if (state != 0 || state != -1 || state != 2) {
									Chat.sendPlayerPrefixedMessage(sender, "�cError removing message number: �7�o" + messageIndex + "�7�r!");
								}
							} else {
								Chat.sendPlayerMessage(sender, "�cUsage: /messages <add|remove> <message index>");

							}
						}

					} else if (args.length == 1) {
						if (args[0].equalsIgnoreCase("list")) {
							if (messages.size() > 0) {
								for (int i = 0; i < messages.size(); i++) {
									Chat.sendPlayerMessage(p, "�e" + i + "�7:�b " + messages.get(i).replaceAll("&", "�"));
								}
							}
						} else {
							Chat.sendPlayerMessage(sender, "�cUsage: /messages <add|remove> <message index>");
						}
					} else {
						Chat.sendPlayerMessage(sender, "�cUsage: /messages <add|remove> <message index>");						
					}
				}
			} else if (label.equalsIgnoreCase("nick")) {
			if(p.hasPermission("Taken.Nick") && p.getName().equalsIgnoreCase("SlyBro3")){ //NOT WORKING
					if(Corrupted.getNickedPlayers().containsKey(p.getName())){
						Corrupted.getNickedPlayers().remove(p.getName());
						Chat.sendPlayerPrefixedMessage(p, "�eYou will no longer recieve a nickname in the FFA Arena.");
						TagAPI.refreshPlayer(p);
					}else{
						ArrayList<HashMap<String, Object>> names = new ArrayList<HashMap<String, Object>>();
						names.addAll(Corrupted.getDatabaseCache().values());
						boolean nameFound = false;
						String newName = "";
						while(!nameFound){
							int i = Corrupted.getInstance().rand(0, Corrupted.getDatabaseCache().size()-1);
							if(Bukkit.getPlayer((String) names.get(i).get("PlayerName")) == null){
								if(!Group.isPartOfGroupInheritance(Bukkit.getOfflinePlayer((String) names.get(i).get("PlayerName")).getUniqueId(), Group.HELPER))
								nameFound = true;
								newName = (String) names.get(i).get("PlayerName");
							}
						}
						Corrupted.getNickedPlayers().put(p.getName(), newName);
						Chat.sendPlayerPrefixedMessage(p, "�eYour new nickname in FFA is: �b" + newName);
						TagAPI.refreshPlayer(p);
					}
				} else {
					Chat.sendPlayerPrefixedMessage(p, "You must purchase the command '/nick' in the Buycraft Shop");
				}
			}else if (label.equalsIgnoreCase("stats")){
				if(sender.hasPermission("Taken.statscommand")){
					if(args.length > 0){
						String playerName = args[0];
						ArrayList<UUID> ids = new ArrayList<UUID>();
						for (String s : Corrupted.getDatabaseCache().keySet()) {
							if (((String) Corrupted.getDatabaseCache().get(s).get("PlayerName")).equalsIgnoreCase(playerName)) {
								ids.add(UUID.fromString(s));
							}
						}
						HashMap<String, Object> stats = Corrupted.getDatabaseCache().get(ids.get(0).toString());
						Chat.sendPlayerPrefixedMessage(p, "�eStats for �b" + stats.get("PlayerName"));
						Chat.sendPlayerMessage(p, "�7Group: �b" + stats.get("Group"));
						Chat.sendPlayerMessage(p, "�7UUID: �b" + ids.get(0));
						Chat.sendPlayerMessage(p, "�7IP: �b" + stats.get("IP"));						
						Chat.sendPlayerMessage(p, "�7FFA Rating: �b" + stats.get("FFARating"));
						Chat.sendPlayerMessage(p, "�71v1 Rating: �b" + stats.get("1v1Rating"));
						Chat.sendPlayerMessage(p, "�72v2 Rating: �b" + stats.get("2v2Rating"));
						Chat.sendPlayerMessage(p, "�7Coins: �b" + stats.get("Coins"));
						Chat.sendPlayerMessage(p, "�7Spins: �b" + stats.get("Spins"));
						Chat.sendPlayerMessage(p, "�7Selected Block: �b" + CustomBlock.getSelectedBlock(ids.get(0)));
						Chat.sendPlayerMessage(p, "�7Selected Vanity Armor: �b" + VanityArmor.getSelectedVanityArmor(ids.get(0)));
						Chat.sendPlayerMessage(p, "�7Selected Trail: �b" + Trail.getSelectedTrail(ids.get(0)));
						}
				}
				} else if (label.equalsIgnoreCase("staff")) {
					String msg = "";
					StringBuilder sb = new StringBuilder(msg);
					for(Player pr : Bukkit.getOnlinePlayers()){
						List<Group> gr = Group.getSpecificGroups(pr);
					if(pr.hasPermission("Taken.staff")){
						sb.append(", " + gr.get(0).getPrefix() + pr.getName()); 
					}
					}
					Chat.sendPlayerMessage(p, "�eOnline staff members: " + sb.toString().substring(2));
				} else if (label.equalsIgnoreCase("Duel")) {
					if(args.length == 1){
						if(Bukkit.getPlayer(args[0]) != null){
							Player receive = Bukkit.getPlayer(args[0]);
							if (!Corrupted.getTeams().containsKey(receive.getName())) {
								if (!Corrupted.getGames().containsKey(receive.getName())) {
									if (!receive.hasMetadata("Class")) {
										if(!Corrupted.getTeams().containsKey(p.getName())) {
											if (!Corrupted.getGames().containsKey(p.getName())) {
												if (!p.hasMetadata("Class")) {
													Corrupted.getInvites().put(receive.getName(), p.getName());
													Chat.sendPlayerPrefixedMessage(p, "�eYou have requested to duel �b" + receive.getName());
													Chat.sendPlayerPrefixedMessage(receive, "�b" + p.getName() + " �ehas requested to duel you!");
													Chat.sendPlayerPrefixedMessage(receive, "�eUse �b'/accept " + p.getName() + "' �eto accept his duel.");
												} else {
													Chat.sendPlayerPrefixedMessage(p, "You can not request to duel someone when you are in the FFA Arena");
													//p has class
												}
											} else {
												Chat.sendPlayerPrefixedMessage(p, "You can not request to duel someone when you are in a game");
												//p is in game
											}
										} else {
											Chat.sendPlayerPrefixedMessage(p, "You can not request to duel someone when you are in a team");
											//p is in team
										}
									} else {
										Chat.sendPlayerPrefixedMessage(p, "You can not duel �b" + receive.getName() + "�c because they're is in the FFA Arena");
										//receive has class
									}
								} else {
									Chat.sendPlayerPrefixedMessage(p, "You can not duel �b" + receive.getName() + "�c because they're in a game");
									//receive is in game
								}
							} else {
								Chat.sendPlayerPrefixedMessage(p, "You can not duel �b" + receive.getName() + "�c because they're in a team");
								//receive is in team
							}
						} else {
							Chat.sendPlayerPrefixedMessage(p, "Could not find �b" + args[0]);
							//receive isnt online
						}
					}

				} else if (label.equalsIgnoreCase("Accept")) {
					//Map contains [Invitee, Invite]
					if(args.length == 1){
						if(Bukkit.getPlayer(args[0]) != null){
							Player receive = Bukkit.getPlayer(args[0]);
							if (Corrupted.getInvites().containsKey(p.getName()) && Corrupted.getInvites().get(p.getName()).equalsIgnoreCase(receive.getName())){
							if (!Corrupted.getTeams().containsKey(receive.getName())) {
								if (!Corrupted.getGames().containsKey(receive.getName())) {
									if (!receive.hasMetadata("Class")) {
										if(!Corrupted.getTeams().containsKey(p.getName())) {
											if (!Corrupted.getGames().containsKey(p.getName())) {
												if (!p.hasMetadata("Class")) {
													Corrupted.getInvites().remove(p.getName());
													DuelListeners.openGUIDuel(p, DuelListeners.formatItemArray(p), receive);
													DuelListeners.openGUIDuel(receive, DuelListeners.formatItemArray(receive), p);
													//LOAD INVENTORIES
												} else {
													Chat.sendPlayerPrefixedMessage(p, "You can duel someone when you are in the FFA Arena");
													//p has class
												}
											} else {
												Chat.sendPlayerPrefixedMessage(p, "You can not duel someone when you are in a game");
												//p is in game
											}
										} else {
											Chat.sendPlayerPrefixedMessage(p, "You can not duel someone when you are in a team");
											//p is in team
										}
									} else {
										Chat.sendPlayerPrefixedMessage(p, "You can not duel �b" + receive.getName() + "�c because they're is in the FFA Arena");
										//receive has class
									}
								} else {
									Chat.sendPlayerPrefixedMessage(p, "You can not duel �b" + receive.getName() + "�c because they're in a game");
									//receive is in game
								}
							} else {
								Chat.sendPlayerPrefixedMessage(p, "You can not duel �b" + receive.getName() + "�c because they're in a team");
								//receive is in team
							}
							} else {
								Chat.sendPlayerPrefixedMessage(p, "�b" + receive.getName() + " �cdidn't invite you");
								//receive isnt online								
							}
						} else {
							Chat.sendPlayerPrefixedMessage(p, "�b" + args[0] + " �cis not online");
							//receive isnt online
						}
					}
				}
		}
		return false;
	}

}
