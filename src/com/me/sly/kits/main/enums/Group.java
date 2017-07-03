package com.me.sly.kits.main.enums;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import com.me.sly.kits.main.Corrupted;

public enum Group {
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
	Group(Group inheritance, String prefix){
		this.inheritance = inheritance;
		this.prefix = prefix;
	}

	public String getPrefix(){
		return this.prefix;
	}
	public Group getInheritance(){
		return this.inheritance;
	}
	public static List<Group> getSpecificGroups(Player p){
		String g = (String) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Rank");
		List<Group> groups = new ArrayList<Group>();
		for(String r : g.split(",")){
			groups.add(Group.valueOf(r));
		}


		return groups;
	}
	public static List<Group> getSpecificGroups(UUID p){
		String g = (String) Corrupted.getDatabaseCache().get(p.toString()).get("Rank");
		List<Group> groups = new ArrayList<Group>();
		for(String r : g.split(",")){
			groups.add(Group.valueOf(r));
		}


		return groups;
	}
	public boolean isInSpecificGroup(Player p){
			for(Group g : Group.getSpecificGroups(p)){
			if(g.name() == this.name())	return true;
			}
		
		return false;
	}
	public boolean isInSpecificGroup(UUID p){
		for(Group g : Group.getSpecificGroups(p)){
		if(g.name() == this.name())	return true;
		}
	
	return false;
}
	public static boolean isPartOfGroupInheritance(Player p, Group group){
		String g = (String) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Rank");
		Group[] groups;
		if(g.contains(",")){
		groups = new Group[2];
		groups[0] = Group.valueOf(g.split(",")[0]);
		groups[1] = Group.valueOf(g.split(",")[1]);
		if(groups[0].equals(group) || groups[1].equals(group)){
			return true;
		}
		Group lastGroup = groups[0];
		for(int i = 0; i < 7; i++){
			if(lastGroup.getInheritance() != null){
				if(lastGroup.getInheritance() == group){
					return true;
				}else{
					lastGroup = lastGroup.getInheritance();
				}
			}else{
				return false;
			}
		}
		lastGroup = groups[1];
		for(int i = 0; i < 7; i++){
			if(lastGroup.getInheritance() != null){
				if(lastGroup.getInheritance() == group){
					return true;
				}else{
					lastGroup = lastGroup.getInheritance();
				}
			}else{
				return false;
			}
		}
		
		}else{
		groups = new Group[1];
		groups[0] = Group.valueOf(g);
		if(groups[0].equals(group)){
			return true;
		}
		Group lastGroup = groups[0];
		for(int i = 0; i < 7; i++){
			if(lastGroup.getInheritance() != null){
				if(lastGroup.getInheritance() == group){
					return true;
				}else{
					lastGroup = lastGroup.getInheritance();
				}
			}else{
				return false;
			}
		}
		}
		return false;
	}
	public static boolean isPartOfGroupInheritance(UUID p, Group group){
		String g = (String) Corrupted.getDatabaseCache().get(p.toString()).get("Rank");
		Group[] groups;
		if(g.contains(",")){
		groups = new Group[2];
		groups[0] = Group.valueOf(g.split(",")[0]);
		groups[1] = Group.valueOf(g.split(",")[1]);
		Group lastGroup = groups[0];
		for(int i = 0; i < 7; i++){
			if(lastGroup.getInheritance() != null){
				if(lastGroup.getInheritance() == group){
					return true;
				}else{
					lastGroup = lastGroup.getInheritance();
				}
			}else{
				return false;
			}
		}
		lastGroup = groups[2];
		for(int i = 0; i < 7; i++){
			if(lastGroup.getInheritance() != null){
				if(lastGroup.getInheritance() == group){
					return true;
				}else{
					lastGroup = lastGroup.getInheritance();
				}
			}else{
				return false;
			}
		}
		
		}else{
		groups = new Group[1];
		groups[0] = Group.valueOf(g);
		Group lastGroup = groups[0];
		for(int i = 0; i < 7; i++){
			if(lastGroup.getInheritance() != null){
				if(lastGroup.getInheritance() == group){
					return true;
				}else{
					lastGroup = lastGroup.getInheritance();
				}
			}else{
				return false;
			}
		}
		}
		return false;
	}
	public static Group getGroup(Player p){
		String g = (String) Corrupted.getDatabaseCache().get(p.getUniqueId().toString()).get("Rank");
		return Group.valueOf(g);
	}
	public static boolean isGroup(String string) {

	    for (Group cr : Group.values()) {
	        if (cr.name().equals(string)) {
	            return true;
	        }
	    }

	    return false;
	}
	public static void registerPlayerPermissions(Player p){
		PermissionAttachment attachment = p.addAttachment(Corrupted.getInstance());

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
		if(Corrupted.getDatabaseCache().containsKey(p.getUniqueId().toString())){
		if(isPartOfGroupInheritance(p, DEV)){
			attachment.setPermission("Bukkit.command.Op", true);
			attachment.setPermission("WorldEdit.*", true);
			attachment.setPermission("Taken.Package", true);
			attachment.setPermission("NoCheat.Admin", true);
			attachment.setPermission("nocheatplus.shortcut.bypass", true);
			attachment.setPermission("Taken.db", true);	
			attachment.setPermission("Essentials.warp", true);
		}
		if(isPartOfGroupInheritance(p, HEADADMIN)){	
			attachment.setPermission("Taken.Arena", true);	
			attachment.setPermission("Taken.GCM", true);
			attachment.setPermission("Taken.setaccess", true);
			attachment.setPermission("Essentials.warp", true);
		}
		if(isPartOfGroupInheritance(p, ADMIN)){
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
		if(isPartOfGroupInheritance(p, MOD)){
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
		if(isPartOfGroupInheritance(p, HELPER)){
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
		if(isPartOfGroupInheritance(p, BUILDERPLUS)){		
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
		if(isPartOfGroupInheritance(p, BUILDER)){
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
		if(isPartOfGroupInheritance(p, YOUTUBE)){
			attachment.setPermission("Essentials.Fly", true);
			attachment.setPermission("Essentials.Vanish", true);			
			attachment.setPermission("Taken.Multiplier.5", true);
			attachment.setPermission("Taken.STFU.Bypass", true);
		}
		if(isPartOfGroupInheritance(p, PATRON)){
			attachment.setPermission("Taken.Multiplier.5", true);			
			attachment.setPermission("Taken.FlyinLobby", true);	
		}
		if(isPartOfGroupInheritance(p, SUBPLUSPLUS)){
			attachment.setPermission("Taken.Multiplier.4", true);			
		}
		if(isPartOfGroupInheritance(p, SUBPLUS)){
			attachment.setPermission("Taken.Multiplier.3", true);			
		}
		if(isPartOfGroupInheritance(p, SUB)){
			attachment.setPermission("Taken.Multiplier.2", true);			
			attachment.setPermission("simplereserve.enter.full", true);
		}
		}
		Corrupted.getAttachments().put(p.getUniqueId(), attachment);
	}
	public static void unregisterPlayerPermissions(Player p){
		PermissionAttachment attachment = Corrupted.getAttachments().get(p.getUniqueId());
		p.removeAttachment(attachment);
	}
}
