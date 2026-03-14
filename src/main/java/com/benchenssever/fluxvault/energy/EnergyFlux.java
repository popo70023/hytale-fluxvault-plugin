package com.benchenssever.fluxvault.energy;

import com.benchenssever.fluxvault.api.AbstractFlux;
import com.benchenssever.fluxvault.api.FluxType;

public class EnergyFlux extends AbstractFlux.Packet<FluxEnergy> {

    public EnergyFlux() {
        super(null);
    }

    public EnergyFlux(FluxEnergy content) {
        super(content);
    }

    public static FluxType<EnergyFlux, FluxEnergy> getFluxType() {
        return FluxType.FLUX_ENERGY;
    }

    @Override
    public EnergyFlux addStack(FluxEnergy... stacks) {
        if (stacks == null) return this;

        for (FluxEnergy stack : stacks) {
            if (this.content == null || this.content.isEmpty()) {
                this.content = stack.copy();
            } else {
                this.content.addQuantity(stack.getQuantity());
            }
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
    public boolean isIndexEmpty(int index) {
        return getStack(index) == null || getStacks().isEmpty();
    }

    @Override
    public boolean matchesWithIndex(int index, FluxEnergy reference) {
        return true;
    }

    @Override
    public EnergyFlux copy() {
        return new EnergyFlux(this.content == null ? null : this.content.copy());
    }

    @Override
    public String toString() {
        return "EnergyFlux{" + (content == null ? 0 : content.getQuantity()) + " FE}";
    }
}
