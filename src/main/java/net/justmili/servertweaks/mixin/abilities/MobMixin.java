package net.justmili.servertweaks.mixin.abilities;

import net.justmili.servertweaks.mechanics.abilities.AbilityManager;
import net.justmili.servertweaks.mechanics.abilities.registry.AbilityRegistry;
import net.justmili.servertweaks.mixin.abilities.context.PlayerContext;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.bee.Bee;
import net.minecraft.world.entity.animal.polarbear.PolarBear;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.monster.illager.Pillager;
import net.minecraft.world.entity.monster.spider.Spider;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public class MobMixin {

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void servertweaks$setTarget(LivingEntity target, CallbackInfo ci) {
        if (!(target instanceof ServerPlayer sp)) return;
        if (!AbilityManager.has(sp.getUUID(), AbilityRegistry.FRIENDS_WITH_NATURE)) return;

        Mob self = (Mob) (Object) this;
        if (self instanceof Wolf wolf && !wolf.isTame()) { ci.cancel(); return; }
        if (self instanceof Bee) { ci.cancel(); return; }
        if (self instanceof PolarBear) { ci.cancel(); return; }
        if (self instanceof Spider) {
            if (!self.level().isDarkOutside()) { ci.cancel(); }
        }
    }

    @Inject(method = "setTarget", at = @At("HEAD"), cancellable = true)
    private void servertweaks$isMonsterSetTarget(LivingEntity target, CallbackInfo ci) {
        if (!(target instanceof ServerPlayer sp)) return;
        if (!AbilityManager.has(sp.getUUID(), AbilityRegistry.IS_MONSTER)) return;

        Mob self = (Mob) (Object) this;
        if (self instanceof Pillager) ci.cancel();
    }

    @Inject(method = "interact", at = @At("HEAD"))
    private void servertweaks$setContext(Player player, InteractionHand hand,
                                         CallbackInfoReturnable<InteractionResult> cir) {
        if ((Object) this instanceof TamableAnimal) PlayerContext.set(player);
    }

    @Inject(method = "interact", at = @At("RETURN"))
    private void servertweaks$clearContext(Player player, InteractionHand hand,
                                           CallbackInfoReturnable<InteractionResult> cir) {
        if ((Object) this instanceof TamableAnimal) PlayerContext.set(null);
    }
}