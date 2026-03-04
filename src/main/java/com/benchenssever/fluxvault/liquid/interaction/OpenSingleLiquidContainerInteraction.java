package com.benchenssever.fluxvault.liquid.interaction;

import com.benchenssever.fluxvault.liquid.container.SingleLiquidContainerComponent;
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
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

//TODO: 這個類別的主要目標是實作一個簡單的互動，當玩家與一個具有 SingleLiquidContainerComponent 的方塊互動時，打開一個 GUI 顯示容器內的液體資訊。核心邏輯會在 interactWithBlock 方法裡實作，從容器組件讀取液體資訊並將其發送給玩家。這個互動不會修改容器的內容，因此 simulateInteractWithBlock 方法可以保持空白。
public class OpenSingleLiquidContainerInteraction extends SimpleBlockInteraction {
    public static final String ID = "OpenSingleLiquidContainer";
    public static final BuilderCodec<OpenSingleLiquidContainerInteraction> CODEC = BuilderCodec.builder(OpenSingleLiquidContainerInteraction.class, OpenSingleLiquidContainerInteraction::new, SimpleBlockInteraction.CODEC)
            .documentation("Open the liquid container GUI.")
            .build();

    @Override
    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext context, @NullableDecl ItemStack itemStack, @NonNullDecl Vector3i pos, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> playerRef = context.getEntity();
        Player player = commandBuffer.getComponent(playerRef, Player.getComponentType());
        SingleLiquidContainerComponent containerComp = InteractionUtil.getBlockComponent(world, pos, SingleLiquidContainerComponent.getComponentType());

        if (player != null && containerComp != null) {
            if (containerComp.getContent().isEmpty()) {
                player.sendMessage(Message.raw("The container is empty."));
            } else {
                player.sendMessage(Message.raw("The container contains: " + containerComp.getContent().getQuantity() + " mB of " + containerComp.getContent().getLiquidId()));
            }
        }
    }

    @Override
    protected void simulateInteractWithBlock(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl World world, @NonNullDecl Vector3i vector3i) {

    }
}
