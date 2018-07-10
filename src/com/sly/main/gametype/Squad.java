package com.sly.main.gametype;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.UUID;

import com.sly.main.player.PlayerModel;

public class Squad implements Iterable<PlayerModel>
{
	private PlayerModel leader;
	private PlayerModel squadmate;
	private UUID id;
	private boolean isDissolved = false;

	public Squad(PlayerModel leader) {
		this.leader = leader;
		leader.setSquad(this);
		this.squadmate = null;
		id = UUID.randomUUID();
	}

	public PlayerModel getLeader() {
		return this.leader;
	}

	public void setSquadMate(PlayerModel squadmate) {
		this.squadmate = squadmate;
		squadmate.setSquad(this);
	}

	public void removeSquadMate() {
		this.squadmate = null;
		squadmate.setSquad(null);
	}

	public Collection<PlayerModel> getSquadMembers() {
		if (!isDissolved)
			return Collections.unmodifiableCollection(Arrays.asList(leader, squadmate));
		else
			return Collections.emptyList();
	}

	public PlayerModel getOtherPlayer(PlayerModel player) {
		for (PlayerModel squadmate : getSquadMembers()) {
			if (!squadmate.getUUID().equals(player.getUUID()))
				return squadmate;
		}
		return null;
	}

	public boolean readyToQueue() {
		return isFull();
	}

	public boolean isFull() {
		return squadmate != null;
	}

	public UUID getId() {
		return id;
	}

	public boolean isDissolved() {
		return isDissolved;
	}

	public void dissolve() {
		for (PlayerModel player : getSquadMembers()) {
			if (player.isOnline()) {
				player.sendPrefixedMessage("§eThe team you were on has been dissolved!");
				player.getPlayer().closeInventory(); // Close their inventory so they can't queue up for games on any inventory still on their screen
				player.setSquad(null); // Make sure they don't have a squad on their model anymore
			}
		}
		isDissolved = true;
	}

	@Override
	public Iterator<PlayerModel> iterator() {
		return getSquadMembers().iterator();
	}
}
