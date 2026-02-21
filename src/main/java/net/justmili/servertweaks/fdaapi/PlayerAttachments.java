package net.justmili.servertweaks.fdaapi;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.justmili.servertweaks.config.Config;

import static net.justmili.servertweaks.util.FdaApiUtil.createPersistentValue;
import static net.justmili.servertweaks.util.FdaApiUtil.createValue;

@SuppressWarnings({"UnstableApiUsage", "NullableProblems"})
public final class PlayerAttachments {
    private PlayerAttachments() {}
    public static void register() {}

    //Scale-Command related
    public static final AttachmentType<Boolean> SCALE_LOCKED = createPersistentValue("scale_locked", false, Codec.BOOL);

    //AFK-Command related
    public static final AttachmentType<Boolean> IS_AFK = createPersistentValue("is_afk", false, Codec.BOOL);
    public static final AttachmentType<Double> AFK_X = createPersistentValue("afk_x", 0.0, Codec.DOUBLE);
    public static final AttachmentType<Double> AFK_Y = createPersistentValue("afk_y", 255.0, Codec.DOUBLE);
    public static final AttachmentType<Double> AFK_Z = createPersistentValue("afk_z", 0.0, Codec.DOUBLE);
    public static final AttachmentType<Integer> AFK_COOLDOWN = createPersistentValue("afk_cooldown", Config.afkCommandCooldown.get(), Codec.INT);

    //Duel-Command related
    public static final AttachmentType<String> AWAITING_DUEL_RECIPIENT = createValue("duel_recipient", "val_inactive", Codec.STRING);
    public static final AttachmentType<String> AWAITING_DUEL_SENDER = createValue("duel_sender", "val_inactive", Codec.STRING);
    public static final AttachmentType<Boolean> IN_DUEL = createValue("in_duel", false, Codec.BOOL);
    public static final AttachmentType<String> DUELING_WITH = createValue("dueling_with", "val_inactive", Codec.STRING);
    public static final AttachmentType<Long> LAST_HIT_TIME = createValue("last_hit_time", 0L, Codec.LONG);

}
