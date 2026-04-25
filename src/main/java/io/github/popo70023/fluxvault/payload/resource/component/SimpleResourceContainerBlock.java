/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.resource.component;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockFace;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import io.github.popo70023.fluxvault.FluxVaultPlugin;
import io.github.popo70023.fluxvault.api.*;
import io.github.popo70023.fluxvault.common.flux.AbstractContainer;
import io.github.popo70023.fluxvault.common.flux.AbstractVault;
import io.github.popo70023.fluxvault.common.flux.BlockHandlerRouter;
import io.github.popo70023.fluxvault.payload.resource.ResourceFlux;
import io.github.popo70023.fluxvault.payload.resource.container.SimpleResourceContainer;
import io.github.popo70023.fluxvault.registry.ComponentTypes;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class SimpleResourceContainerBlock implements Component<ChunkStore>, IFluxVaultHost.Block, IFluxHandlerProvider.Block {
    public static final String Id = FluxVaultPlugin.loc("SimpleResourceContainerBlock");
    public static final BuilderCodec<SimpleResourceContainerBlock> CODEC;
    private transient volatile SimpleResourceContainerVault vault = new SimpleResourceContainerVault();
    private transient volatile BlockHandlerRouter router = new BlockHandlerRouter();

    public SimpleResourceContainerBlock() {
    }

    public SimpleResourceContainerBlock(SimpleResourceContainerBlock other) {
        this.vault = new SimpleResourceContainerVault(other.vault);
        this.router = new BlockHandlerRouter(other.router);
    }

    public static ComponentType<ChunkStore, SimpleResourceContainerBlock> getComponentType() {
        return ComponentTypes.SIMPLE_RESOURCE_CONTAINER_BLOCK_COMPONENT;
    }

    @NullableDecl
    @Override
    public <F extends IFlux<D>, D> IFluxHandler<F> getFluxHandler(@NonNullDecl FluxType<F, D> type, @NullableDecl BlockFace face, @NullableDecl String slotName, @NonNullDecl FluxAccess access) {
        if (FluxType.getResourceFluxTypeByName(type.getName()) == null) return null;
        if (slotName != null && !slotName.isEmpty()) {
            IFluxHandler<ResourceFlux> target = vault.getOrCreateContainer(slotName);
            if (target != null && target.matchesFluxType(type)) {
                return type.castHandler(target);
            }
        }
        String containerName = router.getTargetSlot(face, access, type.getName());
        if (containerName == null) return null;
        IFluxHandler<ResourceFlux> target = vault.getOrCreateContainer(containerName);
        if (target == null || !target.matchesFluxType(type)) return null;
        return type.castHandler(target);
    }

    @Override
    public void sendContainerMessage(Player player) {
        this.vault.sendContainerMessage(player);
    }

    @Override
    public void registerChangeEvent(Consumer<? extends IEvent<Void>> consumer) {

        @SuppressWarnings("unchecked")
        Consumer<AbstractVault.ChangeEvent> safeConsumer = (Consumer<AbstractVault.ChangeEvent>) consumer;

        this.vault.registerChangeEvent(safeConsumer);
    }

    @Override
    public void updateRotation(Rotation yaw) {
        this.router.setRotation(yaw);
    }

    @NullableDecl
    @Override
    public Component<ChunkStore> clone() {
        return new SimpleResourceContainerBlock(this);
    }

    static {
        CODEC = BuilderCodec.builder(SimpleResourceContainerBlock.class, SimpleResourceContainerBlock::new)
                .append(new KeyedCodec<>(BlockHandlerRouter.ROUTING_KEY, new ArrayCodec<>(BlockHandlerRouter.RoutingRule.CODEC, BlockHandlerRouter.RoutingRule[]::new)), (comp, v) -> comp.router.setRoutingRule(v), (comp) -> comp.router.getRoutingRules())
                .add()
                .append(new KeyedCodec<>(AbstractContainer.BLUE_PRINTS_KEY, new MapCodec<SimpleResourceContainer.BluePrint, Map<String, SimpleResourceContainer.BluePrint>>(SimpleResourceContainer.BluePrint.CODEC, HashMap::new)),
                        (comp, v) -> comp.vault.containerBluePrints = v != null ? new HashMap<>(v) : new HashMap<>(), (comp) -> comp.vault.containerBluePrints)
                .add()
                .append(new KeyedCodec<>(AbstractContainer.ACTIVE_CONTAINERS_KEY, new MapCodec<SimpleResourceContainer, Map<String, SimpleResourceContainer>>(SimpleResourceContainer.CODEC, ConcurrentHashMap::new)),
                        (comp, v) -> {
                            comp.vault.activeContainers = v != null ? new ConcurrentHashMap<>(v) : null;
                            comp.vault.rebindAllNerves();
                        }, (comp) -> comp.vault.activeContainers)
                .documentation(AbstractContainer.ACTIVE_CONTAINER_DOCUMENTATION).add()
                .build();
    }
}
