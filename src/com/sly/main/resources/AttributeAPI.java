package com.sly.main.resources;

import java.lang.reflect.Method;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;

public class AttributeAPI {
    public static ItemStack addAttribute(ItemStack item, Attributes attribute, double value, Operation op) {
        try {
            Object nmsItem = NMSStuff.getNMSItem(item);
            Object tag1 = createModifierTag(attribute, value, op);
            Object tag = NMSStuff.getNMSValue("ItemStack", "tag", nmsItem);
            if (tag == null) {
                tag = NMSStuff.getNMSClass("NBTTagCompound").newInstance();
                NMSStuff.setNMSValue("ItemStack", "tag", nmsItem, tag);
            }
            Object modifiers = NMSStuff.getNMSClass("NBTTagCompound").getMethod("get", String.class).invoke(tag, "AttributeModifiers");
            if (modifiers == null)
                modifiers = NMSStuff.getNMSClass("NBTTagList").newInstance();
            NMSStuff.getNMSClass("NBTTagList").getDeclaredMethod("add", NMSStuff.getNMSClass("NBTBase")).invoke(modifiers, tag1);
            NMSStuff.getNMSClass("NBTTagCompound").getDeclaredMethod("set", String.class, NMSStuff.getNMSClass("NBTBase")).invoke(tag, "AttributeModifiers", modifiers);
            NMSStuff.setNMSValue("ItemStack", "tag", nmsItem, tag);
            return NMSStuff.getBukkitItem(nmsItem);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return item;
    }
 
    private static Object createModifierTag(Attributes attribute, double value, Operation op) throws Throwable {
        Class<?> compound = NMSStuff.getNMSClass("NBTTagCompound");
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
            Object nmsItem = NMSStuff.getNMSItem(item);
            Object modifiers = NMSStuff.getNMSClass("NBTTagList").newInstance();
            Object tag = NMSStuff.getNMSValue("ItemStack", "tag", nmsItem);
            if (tag == null) {
                tag = NMSStuff.getNMSClass("NBTTagCompound").newInstance();
                NMSStuff.setNMSValue("ItemStack", "tag", nmsItem, tag);
            }
            Method meth = NMSStuff.getNMSClass("NBTTagCompound").getDeclaredMethod("set", String.class, NMSStuff.getNMSClass("NBTBase"));
            meth.invoke(tag, "AttributeModifiers", modifiers);
            NMSStuff.setNMSValue("ItemStack", "tag", nmsItem, tag);
            return NMSStuff.getBukkitItem(nmsItem);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return item;
    }
 
    public static ItemStack removeAttributesPotion(ItemStack item){
        try {
            Object nmsItem = NMSStuff.getNMSItem(item);
            Object modifiers = NMSStuff.getNMSClass("NBTTagList").newInstance();
            Object tag = NMSStuff.getNMSValue("ItemStack", "tag", nmsItem);
            if (tag == null) {
                tag = NMSStuff.getNMSClass("NBTTagCompound").newInstance();
                NMSStuff.setNMSValue("ItemStack", "tag", nmsItem, tag);
            }
            Method meth = NMSStuff.getNMSClass("NBTTagCompound").getDeclaredMethod("set", String.class, NMSStuff.getNMSClass("NBTBase"));
            meth.invoke(tag, "CustomPotionEffects", modifiers);
            NMSStuff.setNMSValue("ItemStack", "tag", nmsItem, tag);
            return NMSStuff.getBukkitItem(nmsItem);
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        return item;
    }
 
    private enum Attributes {
 
        SPAWN_REINFORCEMENTS(NMSStuff.getNMSValue("EntityZombie", "bp", null)),
        JUMP_STRENGTH(NMSStuff.getNMSValue("EntityHorse", "bv", null)),
        MAX_HEALTH(NMSStuff.getNMSValue("GenericAttributes", "a", null)),
        FOLLOW_RANGE(NMSStuff.getNMSValue("GenericAttributes", "b", null)),
        KNOCKBACK_RESISTANCE(NMSStuff.getNMSValue("GenericAttributes", "c", null)),
        MOVEMENT_SPEED(NMSStuff.getNMSValue("GenericAttributes", "d", null)),
        ATTACK_DAMAGE(NMSStuff.getNMSValue("GenericAttributes", "e", null));
 
        private Object attribute;
 
        private Attributes(Object a){
            attribute = a;
        }
 
        public String getAttributeName(){
            try {
                return (String) NMSStuff.getNMSClass("AttributeBase").getMethod("a").invoke(attribute);
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
            return null;
        }
 
        @SuppressWarnings("unused")
		public double getDefaultValue(){
            try {
                return (Double)NMSStuff.getNMSClass("AttributeBase").getMethod("b").invoke(attribute);
            }
            catch (Throwable t) {
                t.printStackTrace();
            }
            return 0;
        }
 
        @SuppressWarnings("unused")
		public double getFixedValue(double value){
 
            try {
                return (Double)NMSStuff.getNMSClass("AttributeRanged").getMethod("a", Double.class).invoke(attribute, value);
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
