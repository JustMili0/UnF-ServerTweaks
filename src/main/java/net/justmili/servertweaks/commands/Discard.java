package net.justmili.servertweaks.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.justmili.servertweaks.util.CommandUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Clearable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class Discard {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("discard").requires(src -> CommandUtil.hasPerms(src, 4))
            .then(Commands.argument("entity", EntityArgument.entity())
                .executes(context -> {
                    Entity entity = EntityArgument.getEntity(context, "entity");

                    entity.discard();

                    return 1;
                })
            )
            .then(Commands.argument("block", BlockPosArgument.blockPos())
                .executes(context -> {
                    BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "block");
                    ServerLevel level = context.getSource().getLevel();
                    BlockEntity blockEntity = level.getBlockEntity(pos);

                    if (blockEntity instanceof Clearable clearable) {
                        clearable.clearContent();
                    }
                    level.removeBlock(pos, false);

                    return 1;
                })
            )
            .then(Commands.literal("inventory")
                .then(Commands.argument("entity", EntityArgument.entity())
                    .executes(context -> {
                        Entity entity = EntityArgument.getEntity(context, "entity");

                        if (entity instanceof Player player) {
                            player.getInventory().clearContent();
                            player.containerMenu.setCarried(ItemStack.EMPTY);
                        } else if (entity instanceof Mob mob) {
                            if (mob instanceof Container container) {
                                container.clearContent();
                            }
                            for (EquipmentSlot slot : EquipmentSlot.values()) {
                                mob.setItemSlot(slot, ItemStack.EMPTY);
                            }
                        } else if (entity instanceof Container container) {
                            container.clearContent();
                        }

                        return 1;
                    })
                )
                .then(Commands.argument("block", BlockPosArgument.blockPos())
                    .executes(context -> {
                        BlockPos pos = BlockPosArgument.getLoadedBlockPos(context, "block");
                        ServerLevel level = context.getSource().getLevel();
                        BlockEntity blockEntity = level.getBlockEntity(pos);

                        if (blockEntity instanceof Clearable clearable) {
                            clearable.clearContent();
                        }

                        return 1;
                    })
                )
            )
        );
    }
}