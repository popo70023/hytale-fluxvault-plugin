package com.benchenssever.fluxvault.liquid.container;

import com.benchenssever.fluxvault.api.IFluxHandler;
import com.benchenssever.fluxvault.liquid.LiquidFlux;
import com.benchenssever.fluxvault.liquid.LiquidStack;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.List;

public class SingleLiquidContainer extends LiquidContainer.fixedCapacity implements IFluxHandler<LiquidFlux> {
    private LiquidStack content;

    public SingleLiquidContainer(LiquidStack content, long capacity, String capacityType, String[] acceptedHazards) {
        super(capacity, capacityType, acceptedHazards);
        this.content = content;
    }

    @Override
    public int getContainerMaxSize() {
        return 1;
    }

    @Override
    public List<LiquidStack> getContents() {
        return List.of(this.content);
    }

    @Override
    public LiquidStack getContent(int index) {
        return index == 0 ? content : null;
    }

    @Override
    public void setContent(int index, LiquidStack content) {
        if (index == 0) {
            this.content = content;
        }
    }

    @Override
    public long getAllContentQuantity() {
        return this.content.getQuantity();
    }

    @NonNullDecl
    @Override
    public LiquidFlux fill(@NonNullDecl LiquidFlux resource, @NonNullDecl FluxAction action) {
        LiquidFlux resultFlux = new LiquidFlux();
        if (resource.isEmpty() || (isInfiniteContent() && !this.content.isEmpty())) return resultFlux;

        if (isInfiniteContent() && this.content.isEmpty()) {
            int targetIndex = resource.findIndexOfFirstMatchesStack(getValidator());
            LiquidStack resourceStack = resource.getStack(targetIndex);
            long canFill = Math.min(getContainerCapacity(), resource.getTransferLimit());
            if (action.execute()) {
                if (resourceStack.addQuantity(-canFill) == 0) {
                    resource.removeStack(targetIndex);
                }
            }
            return resultFlux.addStack(LiquidStack.of(resourceStack.getLiquid(), canFill));
        }

        int targetIndex;
        if (!this.content.isEmpty()) {
            targetIndex = resource.findIndexOfTarget(this.content);
        } else {
            targetIndex = resource.findIndexOfFirstMatchesStack(getValidator());
        }
        if (targetIndex == -1) return resultFlux;

        LiquidStack resourceStack = resource.getStack(targetIndex);

        long spaceAvailable = getContainerCapacity() - this.content.getQuantity();
        if (spaceAvailable <= 0) return resultFlux;

        long canFill = Math.min(Math.min(spaceAvailable, resourceStack.getQuantity()), resource.getTransferLimit());
        LiquidStack resultStack = LiquidStack.of(resourceStack.getLiquid(), canFill);

        if (action.execute()) {
            if (this.content.isEmpty()) {
                this.content = resultStack;
            } else {
                this.content.addQuantity(canFill);
            }
            onContentsChanged();

            if (resourceStack.addQuantity(-canFill) == 0) {
                resource.removeStack(targetIndex);
            }
        }

        return resultFlux.addStack(resultStack);
    }

    @NonNullDecl
    @Override
    public LiquidFlux drain(@NonNullDecl LiquidFlux requestResources, @NonNullDecl FluxAction action) {
        LiquidFlux resultFlux = new LiquidFlux();
        if (this.content.isEmpty() || requestResources.isEmpty()) return resultFlux;
        if (!requestResources.matchesWithFlux(this.content)) return resultFlux;

        int targetIndex = requestResources.findIndexOfTarget(this.content);
        if (targetIndex == -1) targetIndex = requestResources.findIndexOfTarget(LiquidStack.EMPTY);
        if (targetIndex == -1) return resultFlux;

        LiquidStack requestStack = requestResources.getStack(targetIndex);
        long requestQuantity = requestStack.getQuantity();
        long limit = requestResources.getTransferLimit();
        long toDrain = isInfiniteContent() ? Math.min(Math.min(requestQuantity, getContainerCapacity()), limit) : Math.min(Math.min(requestQuantity, this.content.getQuantity()), limit);
        if (toDrain <= 0) return resultFlux;

        if (action.execute()) {
            if(!isInfiniteContent()) {
                if (this.content.addQuantity(-toDrain) == 0) {
                    this.content = LiquidStack.EMPTY;
                }
                onContentsChanged();
            }

            if (requestStack.isEmpty()) {
                requestStack = requestResources.setStack(targetIndex, LiquidStack.of(this.content.getLiquid(), requestQuantity));
            }
            if (requestStack.addQuantity(-toDrain) == 0) {
                requestResources.removeStack(targetIndex);
            }
        }

        return resultFlux.addStack(LiquidStack.of(this.content.getLiquid(), toDrain));
    }
}
