/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.resource.container;

import io.github.popo70023.fluxvault.api.FluxType;
import io.github.popo70023.fluxvault.api.IFluxHandler;
import io.github.popo70023.fluxvault.payload.resource.ResourceFlux;
import io.github.popo70023.fluxvault.payload.resource.ResourceStack;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public record CreativeResourceHandler(String resourceId, boolean isVoid) implements IFluxHandler<ResourceFlux> {

    @Override
    public boolean matchesFluxType(FluxType<?, ?> fluxType) {
        return resourceId.equals(fluxType.getName());
    }

    @NonNullDecl
    @Override
    public ResourceFlux fill(@NonNullDecl ResourceFlux resource, @NonNullDecl FluxAction action) {
        ResourceFlux resultFlux = new ResourceFlux();
        if (!isVoid || resource.isEmpty()) return resultFlux;

        ResourceStack resourceStack = resource.getStack();
        long resourceQuantity = resourceStack.getQuantity();

        long canFill = Math.min(resourceQuantity, resource.getTransferLimit());
        if (resource.isExact() && canFill < resourceQuantity) return resultFlux;

        if (action.execute()) {
            if (resourceStack.addQuantity(-canFill) == 0) resource.removeStack();
        }

        return resultFlux.addStack(ResourceStack.of(resourceId, canFill));
    }

    @NonNullDecl
    @Override
    public ResourceFlux drain(@NonNullDecl ResourceFlux requestResources, @NonNullDecl FluxAction action) {
        ResourceFlux resultFlux = new ResourceFlux();
        if (isVoid || requestResources.isEmpty()) return resultFlux;

        ResourceStack requestStack = requestResources.getStack();
        long requestQuantity = requestStack.getQuantity();
        long limit = requestResources.getTransferLimit();

        long toDrain = Math.min(requestQuantity, limit);

        if (toDrain <= 0) return resultFlux;

        if (requestResources.isExact() && toDrain < requestQuantity) {
            return resultFlux;
        }

        if (action.execute()) {
            if (requestStack.addQuantity(-toDrain) == 0) {
                requestResources.removeStack();
            }
        }

        return resultFlux.addStack(ResourceStack.of(resourceId, toDrain));
    }
}
