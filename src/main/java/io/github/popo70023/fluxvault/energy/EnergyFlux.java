package io.github.popo70023.fluxvault.energy;

import io.github.popo70023.fluxvault.api.AbstractFlux;
import io.github.popo70023.fluxvault.api.FluxType;

import java.util.function.Predicate;

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
    public EnergyFlux addStack(FluxEnergy stack) {
        if (stack == null) return this;

        if (this.content == null || this.content.isEmpty()) {
            this.content = stack.copy();
        } else {
            this.content.addQuantity(stack.getQuantity());
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

    public EnergyFlux withLimit(long limit) {
        setTransferLimit(limit);
        return this;
    }

    public EnergyFlux withValidator(Predicate<FluxEnergy> validator) {
        setValidator(validator);
        return this;
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
