package net.justmili.servertweaks.mechanics;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.justmili.servertweaks.init.Dimensions;
import net.justmili.servertweaks.util.ScalerUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.Set;

public final class Events {
    public static void register() {
        banishmentEvents();
        convertScaler();
    }

    //             --All this is specifically for the banishment dimension
    //Middle slot
    private static final int HOTBAR_SLOT = 4;
    public static void banishmentEvents() {
        //Safeguard 1 - No damage, can't escape via death
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (!(entity instanceof ServerPlayer player)) return true;
            return player.level().dimension() != Dimensions.BANISHMENT_WORLD;
        });

        ServerTickEvents.END_WORLD_TICK.register((ServerLevel level) -> {
            if (level.dimension() != Dimensions.BANISHMENT_WORLD) return;

            //Give torch so they can even see
            for (ServerPlayer player : level.players()) {
                ItemStack stack = player.getInventory().getItem(HOTBAR_SLOT);
                if (stack.isEmpty()) {
                    player.getInventory().setItem(
                        HOTBAR_SLOT,
                        new ItemStack(Items.TORCH)
                    );
                }

                //Safeguard 2 - Prevent falling into the deep void if the player breaks the bedrock somehow
                if (player.getY() < -1.0) {
                    int centerX = player.blockPosition().getX();
                    int centerZ = player.blockPosition().getZ();

                    for (int dx = -2; dx <= 2; dx++) {
                        for (int dz = -2; dz <= 2; dz++) {
                            BlockPos pos = new BlockPos(centerX + dx, 0, centerZ + dz);
                            if (!level.getBlockState(pos).is(Blocks.BEDROCK)) {
                                level.setBlock(pos, Blocks.BEDROCK.defaultBlockState(), 3);
                            }
                        }
                    }
                    player.teleportTo(level, player.getX(), 3.0, player.getZ(), Set.of(), player.getYRot(), player.getXRot(), true);
                    player.setDeltaMovement(0.0, 0.0, 0.0);
                    player.fallDistance = 0.0F;
                }
            }
        });

        //Safeguard 3 - despawn all dropped torch item entities so player can't infinitely dupe them and overload the server
        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (level.dimension() != Dimensions.BANISHMENT_WORLD) return;
            if (entity instanceof ItemEntity item && item.getItem().is(Items.TORCH)) {
                entity.discard();
            }
        });
    }

    //Temp convert method
    private static void convertScaler() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.getPlayer();
            ScalerUtil.convertScoreToVar(player);
        });
    }
}
