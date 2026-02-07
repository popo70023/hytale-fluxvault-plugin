package com.benchenssever.fluxvault.liquid.container;

import com.benchenssever.fluxvault.liquid.LiquidFlux;
import com.benchenssever.fluxvault.liquid.LiquidStack;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.List;

public class SimpleLiquidContainer extends LiquidContainer {
    private LiquidStack content;

    public SimpleLiquidContainer(LiquidStack content, long capacity, String capacityType, String[] supportedTags) {
        super(capacity, capacityType, supportedTags);
        this.content = content;
    }

    public LiquidStack getContainerContent() {
        return this.content;
    }

    public void setContainerContent(LiquidStack content) {
        this.content = content;
    }

    @Override
    public int getContainerMaxSize() {
        return 1;
    }

    @Override
    public LiquidStack getContent(int index) {
        return this.content;
    }

    @Override
    public List<LiquidStack> getContents() {
        return List.of(this.content);
    }

    @Override
    public long getAllContentQuantity() {
        return this.content.getQuantity();
    }

    @NonNullDecl
    @Override
    public LiquidFlux fill(LiquidFlux resource, FluxAction action) {
        LiquidStack resourceStack = resource.getFirstStack();
        if (resourceStack.getQuantity() == Long.MAX_VALUE && isInfiniteContent()) {
            this.content = new LiquidStack(resourceStack.getLiquid(), getContainerCapacity());
            return new LiquidFlux(this.content);
        }

        if (resource.isEmpty() || !canAcceptLiquid(resourceStack) || !(this.content.isEmpty() || this.content.isLiquidEqual(resourceStack.getLiquid())) || (isInfiniteContent() && !content.isEmpty())) {
            return new LiquidFlux();
        }

        if (isInfiniteContent() && this.content.isEmpty()) {
            return resourceStack.getQuantity() < getContainerCapacity() ? resource : new LiquidFlux(new LiquidStack(resourceStack.getLiquid(), getContainerCapacity()));
        }

        long spaceAvailable = getContainerCapacity() - this.content.getQuantity();
        if (spaceAvailable <= 0) return new LiquidFlux();
        LiquidStack canFill = new LiquidStack(resourceStack.getLiquid(), Math.min(spaceAvailable, resourceStack.getQuantity()));

        if (action.execute() && !canFill.isEmpty()) {
            if (this.content.isEmpty()) {
                this.content = canFill;
            } else {
                this.content.addQuantity(canFill.getQuantity());
            }
            onContentsChanged();
        }
        return new LiquidFlux(canFill);
    }

    @NonNullDecl
    @Override
    public LiquidFlux drain(LiquidFlux maxDrainResource, FluxAction action) {
        LiquidStack resourceStack = maxDrainResource.getFirstStack();
        if (resourceStack.isEmpty() || this.content.isEmpty() || !this.content.isLiquidEqual(resourceStack.getLiquid())) {
            return new LiquidFlux();
        }
        long maxDrainQuantity = resourceStack.getQuantity();
        if (isInfiniteContent() && !this.content.isEmpty()) {
            return new LiquidFlux(new LiquidStack(this.content.getLiquid(), Math.min(maxDrainQuantity, getContainerCapacity())));
        }

        long toDrain = Math.min(maxDrainQuantity, this.content.getQuantity());
        if (this.content.isEmpty() || toDrain == 0) {
            return new LiquidFlux();
        }

        LiquidStack stack = new LiquidStack(this.content.getLiquid(), toDrain);
        if (action.execute() && toDrain > 0) {
            this.content.addQuantity(-toDrain);
            if (this.content.isEmpty()) {
                this.content = LiquidStack.EMPTY;
            }
            onContentsChanged();
        }

        return new LiquidFlux(stack);
    }
}
