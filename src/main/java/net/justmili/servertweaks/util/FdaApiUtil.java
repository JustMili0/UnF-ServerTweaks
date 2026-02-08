package net.justmili.servertweaks.util;

import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.server.level.ServerPlayer;

@SuppressWarnings({"DataFlowIssue", "UnstableApiUsage", "NullableProblems"})
public class FdaApiUtil {
    //True/false and text
    public static boolean getBoolValue(ServerPlayer player, AttachmentType<Boolean> variable) {
        if (variable == null) return false;
        return Boolean.TRUE.equals(player.getAttached(variable));
    }
    public static void setBoolValue(ServerPlayer player, AttachmentType<Boolean> variable, boolean Bool) {
        player.setAttached(variable, Bool);
    }
    public static String getStringValue(ServerPlayer player, AttachmentType<String> variable) {
        if (variable == null) return "ValueReturnedNull";
        return player.getAttached(variable);
    }
    public static void setStringValue(ServerPlayer player, AttachmentType<String> variable, String value) {
        player.setAttached(variable, value);
    }

    //Numbers yay
    public static int getIntValue(ServerPlayer player, AttachmentType<Integer> variable) {
        if (variable == null) return 0;
        return player.getAttached(variable);
    }
    public static void setIntValue(ServerPlayer player, AttachmentType<Integer> variable, int Int) {
        player.setAttached(variable, Int);
    }
    public static double getDoubleValue(ServerPlayer player, AttachmentType<Double> variable) {
        if (variable == null) return 0.0;
        return player.getAttached(variable);
    }
    public static void setDoubleValue(ServerPlayer player, AttachmentType<Double> variable, double Double) {
        player.setAttached(variable, Double);
    }
    public static float getFloatValue(ServerPlayer player, AttachmentType<Float> variable) {
        if (variable == null) return 0.0f;
        return player.getAttached(variable);
    }
    public static void setFloatValue(ServerPlayer player, AttachmentType<Float> variable, float Float) {
        player.setAttached(variable, Float);
    }
    public static long getLongValue(ServerPlayer player, AttachmentType<Long> variable) {
        if (variable == null) return 0;
        return player.getAttached(variable);
    }
    public static void setLongValue(ServerPlayer player, AttachmentType<Long> variable, long Long) {
        player.setAttached(variable, Long);
    }
}
