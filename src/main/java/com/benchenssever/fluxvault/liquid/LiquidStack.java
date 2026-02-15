package com.benchenssever.fluxvault.liquid;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import java.util.Objects;

public class LiquidStack {
    public final static LiquidStack EMPTY = new LiquidStack();
    public static final BuilderCodec<LiquidStack> CODEC = BuilderCodec.builder(LiquidStack.class, LiquidStack::new)
            .append(new KeyedCodec<>("LiquidId", Codec.STRING), LiquidStack::setLiquidId, LiquidStack::getLiquidId).add()
            .append(new KeyedCodec<>("Quantity", Codec.LONG), LiquidStack::setQuantity, LiquidStack::getQuantity).add()
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

    private LiquidStack(String liquidId, long quantity) {
        this.liquidId = liquidId;
        this.quantity = Math.max(0, quantity);
        refreshLiquidCache();
    }

    public static LiquidStack of(String liquidId, long quantity) {
        if (liquidId == null || (Liquid.EMPTY_ID.equals(liquidId) && quantity <= 0)) {
            return EMPTY;
        }
        return new LiquidStack(liquidId, quantity);
    }

    public static LiquidStack of(Liquid liquid, long quantity) {
        if (liquid == null || (liquid.equals(Liquid.EMPTY) && quantity <= 0)) {
            return EMPTY;
        }
        return of(liquid.getId(), quantity);
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
        this.liquid = Liquid.getLiquidById(this.liquidId);
        if (this.liquid == null) this.liquid = Liquid.EMPTY;
    }

    public boolean isEmpty() {
        if (this == EMPTY) return true;
        return getLiquidId().equals(Liquid.EMPTY.getId()) || getQuantity() <= 0;
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

    @Override
    public String toString() {
        return "LiquidStack{liquid='" + liquidId + "', qty=" + quantity + "}";
    }
}
