package net.justmili.servertweaks.mixin;

import net.justmili.servertweaks.config.Config;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;

@Mixin(PistonStructureResolver.class)
public class BetterPushLimit {
    @ModifyConstant(method = "addBlockLine", constant = @Constant(intValue = 12))
    private int modifyPushLimit(int original) {
        return Config.pistonPushLimit.get();
    }
}
