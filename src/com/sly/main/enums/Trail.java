package com.sly.main.enums;

import org.bukkit.Material;

import com.sly.main.database.Cell;
import com.sly.main.player.PlayerModel;
import com.sly.main.resources.ParticleEffects;
import com.sly.main.resources.SerializationUtil;

public enum Trail
{
	NONE(null, 0, "NONE", Material.THIN_GLASS),
	MAGMA(ParticleEffects.DRIP_LAVA, 5, "Magma Trail", Material.LAVA_BUCKET),
	WATER(ParticleEffects.DRIP_WATER, 5, "Water Trail", Material.WATER_BUCKET),
	MAGIC(ParticleEffects.MAGIC_CRIT, 5, "Magic Trail", Material.ENCHANTED_BOOK),
	SMOKE(ParticleEffects.RED_DUST, 5, "Smoke Trail", Material.SULPHUR),
	SLIME(ParticleEffects.SLIME, 3, "Slime Trail", Material.SLIME_BALL),
	SNOW(ParticleEffects.SNOWBALL_POOF, 3, "Snow Trail", Material.SNOW_BALL),
	ENCHANTED(ParticleEffects.ENCHANTMENT_TABLE, 5, "Enchanted Trail", Material.ENCHANTMENT_TABLE),
	VOID(ParticleEffects.TOWN_AURA, 15, "Void Trail", Material.BEDROCK),
	PORTAL(ParticleEffects.PORTAL, 5, "Portal Trail", Material.PORTAL);

	private Material material;
	private String displayName;
	private ParticleEffects pe;
	private int amount;

	Trail(ParticleEffects pe, int particleAmount, String displayName, Material material) {
		this.material = material;
		this.displayName = displayName;
		this.pe = pe;
		this.amount = particleAmount;
	}

	public String getDisplayName() {
		return displayName;
	}

	public ParticleEffects getParticleEffect() {
		return pe;
	}

	public int getParticleAmount() {
		return amount;
	}

	public Material getMaterial() {
		return material;
	}

	public static Trail fromInvName(String name) {
		for (Trail s : values()) {
			if (s.getDisplayName().equalsIgnoreCase(name)) {
				return s;
			}
		}
		return null;
	}

	public boolean isUnlocked(PlayerModel player) {
		String serialized = player.getDatabaseValue("TRAIL").toString();
		Integer[] playerValues = SerializationUtil.deserialize(serialized);
		int ordinal = this.ordinal(); //ordinal returns position in enum and playerValues
		
		return playerValues[ordinal] >= 1; // If it's 1 or 2 (Unlocked or selected)

	}

	public void setUnlocked(PlayerModel player) {
		Cell trailCell = player.getDatabaseCell("TRAIL");
		Integer[] playerValues = SerializationUtil.deserialize(trailCell.getValue().toString());
		int ordinal = this.ordinal(); //ordinal returns position in enum and playerValues
		playerValues[ordinal] = 1;
		
		trailCell.setValue(SerializationUtil.serialize(playerValues));
	}

	public void setSelected(PlayerModel player) {
		Cell trailCell = player.getDatabaseCell("TRAIL");
		Integer[] playerValues = trailCell.getValue().asIntArray();
		
		for (int i = 0; i < playerValues.length; i++) {
			if (values()[i] == this) { // If the index matches the trail we're setting, change the playerValue
				playerValues[i] = 2;
			} else if (playerValues[i] == 2) { // Otherwise, if this trail was selected previously, deselect it
				playerValues[i] = 1;
			}
		}
		
	}

	public static Trail getSelectedTrail(PlayerModel player) {
		String serialized = player.getDatabaseValue("TRAIL").toString();
		Integer[] values = SerializationUtil.deserialize(serialized);

		for (int i = 0; i < values.length; i++) {
			if (values[i] == 2) {
				return Trail.values()[i];
			}
		}
		return null; // Shouldn't happen, there should always be one with value 2
	}

	public static String getDefaultSerialization() {
		Integer[] defaultValues = new Integer[values().length];
		defaultValues[0] = 2;
		for (int i = 1; i < defaultValues.length; i++) {
			defaultValues[i] = 0;
		}
		return SerializationUtil.serialize(defaultValues);
	}
}
