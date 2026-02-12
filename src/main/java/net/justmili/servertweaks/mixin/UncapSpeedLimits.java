package net.justmili.servertweaks.mixin;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;

@Mixin(ServerGamePacketListenerImpl.class)
public abstract class UncapSpeedLimits {
    @ModifyConstant(method = {"handleMovePlayer"}, constant = {@Constant(floatValue = 100.0F)})
    private float uncapPlayerSpeed(float speed) {
        return Float.MAX_VALUE;
    }

    @ModifyConstant(method = {"handleMovePlayer"}, constant = {@Constant(floatValue = 300.0F)})
    private float uncapElytraSpeed(float speed) {
        return Float.MAX_VALUE;
    }

    @ModifyConstant(method = {"handleMoveVehicle"}, constant = {@Constant(doubleValue = (double)100.0F)})
    private double uncapVehicleSpeed(double speed) {
        return Double.MAX_VALUE;
    }
}
