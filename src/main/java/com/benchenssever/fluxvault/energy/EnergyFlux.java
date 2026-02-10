package com.benchenssever.fluxvault.energy;

import com.benchenssever.fluxvault.api.AbstractFlux;
import com.benchenssever.fluxvault.api.FluxType;

public class EnergyFlux extends AbstractFlux.Packet<EnergyFlux, FluxEnergy> {

    public EnergyFlux(FluxEnergy content) {
        super(content);
    }

    @Override
    public FluxType<EnergyFlux, FluxEnergy> getFluxType() {
        return FluxType.FLUX_ENERGY;
    }

    @Override
    public void addStack(FluxEnergy stack) {
        if (stack == null || stack.isEmpty()) return;

        if (this.content == null || this.content.isEmpty()) {
            this.content = stack.copy();
        } else {
            this.content.addQuantity(stack.getQuantity());
        }
    }

    @Override
    public void cleanFlux() {
        if (this.content.getQuantity() <= 0) content = null;
    }

    @Override
    public long getAllQuantity() {
        return content.getQuantity();
    }

    @Override
    public boolean matchesStack(FluxEnergy stack, FluxEnergy reference) {
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
