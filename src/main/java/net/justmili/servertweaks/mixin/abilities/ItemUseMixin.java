package net.justmili.servertweaks.mixin.abilities;

import net.justmili.servertweaks.mechanics.abilities.AbilityEffects;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemUseMixin {
    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void servertweaks$use(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (level.isClientSide()) return;
        if (!(player instanceof ServerPlayer sp)) return;
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.has(DataComponents.FOOD)) return;
        if (!AbilityEffects.onItemUse(sp, stack)) cir.setReturnValue(InteractionResult.FAIL);
    }
}