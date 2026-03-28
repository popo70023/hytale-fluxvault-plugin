package com.benchenssever.fluxvault.liquid.interaction;

import com.benchenssever.fluxvault.api.FluxType;
import com.benchenssever.fluxvault.api.IFluxHandler;
import com.benchenssever.fluxvault.liquid.LiquidCapsuleType;
import com.benchenssever.fluxvault.liquid.LiquidFlux;
import com.benchenssever.fluxvault.util.InteractionUtil;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionSyncData;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class DrainLiquidContainerInteraction extends SimpleBlockInteraction {
    public static final String ID = "DrainLiquidContainer";
    public static final BuilderCodec<DrainLiquidContainerInteraction> CODEC = BuilderCodec.builder(DrainLiquidContainerInteraction.class, DrainLiquidContainerInteraction::new, SimpleBlockInteraction.CODEC)
            .documentation("Drain a target block's liquid container using a fluid-holding item from the player's hand.")
            .build();

    @Override
    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType type, @NonNullDecl InteractionContext context, @NullableDecl ItemStack itemInHand, @NonNullDecl Vector3i pos, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> playerRef = context.getEntity();
        Player player = commandBuffer.getComponent(playerRef, Player.getComponentType());
        IFluxHandler<LiquidFlux> handler = InteractionUtil.getFluxHandler(world, pos, FluxType.LIQUID);
        InteractionSyncData state = context.getState();

        if (player == null || itemInHand == null || itemInHand.isEmpty() || handler == null) {
            state.state = InteractionState.Failed;
            return;
        }

        if (drainWithCapsule(commandBuffer, context, itemInHand, handler)) {
            return;
        }

        state.state = InteractionState.Failed;
    }

    @Override
    protected void simulateInteractWithBlock(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl World world, @NonNullDecl Vector3i vector3i) {

    }

    private boolean drainWithCapsule(CommandBuffer<EntityStore> commandBuffer, InteractionContext context, ItemStack itemInHand, IFluxHandler<LiquidFlux> handler) {
        LiquidCapsuleType capsuleType = LiquidCapsuleType.getLiquidCapsuleType(itemInHand);
        if (capsuleType == null || !capsuleType.isEmptyCapsule(itemInHand)) {
            return false;
        }

        ItemStack resultItem = LiquidCapsuleType.interactWithContainer(itemInHand, handler, IFluxHandler.FluxAction.EXECUTE_EXACT);
        if (resultItem != null) {
            InteractionUtil.exchangeHeldItem(commandBuffer, context, 1, resultItem);
            return true;
        }
        return false;
    }
}
