/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.resource;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

import javax.annotation.Nullable;

public class ResourceStack {
    public static final BuilderCodec<ResourceStack> CODEC;

    private String resourceId;
    private long quantity;

    private ResourceStack() {
        this.resourceId = FluxResource.UNKNOWN_ID;
        this.quantity = 0;
    }

    private ResourceStack(String resourceID, long quantity) {
        this.resourceId = resourceID;
        this.quantity = Math.max(0, quantity);
    }

    public static ResourceStack of(String resourceID) {
        return new ResourceStack(resourceID, 0);
    }

    public static ResourceStack of(String resourceID, long quantity) {
        return new ResourceStack(resourceID, quantity);
    }

    public static boolean isEmpty(@Nullable ResourceStack stack) {
        return stack == null || stack.isEmpty();
    }

    public String getResourceId() {
        return resourceId;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = Math.max(0, quantity);
    }

    public long addQuantity(long quantity) {
        this.quantity = Math.max(0, this.quantity + quantity);
        return this.quantity;
    }

    public boolean isEquivalentType(ResourceStack other) {
        if (other == null) return false;
        return this.resourceId.equals(other.resourceId);
    }

    public boolean isEmpty() {
        return quantity <= 0;
    }

    public ResourceStack copy() {
        return new ResourceStack(resourceId, quantity);
    }

    @Override
    public String toString() {
        return "ResourceStack{ResourceID:" + resourceId + ", Quantity:" + quantity + "}";
    }

    static {
        CODEC = BuilderCodec.builder(ResourceStack.class, ResourceStack::new)
                .append(new KeyedCodec<>(FluxResource.RESOURCE_ID_KEY, Codec.STRING), (o, v) -> o.resourceId = v, ResourceStack::getResourceId).add()
                .append(new KeyedCodec<>("Quantity", Codec.LONG), ResourceStack::setQuantity, ResourceStack::getQuantity).add()
                .build();
    }
}
