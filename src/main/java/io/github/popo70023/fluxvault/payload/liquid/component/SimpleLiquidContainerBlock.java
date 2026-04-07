/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.liquid.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import io.github.popo70023.fluxvault.FluxVaultPlugin;
import io.github.popo70023.fluxvault.api.AbstractContainer;
import io.github.popo70023.fluxvault.payload.liquid.container.LiquidContainer;
import io.github.popo70023.fluxvault.payload.liquid.container.SingleLiquidContainer;
import io.github.popo70023.fluxvault.registry.ComponentTypes;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Set;

public class SimpleLiquidContainerBlock extends SimpleLiquidContainerComponent<ChunkStore> {
    public static final String Id = FluxVaultPlugin.loc("SimpleLiquidContainerBlock");
    public static final BuilderCodec<SimpleLiquidContainerBlock> CODEC;

    public SimpleLiquidContainerBlock() {
    }

    public SimpleLiquidContainerBlock(SimpleLiquidContainerBlock other) {
        super(other);
    }

    public static ComponentType<ChunkStore, SimpleLiquidContainerBlock> getComponentType() {
        return ComponentTypes.SIMPLE_LIQUID_CONTAINER_BLOCK_COMPONENT;
    }

    @NullableDecl
    @Override
    public Component<ChunkStore> clone() {
        return new SimpleLiquidContainerBlock(this);
    }

    static {
        CODEC = BuilderCodec.builder(SimpleLiquidContainerBlock.class, SimpleLiquidContainerBlock::new)
                .append(new KeyedCodec<>(AbstractContainer.CAPACITY_KEY, Codec.LONG), (comp, v) -> comp.capacity = v, (comp) -> comp.capacity)
                .documentation(AbstractContainer.CAPACITY_DOCUMENTATION).add()
                .append(new KeyedCodec<>(LiquidContainer.ACCEPTED_HAZARDS_KEY, Codec.STRING_ARRAY), (comp, v) -> comp.acceptedHazards = v != null ? Set.of(v) : Set.of(), (comp) -> comp.acceptedHazards.toArray(String[]::new))
                .documentation(LiquidContainer.ACCEPTED_HAZARDS_DOCUMENTATION).add()
                .append(new KeyedCodec<>(LiquidContainer.WHITELIST_KEY, Codec.STRING_ARRAY), (comp, v) -> comp.whitelist = v != null ? Set.of(v) : Set.of(), (comp) -> comp.whitelist.toArray(String[]::new))
                .documentation(LiquidContainer.WHITELIST_DOCUMENTATION).add()
                .append(new KeyedCodec<>(AbstractContainer.ACTIVE_CONTAINER_KEY, SingleLiquidContainer.CODEC), (o, v) -> o.activeContainer = v, (o) -> o.activeContainer)
                .documentation(AbstractContainer.ACTIVE_CONTAINER_DOCUMENTATION).add()
                .build();
    }
}
