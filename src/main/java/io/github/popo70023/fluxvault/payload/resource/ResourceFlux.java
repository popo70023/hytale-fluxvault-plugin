/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.resource;

import io.github.popo70023.fluxvault.api.AbstractFlux;

import java.util.function.Predicate;

public class ResourceFlux extends AbstractFlux.Packet<ResourceStack> {

    public ResourceFlux() {
        super(null);
    }

    public ResourceFlux(ResourceStack content) {
        super(content);
    }

    @Override
    public ResourceFlux addStack(ResourceStack stack) {
        if (stack == null || (content != null && !content.isEquivalentType(stack))) return this;

        if (content == null || content.isEmpty()) {
            content = stack;
        } else {
            content.addQuantity(stack.getQuantity());
        }
        return this;
    }

    @Override
    public long getAllQuantity() {
        return content != null ? content.getQuantity() : 0;
    }

    @Override
    public boolean isEmpty() {
        if (content == null) return true;
        return content.isEmpty();
    }

    @Override
    public boolean isContentEmpty() {
        return content == null || content.isEmpty();
    }

    @Override
    public boolean matchesWithContent(ResourceStack reference) {
        return content == null || content.isEquivalentType(reference);
    }

    @Override
    public ResourceFlux withLimit(long limit) {
        setTransferLimit(limit);
        return this;
    }

    @Override
    public ResourceFlux withValidator(Predicate<ResourceStack> validator) {
        setValidator(validator);
        return this;
    }

    @Override
    public ResourceFlux withExact(boolean exact) {
        setExact(exact);
        return this;
    }

    @Override
    public ResourceFlux copy() {
        ResourceFlux copy = new ResourceFlux(content == null ? null : content.copy());
        copy.attributes = this.attributes.copy();
        return copy;
    }

    @Override
    public String toString() {
        return "ResourceFlux{ResourceID:" + (content != null ? content.getResourceId() : FluxResource.UNKNOWN_ID) + ", Quantity:" + (content == null ? 0 : content.getQuantity()) + "}";
    }
}
