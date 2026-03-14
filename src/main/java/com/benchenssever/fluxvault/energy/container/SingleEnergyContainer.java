package com.benchenssever.fluxvault.energy.container;

import com.benchenssever.fluxvault.api.IFluxHandler;
import com.benchenssever.fluxvault.energy.EnergyFlux;
import com.benchenssever.fluxvault.energy.FluxEnergy;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Collections;
import java.util.List;

public class SingleEnergyContainer extends EnergyContainer.fixedCapacity implements IFluxHandler<EnergyFlux> {
    protected FluxEnergy content;

    protected SingleEnergyContainer(FluxEnergy content, long capacity, String capacityTypeStr) {
        super(capacityTypeStr, capacity);
        this.content = content;
    }

    @Override
    public int getContainerMaxSize() {
        return 1;
    }

    @Override
    public List<FluxEnergy> getContents() {
        return Collections.singletonList(content);
    }

    @Override
    public FluxEnergy getContent(int index) {
        return index == 0 ? content : null;
    }

    @Override
    public void setContent(int index, FluxEnergy content) {
        if (index == 0) {
            this.content = content;
        }
    }

    @Override
    public long getAllContentQuantity() {
        return content == null ? 0 : content.getQuantity();
    }

    @NonNullDecl
    @Override
    public EnergyFlux fill(@NonNullDecl EnergyFlux resource, @NonNullDecl FluxAction action) {
        EnergyFlux resultFlux = new EnergyFlux();
        if (resource.isEmpty()) return resultFlux;
        if (isInfiniteContent() && !this.content.isEmpty()) return resultFlux;

        FluxEnergy resourceStack = resource.getStack();
        long resourceQuantity = resourceStack.getQuantity();

        if (isInfiniteContent() && this.content.isEmpty()) {
            long canFill = Math.min(Math.min(resourceQuantity, getContainerCapacity()), resource.getTransferLimit());
            if (action.execute()) {
                if (resourceStack.addQuantity(-canFill) == 0) {
                    resource.removeStack();
                }
            }

            return resultFlux.addStack(FluxEnergy.of(canFill));
        }

        long spaceAvailable = capacity - content.getQuantity();
        if (spaceAvailable <= 0) return resultFlux;

        long canFill = Math.min(Math.min(spaceAvailable, resourceQuantity), resource.getTransferLimit());
        if (action.execute()) {
            content.addQuantity(canFill);
            onContentsChanged();

            if (resourceStack.addQuantity(-canFill) == 0) {
                resource.removeStack();
            }
        }

        return resultFlux.addStack(FluxEnergy.of(canFill));
    }

    @NonNullDecl
    @Override
    public EnergyFlux drain(@NonNullDecl EnergyFlux requestResources, @NonNullDecl FluxAction action) {
        EnergyFlux resultFlux = new EnergyFlux();
        if (this.content.isEmpty() || requestResources.isEmpty()) return resultFlux;

        FluxEnergy requestStack = requestResources.getStack();
        long requestQuantity = requestStack.getQuantity();
        long limit = requestResources.getTransferLimit();
        long toDrain = isInfiniteContent() ? Math.min(Math.min(requestQuantity, getContainerCapacity()), limit) : Math.min(Math.min(requestQuantity, content.getQuantity()), limit);
        if (toDrain <= 0) return resultFlux;

        if (action.execute()) {
            if (!isInfiniteContent()) {
                content.addQuantity(-toDrain);
                onContentsChanged();
            }

            if (requestStack.addQuantity(-toDrain) == 0) {
                requestResources.removeStack();
            }
        }

        return resultFlux.addStack(FluxEnergy.of(toDrain));
    }
}
