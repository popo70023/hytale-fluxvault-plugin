package com.benchenssever.fluxvault.liquid.container;

import com.benchenssever.fluxvault.api.IFluxHandler;
import com.benchenssever.fluxvault.liquid.Liquid;
import com.benchenssever.fluxvault.liquid.LiquidCapsuleType;
import com.benchenssever.fluxvault.liquid.LiquidFlux;
import com.benchenssever.fluxvault.liquid.LiquidStack;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// Deprecated
public class LiquidContainerInteraction extends SimpleBlockInteraction {
    public static final BuilderCodec<LiquidContainerInteraction> CODEC = BuilderCodec.builder(LiquidContainerInteraction.class, LiquidContainerInteraction::new, SimpleBlockInteraction.CODEC)
            .documentation("Interaction with a liquid container.")
            .build();

    public static boolean interactWithContainer(Player player, InteractionContext context, ItemStack itemInHand, IFluxHandler<LiquidFlux, LiquidStack> container) {
        if (ItemStack.isEmpty(itemInHand)) return false;
        if (LiquidCapsuleType.isLiquidCapsule(itemInHand))
            return interactByCapsule(player, context, itemInHand, container);
        return false;
    }

    private static boolean interactByCapsule(Player player, InteractionContext context, ItemStack itemInHand, IFluxHandler<LiquidFlux, LiquidStack> container) {
        LiquidCapsuleType capsuleType = LiquidCapsuleType.getLiquidCapsuleType(itemInHand);
        if (capsuleType == null) return false;
        LiquidStack capacity = capsuleType.isEmptyCapsule(itemInHand) ? new LiquidStack(Liquid.EMPTY, capsuleType.getCapacity()) : capsuleType.getLiquidStackInCapsule(itemInHand);

        if (capacity.isEmpty()) {
            LiquidFlux extracted = container.drain(new LiquidFlux(capacity), IFluxHandler.FluxAction.SIMULATE);
            LiquidStack extractedStack = extracted.getStack(0);
            ItemStack newItemStack = capsuleType.getCapsuleWithLiquid(extractedStack.getLiquid());
            if (!newItemStack.isEmpty() && !extracted.isEmpty() && extractedStack.getQuantity() == capsuleType.getCapacity()) {
                container.drain(extracted, IFluxHandler.FluxAction.EXECUTE);
                return true;
            }
        } else {
            LiquidFlux acceptedAmount = container.fill(new LiquidFlux(capacity), IFluxHandler.FluxAction.SIMULATE);
            if (acceptedAmount.getStack(0).isEqual(capacity)) {
                container.fill(new LiquidFlux(capacity), IFluxHandler.FluxAction.EXECUTE);
                return true;
            }
        }

        return false;
    }

    protected void interactWithBlock(@Nonnull World world, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull Vector3i pos, @Nonnull CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getEntity();
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());

        if (player != null) {
            player.sendMessage(Message.raw("Interact with a Liquid Container"));
        }
    }

    protected void simulateInteractWithBlock(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull World world, @Nonnull Vector3i targetBlock) {
    }
}
