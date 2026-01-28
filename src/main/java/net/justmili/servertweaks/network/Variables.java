package net.justmili.servertweaks.network;

import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.ladysnake.cca.api.v3.component.Component;
import org.ladysnake.cca.api.v3.component.sync.AutoSyncedComponent;

public class Variables implements Component, AutoSyncedComponent {
    private boolean scaleLocked = false;

    public boolean isScaleLocked() {
        return scaleLocked;
    }

    public void setScaleLocked(boolean locked) {
        this.scaleLocked = locked;
    }

    @Override
    public void readData(ValueInput valueIn) {
        scaleLocked = valueIn.getBooleanOr("isScaleLocked", false);
    }

    @Override
    public void writeData(ValueOutput valueOut) {
        valueOut.putBoolean("isScaleLocked", scaleLocked);
    }
}
