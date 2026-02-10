package com.benchenssever.fluxvault.energy;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class FluxEnergy {
    public static final BuilderCodec<FluxEnergy> CODEC = BuilderCodec.builder(FluxEnergy.class, FluxEnergy::new)
            .append(new KeyedCodec<>("Quantity", Codec.LONG), FluxEnergy::setQuantity, FluxEnergy::getQuantity).add()
            .build();

    private long quantity;

    /**
     * FE (Flux Energy).
     * The standard energy unit for FluxVault.
     * Pure quantity, no stack size limit, fungible.
     */
    private FluxEnergy() {
        this.quantity = 0;
    }

    private FluxEnergy(long quantity) {
        this.quantity = Math.max(0, quantity);
    }

    public static FluxEnergy of(long quantity) {
        return new FluxEnergy(quantity);
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = Math.max(0, quantity);
    }

    public long addQuantity(long quantity) {
        this.quantity = Math.max(0, this.quantity + quantity);
        return this.quantity;
    }

    public boolean isEmpty() {
        return quantity <= 0;
    }

    public FluxEnergy copy() {
        return new FluxEnergy(this.quantity);
    }

    @Override
    public String toString() {
        return "FluxEnergy{" + quantity + " FE}";
    }
}
