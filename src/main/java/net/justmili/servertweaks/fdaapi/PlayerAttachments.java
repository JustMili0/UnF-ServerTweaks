package net.justmili.servertweaks.fdaapi;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.justmili.servertweaks.ServerTweaks;
import net.justmili.servertweaks.config.Config;

@SuppressWarnings({"UnstableApiUsage", "NullableProblems"})
public final class PlayerAttachments {
    //Scale-Command related
    public static final AttachmentType<Boolean> SCALE_LOCKED = createPersistentValue("scale_locked", Codec.BOOL);

    //AFK-Command related
    public static final AttachmentType<Boolean> IS_AFK = AttachmentRegistry.<Boolean>builder().initializer(() -> false).persistent(Codec.BOOL).copyOnDeath().buildAndRegister(ServerTweaks.asResource("is_afk"));
    public static final AttachmentType<Double> AFK_X = createValue("afk_x", 0.0, Codec.DOUBLE);
    public static final AttachmentType<Double> AFK_Y = createValue("afk_y", 255.0, Codec.DOUBLE);
    public static final AttachmentType<Double> AFK_Z = createValue("afk_z", 0.0, Codec.DOUBLE);
    public static final AttachmentType<Integer> AFK_COOLDOWN =
        AttachmentRegistry.<Integer>builder().initializer(Config.afkCommandCooldown)
            .persistent(Codec.INT).copyOnDeath()
            .buildAndRegister(ServerTweaks.asResource("afk_cooldown")); //make it restart-persistant

    private PlayerAttachments() {}

    //Helper Methods
    //Creates values that will clear after a restart
    public static <T> AttachmentType<T> createValue(String path, T defaultValue, Codec<T> codec) {
        return AttachmentRegistry.create(ServerTweaks.asResource(path),
            builder -> builder.initializer(() -> defaultValue).persistent(codec).copyOnDeath());
    }
    //Creates values that will NOT clear after a restart
    /// DOESN'T WORK
    public static <T> AttachmentType<T> createPersistentValue(String path, Codec<T> codec) {
        return AttachmentRegistry.createPersistent(ServerTweaks.asResource(path), codec);
    }

    /// DOCS: https://docs.fabricmc.net/develop/data-attachments
}
