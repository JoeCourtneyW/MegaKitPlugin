package com.sly.main.utils;

import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Method;
import java.util.UUID;

public class AttributeAPI {
    public static ItemStack addAttribute(ItemStack item, Attributes attribute, double value, Operation op) {
        try {
            Object nmsItem = com.sly.main.utils.NMSStuff.getNMSItem(item);
            Object tag1 = createModifierTag(attribute, value, op);
            Object tag = com.sly.main.utils.NMSStuff.getNMSValue("ItemStack", "tag", nmsItem);
            if (tag == null) {
                tag = com.sly.main.utils.NMSStuff.getNMSClass("NBTTagCompound").newInstance();
                com.sly.main.utils.NMSStuff.setNMSValue("ItemStack", "tag", nmsItem, tag);
            }
            Object modifiers = com.sly.main.utils.NMSStuff.getNMSClass("NBTTagCompound").getMethod("get", String.class).invoke(tag, "AttributeModifiers");
            if (modifiers == null)
                modifiers = com.sly.main.utils.NMSStuff.getNMSClass("NBTTagList").newInstance();
            com.sly.main.utils.NMSStuff.getNMSClass("NBTTagList").getDeclaredMethod("add", com.sly.main.utils.NMSStuff.getNMSClass("NBTBase")).invoke(modifiers, tag1);
            com.sly.main.utils.NMSStuff.getNMSClass("NBTTagCompound").getDeclaredMethod("set", String.class, com.sly.main.utils.NMSStuff.getNMSClass("NBTBase")).invoke(tag, "AttributeModifiers", modifiers);
            com.sly.main.utils.NMSStuff.setNMSValue("ItemStack", "tag", nmsItem, tag);
            return com.sly.main.utils.NMSStuff.getBukkitItem(nmsItem);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return item;
    }
 
    private static Object createModifierTag(Attributes attribute, double value, Operation op) throws Throwable {
        Class<?> compound = com.sly.main.utils.NMSStuff.getNMSClass("NBTTagCompound");
        Method setString = compound.getDeclaredMethod("setString", String.class, String.class);
        Method setLong = compound.getDeclaredMethod("setString", String.class, long.class);
        Method setInt = compound.getDeclaredMethod("setString", String.class, int.class);
        UUID uid = UUID.randomUUID();
        Object tag = compound.newInstance();
        setString.invoke(tag, "AttributeName", attribute.getAttributeName());
        setString.invoke(tag, "Name", attribute.getAttributeName());
        setString.invoke(tag, "Amount", value);
        setInt.invoke(tag, "Operation", op.getValue());
        setLong.invoke(tag, "UUIDMost", uid.getMostSignificantBits());
        setLong.invoke(tag, "UUIDLeast", uid.getLeastSignificantBits());
        return tag;
    }
 
 
    public static ItemStack removeAttributes(ItemStack item){
        try {
            Object nmsItem = com.sly.main.utils.NMSStuff.getNMSItem(item);
            Object modifiers = com.sly.main.utils.NMSStuff.getNMSClass("NBTTagList").newInstance();
            Object tag = com.sly.main.utils.NMSStuff.getNMSValue("ItemStack", "tag", nmsItem);
            if (tag == null) {
                tag = com.sly.main.utils.NMSStuff.getNMSClass("NBTTagCompound").newInstance();
                com.sly.main.utils.NMSStuff.setNMSValue("ItemStack", "tag", nmsItem, tag);
            }
            Method meth = com.sly.main.utils.NMSStuff.getNMSClass("NBTTagCompound").getDeclaredMethod("set", String.class, com.sly.main.utils.NMSStuff.getNMSClass("NBTBase"));
            meth.invoke(tag, "AttributeModifiers", modifiers);
            com.sly.main.utils.NMSStuff.setNMSValue("ItemStack", "tag", nmsItem, tag);
            return com.sly.main.utils.NMSStuff.getBukkitItem(nmsItem);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return item;
    }
 
    public static ItemStack removeAttributesPotion(ItemStack item){
        try {
            Object nmsItem = com.sly.main.utils.NMSStuff.getNMSItem(item);
            Object modifiers = com.sly.main.utils.NMSStuff.getNMSClass("NBTTagList").newInstance();
            Object tag = com.sly.main.utils.NMSStuff.getNMSValue("ItemStack", "tag", nmsItem);
            if (tag == null) {
                tag = com.sly.main.utils.NMSStuff.getNMSClass("NBTTagCompound").newInstance();
                com.sly.main.utils.NMSStuff.setNMSValue("ItemStack", "tag", nmsItem, tag);
            }
            Method meth = com.sly.main.utils.NMSStuff.getNMSClass("NBTTagCompound").getDeclaredMethod("set", String.class, com.sly.main.utils.NMSStuff.getNMSClass("NBTBase"));
            meth.invoke(tag, "CustomPotionEffects", modifiers);
            com.sly.main.utils.NMSStuff.setNMSValue("ItemStack", "tag", nmsItem, tag);
            return com.sly.main.utils.NMSStuff.getBukkitItem(nmsItem);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return item;
    }
 
    private enum Attributes {
 
        SPAWN_REINFORCEMENTS(com.sly.main.utils.NMSStuff.getNMSValue("EntityZombie", "bp", null)),
        JUMP_STRENGTH(com.sly.main.utils.NMSStuff.getNMSValue("EntityHorse", "bv", null)),
        MAX_HEALTH(com.sly.main.utils.NMSStuff.getNMSValue("GenericAttributes", "a", null)),
        FOLLOW_RANGE(com.sly.main.utils.NMSStuff.getNMSValue("GenericAttributes", "b", null)),
        KNOCKBACK_RESISTANCE(com.sly.main.utils.NMSStuff.getNMSValue("GenericAttributes", "c", null)),
        MOVEMENT_SPEED(com.sly.main.utils.NMSStuff.getNMSValue("GenericAttributes", "d", null)),
        ATTACK_DAMAGE(com.sly.main.utils.NMSStuff.getNMSValue("GenericAttributes", "e", null));
 
        private Object attribute;
 
        private Attributes(Object a){
            attribute = a;
        }
 
        public String getAttributeName(){
            try {
                return (String) com.sly.main.utils.NMSStuff.getNMSClass("AttributeBase").getMethod("a").invoke(attribute);
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
 
        @SuppressWarnings("unused")
		public double getDefaultValue(){
            try {
                return (Double) com.sly.main.utils.NMSStuff.getNMSClass("AttributeBase").getMethod("b").invoke(attribute);
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
            return 0;
        }
 
        @SuppressWarnings("unused")
		public double getFixedValue(double value){
 
            try {
                return (Double) com.sly.main.utils.NMSStuff.getNMSClass("AttributeRanged").getMethod("a", Double.class).invoke(attribute, value);
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
            return 0;
        }
 
        @SuppressWarnings("unused")
		public Object getAttributeInstance(){
            return attribute;
        }
    }
 
    private enum Operation {
 
        ADD_AMOUNT(0),
        ADDITIVE_PERCENTAGE(1),
        MULTIPLICATIVE_PERCENTAGE(2);
 
        private int i;
 
        private Operation(int i){
            this.i = i;
        }
 
        public int getValue() {
            return i;
        }
 
    }
}
