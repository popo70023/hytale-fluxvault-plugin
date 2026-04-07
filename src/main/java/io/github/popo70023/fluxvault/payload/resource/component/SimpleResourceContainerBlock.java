package io.github.popo70023.fluxvault.payload.resource.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import io.github.popo70023.fluxvault.FluxVaultPlugin;
import io.github.popo70023.fluxvault.api.AbstractContainer;
import io.github.popo70023.fluxvault.payload.resource.container.SingleResourceContainer;
import io.github.popo70023.fluxvault.registry.ComponentTypes;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleResourceContainerBlock extends SimpleResourceContainerComponent<ChunkStore> {
    public static final String Id = FluxVaultPlugin.loc("SimpleResourceContainerBlock");
    public static final BuilderCodec<SimpleResourceContainerBlock> CODEC;

    public SimpleResourceContainerBlock() {
    }

    public SimpleResourceContainerBlock(SimpleResourceContainerComponent<ChunkStore> other) {
        super(other);
    }

    public static ComponentType<ChunkStore, SimpleResourceContainerBlock> getComponentType() {
        return ComponentTypes.SIMPLE_RESOURCE_CONTAINER_BLOCK_COMPONENT;
    }

    @NullableDecl
    @Override
    public Component<ChunkStore> clone() {
        return new SimpleResourceContainerBlock(this);
    }

    static {
        CODEC = BuilderCodec.builder(SimpleResourceContainerBlock.class, SimpleResourceContainerBlock::new)
                .append(new KeyedCodec<>("ResourceCapacities", new MapCodec<Long, Map<String, Long>>(Codec.LONG, ConcurrentHashMap::new)),
                        (comp, map) -> comp.capacities = (map != null) ? map : new ConcurrentHashMap<>(), (comp) -> comp.capacities)
                .documentation("The initial blueprint of this component. A map where the Key is the Resource Id (e.g., 'MyMod.Energy') and the Value is the maximum Capacity. This defines what resources this block can accept.").add()
                .append(new KeyedCodec<>(AbstractContainer.ACTIVE_CONTAINERS_KEY, new MapCodec<SingleResourceContainer, Map<String, SingleResourceContainer>>(SingleResourceContainer.CODEC, ConcurrentHashMap::new)),
                        (comp, v) -> comp.activeContainers = v != null ? new ConcurrentHashMap<>(v) : null, (comp) -> comp.activeContainers)
                .documentation(AbstractContainer.ACTIVE_CONTAINER_DOCUMENTATION).add()
                .build();
    }
}
