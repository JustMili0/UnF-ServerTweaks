package net.justmili.servertweaks.mixin.abilities;

import net.justmili.servertweaks.mechanics.abilities.AbilityManager;
import net.justmili.servertweaks.mechanics.abilities.AbilityRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "knockback", at = @At("HEAD"), cancellable = true)
    private void servertweaks$knockback(double strength, double x, double z, CallbackInfo ci) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof ServerPlayer sp)) return;
        if (AbilityManager.has(sp.getUUID(), AbilityRegistry.NO_KNOCKBACK)) ci.cancel();
    }
}