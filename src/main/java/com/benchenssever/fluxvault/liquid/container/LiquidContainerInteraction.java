package com.benchenssever.fluxvault.liquid.container;

import com.benchenssever.fluxvault.api.FluxType;
import com.benchenssever.fluxvault.api.IFluxHandler;
import com.benchenssever.fluxvault.liquid.LiquidCapsuleType;
import com.benchenssever.fluxvault.liquid.LiquidFlux;
import com.benchenssever.fluxvault.liquid.LiquidStack;
import com.benchenssever.fluxvault.registry.ComponentTypes;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
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
        LiquidStack itemContent = capsuleType.getLiquidStackInCapsule(itemInHand);

        if (itemContent.isEmpty()) {
            LiquidFlux extracted = container.drain(new LiquidFlux(itemContent), IFluxHandler.FluxAction.SIMULATE);
            LiquidStack extractedStack = extracted.getStack(0);
            ItemStack newItemStack = capsuleType.getCapsuleWithLiquid(extractedStack.getLiquid());
            if (!newItemStack.isEmpty() && !extracted.isEmpty() && extractedStack.getQuantity() == capsuleType.getCapacity()) {
                container.drain(extracted, IFluxHandler.FluxAction.EXECUTE);
                return true;
            }
        } else {
            LiquidFlux remainder = container.fill(new LiquidFlux(itemContent), IFluxHandler.FluxAction.SIMULATE);
            if (remainder.isEmpty()) {
                container.fill(new LiquidFlux(itemContent), IFluxHandler.FluxAction.EXECUTE);
                return true;
            }
        }

        return false;
    }

    public static SingleLiquidContainerComponent getBlockComponent(Vector3i targetBlock, World world, ComponentType<ChunkStore, SingleLiquidContainerComponent> componentType) {
        ChunkStore chunkStore = world.getChunkStore();
        long chunkIndex = ChunkUtil.indexChunkFromBlock(targetBlock.getX(), targetBlock.getZ());
        BlockComponentChunk blockComponentChunk = chunkStore.getChunkComponent(chunkIndex, BlockComponentChunk.getComponentType());
        if (blockComponentChunk != null) {
            int blockIndex = ChunkUtil.indexBlockInColumn(targetBlock.x, targetBlock.y, targetBlock.z);
            Ref<ChunkStore> blockRef = blockComponentChunk.getEntityReference(blockIndex);
            if (blockRef != null && blockRef.isValid()) {
                BlockModule.BlockStateInfo blockStateInfoComponent = blockRef.getStore().getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());
                if (blockStateInfoComponent != null) {
                    Ref<ChunkStore> chunkRef = blockStateInfoComponent.getChunkRef();
                    if (chunkRef != null || chunkRef.isValid()) {
                        return chunkStore.getStore().getComponent(blockRef, componentType);
                    }
                }
            }
        }
        return null;
    }

    protected void interactWithBlock(@Nonnull World world, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull Vector3i pos, @Nonnull CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getEntity();
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());

        if (player != null) {
            player.sendMessage(Message.raw("Interact with a Liquid Container"));
            SingleLiquidContainerComponent containerComponent = getBlockComponent(pos, world, ComponentTypes.SINGLE_LIQUID_CONTAINER);
            if (containerComponent == null) {
                player.sendMessage(Message.raw("NO Liquid Container Component"));
                return;
            }
            interactWithContainer(player, context, itemInHand, containerComponent.getFluxHandler(FluxType.LIQUID));
            player.sendMessage(Message.raw(containerComponent.getContent().toString()));
        }
    }

    protected void simulateInteractWithBlock(@Nonnull InteractionType type, @Nonnull InteractionContext context, @Nullable ItemStack itemInHand, @Nonnull World world, @Nonnull Vector3i targetBlock) {
    }
}
