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
import io.github.popo70023.fluxvault.payload.liquid.Liquid;
import io.github.popo70023.fluxvault.registry.ComponentTypes;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class CreativeLiquidBlock extends CreativeLiquidComponent<ChunkStore> {
    public static final String Id = FluxVaultPlugin.loc("CreativeLiquidBlock");
    public static final BuilderCodec<CreativeLiquidBlock> CODEC;

    public CreativeLiquidBlock() {
    }

    public CreativeLiquidBlock(CreativeLiquidBlock creativeLiquidBlock) {
        super(creativeLiquidBlock);
    }

    public static ComponentType<ChunkStore, CreativeLiquidBlock> getComponentType() {
        return ComponentTypes.CREATIVE_LIQUID_BLOCK_COMPONENT;
    }

    @NullableDecl
    @Override
    public Component<ChunkStore> clone() {
        return new CreativeLiquidBlock(this);
    }

    static {
        CODEC = BuilderCodec.builder(CreativeLiquidBlock.class, CreativeLiquidBlock::new)
                .append(new KeyedCodec<>(Liquid.LIQUID_ID_KEY, Codec.STRING), (comp, v) -> comp.liquidId = v, (comp) -> comp.liquidId)
                .documentation(CreativeLiquidComponent.DOCUMENTATION).add()
                .build();
    }
}
