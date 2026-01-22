package net.justmili.servertweaks.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.IdentifierArgument;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class DamageToggle {
    private static final Map<Identifier, Boolean> damageDisabled = new HashMap<>();
    private static boolean eventRegistered = false;

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext, Commands.CommandSelection environment) {
        if (!eventRegistered) {
            eventRegistered = true;

            ServerLivingEntityEvents.ALLOW_DAMAGE.register(
                (LivingEntity entity, DamageSource source, float amount) -> {
                    for (Map.Entry<Identifier, Boolean> entry : damageDisabled.entrySet()) {
                        if (!entry.getValue()) continue;

                        Identifier id = entry.getKey();
                        if (source.is(ResourceKey.create(Registries.DAMAGE_TYPE, id))) {
                            return false;
                        }
                    }
                    return true;
                }
            );

        }

        dispatcher.register(
            Commands.literal("damagetoggle")
                .requires(src -> src.permissions().hasPermission(Permissions.COMMANDS_MODERATOR))
                .then(Commands.argument("type", IdentifierArgument.id())
                    .suggests(DamageToggle::suggestDamageTypes)
                    .then(Commands.literal("true")
                        .executes(ctx -> setDamage(ctx, false)))
                    .then(Commands.literal("false")
                        .executes(ctx -> setDamage(ctx, true)))
                    .then(Commands.literal("status")
                        .executes(DamageToggle::sendStatus))
                )
        );
    }
    private static CompletableFuture<Suggestions> suggestDamageTypes(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        Registry<DamageType> registry =
            ctx.getSource()
                .getServer()
                .registryAccess()
                .lookupOrThrow(Registries.DAMAGE_TYPE);

        String remaining = builder.getRemainingLowerCase();

        for (Identifier key : registry.keySet()) {
            String id = key.toString(); // minecraft:fall
            if (id.startsWith(remaining)) {
                builder.suggest(id);
            }
        }

        return builder.buildFuture();
    }

    private static int setDamage(CommandContext<CommandSourceStack> ctx, boolean disable) {
        Identifier id = IdentifierArgument.getId(ctx, "type");

        damageDisabled.put(id, disable);

        ctx.getSource().sendSuccess(
            () -> Component.literal("Damage type '")
                .append(Component.literal(id.toString()).withStyle(ChatFormatting.AQUA))
                .append(Component.literal("' is now "))
                .append(Component.literal(disable ? "DISABLED" : "ENABLED")
                    .withStyle(disable ? ChatFormatting.RED : ChatFormatting.GREEN, ChatFormatting.BOLD)),
            true
        );
        return 1;
    }
    private static int sendStatus(CommandContext<CommandSourceStack> ctx) {
        Identifier id = IdentifierArgument.getId(ctx, "type");

        boolean disabled = damageDisabled.getOrDefault(id, false);

        ctx.getSource().sendSuccess(
            () -> Component.literal("Damage type '")
                .append(Component.literal(id.toString()).withStyle(ChatFormatting.AQUA))
                .append(Component.literal("' status: "))
                .append(Component.literal(disabled ? "DISABLED" : "ENABLED")
                    .withStyle(disabled ? ChatFormatting.RED : ChatFormatting.GREEN)),
            false
        );
        return 1;
    }
}
