package com.benchenssever.fluxvault.util;

import com.benchenssever.fluxvault.api.FluxType;
import com.benchenssever.fluxvault.api.IFlux;
import com.benchenssever.fluxvault.api.IFluxHandler;
import com.benchenssever.fluxvault.api.IFluxProvider;
import com.hypixel.hytale.component.*;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.inventory.container.SimpleItemContainer;
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackSlotTransaction;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InteractionUtil {
    @Nullable
    public static <T extends Component<ChunkStore>> T getBlockComponent(World world, Vector3i targetBlock, ComponentType<ChunkStore, T> componentType) {
        ChunkStore chunkStore = world.getChunkStore();
        long chunkIndex = ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z);
        BlockComponentChunk blockComponentChunk = chunkStore.getChunkComponent(chunkIndex, BlockComponentChunk.getComponentType());

        if (blockComponentChunk != null) {
            int blockIndex = ChunkUtil.indexBlockInColumn(targetBlock.x, targetBlock.y, targetBlock.z);
            Ref<ChunkStore> blockRef = blockComponentChunk.getEntityReference(blockIndex);

            if (blockRef != null && blockRef.isValid()) {
                BlockModule.BlockStateInfo blockStateInfoComponent = blockRef.getStore().getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());

                if (blockStateInfoComponent != null) {
                    Ref<ChunkStore> chunkRef = blockStateInfoComponent.getChunkRef();

                    if (chunkRef.isValid()) {
                        return chunkStore.getStore().getComponent(blockRef, componentType);
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    public static <F extends IFlux<D>, D> IFluxHandler<F> getFluxHandler(World world, Vector3i targetBlock, FluxType<F, D> fluxType) {
        ChunkStore chunkStore = world.getChunkStore();
        long chunkIndex = ChunkUtil.indexChunkFromBlock(targetBlock.x, targetBlock.z);
        BlockComponentChunk blockComponentChunk = chunkStore.getChunkComponent(chunkIndex, BlockComponentChunk.getComponentType());

        if (blockComponentChunk != null) {
            int blockIndex = ChunkUtil.indexBlockInColumn(targetBlock.x, targetBlock.y, targetBlock.z);
            Ref<ChunkStore> blockRef = blockComponentChunk.getEntityReference(blockIndex);

            if (blockRef != null && blockRef.isValid()) {
                Store<ChunkStore> store = chunkStore.getStore();
                Archetype<ChunkStore> archetype = store.getArchetype(blockRef);
                for (int i = archetype.getMinIndex(); i < archetype.length(); ++i) {
                    ComponentType<ChunkStore, ?> compType = archetype.get(i);

                    if (compType != null) {
                        Component<ChunkStore> component = store.getComponent(blockRef, compType);

                        if (component instanceof IFluxProvider provider) {
                            IFluxHandler<F> handler = provider.getFluxHandler(fluxType);
                            if (handler != null) {
                                return handler;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    public static void exchangeHeldItem(CommandBuffer<EntityStore> commandBuffer, InteractionContext context, int consumeAmount, ItemStack resultItem) {
        boolean consume = consumeHeldItem(context, consumeAmount);
        if (consume && resultItem != null && !resultItem.isEmpty()) {
            giveOrDropItem(commandBuffer, context, resultItem);
        }
    }

    public static boolean consumeHeldItem(InteractionContext context, int amount) {
        ItemContainer heldContainer = context.getHeldItemContainer();

        if (heldContainer != null) {
            short heldSlot = context.getHeldItemSlot();
            ItemStackSlotTransaction transaction = heldContainer.removeItemStackFromSlot(heldSlot, amount);

            if (transaction.succeeded()) {
                context.setHeldItem(heldContainer.getItemStack(heldSlot));
                return true;
            }
        }
        return false;
    }

    public static void giveOrDropItem(CommandBuffer<EntityStore> commandBuffer, InteractionContext context, @Nonnull ItemStack itemToGive) {
        Ref<EntityStore> playerRef = context.getEntity();
        Player player = commandBuffer.getComponent(playerRef, Player.getComponentType());
        ItemContainer heldContainer = context.getHeldItemContainer();

        if (heldContainer != null) {
            short heldSlot = context.getHeldItemSlot();
            ItemStack currentHeld = heldContainer.getItemStack(heldSlot);
            if (currentHeld == null || currentHeld.isEmpty() || currentHeld.getQuantity() <= 0) {
                heldContainer.setItemStackForSlot(heldSlot, itemToGive);
                context.setHeldItem(itemToGive);
                return;
            }
        }

        if (player == null) return;
        Inventory inventory = player.getInventory();
        if (inventory != null) {
            SimpleItemContainer.addOrDropItemStack(commandBuffer, playerRef, inventory.getCombinedHotbarFirst(), itemToGive);
        }
    }
}
