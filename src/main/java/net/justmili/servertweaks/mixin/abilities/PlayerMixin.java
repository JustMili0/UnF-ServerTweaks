package net.justmili.servertweaks.mixin.abilities;

import net.justmili.servertweaks.config.Config;
import net.justmili.servertweaks.mechanics.abilities.AbilityEffects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class PlayerMixin {
    // CLIMBS_WALLS
    @Inject(method = "onClimbable", at = @At("RETURN"), cancellable = true)
    private void servertweaks$onClimbable(CallbackInfoReturnable<Boolean> cir) {
        if (!(Config.playerAbilities.get())) return;
        Player self = (Player) (Object) this;
        if (!(self instanceof ServerPlayer player)) return;
        if (cir.getReturnValue()) return;
        if (AbilityEffects.shouldClimb(player)) cir.setReturnValue(true);
    }
}
