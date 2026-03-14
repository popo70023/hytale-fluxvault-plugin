package com.benchenssever.fluxvault.liquid.interaction;

import com.benchenssever.fluxvault.api.FluxType;
import com.benchenssever.fluxvault.api.IFluxHandler;
import com.benchenssever.fluxvault.liquid.LiquidCapsuleType;
import com.benchenssever.fluxvault.liquid.LiquidFlux;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
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

public class FillLiquidContainerInteraction extends SimpleBlockInteraction {
    public static final String ID = "FillLiquidContainer";
    public static final BuilderCodec<FillLiquidContainerInteraction> CODEC = BuilderCodec.builder(FillLiquidContainerInteraction.class, FillLiquidContainerInteraction::new, SimpleBlockInteraction.CODEC)
            .append(new KeyedCodec<>("SimulateOnly", Codec.BOOLEAN), (interact, val) -> interact.simulateOnly = val, interact -> interact.simulateOnly).add()
            .documentation("Fills a target block's liquid container using a fluid-holding item from the player's hand.")
            .build();

    protected boolean simulateOnly = false;

    @Override
    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType type, @NonNullDecl InteractionContext context, @NullableDecl ItemStack itemInHand, @NonNullDecl Vector3i pos, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> playerRef = context.getEntity();
        Player player = commandBuffer.getComponent(playerRef, Player.getComponentType());
        IFluxHandler<LiquidFlux> handler = InteractionUtil.getFluxHandler(world, pos, FluxType.LIQUID);
        InteractionSyncData state = context.getState();
        IFluxHandler.FluxAction action = simulateOnly ? IFluxHandler.FluxAction.SIMULATE : IFluxHandler.FluxAction.EXECUTE;

        if (player == null || itemInHand == null || itemInHand.isEmpty() || handler == null) {
            state.state = InteractionState.Failed;
            return;
        }

        if (fillWithCapsule(commandBuffer, context, itemInHand, handler, action)) {
            return;
        }

        state.state = InteractionState.Failed;
    }

    @Override
    protected void simulateInteractWithBlock(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl World world, @NonNullDecl Vector3i vector3i) {

    }

    private boolean fillWithCapsule(CommandBuffer<EntityStore> commandBuffer, InteractionContext context, ItemStack itemInHand, IFluxHandler<LiquidFlux> handler, IFluxHandler.FluxAction action) {
        LiquidCapsuleType capsuleType = LiquidCapsuleType.getLiquidCapsuleType(itemInHand);
        if (capsuleType == null || capsuleType.isEmptyCapsule(itemInHand)) {
            return false;
        }

        ItemStack resultItem = LiquidCapsuleType.interactWithContainer(itemInHand, handler, action);
        if (resultItem != null) {
            if (action.execute()) {
                InteractionUtil.exchangeHeldItem(commandBuffer, context, 1, resultItem);
            }
            return true;
        }
        return false;
    }
}
