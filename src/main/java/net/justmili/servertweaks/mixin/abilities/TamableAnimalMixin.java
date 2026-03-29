package net.justmili.servertweaks.mixin.abilities;

import net.justmili.servertweaks.config.Config;
import net.justmili.servertweaks.mechanics.abilities.AbilityManager;
import net.justmili.servertweaks.mechanics.abilities.registry.AbilitiesRegistry;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TamableAnimal.class)
public class TamableAnimalMixin {
    // 100% tame chance for FRIENDS_WITH_NATURE
    @Inject(method = "tame", at = @At("HEAD"), cancellable = true)
    private void servertweaks$tame(Player player, CallbackInfo ci) {
        if (!(Config.playerAbilities.get())) return;
        if (!AbilityManager.has(player.getUUID(), AbilitiesRegistry.FRIENDS_WITH_NATURE)) return;
        TamableAnimal self = (TamableAnimal)(Object)this;
        self.setOwner(player);
        self.setTame(true, true);
        ci.cancel();
    }
}