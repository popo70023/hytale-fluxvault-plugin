/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.liquid.interaction;

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
import io.github.popo70023.fluxvault.FluxVaultPlugin;
import io.github.popo70023.fluxvault.api.FluxType;
import io.github.popo70023.fluxvault.api.IFluxHandler;
import io.github.popo70023.fluxvault.api.IFluxHandlerProvider;
import io.github.popo70023.fluxvault.payload.liquid.LiquidCapsuleType;
import io.github.popo70023.fluxvault.payload.liquid.LiquidFlux;
import io.github.popo70023.fluxvault.util.InteractionUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class FillLiquidContainerBlockInteraction extends SimpleBlockInteraction {
    public static final String Id = FluxVaultPlugin.loc("Fill_Liquid_Container_Block");
    public static final BuilderCodec<FillLiquidContainerBlockInteraction> CODEC;
    private String targetName = null;

    @Override
    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType type, @NonNullDecl InteractionContext context, @NullableDecl ItemStack itemInHand, @NonNullDecl Vector3i pos, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> playerRef = context.getEntity();
        Player player = commandBuffer.getComponent(playerRef, Player.getComponentType());
        IFluxHandler<LiquidFlux> handler = InteractionUtil.getBlockFluxHandler(world, pos, FluxType.LIQUID, targetName, IFluxHandlerProvider.FluxAccess.FILL);
        InteractionSyncData state = context.getState();

        if (player == null || itemInHand == null || itemInHand.isEmpty() || handler == null) {
            state.state = InteractionState.Failed;
            return;
        }

        if (fillWithCapsule(commandBuffer, context, itemInHand, handler)) {
            return;
        }

        state.state = InteractionState.Failed;
    }

    @Override
    protected void simulateInteractWithBlock(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl World world, @NonNullDecl Vector3i vector3i) {
    }

    private boolean fillWithCapsule(CommandBuffer<EntityStore> commandBuffer, InteractionContext context, ItemStack itemInHand, IFluxHandler<LiquidFlux> handler) {
        LiquidCapsuleType capsuleType = LiquidCapsuleType.getLiquidCapsuleType(itemInHand);
        if (capsuleType == null || capsuleType.isEmptyCapsule(itemInHand)) {
            return false;
        }

        ItemStack resultItem = LiquidCapsuleType.interactWithContainer(itemInHand, handler, IFluxHandler.FluxAction.EXECUTE);
        if (resultItem != null) {
            InteractionUtil.exchangeHeldItem(commandBuffer, context, 1, resultItem);
            return true;
        }
        return false;
    }

    static {
        CODEC = BuilderCodec.builder(FillLiquidContainerBlockInteraction.class, FillLiquidContainerBlockInteraction::new, SimpleBlockInteraction.CODEC)
                .documentation("Fills a target block's liquid activeContainer using a fluid-holding item from the player's hand.")
                .append(new KeyedCodec<>("TargetName", Codec.STRING), (o, v) -> o.targetName = v, (o) -> o.targetName).add()
                .build();
    }
}
