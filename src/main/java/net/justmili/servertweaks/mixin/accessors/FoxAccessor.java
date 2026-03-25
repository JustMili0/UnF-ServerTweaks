package net.justmili.servertweaks.mixin.accessors;

import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.fox.Fox;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.stream.Stream;

@Mixin(Fox.class)
public interface FoxAccessor {
    @Invoker("trusts")
    boolean invokeTrusts(LivingEntity entity);

    @Invoker("addTrustedEntity")
    void invokeAddTrustedEntity(LivingEntity entity);

    @Invoker("getTrustedEntities")
    Stream<EntityReference<LivingEntity>> invokeGetTrustedEntities();
}