package com.sly.main.kits;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.sly.main.player.PlayerModel;

public class GlobalKitListener implements Listener
{
	/**
	 * Energy On Hit
	 */
	@EventHandler(priority = EventPriority.HIGH)
	public void EnergyOnHit(EntityDamageByEntityEvent event) {
		if (event.isCancelled())
			return;
		if (event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.PLAYER) {
			PlayerModel player = PlayerModel.getPlayerModel((Player) event.getDamager());
			Kits kit = player.getKit();
			player.addEnergy(kit.getListenerClass().getEnergyOnBowHit());
		}
	}

	/**
	 * Energy On Bow Hit
	 */
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.HIGH)
	public void EnergyOnBowHit(EntityDamageByEntityEvent event) {
		if (event.isCancelled())
			return;
		if (event.getEntity().getType() == EntityType.PLAYER && event.getDamager().getType() == EntityType.ARROW
				&& ((Arrow) event.getDamager()).getShooter().getType() == EntityType.PLAYER) { // make sure its a player shooting another player with an arrow
			Arrow arrow = (Arrow) event.getDamager();
			if (event.getEntity() != arrow.getShooter()) { // make sure they don't shoot themselves
				PlayerModel player = PlayerModel.getPlayerModel(((Player) arrow.getShooter()));
				Kits kit = player.getKit();
				player.addEnergy(kit.getListenerClass().getEnergyOnBowHit());
			}
		}
	}

	/**
	 * Main Ability Listener
	 */
	@EventHandler
	public void mainAbility(PlayerInteractEvent event) {
		if (isActivateAbilityEvent(event)) {
			PlayerModel player = PlayerModel.getPlayerModel(event.getPlayer());
			Kits kit = player.getKit();
			kit.getListenerClass().mainAbility(player.getPlayer());
			player.resetEnergy();
		}
	}

	@SuppressWarnings("deprecation")
	public static void energyOverTime() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			PlayerModel player = PlayerModel.getPlayerModel(p);
			int energy = player.getKit().getListenerClass().getEnergyOverTime();
			player.addEnergy(energy);
		}
	}

	private static boolean isActivateAbilityEvent(PlayerInteractEvent event) {
		if (event.getItem() == null || event.getPlayer().getLevel() < 100)
			return false;
		if ((event.getAction().equals(Action.RIGHT_CLICK_AIR) || event.getAction().equals(Action.RIGHT_CLICK_BLOCK))
				&& (event.getItem().getType() == Material.IRON_SWORD || event.getItem().getType() == Material.IRON_AXE
						|| event.getItem().getType() == Material.DIAMOND_SWORD))
			return true;
		else if ((event.getAction().equals(Action.LEFT_CLICK_AIR) || event.getAction().equals(Action.LEFT_CLICK_BLOCK))
				&& event.getItem().getType().equals(Material.BOW))
			return true;
		return false;
	}
}
