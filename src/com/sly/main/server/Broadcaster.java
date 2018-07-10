package com.sly.main.server;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.scheduler.BukkitRunnable;

import com.sly.main.Server;

public class Broadcaster {

	public Broadcaster() {
		if(Server.getInstance().getConfig().getStringList("BroadcastMessages") == null){
			Server.getInstance().getConfig().set("BroadcastMessages", new ArrayList<String>());
			Server.getInstance().saveConfig();
		}
	}

	public void startBroadcaster() {
		new BukkitRunnable() {
			public void run() {
				Random random = new Random();
				if (Server.getInstance().getConfig().getStringList("BroadcastMessages").size() > 0) {
					int index = random.nextInt(Server.getInstance().getConfig().getStringList("BroadcastMessages").size());
					Chat.broadcastPrefixedMessage(Server.getInstance().getConfig().getStringList("BroadcastMessages").get(index).replaceAll("&", "§").substring(1));
				}
			}
		}.runTaskTimer(Server.getInstance(), 150 * 20, 150 * 20);
	}

	public void addMessage(String message) {
		List<String> messagess = Server.getInstance().getConfig().getStringList("BroadcastMessages");
		messagess.add(message);
		Server.getInstance().getConfig().set("BroadcastMessages", messagess);
		Server.getInstance().saveConfig();
	}

	public int removeMessage(String messageIndexString) {
		if (Server.getInstance().getConfig().getStringList("BroadcastMessages").size() > 0) {
			int messageIndex = 0;
			try{
				messageIndex = Integer.parseInt(messageIndexString);
			}catch(Exception e){
				return 2;
			}
			if (messageIndex > 0  && messageIndex < Server.getInstance().getConfig().getStringList("BroadcastMessages").size()) {
				List<String> s = Server.getInstance().getConfig().getStringList("BroadcastMessages");
				s.remove(messageIndex);
				Server.getInstance().getConfig().set("BroadcastMessages", s);
				Server.getInstance().saveConfig();
				return 0;
			} else {
				return 1;
			}
		}
		return -1;
	}

	public List<String> getMessages() {
		return Server.getInstance().getConfig().getStringList("BroadcastMessages");
	}
}