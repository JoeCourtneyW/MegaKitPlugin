package com.sly.main.resources;

import net.minecraft.server.v1_7_R4.PacketPlayOutWorldParticles;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public enum ParticleEffects
{

	HUGE_EXPLOSION("hugeexplosion", Material.STICK), LARGE_EXPLODE("largeexplode", Material.TNT), FIREWORKS_SPARK(
			"fireworksSpark", Material.FIREWORK), BUBBLE("bubble", Material.WATER_BUCKET), SUSPEND("suspend",
					Material.STICK), DEPTH_SUSPEND("depthSuspend", Material.STICK), TOWN_AURA("townaura",
							Material.BEDROCK), CRIT("crit", Material.DIAMOND_SWORD), MAGIC_CRIT("magicCrit",
									Material.ENCHANTED_BOOK), MOB_SPELL("mobSpell", Material.POTION), MOB_SPELL_AMBIENT(
											"mobSpellAmbient",
											Material.POTION), SPELL("spell", Material.POTION), INSTANT_SPELL(
													"instantSpell", Material.POTION), WITCH_MAGIC("witchMagic",
															Material.CAULDRON), NOTE("note",
																	Material.NOTE_BLOCK), PORTAL("portal",
																			Material.PORTAL), ENCHANTMENT_TABLE(
																					"enchantmenttable",
																					Material.ENCHANTMENT_TABLE), EXPLODE(
																							"explode",
																							Material.TNT), FLAME(
																									"flame",
																									Material.BLAZE_POWDER), LAVA(
																											"lava",
																											Material.LAVA_BUCKET), FOOTSTEP(
																													"footstep",
																													Material.STICK), SPLASH(
																															"splash",
																															Material.WATER_BUCKET), LARGE_SMOKE(
																																	"largesmoke",
																																	Material.WEB), CLOUD(
																																			"cloud",
																																			Material.SULPHUR), RED_DUST(
																																					"reddust",
																																					Material.REDSTONE_TORCH_ON), SNOWBALL_POOF(
																																							"snowballpoof",
																																							Material.SNOW_BALL), DRIP_WATER(
																																									"dripWater",
																																									Material.WATER), DRIP_LAVA(
																																											"dripLava",
																																											Material.LAVA), SNOW_SHOVEL(
																																													"snowshovel",
																																													Material.SNOW_BALL), SLIME(
																																															"slime",
																																															Material.SLIME_BALL), HEART(
																																																	"heart",
																																																	Material.REDSTONE), ANGRY_VILLAGER(
																																																			"angryVillager",
																																																			Material.EMERALD), HAPPY_VILLAGER(
																																																					"happyVillager",
																																																					Material.EMERALD), ICONCRACK(
																																																							"iconcrack_",
																																																							Material.STICK), TILECRACK(
																																																									"tilecrack_",
																																																									Material.STICK);

	private String particleName;
	private Material displayItem;

	ParticleEffects(String particleName, Material displayItem) {
		this.particleName = particleName;
		this.displayItem = displayItem;
	}

	public String getName() {
		return particleName;
	}

	public Material getDisplayItem() {
		return displayItem;
	}

	public void sendToPlayer(Player player, Location location, float offsetX, float offsetY, float offsetZ, float speed,
			int count) {
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles();

		try {
			ReflectionUtilities.setValue(packet, "a", particleName);
			ReflectionUtilities.setValue(packet, "b", (float) location.getX());
			ReflectionUtilities.setValue(packet, "c", (float) location.getY());
			ReflectionUtilities.setValue(packet, "d", (float) location.getZ());
			ReflectionUtilities.setValue(packet, "e", offsetX);
			ReflectionUtilities.setValue(packet, "f", offsetY);
			ReflectionUtilities.setValue(packet, "g", offsetZ);
			ReflectionUtilities.setValue(packet, "h", speed);
			ReflectionUtilities.setValue(packet, "i", count);
		} catch (Exception e) {
			System.out.println("Error trying to send particle effect to single player");
			return;
		}
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	@SuppressWarnings("deprecation")
	public void sendToAllPlayers(Location location, float speed, int count) {
		PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles();
		try {
			ReflectionUtilities.setValue(packet, "a", particleName);
			ReflectionUtilities.setValue(packet, "b", (float) location.getX());
			ReflectionUtilities.setValue(packet, "c", (float) location.getY());
			ReflectionUtilities.setValue(packet, "d", (float) location.getZ());
			ReflectionUtilities.setValue(packet, "e", /* new Random().nextFloat() */ 0.2F);
			ReflectionUtilities.setValue(packet, "f", /* new Random().nextFloat() */ 0.2F);
			ReflectionUtilities.setValue(packet, "g", /* new Random().nextFloat() */ 0.2F);
			ReflectionUtilities.setValue(packet, "h", speed);
			ReflectionUtilities.setValue(packet, "i", count);
		} catch (Exception e) {
			System.out.println("Error trying to send particle effect to single player");
			return;
		}
		for (Player player : Bukkit.getOnlinePlayers()) {
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
		}
	}
}