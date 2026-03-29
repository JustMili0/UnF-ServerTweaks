package net.justmili.servertweaks.mixin.abilities;

import net.justmili.servertweaks.config.Config;
import net.justmili.servertweaks.mechanics.abilities.AbilityEffects;
import net.justmili.servertweaks.mechanics.abilities.AbilityManager;
import net.justmili.servertweaks.mechanics.abilities.registry.AbilitiesRegistry;
import net.justmili.servertweaks.mixin.accessors.LivingEntityAccessor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    // TOUGH
    @Inject(method = "knockback", at = @At("HEAD"), cancellable = true)
    private void servertweaks$knockback(double strength, double x, double z, CallbackInfo ci) {
        if (!(Config.playerAbilities.get())) return;
        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof ServerPlayer player)) return;
        if (AbilityManager.has(player.getUUID(), AbilitiesRegistry.TOUGH)) ci.cancel();
    }

    // WEAK_TO_DAMAGE
    @Unique private boolean applyDamageModifyer = false;
    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    private void onActuallyHurt(ServerLevel level, DamageSource source, float amount, CallbackInfo ci) {
        if (!(Config.playerAbilities.get())) return;
        if (applyDamageModifyer) return;
        if (!(((LivingEntity) (Object) this) instanceof ServerPlayer player)) return;
        if (!AbilityManager.has(player.getUUID(), AbilitiesRegistry.WEAK_TO_DAMAGE)) return;
        if (source.is(DamageTypes.FALL)) return;

        applyDamageModifyer = true;
        ci.cancel();
        ((LivingEntityAccessor) this).invokeActuallyHurt(level, source, amount * 1.25f);
        applyDamageModifyer = false;
    }

    // CANT_BREATHE_AIR
    @Inject(method = "increaseAirSupply", at = @At("HEAD"), cancellable = true)
    private void servertweaks$increaseAirSupply(int currentAir, CallbackInfoReturnable<Integer> cir) {
        if (!(Config.playerAbilities.get())) return;
        LivingEntity self = (LivingEntity)(Object)this;
        if (!(self instanceof ServerPlayer player)) return;
        if (!AbilityManager.has(player.getUUID(), AbilitiesRegistry.CANT_BREATHE_AIR)) return;
        if (!player.isInWater()) cir.setReturnValue(currentAir);
    }

    // CLIMBS_WALLS
    @Inject(method = "onClimbable", at = @At("RETURN"), cancellable = true)
    private void servertweaks$onClimbable(CallbackInfoReturnable<Boolean> cir) {
        if (!(Config.playerAbilities.get())) return;
        if (!((Object) this instanceof ServerPlayer player)) return;
        if (cir.getReturnValue()) return;
        if (AbilityEffects.shouldClimb(player)) {
            ((LivingEntityAccessor) this).setLastClimbablePos(Optional.of(player.blockPosition()));
            cir.setReturnValue(true);
        }
    }
}