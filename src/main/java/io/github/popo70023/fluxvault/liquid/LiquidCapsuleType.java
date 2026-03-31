/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.liquid;

import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import io.github.popo70023.fluxvault.FluxVaultPlugin;
import io.github.popo70023.fluxvault.api.IFluxHandler;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LiquidCapsuleType {
    public static final Map<String, LiquidCapsuleType> capsuleIdToCapsuleTypes = new ConcurrentHashMap<>();
    private static final Map<String, LiquidCapsuleType> itemToCapsuleType = new ConcurrentHashMap<>();

    private final String capsuleId;
    private final int capacity;
    private final Map<String, String> liquidToCapsule = new ConcurrentHashMap<>();
    private final Map<String, String> capsuleToLiquid = new ConcurrentHashMap<>();

    private LiquidCapsuleType(String capsuleId, int capacity) {
        this.capsuleId = capsuleId;
        this.capacity = capacity;
    }

    public static LiquidCapsuleType of(String capsuleId, int capacity) {
        if (capsuleId == null || capsuleId.isEmpty()) {
            throw new IllegalArgumentException("Capsule ID cannot be null or empty.");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero.");
        }
        return capsuleIdToCapsuleTypes.computeIfAbsent(capsuleId, id -> new LiquidCapsuleType(id, capacity));
    }

    public static void registerItemToCapsule(String capsuleId, int capacity, String itemId, String liquidId) {
        if (itemId == null || itemId.isEmpty()) {
            FluxVaultPlugin.getPluginLogger().atWarning().log("Item ID cannot be null or empty.");
            return;
        }

        if (Item.getAssetMap().getAsset(itemId) == Item.UNKNOWN) {
            FluxVaultPlugin.getPluginLogger().atWarning().log("ItemId " + itemId + " is UNKNOWN.");
        }
        if (Liquid.getLiquidById(liquidId) == Liquid.UNKNOWN) {
            FluxVaultPlugin.getPluginLogger().atWarning().log("liquidId " + liquidId + " is UNKNOWN.");
        }

        String currentId = capsuleId;
        int counter = 0;
        LiquidCapsuleType targetType = null;

        while (targetType == null) {
            LiquidCapsuleType existing = capsuleIdToCapsuleTypes.get(currentId);

            if (existing == null) {
                targetType = LiquidCapsuleType.of(currentId, capacity);

                if (counter > 0) {
                    FluxVaultPlugin.getPluginLogger().atWarning().log(String.format(
                            "Capsule conflict resolved. '%s' (Cap: %d) renamed to '%s' to allow coexistence.",
                            capsuleId, capacity, currentId
                    ));
                }
            } else {
                if (existing.getCapacity() == capacity) {
                    targetType = existing;
                } else {
                    counter++;
                    currentId = capsuleId + "_" + counter;
                }
            }
        }

        if (itemToCapsuleType.putIfAbsent(itemId, targetType) != null) {
            FluxVaultPlugin.getPluginLogger().atWarning().log("Item '" + itemId + "' is already registered to a capsule.");
            return;
        }

        targetType.putMapping(liquidId, itemId);
    }

    public static boolean isLiquidCapsule(ItemStack itemStack) {
        return getLiquidCapsuleType(itemStack) != null;
    }

    public static LiquidCapsuleType getLiquidCapsuleType(ItemStack itemStack) {
        return itemToCapsuleType.get(itemStack.getItemId());
    }

    @Nullable
    public static ItemStack interactWithContainer(ItemStack capsule, IFluxHandler<LiquidFlux> handler, IFluxHandler.FluxAction action) {
        LiquidCapsuleType capsuleType = getLiquidCapsuleType(capsule);
        if (capsuleType == null) return null;
        boolean isEmptyCapsule = capsuleType.isEmptyCapsule(capsule);

        LiquidFlux contentFlux = new LiquidFlux(capsuleType.getLiquidStackInCapsule(capsule)).withValidator(capsuleType::isLiquidHasCapsule).withExact(true);
        LiquidFlux interactedFlux = isEmptyCapsule ? handler.drain(contentFlux, action) : handler.fill(contentFlux, action);

        if (!interactedFlux.isEmpty()) {
            if (isEmptyCapsule) {
                return capsuleType.getCapsuleWithLiquid(interactedFlux.getStack(0).getLiquidId());
            } else {
                return capsuleType.getEmptyCapsule();
            }
        }

        return null;
    }

    private void putMapping(String liquidId, String itemId) {
        if (this.liquidToCapsule.putIfAbsent(liquidId, itemId) != null) {
            FluxVaultPlugin.getPluginLogger().atWarning().log("Liquid '" + liquidId + "' is already registered to this capsule.");
            return;
        }
        this.capsuleToLiquid.put(itemId, liquidId);
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isEmptyCapsule(ItemStack itemStack) {
        return itemStack.getItemId().equals(liquidToCapsule.get(Liquid.EMPTY_ID));
    }

    public boolean isLiquidHasCapsule(LiquidStack liquidStack) {
        return liquidToCapsule.containsKey(liquidStack.getLiquidId());
    }

    public LiquidStack getLiquidStackInCapsule(ItemStack itemStack) {
        String liquidId = capsuleToLiquid.get(itemStack.getItemId());
        return LiquidStack.of(liquidId, capacity);
    }

    public ItemStack getEmptyCapsule() {
        return getCapsuleWithLiquid(Liquid.EMPTY_ID);
    }

    public ItemStack getCapsuleWithLiquid(String liquidID) {
        String filledCapsuleID = liquidToCapsule.get(liquidID);
        if (filledCapsuleID == null) return ItemStack.EMPTY;
        return new ItemStack(filledCapsuleID);
    }
}
