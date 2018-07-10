package com.sly.main.slots;

import com.sly.main.player.PlayerModel;

public interface SlotItem
{

	/**
	 * This method should call methods to give the player this item, handle duplicates, and send the player any messages that you wish
	 */
	public void onWin(PlayerModel player);

}
