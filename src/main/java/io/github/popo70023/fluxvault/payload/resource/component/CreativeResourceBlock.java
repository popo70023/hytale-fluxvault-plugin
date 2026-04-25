/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.resource.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import io.github.popo70023.fluxvault.FluxVaultPlugin;
import io.github.popo70023.fluxvault.registry.ComponentTypes;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Collections;
import java.util.Set;

public class CreativeResourceBlock extends CreativeResourceComponent<ChunkStore> {
    public static final String Id = FluxVaultPlugin.loc("CreativeResourceBlock");
    public static final BuilderCodec<CreativeResourceBlock> CODEC;

    public CreativeResourceBlock() {
    }

    public CreativeResourceBlock(CreativeResourceComponent<ChunkStore> other) {
        super(other);
    }

    public static ComponentType<ChunkStore, CreativeResourceBlock> getComponentType() {
        return ComponentTypes.CREATIVE_RESOURCE_BLOCK_COMPONENT;
    }

    @NullableDecl
    @Override
    public Component<ChunkStore> clone() {
        return new CreativeResourceBlock(this);
    }

    static {
        CODEC = BuilderCodec.builder(CreativeResourceBlock.class, CreativeResourceBlock::new)
                .append(new KeyedCodec<>("ResourceIds", Codec.STRING_ARRAY),
                        (comp, v) -> comp.supportedResources = (v == null || v.length == 0) ? Collections.emptySet() : Set.of(v),
                        (comp) -> comp.supportedResources.toArray(new String[0]))
                .documentation("An array of Resource Ids this block handles.").add()

                .append(new KeyedCodec<>("IsVoid", Codec.BOOLEAN),
                        (comp, v) -> comp.isVoid = v,
                        (comp) -> comp.isVoid)
                .documentation("Set to 'true' to act as a Void, 'false' for an Infinite Source.").add()

                .build();
    }
}
