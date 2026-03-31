/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.energy;

import io.github.popo70023.fluxvault.api.AbstractFlux;
import io.github.popo70023.fluxvault.api.FluxType;
import io.github.popo70023.fluxvault.api.IFlux;

import java.util.function.Predicate;

public class EnergyFlux extends AbstractFlux.Packet<FluxEnergy> {

    public EnergyFlux() {
        super(null);
    }

    public EnergyFlux(FluxEnergy content) {
        super(content);
    }

    public static EnergyFlux copyOf(FluxEnergy... contents) {
        FluxEnergy allContent = FluxEnergy.of(0);
        for (FluxEnergy content : contents) {
            allContent.addQuantity(content.getQuantity());
        }
        return new EnergyFlux(allContent);
    }

    public static FluxType<EnergyFlux, FluxEnergy> getFluxType() {
        return FluxType.FLUX_ENERGY;
    }

    @Override
    public EnergyFlux addStack(FluxEnergy stack) {
        if (stack == null) return this;

        if (content == null || content.isEmpty()) {
            content = stack;
        } else {
            content.addQuantity(stack.getQuantity());
        }
        return this;
    }

    @Override
    public long getAllQuantity() {
        return content.getQuantity();
    }

    @Override
    public boolean isEmpty() {
        if (content == null) return true;
        return content.isEmpty();
    }

    @Override
    public boolean isContentEmpty() {
        return content == null || content.isEmpty();
    }

    @Override
    public boolean matchesWithContent(FluxEnergy reference) {
        return true;
    }

    @Override
    public EnergyFlux withLimit(long limit) {
        setTransferLimit(limit);
        return this;
    }

    @Override
    public EnergyFlux withValidator(Predicate<FluxEnergy> validator) {
        setValidator(validator);
        return this;
    }

    @Override
    public EnergyFlux withExact(boolean exact) {
        setExact(exact);
        return this;
    }

    @Override
    public EnergyFlux copy() {
        EnergyFlux copy = new EnergyFlux(content == null ? null : content.copy());
        copy.attributes = this.attributes.copy();
        return copy;
    }

    @Override
    public String toString() {
        return "EnergyFlux{" + (content == null ? 0 : content.getQuantity()) + " FE}";
    }
}
