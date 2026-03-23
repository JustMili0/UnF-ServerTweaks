package net.justmili.servertweaks.mixin.abilities;

import net.justmili.servertweaks.mechanics.abilities.AbilityManager;
import net.justmili.servertweaks.mechanics.abilities.sets.Abilities;
import net.justmili.servertweaks.mixin.abilities.context.PlayerContext;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import net.minecraft.world.entity.TamableAnimal;

@Mixin(TamableAnimal.class)
public class TamableAnimalMixin {

    @Inject(method = "isTame", at = @At("RETURN"), cancellable = true)
    private void servertweaks$isTame(CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) return;
        Player interacting = PlayerContext.get();
        if (interacting == null) return;
        if (AbilityManager.has(interacting.getUUID(), Abilities.FRIENDS_WITH_NATURE)) cir.setReturnValue(true);
    }
}