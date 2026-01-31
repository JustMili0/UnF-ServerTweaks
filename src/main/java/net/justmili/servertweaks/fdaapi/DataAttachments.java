package net.justmili.servertweaks.fdaapi;

import com.mojang.serialization.Codec;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.justmili.servertweaks.ServerTweaks;

public final class DataAttachments {
    public static final AttachmentType<Boolean> SCALE_LOCKED =
        AttachmentRegistry.<Boolean>builder().initializer(() -> false)
            .persistent(Codec.BOOL)
            .copyOnDeath()
            .buildAndRegister(ServerTweaks.asId("scale_locked"));

    private DataAttachments() {}
}
