package net.justmili.servertweaks.fdaapi;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.justmili.servertweaks.ServerTweaks;
import net.justmili.servertweaks.config.Config;

@SuppressWarnings({"UnstableApiUsage", "NullableProblems"})
public final class PlayerAttachments {
    //Scale-Command related
    public static final AttachmentType<Boolean> SCALE_LOCKED =
        AttachmentRegistry.<Boolean>builder().initializer(() -> false)
            .persistent(Codec.BOOL).copyOnDeath()
            .buildAndRegister(ServerTweaks.asResource("scale_locked"));

    //AFK-Command related
    public static final AttachmentType<Boolean> IS_AFK =
        AttachmentRegistry.<Boolean>builder().initializer(() -> false)
            .persistent(Codec.BOOL).copyOnDeath()
            .buildAndRegister(ServerTweaks.asResource("is_afk"));
    public static final AttachmentType<Double> AFK_X =
        AttachmentRegistry.<Double>builder().initializer(() -> 0.0)
            .persistent(Codec.DOUBLE).copyOnDeath()
            .buildAndRegister(ServerTweaks.asResource("afk_x"));
    public static final AttachmentType<Double> AFK_Y =
        AttachmentRegistry.<Double>builder().initializer(() -> 255.0)
            .persistent(Codec.DOUBLE).copyOnDeath()
            .buildAndRegister(ServerTweaks.asResource("afk_y"));
    public static final AttachmentType<Double> AFK_Z =
        AttachmentRegistry.<Double>builder().initializer(() -> 0.0)
            .persistent(Codec.DOUBLE).copyOnDeath()
            .buildAndRegister(ServerTweaks.asResource("afk_z"));
    public static final AttachmentType<Integer> AFK_COOLDOWN =
        AttachmentRegistry.<Integer>builder().initializer(() -> Config.commandCooldown)
            .persistent(Codec.INT).copyOnDeath()
            .buildAndRegister(ServerTweaks.asResource("afk_cooldown"));

    private PlayerAttachments() {}
}
