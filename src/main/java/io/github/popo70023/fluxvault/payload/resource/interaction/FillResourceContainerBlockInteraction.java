/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.resource.interaction;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
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
import io.github.popo70023.fluxvault.FluxVaultPlugin;
import io.github.popo70023.fluxvault.api.FluxType;
import io.github.popo70023.fluxvault.api.IFluxHandler;
import io.github.popo70023.fluxvault.api.IFluxHandlerProvider;
import io.github.popo70023.fluxvault.payload.resource.FluxResource;
import io.github.popo70023.fluxvault.payload.resource.ResourceFlux;
import io.github.popo70023.fluxvault.payload.resource.ResourceStack;
import io.github.popo70023.fluxvault.util.InteractionUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

//TODO: 實作ResourceCapsule後對接
public class FillResourceContainerBlockInteraction extends SimpleBlockInteraction {
    public static final String Id = FluxVaultPlugin.loc("Fill_Resource_Container_Block");
    public static final BuilderCodec<FillResourceContainerBlockInteraction> CODEC;

    private String targetResourceId;

    @Override
    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext context, @NullableDecl ItemStack itemStack, @NonNullDecl Vector3i pos, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> playerRef = context.getEntity();
        Player player = commandBuffer.getComponent(playerRef, Player.getComponentType());
        if (player == null) return;
        FluxType<ResourceFlux, ResourceStack> fluxType = FluxType.getResourceFluxTypeByName(targetResourceId);
        if (fluxType == null) {
            player.sendMessage(Message.raw("Can't find FluxType " + targetResourceId));
            return;
        }
        IFluxHandler<ResourceFlux> handler = InteractionUtil.getBlockFluxHandler(world, pos, fluxType, "", IFluxHandlerProvider.FluxAccess.FILL);
        if (handler != null) {
            ResourceFlux resultFlux = handler.fill(new ResourceFlux(ResourceStack.of(targetResourceId, 100)), IFluxHandler.FluxAction.EXECUTE);
            player.sendMessage(Message.raw(resultFlux.toString()));
        }
    }

    @Override
    protected void simulateInteractWithBlock(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl World world, @NonNullDecl Vector3i vector3i) {

    }

    static {
        CODEC = BuilderCodec.builder(FillResourceContainerBlockInteraction.class, FillResourceContainerBlockInteraction::new, SimpleBlockInteraction.CODEC)
                .documentation("Fill target resource to container.")
                .append(new KeyedCodec<>(FluxResource.RESOURCE_ID_KEY, Codec.STRING), (o, v) -> o.targetResourceId = v, (o) -> o.targetResourceId)
                .add()
                .build();
    }
}
