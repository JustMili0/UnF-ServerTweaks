package net.justmili.servertweaks.mixin.abilities;

import net.justmili.servertweaks.config.Config;
import net.justmili.servertweaks.mechanics.abilities.context.PlayerContext;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobMixin {
    @Inject(method = "interact", at = @At("HEAD"))
    private void servertweaks$setContext(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!(Config.playerAbilities.get())) return;
        if ((Object) this instanceof TamableAnimal) PlayerContext.set(player);
    }

    @Inject(method = "interact", at = @At("RETURN"))
    private void servertweaks$clearContext(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (!(Config.playerAbilities.get())) return;
        if ((Object) this instanceof TamableAnimal) PlayerContext.set(null);
    }
}
