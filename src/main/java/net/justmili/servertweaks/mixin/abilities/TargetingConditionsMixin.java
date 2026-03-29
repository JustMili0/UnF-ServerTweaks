package net.justmili.servertweaks.mixin.abilities;

import net.justmili.servertweaks.config.Config;
import net.justmili.servertweaks.mechanics.abilities.AbilityManager;
import net.justmili.servertweaks.mechanics.abilities.registry.AbilitiesRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TargetingConditions.class)
public class TargetingConditionsMixin {
    // FRIENDS_WITH_NATURE
    @Inject(method = "test", at = @At("HEAD"), cancellable = true)
    private void servertweaks$preventTargetingFriendlyPlayer(ServerLevel level, LivingEntity attacker, LivingEntity target, CallbackInfoReturnable<Boolean> cir) {
        if (!(Config.playerAbilities.get())) return;
        if (!(target instanceof ServerPlayer player)) return;
        if (!AbilityManager.has(player.getUUID(), AbilitiesRegistry.FRIENDS_WITH_NATURE)) return;

        if (attacker instanceof TamableAnimal tamed && tamed.isTame()) return;
        cir.setReturnValue(false);
    }
}