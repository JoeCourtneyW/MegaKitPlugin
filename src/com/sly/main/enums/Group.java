package com.sly.main.enums;

import org.bukkit.permissions.PermissionAttachment;

import com.sly.main.Server;
import com.sly.main.player.PlayerModel;

public enum Group
{
	DEFAULT(null, "§7"),
	SUB(DEFAULT, "§a§lS §a"),
	SUBPLUS(SUB, "§a§lS+ §a"),
	SUBPLUSPLUS(SUBPLUS, "§a§lS++ §a"),
	PATRON(SUBPLUSPLUS, "§d§lPATRON §d"),
	YOUTUBE(PATRON, "§f§lYOU§c§lTUBE §f"),
	HELPER(DEFAULT, "§9§lMOD §9"),
	MOD(HELPER, "§9§lMOD §9"),
	BUILDER(DEFAULT, "§3§lBUILDER §3"),
	BUILDERPLUS(BUILDER, "§3§lBUILDER§c§l+ §3"),
	ADMIN(MOD, "§c§lADMIN §c"),
	HEADADMIN(ADMIN, "§c§lHEAD ADMIN §c"),
	DEV(HEADADMIN, "§3§lDEV §3"),
	OWNER(DEV, "§b§lOWNER §b");

	private Group inheritance;
	private String prefix;

	Group(Group inheritance, String prefix) {
		this.inheritance = inheritance;
		this.prefix = prefix;
	}

	public String getPrefix() {
		return this.prefix;
	}

	public Group getInheritance() {
		return this.inheritance;
	}

	public boolean hasInheritance() {
		return this.inheritance != null;
	}

	public static Group[] getExplicitGroups(PlayerModel p) {
		String groupsSerialized = p.getDatabaseValue("Rank").asString();
		Group[] groups = new Group[groupsSerialized.split(",").length];
		for (int i = 0; i < groups.length; i++) {
			groups[i] = Group.valueOf(groupsSerialized.split(",")[i]);
		}

		return groups;
	}

	public boolean isInGroupExplicity(PlayerModel p) {
		for (Group g : Group.getExplicitGroups(p)) {
			if (g.name() == this.name())
				return true;
		}

		return false;
	}

	public boolean isInGroupInheritance(PlayerModel p) {
		String groupsSerialized = p.getDatabaseValue("Rank").asString();
		Group[] groups = new Group[groupsSerialized.split(",").length]; // If they only have one rank, the length will be 1

		for (int i = 0; i < groups.length; i++) { // Loop through (If more than 1) the player's groups
			groups[i] = Group.valueOf(groupsSerialized.split(",")[i]);
			if (groups[i].equals(this)) // If the group is the same group, then they are in the inheritance
				return true;
			Group chaseInheritance = groups[i]; // Group that I can modify and follow the inheritance tree with
			while (groups[i].hasInheritance()) { // All the inheritance trees will end with DEFAULT(null)
				chaseInheritance = groups[i].getInheritance();
				if (chaseInheritance == this)
					return true;
			}
		}
		return false;
	}

	public static Group getGroup(PlayerModel p) {
		return Group.valueOf(p.getDatabaseValue("Rank").asString());
	}

	public static boolean isGroup(String string) {
		for (Group cr : Group.values())
			if (cr.name().equals(string))
				return true;
		return false;
	}

