/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.liquid.component;

import com.hypixel.hytale.component.Component;
import io.github.popo70023.fluxvault.payload.liquid.Liquid;
import io.github.popo70023.fluxvault.payload.liquid.container.CreativeLiquidHandler;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public abstract class CreativeLiquidComponent<ECS_TYPE> implements Component<ECS_TYPE> {
    public static final String DOCUMENTATION = "Set to a LiquidId to act as an Infinite Source (provides endless liquid). Leave empty (null) or set to '" + Liquid.EMPTY_ID + "' to act as a Liquid Void (destroys incoming liquid).";
    protected String liquidId = Liquid.EMPTY_ID;
    protected transient volatile CreativeLiquidHandler fluxHandler;

    public CreativeLiquidComponent() {
    }

    public CreativeLiquidComponent(CreativeLiquidComponent<ECS_TYPE> other) {
        this.liquidId = other.liquidId;
        this.fluxHandler = null;
    }

    @NullableDecl
    @Override
    public abstract Component<ECS_TYPE> clone();
}
