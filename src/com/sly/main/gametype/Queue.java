package com.sly.main.gametype;

import java.util.Collection;
import java.util.LinkedList;

import com.sly.main.guis.kitselector.KitSelector;
import com.sly.main.kits.Kits;
import com.sly.main.player.PlayerModel;

/**
 * T refers to the data type you wish to put into the queue
 */
public class Queue
{

	private java.util.Queue<Collection<PlayerModel>> queue;
	private GameType gameType;

	public Queue(GameType gameType) {
		queue = new LinkedList<Collection<PlayerModel>>();
		this.gameType = gameType;
	}

	public GameType getGameType() {
		return gameType;
	}

	public java.util.Queue<Collection<PlayerModel>> getQueue() {
		return queue;
	}
	public boolean isQueued(Collection<PlayerModel> team){
		return queue.contains(team);
	}

	public void addToQueue(Collection<PlayerModel> team) {
		queue.offer(team);
		for (PlayerModel player : team) {
			player.setQueuedInventory();
		}
	}

	public void removeFromQueue(Collection<PlayerModel> team) {
		queue.remove(team);
		for (PlayerModel player : team) {
			player.setDefaultInventory();
			player.setChosenKit(Kits.NONE);
		}
	}

	public void process() {
		if (queue.size() > 1) {
			Collection<PlayerModel> blueTeam = queue.poll();
			Collection<PlayerModel> yellowTeam = queue.poll();
			for (Arena arena : Arena.getArenas()) {
				if (arena.getGameType() == gameType && !arena.isInUse()) {
					queue.poll().forEach(s -> s.sendAbilityMessage());
					for (PlayerModel blue : blueTeam) {
						//TODO: WHY IS THIS STILL HERE
						new KitSelector(blue).setKit(blue.getChosenKit());
					}

					for (PlayerModel yellow : yellowTeam) {
						new KitSelector(yellow).setKit(yellow.getChosenKit());
					}

					new Game(arena, gameType, blueTeam, yellowTeam);

				}
			}
		}
	}
}
