/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.common.interaction;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.popo70023.fluxvault.FluxVaultPlugin;
import io.github.popo70023.fluxvault.api.IFluxVaultHost;
import io.github.popo70023.fluxvault.util.FluxUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class InspectContainerInteraction extends SimpleInstantInteraction {
    public static final String Id = FluxVaultPlugin.loc("Inspect_Container");
    public static final BuilderCodec<InspectContainerInteraction> CODEC;

    @Override
    protected void firstRun(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext context, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getEntity();
        CommandBuffer<EntityStore> commandBuffer = context.getCommandBuffer();
        if (commandBuffer == null) return;

        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        BlockPosition targetBlockPos = context.getTargetBlock();
        if (targetBlockPos != null) {
            FluxUtil.forEachBlockComponent(commandBuffer.getExternalData().getWorld(), new Vector3i(targetBlockPos.x, targetBlockPos.y, targetBlockPos.z), component -> {
                if (component instanceof IFluxVaultHost provider) {
                    provider.sendContainerMessage(player);
                }
            });
        }
    }

    static {
        CODEC = BuilderCodec.builder(InspectContainerInteraction.class, InspectContainerInteraction::new, SimpleInstantInteraction.CODEC)
                .documentation("Send the target's FluxContainer interaction to player.")
                .build();
    }
}
