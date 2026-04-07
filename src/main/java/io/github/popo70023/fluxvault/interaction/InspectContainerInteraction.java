/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.interaction;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.popo70023.fluxvault.FluxVaultPlugin;
import io.github.popo70023.fluxvault.api.IFluxContainerProvider;
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
        BlockPosition targetBlockPosRew = context.getMetaStore().getIfPresentMetaObject(Interaction.TARGET_BLOCK_RAW);
        if (targetBlockPos != null) {
            FluxUtil.forEachBlockComponent(commandBuffer.getExternalData().getWorld(), new Vector3i(targetBlockPos.x, targetBlockPos.y, targetBlockPos.z), component -> {
                if (component instanceof IFluxContainerProvider provider) {
                    provider.sendContainerMessage(player);
                }
            });
            player.sendMessage(Message.raw("targetBlockPos: " + targetBlockPos.x + ", " + targetBlockPos.y + ", " + targetBlockPos.z));
            player.sendMessage(Message.raw("targetBlockPosRew: " + targetBlockPosRew.x + ", " + targetBlockPosRew.y + ", " + targetBlockPosRew.z));

        }
    }

    static {
        CODEC = BuilderCodec.builder(InspectContainerInteraction.class, InspectContainerInteraction::new, SimpleInstantInteraction.CODEC)
                .documentation("Send the target's FluxContainer interaction to player.")
                .build();
    }
}
