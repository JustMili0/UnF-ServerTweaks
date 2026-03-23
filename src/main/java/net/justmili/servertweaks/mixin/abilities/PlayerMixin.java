package net.justmili.servertweaks.mixin.abilities;

import net.justmili.servertweaks.mechanics.abilities.AbilityManager;
import net.justmili.servertweaks.mechanics.abilities.AbilityRegistry;
import net.justmili.servertweaks.mechanics.abilities.AbilityEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {

    @Inject(method = "onClimbable", at = @At("RETURN"), cancellable = true)
    private void servertweaks$onClimbable(CallbackInfoReturnable<Boolean> cir) {
        Player self = (Player) (Object) this;
        if (!(self instanceof ServerPlayer sp)) return;
        if (cir.getReturnValue()) return;
        if (AbilityEffects.shouldClimb(sp)) cir.setReturnValue(true);
    }

    @Inject(method = "attack", at = @At("HEAD"))
    private void servertweaks$attack(Entity target, CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (!(self instanceof ServerPlayer sp)) return;
        if (!AbilityManager.has(sp.getUUID(), AbilityRegistry.STRONG)) return;
        if (target instanceof LivingEntity living) {
            living.hurt(sp.level().damageSources().playerAttack(sp), 4.0F);
        }
    }
}