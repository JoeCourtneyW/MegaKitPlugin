package com.sly.main.server;

import com.sly.main.Server;

public class Chat
{

	public static final String PREFIX = "§7[§bTaken Gaming§7] §c";
	private static boolean globalMute = false;

	public static void muteServer() {
		globalMute = true;
	}

	public static void unmuteServer() {
		globalMute = false;
	}

	public static boolean isServerMuted() {
		return globalMute;
	}

	/**
	 * 
	 * @param message
	 *            %name% gets replaced with each specific player name
	 * @param permission
	 *            the permission a player needs to see the message
	 */
	public static void broadcastMessage(String message, String permission) {
		Server.getPlayers(p -> p.isOnline() && p.getPlayer().hasPermission(permission))
				.forEach(p -> p.sendMessage(message.replaceAll("%name%", p.getName())));
	}

	/**
	 * 
	 * @param message
	 *            %name% gets replaced with each specific player name
	 */
	public static void broadcastMessage(String message) {
		Server.getOnlinePlayers().forEach(p -> p.sendMessage(message.replaceAll("%name%", p.getName())));
	}

	/**
	 * z
	 * 
	 * @param message
	 *            %name% gets replaced with each specific player name
	 * @param permission
	 *            the permission a player needs to see the message
	 */
	public static void broadcastPrefixedMessage(String message, String permission) {
		Server.getPlayers(p -> p.isOnline() && p.getPlayer().hasPermission(permission))
				.forEach(p -> p.sendPrefixedMessage(message.replaceAll("%name%", p.getName())));
	}

	/**
	 * 
	 * @param message
	 *            %name% gets replaced with each specific player name
	 */
	public static void broadcastPrefixedMessage(String message) {
		Server.getOnlinePlayers().forEach(p -> p.sendPrefixedMessage(message.replaceAll("%name%", p.getName())));
	}

}
