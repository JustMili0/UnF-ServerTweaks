package net.justmili.servertweaks.mixin.abilities;

import net.justmili.servertweaks.mechanics.abilities.AbilityManager;
import net.justmili.servertweaks.mechanics.abilities.AbilityRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.gossip.GossipType;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Villager.class)
public class VillagerMixin {
    @Inject(method = "customServerAiStep", at = @At("HEAD"))
    private void servertweaks$customServerAiStep(ServerLevel level, CallbackInfo ci) {
        Villager self = (Villager) (Object) this;
        for (ServerPlayer player : level.players()) {
            if (!AbilityManager.has(player.getUUID(), AbilityRegistry.IS_MONSTER)) continue;
            if (self.distanceTo(player) > 16.0F) continue;
            if (self.getGossips().getReputation(player.getUUID(), t -> true) > -100) {
                self.getGossips().add(player.getUUID(), GossipType.MAJOR_NEGATIVE, 25);
            }
        }
    }
}