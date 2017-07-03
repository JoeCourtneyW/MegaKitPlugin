package com.me.sly.kits.main;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;

import com.me.sly.kits.main.classes.Classes;
import com.me.sly.kits.main.enums.Group;

public class Chat {

	private static String prefix = "§7[§bTaken Gaming§7] §c";

	public static void sendPlayerMessage(Permissible p, String message) {
		if (p instanceof CommandSender) {
			((CommandSender) p).sendMessage(message);
		} else if (p instanceof Player) {
			((Player) p).sendMessage(message);
		}
	}

	public static void sendPlayerPrefixedMessage(Permissible p, String message) {
		if (p instanceof CommandSender) {
			((CommandSender) p).sendMessage(prefix + message);
		} else if (p instanceof Player) {
			((Player) p).sendMessage(prefix + message);
		}
	}

	/**
	 * 
	 * @param message
	 *            %name% gets replaced with each specific player name
	 * @param permission
	 *            the permission a player needs to see the message
	 */
	@SuppressWarnings("deprecation")
	public static void broadcastMessage(String message, String permission) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.hasPermission(permission)) {
				p.sendMessage(message.replaceAll("%name%", p.getName()));
			}
		}
	}

	/**
	 * 
	 * @param message
	 *            %name% gets replaced with each specific player name
	 */
	@SuppressWarnings("deprecation")
	public static void broadcastMessage(String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(message.replaceAll("%name%", p.getName()));
		}
	}

	/**
	 * 
	 * @param message
	 *            %name% gets replaced with each specific player name
	 * @param permission
	 *            the permission a player needs to see the message
	 */
	@SuppressWarnings("deprecation")
	public static void broadcastPrefixedMessage(String message, String permission) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (p.hasPermission(permission)) {
				p.sendMessage(prefix + message.replaceAll("%name%", p.getName()));
			}
		}
	}

	/**
	 * 
	 * @param message
	 *            %name% gets replaced with each specific player name
	 */
	@SuppressWarnings("deprecation")
	public static void broadcastPrefixedMessage(String message) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.sendMessage(prefix + message.replaceAll("%name%", p.getName()));
		}
	}

	public static void sendAbilityMessage(Player p) {
		if (p.hasMetadata("Class")) {
			Classes c = Classes.valueOf(p.getMetadata("Class").get(0).value().toString().toUpperCase());
			p.sendMessage("§aYour §b§l" + c.getAbility() + "§a Skill is ready!");
			if (c.getName().equalsIgnoreCase("Skeleton"))
				Chat.sendPlayerMessage(p, "§bLeft Click §awith any bow to activate your skill!");
			else
				Chat.sendPlayerMessage(p, "§bRight Click §awith any sword to activate your skill!");
		} else {
			return;
		}
	}

	public static String getFormattedChatMessage(Player p, String msg) {
		String format = "";
		if (Group.OWNER.isInSpecificGroup(p)) {
			format = ("§b§lOWNER §b" + p.getDisplayName() + "§7: §f" + msg);
		} else if (Group.DEV.isInSpecificGroup(p)) {
			format = ("§3§lDEV §3" + p.getDisplayName() + "§7: §f" + msg);
		} else if (Group.HEADADMIN.isInSpecificGroup(p)) {
			format = ("§c§lHEAD ADMIN §c" + p.getName() + "§7: §f" + msg);
		} else if (Group.ADMIN.isInSpecificGroup(p)) {
			format = ("§c§lADMIN §c" + p.getName() + "§7: §f" + msg);
		} else if (Group.BUILDERPLUS.isInSpecificGroup(p)) {
			format = ("§3§lBUILDER§c§l+ §3" + p.getName() + "§7: §f" + msg);
		} else if (Group.BUILDER.isInSpecificGroup(p)) {
			format = ("§3§lBUILDER §3" + p.getName() + "§7: §f" + msg);
		} else if (Group.MOD.isInSpecificGroup(p)) {
			format = ("§9§lMOD §9" + p.getName() + "§7: §f" + msg);
		} else if (Group.HELPER.isInSpecificGroup(p)) {
			format = ("§6§lHELPER §6" + p.getName() + "§7: §f" + msg);
		} else if (Group.YOUTUBE.isInSpecificGroup(p)) {
			format = ("§f§lYOU§4§lTUBE §f" + p.getName() + "§7: §f" + msg);
		} else if (Group.PATRON.isInSpecificGroup(p)) {
			format = ("§d§lPATRON §d" + p.getName() + "§7: §f" + msg);
		} else if (Group.SUBPLUSPLUS.isInSpecificGroup(p)) {
			format = ("§a§lS++ §a" + p.getName() + "§7: §f" + msg);
		} else if (Group.SUBPLUS.isInSpecificGroup(p)) {
			format = ("§a§lS+ §a" + p.getName() + "§7: §f" + msg);
		} else if (Group.SUB.isInSpecificGroup(p)) {
			format = ("§a§lS §a" + p.getName() + "§7: §f" + msg);
		} else {
			format = ("§7" + p.getName() + "§7: §7" + msg);
		}
		return format;
	}

}
