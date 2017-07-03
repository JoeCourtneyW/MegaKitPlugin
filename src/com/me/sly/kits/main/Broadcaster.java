package com.me.sly.kits.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.scheduler.BukkitRunnable;

public class Broadcaster {

	public Broadcaster() {
		if(Corrupted.getInstance().getConfig().getStringList("BroadcastMessages") == null){
			Corrupted.getInstance().getConfig().set("BroadcastMessages", new ArrayList<String>());
			Corrupted.getInstance().saveConfig();
		}
	}

	public void startBroadcaster() {
		new BukkitRunnable() {
			public void run() {
				Random random = new Random();
				if (Corrupted.getInstance().getConfig().getStringList("BroadcastMessages").size() > 0) {
					int index = random.nextInt(Corrupted.getInstance().getConfig().getStringList("BroadcastMessages").size());
					Chat.broadcastPrefixedMessage(Corrupted.getInstance().getConfig().getStringList("BroadcastMessages").get(index).replaceAll("&", "§").substring(1));
				}
			}
		}.runTaskTimer(Corrupted.getInstance(), 150 * 20, 150 * 20);
	}

	public void addMessage(String message) {
		List<String> messagess = Corrupted.getInstance().getConfig().getStringList("BroadcastMessages");
		messagess.add(message);
		Corrupted.getInstance().getConfig().set("BroadcastMessages", messagess);
		Corrupted.getInstance().saveConfig();
	}

	public int removeMessage(String messageIndexString) {
		if (Corrupted.getInstance().getConfig().getStringList("BroadcastMessages").size() > 0) {
			int messageIndex = 0;
			try{
				messageIndex = Integer.parseInt(messageIndexString);
			}catch(Exception e){
				return 2;
			}
			if (messageIndex > 0  && messageIndex < Corrupted.getInstance().getConfig().getStringList("BroadcastMessages").size()) {
				List<String> s = Corrupted.getInstance().getConfig().getStringList("BroadcastMessages");
				s.remove(messageIndex);
				Corrupted.getInstance().getConfig().set("BroadcastMessages", s);
				Corrupted.getInstance().saveConfig();
				return 0;
			} else {
				return 1;
			}
		}
		return -1;
	}

	public List<String> getMessages() {
		return Corrupted.getInstance().getConfig().getStringList("BroadcastMessages");
	}
}