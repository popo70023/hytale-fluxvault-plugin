/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.energy.component;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.protocol.BlockFace;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import io.github.popo70023.fluxvault.api.FluxType;
import io.github.popo70023.fluxvault.api.IFlux;
import io.github.popo70023.fluxvault.api.IFluxHandler;
import io.github.popo70023.fluxvault.api.IFluxProvider;
import io.github.popo70023.fluxvault.energy.EnergyFlux;
import io.github.popo70023.fluxvault.energy.FluxEnergy;
import io.github.popo70023.fluxvault.registry.ComponentTypes;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class CreativeEnergyComponent implements Component<ChunkStore>, IFluxProvider {
    public static final String ID = "CreativeEnergyComponent";
    public static final BuilderCodec<CreativeEnergyComponent> CODEC = BuilderCodec.builder(CreativeEnergyComponent.class, CreativeEnergyComponent::new)
            .append(new KeyedCodec<>("IsVoid", Codec.BOOLEAN), (o, v) -> o.isVoid = v, (o) -> o.isVoid)
            .documentation("Set to 'true' to act as an Energy Void (destroys incoming energy). Leave empty (null) or set to 'false' to act as an Infinite Source (provides endless energy).").add()
            .build();
    private boolean isVoid;
    private final IFluxHandler<EnergyFlux> fluxHandler = new IFluxHandler<>() {
        @Override
        public boolean matchesFluxType(FluxType<?, ?> fluxType) {
            return EnergyFlux.class.isAssignableFrom(fluxType.getResourceClass());
        }

        @NonNullDecl
        @Override
        public EnergyFlux fill(@NonNullDecl EnergyFlux resource, @NonNullDecl FluxAction action) {
            EnergyFlux resultFlux = new EnergyFlux();
            if (!isVoid || resource.isEmpty()) return resultFlux;

            FluxEnergy resourceStack = resource.getStack();
            long resourceQuantity = resourceStack.getQuantity();

            long canFill = Math.min(resourceQuantity, resource.getTransferLimit());
            if (resource.isExact() && canFill < resourceQuantity) return resultFlux;

            if (action.execute()) {
                if (resourceStack.addQuantity(-canFill) == 0) resource.removeStack();
            }

            return resultFlux.addStack(FluxEnergy.of(canFill));
        }

        @NonNullDecl
        @Override
        public EnergyFlux drain(@NonNullDecl EnergyFlux requestResources, @NonNullDecl FluxAction action) {
            EnergyFlux resultFlux = new EnergyFlux();
            if (isVoid || requestResources.isEmpty()) return resultFlux;

            FluxEnergy requestStack = requestResources.getStack();
            long requestQuantity = requestStack.getQuantity();
            long limit = requestResources.getTransferLimit();

            long toDrain = Math.min(requestQuantity, limit);

            if (toDrain <= 0) return resultFlux;

            if (requestResources.isExact() && toDrain < requestQuantity) {
                return resultFlux;
            }

            if (action.execute()) {
                if (requestStack.addQuantity(-toDrain) == 0) {
                    requestResources.removeStack();
                }
            }

            return resultFlux.addStack(FluxEnergy.of(toDrain));
        }
    };

    public CreativeEnergyComponent() {
        this.isVoid = false;
    }

    public CreativeEnergyComponent(boolean isVoid) {
        this.isVoid = isVoid;
    }

    public static ComponentType<ChunkStore, CreativeEnergyComponent> getComponentType() {
        return ComponentTypes.CREATIVE_ENERGY_COMPONENT;
    }

    @NullableDecl
    @Override
    public <F extends IFlux<D>, D> IFluxHandler<F> getFluxHandler(@NonNullDecl FluxType<F, D> type, @NonNullDecl BlockFace side, @NonNullDecl FluxAccess access) {
        if (fluxHandler.matchesFluxType(type)) {
            if ((isVoid && access.fill()) || (!isVoid && access.drain())) {
                return type.castHandler(fluxHandler);
            }
        }
        return null;
    }

    @NullableDecl
    @Override
    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public Component<ChunkStore> clone() {
        return new CreativeEnergyComponent(isVoid);
    }
}
