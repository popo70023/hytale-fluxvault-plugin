package com.benchenssever.fluxvault.liquid.container;

import com.benchenssever.fluxvault.liquid.LiquidFlux;
import com.benchenssever.fluxvault.liquid.LiquidStack;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.List;

public class SingleLiquidContainer extends LiquidContainer {
    private LiquidStack content;

    public SingleLiquidContainer(LiquidStack content, long capacity, String capacityType, String[] supportedTags) {
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
    public void setContent(int index, LiquidStack content) {
        this.content = content;
    }

    @Override
    public LiquidStack addContent(LiquidStack content) {
        return content;
    }

    @Override
    public int getFirstContentIndex() {
        return 0;
    }

    @Override
    public int findContentIndex(LiquidStack content) {
        return this.content.isLiquidEqual(content.getLiquid()) ? 0 : -1;
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
        resource.cleanFlux();
        if (resource.isEmpty() || (isInfiniteContent() && !this.content.isEmpty())) return resource;

        if (isInfiniteContent() && this.content.isEmpty()) {
            if (action.simulate()) resource = resource.copy();
            LiquidStack resourceStack = resource.getStack(0);
            if (resourceStack.addQuantity(-Math.min(getContainerCapacity(), resource.getTransferLimit())) == 0) {
                resource.setStack(0, LiquidStack.EMPTY);
                resource.cleanFlux();
            } else {
                resource.setStack(0, resourceStack);
            }
            return resource;
        }

        int targetIndex = 0;
        if (!this.content.isEmpty()) {
            targetIndex = resource.getIndexOf(this.content);
            if (targetIndex == -1) return resource;
        }

        LiquidStack resourceStack = resource.getStack(targetIndex);
        if (resourceStack.isEmpty() || !canAcceptLiquid(resourceStack)) {
            return resource;
        }

        long spaceAvailable = getContainerCapacity() - this.content.getQuantity();
        if (spaceAvailable <= 0) return resource;

        long canFill = Math.min(Math.min(spaceAvailable, resourceStack.getQuantity()), resource.getTransferLimit());

        if (action.execute()) {
            if (this.content.isEmpty()) {
                this.content = new LiquidStack(resourceStack.getLiquid(), canFill);
            } else {
                this.content.addQuantity(canFill);
            }
            onContentsChanged();
        }

        if (action.simulate()) {
            resource = resource.copy();
            resourceStack = resource.getStack(targetIndex);
        }
        resourceStack.addQuantity(-canFill);
        if (resourceStack.isEmpty()) {
            resource.setStack(targetIndex, LiquidStack.EMPTY);
            resource.cleanFlux();
        } else {
            resource.setStack(targetIndex, resourceStack);
        }

        return resource;
    }

    @NonNullDecl
    @Override
    public LiquidFlux drain(LiquidFlux requestResources, FluxAction action) {
        requestResources.cleanFlux();
        if (this.content.isEmpty() || requestResources.isEmpty()) return new LiquidFlux();
        if (!requestResources.matches(this.content)) return new LiquidFlux();

        int targetIndex = requestResources.getIndexOf(this.content);
        if (targetIndex == -1) targetIndex = requestResources.getIndexOf(LiquidStack.EMPTY);
        if (targetIndex == -1) return new LiquidFlux();
        LiquidStack requestStack = requestResources.getStack(targetIndex);

        long requestQuantity = requestStack.getQuantity();
        if (requestQuantity <= 0) return new LiquidFlux();

        if (isInfiniteContent()) {
            long toDrain = Math.min(Math.min(requestQuantity, getContainerCapacity()), requestResources.getTransferLimit());
            if (action.execute()) {
                requestStack.addQuantity(-toDrain);
                if (requestStack.isEmpty()) requestStack = LiquidStack.EMPTY;
                requestResources.setStack(targetIndex, requestStack);
                requestResources.cleanFlux();
            }
            return new LiquidFlux(new LiquidStack(this.content.getLiquid(), toDrain));
        }

        long toDrain = Math.min(Math.min(requestQuantity, this.content.getQuantity()), requestResources.getTransferLimit());
        LiquidStack stack = new LiquidStack(this.content.getLiquid(), toDrain);
        if (action.execute() && toDrain > 0) {
            this.content.addQuantity(-toDrain);
            if (this.content.isEmpty()) {
                this.content = LiquidStack.EMPTY;
            }
            onContentsChanged();
        }

        if (action.execute()) {
            requestStack.addQuantity(-toDrain);
            if (requestStack.isEmpty()) {
                requestResources.setStack(targetIndex, LiquidStack.EMPTY);
                requestResources.cleanFlux();
            } else {
                requestResources.setStack(targetIndex, requestStack);
            }
        }

        return new LiquidFlux(stack);
    }
}
