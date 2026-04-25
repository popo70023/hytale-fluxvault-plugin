/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.util;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.popo70023.fluxvault.api.FluxType;
import io.github.popo70023.fluxvault.api.IFlux;
import io.github.popo70023.fluxvault.api.IFluxHandler;
import io.github.popo70023.fluxvault.api.IFluxHandlerProvider;

import javax.annotation.Nonnull;

public final class InteractionUtil {

    private InteractionUtil() {
    }

    public static <F extends IFlux<D>, D> IFluxHandler<F> getBlockFluxHandler(World world, Vector3i targetBlock, @Nonnull FluxType<F, D> fluxType, String targetName, IFluxHandlerProvider.FluxAccess access) {
        return FluxUtil.getBlockFluxHandler(world, targetBlock, fluxType, null, targetName, access);
    }

    public static <F extends IFlux<D>, D> IFluxHandler<F> getEntityFluxHandler(@Nonnull InteractionContext context, @Nonnull FluxType<F, D> fluxType, String targetName, IFluxHandlerProvider.FluxAccess access) {
        Ref<EntityStore> targetEntity = context.getTargetEntity();
        String hitDetail = context.getMetaStore().getIfPresentMetaObject(Interaction.HIT_DETAIL);
        return FluxUtil.getEntityFluxHandler(targetEntity, fluxType, hitDetail != null ? hitDetail : "", targetName, access);
    }

    public static void exchangeHeldItem(CommandBuffer<EntityStore> commandBuffer, InteractionContext context, int consumeAmount, ItemStack resultItem) {
        boolean consume = consumeHeldItem(context, consumeAmount);
        if (consume && resultItem != null && !resultItem.isEmpty()) {
            giveOrDropItem(commandBuffer, context, resultItem);
        }
    }

    public static boolean consumeHeldItem(InteractionContext context, int amount) {
        ItemContainer heldContainer = context.getHeldItemContainer();

        if (heldContainer != null) {
            short heldSlot = context.getHeldItemSlot();
            ItemStackSlotTransaction transaction = heldContainer.removeItemStackFromSlot(heldSlot, amount);

            if (transaction.succeeded()) {
                context.setHeldItem(heldContainer.getItemStack(heldSlot));
                return true;
            }
        }
        return false;
    }

    public static void giveOrDropItem(CommandBuffer<EntityStore> commandBuffer, InteractionContext context, @Nonnull ItemStack itemToGive) {
        ItemContainer heldContainer = context.getHeldItemContainer();
        if (heldContainer != null) {
            short heldSlot = context.getHeldItemSlot();
            ItemStack currentHeld = heldContainer.getItemStack(heldSlot);
            if (ItemStack.isEmpty(currentHeld)) {
                heldContainer.setItemStackForSlot(heldSlot, itemToGive);
                context.setHeldItem(itemToGive);
                return;
            }
        }

        Ref<EntityStore> playerRef = context.getEntity();
        CombinedItemContainer combinedContainer = InventoryComponent.getCombined(playerRef.getStore(), playerRef, InventoryComponent.HOTBAR_STORAGE_BACKPACK);
        SimpleItemContainer.addOrDropItemStack(commandBuffer, playerRef, combinedContainer, itemToGive);
    }
}
