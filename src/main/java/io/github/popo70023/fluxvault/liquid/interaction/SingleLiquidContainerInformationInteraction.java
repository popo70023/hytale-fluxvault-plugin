package io.github.popo70023.fluxvault.liquid.interaction;

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
import io.github.popo70023.fluxvault.liquid.LiquidStack;
import io.github.popo70023.fluxvault.liquid.container.SingleLiquidContainerComponent;
import io.github.popo70023.fluxvault.util.FluxUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class SingleLiquidContainerInformationInteraction extends SimpleBlockInteraction {
    public static final String ID = "SingleLiquidContainerInformation";
    public static final BuilderCodec<SingleLiquidContainerInformationInteraction> CODEC = BuilderCodec.builder(SingleLiquidContainerInformationInteraction.class, SingleLiquidContainerInformationInteraction::new, SimpleBlockInteraction.CODEC)
            .documentation("Send liquid container interaction to player.")
            .build();

    @Override
    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl Vector3i vector3i, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = interactionContext.getEntity();
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        SingleLiquidContainerComponent containerComp = FluxUtil.getBlockComponent(world, vector3i, SingleLiquidContainerComponent.getComponentType());

        if (player != null && containerComp != null) {
            LiquidStack content = containerComp.getContent();
            if (content.isEmpty()) {
                player.sendMessage(Message.translation("server.fluxvault.interaction.container.empty"));
            } else {
                String liquidNameKey = content.getLiquid().getTranslationProperties().getName();
                Message message = Message.translation("server.fluxvault.interaction.liquid_container.contains")
                        .param("liquidName", Message.translation(liquidNameKey))
                        .param("quantity", content.getQuantity());
                player.sendMessage(message);
            }
        }

    }

    @Override
    protected void simulateInteractWithBlock(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl World world, @NonNullDecl Vector3i vector3i) {

    }
}
