package com.benchenssever.fluxvault.liquid;

import com.benchenssever.fluxvault.FluxVaultPlugin;
import com.benchenssever.fluxvault.api.IFluxHandler;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LiquidCapsuleType {
    public static final Map<String, LiquidCapsuleType> capsuleIdToCapsuleTypes = new HashMap<>();
    private static final Map<String, LiquidCapsuleType> capsuleToCapsuleType = new HashMap<>();
    private final String capsuleId;
    private final int capacity;
    private final Map<Liquid, String> liquidToCapsule = new HashMap<>();
    private final Map<String, Liquid> capsuleToLiquid = new HashMap<>();

    private LiquidCapsuleType(String capsuleId, int capacity) {
        this.capsuleId = capsuleId;
        this.capacity = capacity;
        LiquidCapsuleType.capsuleIdToCapsuleTypes.put(capsuleId, this);
    }

    public static LiquidCapsuleType of(String capsuleId, int capacity) {
        if (capsuleId == null || capsuleId.isEmpty()) {
            throw new IllegalArgumentException("Capsule ID cannot be null or empty.");
        }
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero.");
        }
        if (capsuleIdToCapsuleTypes.containsKey(capsuleId)) {
            throw new IllegalArgumentException("A capsule type with the ID '" + capsuleId + "' is already registered.");
        }
        return new LiquidCapsuleType(capsuleId, capacity);
    }

    public static void registerItemToCapsule(String capsuleId, int capacity, String itemId, String liquidId) {
        if (itemId == null || itemId.isEmpty()) {
            FluxVaultPlugin.getPluginLogger().atWarning().log("Item ID cannot be null or empty.");
            return;
        }
        if (Item.getAssetMap().getAsset(itemId) == Item.UNKNOWN) {
            FluxVaultPlugin.getPluginLogger().atWarning().log("Item is UNKNOWN.");
            return;
        }
        if (capsuleToCapsuleType.containsKey(itemId)) {
            FluxVaultPlugin.getPluginLogger().atWarning().log("Item '" + itemId + "' is already registered to a capsule.");
            return;
        }
        Liquid liquid;
        if (Objects.equals(liquidId, Liquid.EMPTY_ID)) liquid = Liquid.EMPTY;
        else {
            liquid = Liquid.getLiquidById(liquidId);
            if (liquid == Liquid.EMPTY) {
                FluxVaultPlugin.getPluginLogger().atWarning().log("Liquid with ID '" + liquidId + "' does not exist.");
                return;
            }
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

        targetType.putMapping(liquid, itemId);
    }

    public static boolean isLiquidCapsule(ItemStack itemStack) {
        return getLiquidCapsuleType(itemStack) != null;
    }

    public static LiquidCapsuleType getLiquidCapsuleType(ItemStack itemStack) {
        return capsuleToCapsuleType.get(itemStack.getItemId());
    }

    @Nullable
    public static ItemStack interactWithContainer(ItemStack capsule, IFluxHandler<LiquidFlux> handler, IFluxHandler.FluxAction action) {
        LiquidCapsuleType capsuleType = getLiquidCapsuleType(capsule);
        if (capsuleType == null) return null;
        boolean isEmptyCapsule = capsuleType.isEmptyCapsule(capsule);

        LiquidFlux contentFlux = (LiquidFlux) new LiquidFlux(capsuleType.getLiquidStackInCapsule(capsule)).withValidator(capsuleType::isLiquidHasCapsule);
        LiquidFlux interactedFlux = isEmptyCapsule ? handler.drain(contentFlux, IFluxHandler.FluxAction.SIMULATE) : handler.fill(contentFlux, IFluxHandler.FluxAction.SIMULATE);

        if (!interactedFlux.isEmpty() && interactedFlux.getAllQuantity() == capsuleType.getCapacity()) {
            if (isEmptyCapsule) {
                if (action.execute()) {
                    handler.drain(contentFlux, IFluxHandler.FluxAction.EXECUTE);
                }
                return capsuleType.getCapsuleWithLiquid(interactedFlux.getStack(0).getLiquid());
            } else {
                if (action.execute()) {
                    handler.fill(contentFlux, IFluxHandler.FluxAction.EXECUTE);
                }
                return capsuleType.getEmptyCapsule();
            }
        }

        return null;
    }

    private void putMapping(Liquid liquid, String itemId) {
        if (this.liquidToCapsule.containsKey(liquid)) {
            FluxVaultPlugin.getPluginLogger().atWarning().log("Liquid '" + liquid.getId() + "' is already registered to this capsule.");
            return;
        }
        this.liquidToCapsule.put(liquid, itemId);
        this.capsuleToLiquid.put(itemId, liquid);
        capsuleToCapsuleType.put(itemId, this);
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isEmptyCapsule(ItemStack itemStack) {
        return itemStack.getItemId().equals(liquidToCapsule.get(Liquid.EMPTY));
    }

    public boolean isLiquidHasCapsule(LiquidStack liquidStack) {
        return liquidToCapsule.containsKey(liquidStack.getLiquid());
    }

    public LiquidStack getLiquidStackInCapsule(ItemStack itemStack) {
        Liquid liquid = capsuleToLiquid.get(itemStack.getItemId());
        return liquid == null ? LiquidStack.EMPTY : LiquidStack.of(liquid, capacity);
    }

    public ItemStack getEmptyCapsule() {
        String emptyCapsuleID = liquidToCapsule.get(Liquid.EMPTY);
        if (emptyCapsuleID == null) return ItemStack.EMPTY;
        return new ItemStack(emptyCapsuleID);
    }

    public ItemStack getCapsuleWithLiquid(Liquid liquid) {
        String filledCapsuleID = liquidToCapsule.get(liquid);
        if (filledCapsuleID == null) return ItemStack.EMPTY;
        return new ItemStack(filledCapsuleID);
    }
}
