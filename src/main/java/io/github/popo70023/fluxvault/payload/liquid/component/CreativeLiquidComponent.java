/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.liquid.component;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.protocol.BlockFace;
import io.github.popo70023.fluxvault.api.FluxType;
import io.github.popo70023.fluxvault.api.IFlux;
import io.github.popo70023.fluxvault.api.IFluxHandler;
import io.github.popo70023.fluxvault.api.IFluxHandlerProvider;
import io.github.popo70023.fluxvault.payload.liquid.Liquid;
import io.github.popo70023.fluxvault.payload.liquid.container.CreativeLiquidHandler;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Objects;

public abstract class CreativeLiquidComponent<ECS_TYPE> implements Component<ECS_TYPE>, IFluxHandlerProvider {
    public static final String DOCUMENTATION = "Set to a LiquidId to act as an Infinite Source (provides endless liquid). Leave empty (null) or set to '" + Liquid.EMPTY_ID + "' to act as a Liquid Void (destroys incoming liquid).";
    protected String liquidId = Liquid.EMPTY_ID;
    private transient volatile CreativeLiquidHandler fluxHandler;

    public CreativeLiquidComponent() {
    }

    public CreativeLiquidComponent(CreativeLiquidComponent<ECS_TYPE> other) {
        this.liquidId = other.liquidId;
        this.fluxHandler = null;
    }

    @NullableDecl
    @Override
    public <F extends IFlux<D>, D> IFluxHandler<F> getFluxHandler(@NonNullDecl FluxType<F, D> type, @NonNullDecl BlockFace face, String slotName, @NonNullDecl FluxAccess access) {
        if (fluxHandler == null || !fluxHandler.liquidId().equals(liquidId)) {
            fluxHandler = new CreativeLiquidHandler(liquidId);
        }

        if (fluxHandler.matchesFluxType(type)) {
            boolean isVoid = Objects.equals(liquidId, Liquid.EMPTY_ID);
            if ((isVoid && access.fill()) || (!isVoid && access.drain())) {

                return type.castHandler(fluxHandler);
            }
        }
        return null;
    }

    @NullableDecl
    @Override
    public abstract Component<ECS_TYPE> clone();
}
