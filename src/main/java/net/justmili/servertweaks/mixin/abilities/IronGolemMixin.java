package net.justmili.servertweaks.mixin.abilities;

import net.justmili.servertweaks.mechanics.abilities.AbilityManager;
import net.justmili.servertweaks.mechanics.abilities.registry.AbilityRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.golem.IronGolem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IronGolem.class)
public class IronGolemMixin {
    @Inject(method = "canAttackType", at = @At("HEAD"), cancellable = true)
    private void servertweaks$canAttackType(EntityType<?> entityType, CallbackInfoReturnable<Boolean> cir) {
        if (entityType != EntityType.PLAYER) return;
        IronGolem self = (IronGolem) (Object) this;
        if (self.getTarget() instanceof ServerPlayer sp && AbilityManager.has(sp.getUUID(), AbilityRegistry.IS_MONSTER)) {
            cir.setReturnValue(true);
        }
    }
}