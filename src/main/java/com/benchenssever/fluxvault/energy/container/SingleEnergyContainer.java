package com.benchenssever.fluxvault.energy.container;

import com.benchenssever.fluxvault.api.AbstractContainer;
import com.benchenssever.fluxvault.energy.EnergyFlux;
import com.benchenssever.fluxvault.energy.FluxEnergy;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.List;

public class SingleEnergyContainer extends AbstractContainer.fixedCapacity<EnergyFlux, FluxEnergy> {
    protected FluxEnergy content;

    protected SingleEnergyContainer(FluxEnergy content, long capacity, String capacityTypeStr) {
        super(capacity, capacityTypeStr);
        this.content = content;
    }

    @Override
    public int getContainerMaxSize() {
        return 1;
    }

    @Override
    public List<FluxEnergy> getContents() {
        return List.of(content);
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
    public FluxEnergy addContent(FluxEnergy content) {
        return content;
    }

    @Override
    public int getFirstContentIndex() {
        return 0;
    }

    @Override
    public int findContentIndex(FluxEnergy content) {
        return 0;
    }

    @Override
    public long getAllContentQuantity() {
        return content.getQuantity();
    }

    @NonNullDecl
    @Override
    public EnergyFlux fill(EnergyFlux resource, FluxAction action) {
        resource.cleanFlux();
        if (resource.isEmpty()) return resource;
        if (isInfiniteContent() && !this.content.isEmpty()) return resource;

        if (isInfiniteContent() && this.content.isEmpty()) {
            if (action.simulate()) resource = resource.copy();
            FluxEnergy resourceStack = resource.getStack(0);
            if (resourceStack.addQuantity(-Math.min(getContainerCapacity(), resource.getTransferLimit())) == 0)
                resource.cleanFlux();
            return resource;
        }

        long spaceAvailable = capacity - content.getQuantity();
        if (spaceAvailable <= 0) return resource;

        long canFill = Math.min(Math.min(spaceAvailable, resource.getStack(0).getQuantity()), resource.getTransferLimit());

        if (action.execute()) {
            content.addQuantity(canFill);
            onContentsChanged();
        }
        if (action.simulate()) {
            resource = resource.copy();
        }
        resource.getStack(0).addQuantity(-canFill);
        resource.cleanFlux();

        return resource;
    }

    @NonNullDecl
    @Override
    public EnergyFlux drain(EnergyFlux requestResources, FluxAction action) {
        requestResources.cleanFlux();
        if (this.content.isEmpty() || requestResources.isEmpty()) return new EnergyFlux(null);

        FluxEnergy requestStack = requestResources.getStack(0);
        long requestQuantity = requestResources.getStack(0).getQuantity();
        long limit = requestResources.getTransferLimit();

        if (isInfiniteContent()) {
            long toDrain = Math.min(Math.min(requestQuantity, getContainerCapacity()), requestResources.getTransferLimit());
            if (action.execute()) {
                requestStack.addQuantity(-toDrain);
                requestResources.cleanFlux();
            }
            return new EnergyFlux(FluxEnergy.of(toDrain));
        }

        long toDrain = Math.min(Math.min(content.getQuantity(), requestQuantity), limit);

        if (toDrain <= 0) return new EnergyFlux(null);

        if (action.execute()) {
            content.addQuantity(-toDrain);
            onContentsChanged();
        }

        return new EnergyFlux(FluxEnergy.of(toDrain));
    }
}