	public static void registerPlayerPermissions(PlayerModel p) {
		PermissionAttachment attachment = p.getPlayer().addAttachment(Server.getInstance());

		/**
		 * DEFAULT PERMISSIONS
		 */
		attachment.setPermission("Taken.spawn", true);
		attachment.setPermission("Taken.kill", true);
		attachment.setPermission("Taken.team", true);
		attachment.setPermission("Essentials.afk", false);
		attachment.setPermission("Essentials.ignore", false);
		attachment.setPermission("Essentials.list", false);
		attachment.setPermission("Essentials.who", false);
		attachment.setPermission("Essentials.suicide", false);
		attachment.setPermission("Essentials.warp", false);
		if (DEV.isInGroupInheritance(p)) {
			attachment.setPermission("Bukkit.command.Op", true);
			attachment.setPermission("WorldEdit.*", true);
			attachment.setPermission("Taken.Package", true);
			attachment.setPermission("NoCheat.Admin", true);
			attachment.setPermission("nocheatplus.shortcut.bypass", true);
			attachment.setPermission("Taken.db", true);
			attachment.setPermission("Essentials.warp", true);
		}
		if (HEADADMIN.isInGroupInheritance(p)) {
			attachment.setPermission("Taken.Arena", true);
			attachment.setPermission("Taken.GCM", true);
			attachment.setPermission("Taken.setaccess", true);
			attachment.setPermission("Essentials.warp", true);
		}
		if (ADMIN.isInGroupInheritance(p)) {
			attachment.setPermission("Essentials.*", true);
			attachment.setPermission("Taken.instanttp", true);
			attachment.setPermission("Taken.group", true);
			attachment.setPermission("Taken.wipe", true);
			attachment.setPermission("nocheatplus.shortcut.bypass", true);
			attachment.setPermission("bm.command.unban", true);
			attachment.setPermission("bm.command.unbanip", true);
			attachment.setPermission("bm.command.tempbanip", true);
			attachment.setPermission("bm.command.banip", true);
			attachment.setPermission("bm.command.mute", true);
			attachment.setPermission("Essentials.warp", true);
		}
		if (MOD.isInGroupInheritance(p)) {
			attachment.setPermission("Taken.Bug", true);
			attachment.setPermission("Taken.Idea", true);
			attachment.setPermission("Taken.SC", true);
			attachment.setPermission("VoxelSniper.Brush.*", false);
			attachment.setPermission("VoxelSniper.Sniper", false);
			attachment.setPermission("VoxelSniper.IgnoreLimitations", false);
			attachment.setPermission("bm.command.ban", true);
			attachment.setPermission("bm.command.tempban", true);
			attachment.setPermission("bm.command.tempmute", true);
			attachment.setPermission("bm.command.kick", true);
			attachment.setPermission("bm.command.unmute", true);
			attachment.setPermission("bm.command.warn", true);
			attachment.setPermission("bm.command.addnote", true);
			attachment.setPermission("bm.command.notes", true);
			attachment.setPermission("bm.command.dwarn", true);
			attachment.setPermission("bm.command.bminfo", true);
			attachment.setPermission("bm.command.notes.online", true);
			attachment.setPermission("bm.command.alts.online", true);
			attachment.setPermission("Taken.group", true);
			attachment.setPermission("Taken.staff", true);
			attachment.setPermission("Taken.STFU.Bypass", true);
			attachment.setPermission("simplereserve.enter.full", true);
			attachment.setPermission("Essentials.*", true);
		}
		if (HELPER.isInGroupInheritance(p)) {
			attachment.setPermission("Taken.Bug", true);
			attachment.setPermission("Taken.Idea", true);
			attachment.setPermission("Taken.SC", true);
			attachment.setPermission("VoxelSniper.Brush.*", false);
			attachment.setPermission("VoxelSniper.Sniper", false);
			attachment.setPermission("VoxelSniper.IgnoreLimitations", false);
			attachment.setPermission("bm.command.tempmute", true);
			attachment.setPermission("bm.command.kick", true);
			attachment.setPermission("bm.command.unmute", true);
			attachment.setPermission("bm.command.warn", true);
			attachment.setPermission("bm.command.dwarn", true);
			attachment.setPermission("bm.command.notes.online", true);
			attachment.setPermission("bm.command.alts.online", true);
			attachment.setPermission("bm.command.alts", true);
			attachment.setPermission("Taken.group", true);
			attachment.setPermission("Taken.staff", true);
			attachment.setPermission("Taken.STFU.Bypass", true);
			attachment.setPermission("simplereserve.enter.full", true);
			attachment.setPermission("Taken.statscommand", true);
			attachment.setPermission("Taken.FlyinLobby", true);
		}
		if (BUILDERPLUS.isInGroupInheritance(p)) {
			attachment.setPermission("bm.command.ban", true);
			attachment.setPermission("bm.command.tempban", true);
			attachment.setPermission("bm.command.tempmute", true);
			attachment.setPermission("bm.command.kick", true);
			attachment.setPermission("bm.command.unmute", true);
			attachment.setPermission("bm.command.warn", true);
			attachment.setPermission("bm.command.addnote", true);
			attachment.setPermission("bm.command.notes", true);
			attachment.setPermission("bm.command.dwarn", true);
			attachment.setPermission("bm.command.bminfo", true);
			attachment.setPermission("bm.command.alts", true);
			attachment.setPermission("bm.command.unban", true);
			attachment.setPermission("bm.command.unbanip", true);
			attachment.setPermission("bm.command.tempbanip", true);
			attachment.setPermission("bm.command.banip", true);
			attachment.setPermission("bm.command.mute", true);
			attachment.setPermission("Essentials.warp", true);
		}
		if (BUILDER.isInGroupInheritance(p)) {
			attachment.setPermission("VoxelSniper.Brush.*", true);
			attachment.setPermission("VoxelSniper.Sniper", true);
			attachment.setPermission("VoxelSniper.IgnoreLimitations", true);
			attachment.setPermission("WorldEdit.*", true);
			attachment.setPermission("WorldGuard.*", true);
			attachment.setPermission("Essentials.*", true);
			attachment.setPermission("Taken.Bug", true);
			attachment.setPermission("Taken.Idea", true);
			attachment.setPermission("Taken.SC", true);
			attachment.setPermission("Taken.STFU.Bypass", true);
			attachment.setPermission("NoCheat.Admin", true);
			attachment.setPermission("nocheatplus.shortcut.bypass", true);
			attachment.setPermission("Taken.instanttp", true);
			attachment.setPermission("Taken.trail", true);
			attachment.setPermission("Essentials.warp", true);
			attachment.setPermission("simplereserve.enter.full", true);
			attachment.setPermission("Taken.statscommand", true);
			attachment.setPermission("Taken.FlyinLobby", true);
		}
		if (YOUTUBE.isInGroupInheritance(p)) {
			attachment.setPermission("Essentials.Fly", true);
			attachment.setPermission("Essentials.Vanish", true);
			attachment.setPermission("Taken.Multiplier.5", true);
			attachment.setPermission("Taken.STFU.Bypass", true);
		}
		if (PATRON.isInGroupInheritance(p)) {
			attachment.setPermission("Taken.Multiplier.5", true);
			attachment.setPermission("Taken.FlyinLobby", true);
		}
		if (SUBPLUSPLUS.isInGroupInheritance(p)) {
			attachment.setPermission("Taken.Multiplier.4", true);
		}
		if (SUBPLUS.isInGroupInheritance(p)) {
			attachment.setPermission("Taken.Multiplier.3", true);
		}
		if (SUB.isInGroupInheritance(p)) {
			attachment.setPermission("Taken.Multiplier.2", true);
			attachment.setPermission("simplereserve.enter.full", true);
		}
		p.setPermissionAttachment(attachment);
	}

	public static void unregisterPlayerPermissions(PlayerModel p) {
		PermissionAttachment attachment = p.getPermissionAttachment();
		p.getPlayer().removeAttachment(attachment);
	}
}
