package net.justmili.servertweaks.network;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
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
    public void readFromNbt(CompoundTag tag, HolderLookup.Provider provider) {
        scaleLocked = tag.getBoolean("isScaleLocked").orElse(false);
    }

    @Override
    public void writeToNbt(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putBoolean("isScaleLocked", scaleLocked);
    }
}
