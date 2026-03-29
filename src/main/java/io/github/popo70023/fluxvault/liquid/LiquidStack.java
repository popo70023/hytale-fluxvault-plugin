package io.github.popo70023.fluxvault.liquid;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import javax.annotation.Nullable;

public class LiquidStack {
    public final static LiquidStack EMPTY = new LiquidStack() {
        @Override
        public void setQuantity(long setQuantity) {
            throw new UnsupportedOperationException("Immutable Empty Stack");
        }

        @Override
        public long addQuantity(long addQuantity) {
            throw new UnsupportedOperationException("Immutable Empty Stack");
        }
    };

    public static final BuilderCodec<LiquidStack> CODEC = BuilderCodec.builder(LiquidStack.class, LiquidStack::new)
            .append(new KeyedCodec<>("LiquidId", Codec.STRING), LiquidStack::setLiquidId, LiquidStack::getLiquidId).add()
            .append(new KeyedCodec<>("Quantity", Codec.LONG), LiquidStack::setQuantity, LiquidStack::getQuantity).add()
            .build();
    private String liquidId;
    private long quantity;

    private LiquidStack() {
        this.liquidId = Liquid.EMPTY_ID;
        this.quantity = 0;
    }

    private LiquidStack(LiquidStack liquidStack) {
        this.liquidId = liquidStack.getLiquidId();
        this.quantity = liquidStack.getQuantity();
    }

    private LiquidStack(String liquidId, long quantity) {
        this.liquidId = liquidId;
        this.quantity = Math.max(0, quantity);
    }

    public static LiquidStack of(String liquidId, long quantity) {
        if (liquidId == null || quantity <= 0) {
            return EMPTY;
        }
        return new LiquidStack(liquidId, quantity);
    }

    public static boolean isEmpty(@Nullable LiquidStack stack) {
        return stack == null || stack.isEmpty();
    }

    public String getLiquidId() {
        return this.liquidId;
    }

    private void setLiquidId(String liquidId) {
        this.liquidId = liquidId;
    }

    public long getQuantity() {
        return this.quantity;
    }

    public void setQuantity(long setQuantity) {
        this.quantity = Math.max(0, setQuantity);
    }

    public long addQuantity(long addQuantity) {
        this.quantity += addQuantity;
        if (this.quantity < 0) this.quantity = 0;
        return this.quantity;
    }

    public Liquid getLiquid() {
        if (this.liquidId.equals(Liquid.EMPTY_ID)) return Liquid.EMPTY;
        Liquid result = Liquid.getLiquidById(this.liquidId);
        return result != null ? result : Liquid.UNKNOWN;
    }

    public boolean isEmpty() {
        if (this == EMPTY) return true;
        return getLiquidId().equals(Liquid.EMPTY.getId()) || getQuantity() <= 0;
    }

    public LiquidStack copy() {
        return new LiquidStack(this);
    }

    public boolean isEquivalentType(LiquidStack liquidStack) {
        if (liquidStack == null) {
            return false;
        } else return this.liquidId.equals(liquidStack.liquidId);
    }

    @Override
    public String toString() {
        return "LiquidStack{liquid='" + liquidId + "', qty=" + quantity + "}";
    }
}
