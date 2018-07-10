package com.sly.main.utils;


import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

public enum ParticleEffects {

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

    public void sendToAllPlayers(Particle particle, Location location, float speed, int count) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.spawnParticle(particle, location, count, speed);
        }
    }
}