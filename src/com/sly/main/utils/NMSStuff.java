package com.sly.main.utils;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class NMSStuff {
    public static Class<?> getNMSClass(String clazz){
        try {
            return Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + clazz);
        }
        catch (Throwable t){
            t.printStackTrace();
        }
        return null;
    }
 
    public static Class<?> getCBClass(String clazz){
        try {
            return Class.forName("org.bukkit.craftbukkit." + Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3] + "." + clazz);
        }
        catch (Throwable t){
            t.printStackTrace();
        }
        return null;
    }
 
    public static Object getNMSValue(String clazz, String field, Object object){
        try {
            Field f = getNMSClass(clazz).getDeclaredField(field);
            f.setAccessible(true);
            return f.get(object);
        }
        catch (Throwable t){
            t.printStackTrace();
        }
        return null;
    }
 
    public static void setNMSValue(String clazz, String field, Object object, Object value){
        try {
            Field f = getNMSClass(clazz).getDeclaredField(field);
            f.setAccessible(true);
            f.set(object, value);
        }
        catch (Throwable t){
            t.printStackTrace();
        }
    }
 
    public static Object getCBValue(String clazz, String field, Object object){
        try {
            Field f = getCBClass(clazz).getDeclaredField(field);
            f.setAccessible(true);
            return f.get(object);
        }
        catch (Throwable t){
            t.printStackTrace();
        }
        return null;
    }
 
    public static void setCBValue(String clazz, String field, Object object, Object value){
        try {
            Field f = getCBClass(clazz).getDeclaredField(field);
            f.setAccessible(true);
            f.set(object, value);
        }
        catch (Throwable t){
            t.printStackTrace();
        }
    }
 
    public static Object getNMSItem(ItemStack item){
        try {
            Method meth = getCBClass("inventory.CraftItemStack").getDeclaredMethod("asNMSCopy", ItemStack.class);
            return meth.invoke(null, item);
        }
        catch (Throwable t){
            t.printStackTrace();
        }
        return null;
    }
 
    public static ItemStack getBukkitItem(Object item){
        try {
            Method meth = getCBClass("inventory.CraftItemStack").getDeclaredMethod("asCraftMirror", getNMSClass("ItemStack"));
            return (ItemStack)meth.invoke(null, item);
        }
        catch (Throwable t){
            t.printStackTrace();
        }
        return null;
    }
 
}
