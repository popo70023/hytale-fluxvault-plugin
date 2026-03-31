/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.energy.interaction;

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
import io.github.popo70023.fluxvault.energy.FluxEnergy;
import io.github.popo70023.fluxvault.energy.component.SimpleEnergyContainerComponent;
import io.github.popo70023.fluxvault.util.FluxUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class SingleEnergyContainerInformationInteraction extends SimpleBlockInteraction {
    public static final String ID = "SingleEnergyContainerInformation";
    public static final BuilderCodec<SingleEnergyContainerInformationInteraction> CODEC = BuilderCodec.builder(SingleEnergyContainerInformationInteraction.class, SingleEnergyContainerInformationInteraction::new, SimpleBlockInteraction.CODEC)
            .documentation("Send energy container interaction to player.")
            .build();

    @Override
    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl Vector3i vector3i, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = interactionContext.getEntity();
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        SimpleEnergyContainerComponent containerComp = FluxUtil.getBlockComponent(world, vector3i, SimpleEnergyContainerComponent.getComponentType());

        if (player != null && containerComp != null) {
            FluxEnergy content = containerComp.getContent();
            if (content.isEmpty()) {
                player.sendMessage(Message.translation("server.fluxvault.interaction.container.empty"));
            } else {
                Message message = Message.translation("server.fluxvault.interaction.energy_container.contains")
                        .param("energyName", Message.translation(FluxEnergy.FLUX_ENERGY_TRANSLATION.getName()))
                        .param("quantity", content.getQuantity());
                player.sendMessage(message);
            }
        }

    }

    @Override
    protected void simulateInteractWithBlock(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl World world, @NonNullDecl Vector3i vector3i) {

    }
}
