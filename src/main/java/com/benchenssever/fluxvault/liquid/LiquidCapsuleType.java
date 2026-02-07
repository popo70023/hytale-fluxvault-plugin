package com.benchenssever.fluxvault.liquid;

import com.hypixel.hytale.server.core.inventory.ItemStack;

import java.util.Map;

public class LiquidCapsuleType {
    private final String capsuleID;
    private final int capacity;
    private final String emptyCapsuleID;
    private final Map<Liquid, String> liquidToFilledCapsule;

    public LiquidCapsuleType(String capsuleID, int capacity, String emptyItemID, Map<Liquid, String> fluidToFilledItem) {
        this.capsuleID = capsuleID;
        this.capacity = capacity;
        this.emptyCapsuleID = emptyItemID;
        this.liquidToFilledCapsule = fluidToFilledItem;
    }

    public static boolean isLiquidCapsule(ItemStack itemStack) {
        return getLiquidCapsuleType(itemStack) != null;
    }

    public static LiquidCapsuleType getLiquidCapsuleType(ItemStack itemStack) {
        return null;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isEmptyCapsule(ItemStack itemStack) {
        return itemStack.getItemId().equals(emptyCapsuleID);
    }

    public LiquidStack getLiquidStackInCapsule(ItemStack itemStack) {
        return LiquidStack.EMPTY;
    }

    public ItemStack getEmptyCapsule() {
        return new ItemStack(emptyCapsuleID);
    }

    public ItemStack getCapsuleWithLiquid(Liquid iiquid) {
        return ItemStack.EMPTY;
    }
}
