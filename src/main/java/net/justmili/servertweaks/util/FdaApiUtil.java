package net.justmili.servertweaks.util;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.server.level.ServerPlayer;

@SuppressWarnings({"UnstableApiUsage", "NullableProblems"})
public class FdaApiUtil {
    //True/false and text
    public static boolean getBoolValue(ServerPlayer player, AttachmentType<Boolean> variable) {
        return getValue(player, variable, false);
    }
    public static void setBoolValue(ServerPlayer player, AttachmentType<Boolean> variable, boolean Bool) {
        player.setAttached(variable, Bool);
    }
    public static String getStringValue(ServerPlayer player, AttachmentType<String> variable) {
        return getValue(player, variable, "ValueReturnedNull");
    }
    public static void setStringValue(ServerPlayer player, AttachmentType<String> variable, String value) {
        player.setAttached(variable, value);
    }

    //Numbers yay
    public static int getIntValue(ServerPlayer player, AttachmentType<Integer> variable) {
        return getValue(player, variable, 0);
    }
    public static void setIntValue(ServerPlayer player, AttachmentType<Integer> variable, int Int) {
        player.setAttached(variable, Int);
    }
    public static double getDoubleValue(ServerPlayer player, AttachmentType<Double> variable) {
        return getValue(player, variable, 0.0);
    }
    public static void setDoubleValue(ServerPlayer player, AttachmentType<Double> variable, double Double) {
        player.setAttached(variable, Double);
    }
    public static float getFloatValue(ServerPlayer player, AttachmentType<Float> variable) {
        return getValue(player, variable, 0f);
    }
    public static void setFloatValue(ServerPlayer player, AttachmentType<Float> variable, float Float) {
        player.setAttached(variable, Float);
    }
    public static long getLongValue(ServerPlayer player, AttachmentType<Long> variable) {
        return getValue(player, variable, 0L);
    }
    public static void setLongValue(ServerPlayer player, AttachmentType<Long> variable, long Long) {
        player.setAttached(variable, Long);
    }

    //Helper
    public static <T> T getValue(ServerPlayer player, AttachmentType<T> variable, T defaultValue) {
        if (variable == null) return defaultValue;
        T value = player.getAttached(variable);
        return value != null ? value : defaultValue;
    }
}
