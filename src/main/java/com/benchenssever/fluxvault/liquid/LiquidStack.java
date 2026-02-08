package com.benchenssever.fluxvault.liquid;

import com.benchenssever.fluxvault.registry.LiquidRegistry;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import java.util.Objects;

public class LiquidStack {
    public final static LiquidStack EMPTY = new LiquidStack();
    public static final BuilderCodec<LiquidStack> CODEC = BuilderCodec.builder(LiquidStack.class, LiquidStack::new)
            .append(new KeyedCodec<>("liquidId", Codec.STRING), LiquidStack::setLiquidId, LiquidStack::getLiquidId).add()
            .append(new KeyedCodec<>("quantity", Codec.LONG), LiquidStack::setQuantity, LiquidStack::getQuantity).add()
            .build();
    private String liquidId;
    private long quantity;
    private transient Liquid liquid;

    private LiquidStack() {
        this.liquidId = Liquid.EMPTY_ID;
        this.liquid = null;
        this.quantity = 0;
    }

    private LiquidStack(LiquidStack liquidStack) {
        this.liquidId = liquidStack.getLiquidId();
        this.liquid = liquidStack.getLiquid();
        this.quantity = liquidStack.getQuantity();
    }

    public LiquidStack(String liquidId, long quantity) {
        this.liquidId = liquidId;
        this.quantity = Math.max(0, quantity);
        refreshLiquidCache();
    }

    public LiquidStack(Liquid liquid, long quantity) {
        this.liquidId = liquid.liquidID();
        this.liquid = liquid;
        this.quantity = Math.max(0, quantity);
    }

    public String getLiquidId() {
        return this.liquidId;
    }

    private void setLiquidId(String liquidId) {
        this.liquidId = liquidId;
        refreshLiquidCache();
    }

    public long getQuantity() {
        return this.quantity;
    }

    public void setQuantity(long setQuantity) {
        if (this == EMPTY) {
            throw new UnsupportedOperationException("Immutable Empty Stack");
        }
        this.quantity = Math.max(0, setQuantity);
    }

    public long addQuantity(long addQuantity) {
        if (this == EMPTY) {
            throw new UnsupportedOperationException("Immutable Empty Stack");
        }
        this.quantity += addQuantity;
        if (this.quantity < 0) this.quantity = 0;
        return this.quantity;
    }

    public Liquid getLiquid() {
        if (this.liquid == null) refreshLiquidCache();
        return this.liquid;
    }

    private void refreshLiquidCache() {
        this.liquid = LiquidRegistry.getLiquid(getLiquidId());
    }

    public boolean isEmpty() {
        if (this == EMPTY) return true;
        return getLiquidId().equals(Liquid.EMPTY.liquidID()) || getQuantity() <= 0;
    }

    public LiquidStack copy() {
        return new LiquidStack(this);
    }

    public boolean isEqual(LiquidStack resource) {
        if (resource == null) return false;
        if (this == resource) return true;
        return Objects.equals(getLiquidId(), resource.getLiquidId()) && getQuantity() == resource.getQuantity();
    }

    public boolean isLiquidEqual(Liquid resource) {
        if (resource == null) return false;
        return getLiquid().equals(resource);
    }
}
