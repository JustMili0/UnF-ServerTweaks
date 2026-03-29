package net.justmili.servertweaks.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.justmili.servertweaks.util.CommandUtil;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.blocks.BlockStateArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class FillExtras {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection environment) {
        dispatcher.register(Commands.literal("fillextras")
            .requires(src -> CommandUtil.hasPerms(src, 2))
            .then(Commands.argument("from", BlockPosArgument.blockPos())
                .then(Commands.argument("to", BlockPosArgument.blockPos())
                    .then(Commands.argument("block", BlockStateArgument.block(commandBuildContext))
                        .then(Commands.literal("destroyonly")
                            .then(Commands.argument("target", BlockStateArgument.block(commandBuildContext))
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    ServerLevel level = source.getLevel();

                                    BlockPos from = BlockPosArgument.getLoadedBlockPos(context, "from");
                                    BlockPos to = BlockPosArgument.getLoadedBlockPos(context, "to");
                                    BlockState replaceWith = BlockStateArgument.getBlock(context, "block").getState();
                                    BlockState targetBlock = BlockStateArgument.getBlock(context, "target").getState();

                                    int minX = Math.min(from.getX(), to.getX());
                                    int minY = Math.min(from.getY(), to.getY());
                                    int minZ = Math.min(from.getZ(), to.getZ());
                                    int maxX = Math.max(from.getX(), to.getX());
                                    int maxY = Math.max(from.getY(), to.getY());
                                    int maxZ = Math.max(from.getZ(), to.getZ());

                                    int count = 0;
                                    for (int x = minX; x <= maxX; x++) {
                                        for (int y = minY; y <= maxY; y++) {
                                            for (int z = minZ; z <= maxZ; z++) {
                                                BlockPos pos = new BlockPos(x, y, z);
                                                BlockState current = level.getBlockState(pos);

                                                if (current.getBlock() == targetBlock.getBlock()) {
                                                    Block.dropResources(current, level, pos, level.getBlockEntity(pos));
                                                    level.setBlock(pos, replaceWith, 3);
                                                    count++;
                                                }
                                            }
                                        }
                                    }

                                    CommandUtil.sendSucc(source, "[ServerTweaks] Destroyed " + count + " block(s) of " + targetBlock.getBlock().getName().getString() + ".");
                                    return count;
                                })
                            )
                        )
                        .then(Commands.literal("replaceonly")
                            .then(Commands.argument("target", BlockStateArgument.block(commandBuildContext))
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    ServerLevel level = source.getLevel();

                                    BlockPos from = BlockPosArgument.getLoadedBlockPos(context, "from");
                                    BlockPos to = BlockPosArgument.getLoadedBlockPos(context, "to");
                                    BlockState replaceWith = BlockStateArgument.getBlock(context, "block").getState();
                                    BlockState targetBlock = BlockStateArgument.getBlock(context, "target").getState();

                                    int minX = Math.min(from.getX(), to.getX());
                                    int minY = Math.min(from.getY(), to.getY());
                                    int minZ = Math.min(from.getZ(), to.getZ());
                                    int maxX = Math.max(from.getX(), to.getX());
                                    int maxY = Math.max(from.getY(), to.getY());
                                    int maxZ = Math.max(from.getZ(), to.getZ());

                                    int count = 0;
                                    for (int x = minX; x <= maxX; x++) {
                                        for (int y = minY; y <= maxY; y++) {
                                            for (int z = minZ; z <= maxZ; z++) {
                                                BlockPos pos = new BlockPos(x, y, z);
                                                BlockState current = level.getBlockState(pos);

                                                if (current.getBlock() == targetBlock.getBlock()) {
                                                    level.setBlock(pos, replaceWith, 3);
                                                    count++;
                                                }
                                            }
                                        }
                                    }

                                    CommandUtil.sendSucc(source, "[ServerTweaks] Replaced " + count + " block(s) of " + targetBlock.getBlock().getName().getString() + ".");
                                    return count;
                                })
                            )
                        )
                    )
                )
            )
        );
    }
}