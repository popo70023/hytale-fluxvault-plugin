/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.common.flux;

import io.github.popo70023.fluxvault.api.IFlux;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.function.Predicate;

/**
 * Base abstract implementation for a Flux carrier.
 * <p>
 * This class provides the foundational state (validation rules, transfer limits) required by {@link IFlux}.
 * It delegates actual data storage and access patterns to internal strategy classes like {@link Bundle} (list-backed)
 * and {@link Packet} (single-value optimized). Concrete implementations should extend one of these strategies.
 * </p>
 *
 * @param <D> The data type.
 */
public abstract class AbstractFlux<D> implements IFlux<D> {
    private static final FluxAttributes<?> DEFAULT_ATTRIBUTES = new FluxAttributes<>();

    @SuppressWarnings("unchecked")
    protected FluxAttributes<D> attributes = (FluxAttributes<D>) DEFAULT_ATTRIBUTES;

    @Override
    public int findIndexOfFirstMatchesStack(Predicate<D> validator) {
        for (int i = 0; i < getStackCount(); i++) {
            if (!isIndexEmpty(i) && validator.test(getStack(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public long getTransferLimit() {
        return attributes.transferLimit;
    }

    @Override
    public void setTransferLimit(long limit) {
        ensureMutableAttributes();
        attributes.transferLimit = Math.max(0, limit);
    }

    @Override
    @Nonnull
    public Predicate<D> getValidator() {
        return attributes.validator;
    }

    @Override
    public void setValidator(Predicate<D> validator) {
        ensureMutableAttributes();
        attributes.validator = validator != null ? validator : _ -> true;
    }

    @Override
    public boolean isExact() {
        return attributes.isExact;
    }

    @Override
    public void setExact(boolean exact) {
        ensureMutableAttributes();
        attributes.isExact = exact;
    }

    private void ensureMutableAttributes() {
        if (this.attributes == DEFAULT_ATTRIBUTES) {
            this.attributes = new FluxAttributes<>();
        }
    }

    // =================================================================================
    // Internal Attribute Container
    // =================================================================================

    protected static class FluxAttributes<D> {
        @Nonnull
        public Predicate<D> validator = _ -> true;
        public long transferLimit = Long.MAX_VALUE;
        public boolean isExact = false;

        public FluxAttributes() {
        }

        public FluxAttributes<D> copy() {
            if (this == DEFAULT_ATTRIBUTES) return this;

            FluxAttributes<D> clone = new FluxAttributes<>();
            clone.validator = this.validator;
            clone.transferLimit = this.transferLimit;
            clone.isExact = this.isExact;
            return clone;
        }
    }

    // =================================================================================
    // Strategy: Bundle (List-backed)
    // =================================================================================

    public abstract static class Bundle<D> extends AbstractFlux<D> {

        /**
         * Backing storage for data stacks.
         */
        protected final List<D> stacks;

        /**
         * Constructs from a list of stacks.
         */
        public Bundle(List<D> stacks) {
            this.stacks = stacks;
        }

        @Override
        public int getStackCount() {
            return stacks.size();
        }

        @Override
        public D getStack(int index) {
            if (index >= 0 && index < stacks.size()) {
                return stacks.get(index);
            }
            return null;
        }

        @Override
        public int findIndexOfTarget(D target) {
            if (target == null) return -1;

            for (int i = 0; i < stacks.size(); i++) {
                if (matchesWithIndex(i, target)) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public D setStack(int index, D stack) {
            if (index >= 0 && index < stacks.size()) {
                stacks.set(index, stack);
            }
            return stack;
        }

        @Override
        public D removeStack(int index) {
            return stacks.remove(index);
        }
    }

    // =================================================================================
    // Strategy: Packet (Single-Value optimized)
    // =================================================================================

    public abstract static class Packet<D> extends AbstractFlux<D> {

        /**
         * Direct storage for the single data stack.
         * <p>
         * Optimized to avoid the memory overhead of a List structure for single-item carriers.
         * </p>
         */
        protected D content;

        /**
         * Constructs a packet containing a single data value.
         *
         * @param content The data stack. Can be null if the packet starts empty.
         */
        public Packet(D content) {
            this.content = content;
        }

        @Override
        public int getStackCount() {
            return 1;
        }

        /**
         * Convenience method to retrieve the single payload. Equivalent to calling {@code getStack(0)}.
         *
         * @return The contained data stack, or null if empty.
         */
        public D getStack() {
            return getStack(0);
        }

        @Override
        public D getStack(int index) {
            return index == 0 ? content : null;
        }

        @Override
        public int findIndexOfTarget(D target) {
            if (target == null) return -1;
            return matchesWithIndex(0, target) ? 0 : -1;
        }

        /**
         * Convenience method to replace the single payload. Equivalent to calling {@code setStack(0, content)}.
         *
         * @param content The new data stack.
         */
        public void setContent(D content) {
            setStack(0, content);
        }

        @Override
        public D setStack(int index, D stack) {
            if (index == 0) {
                content = stack;
            } else {
                throw new IndexOutOfBoundsException("Packet only supports index 0!");
            }
            return stack;
        }

        /**
         * Convenience method to clear the payload. Equivalent to calling {@code removeStack(0)}.
         *
         * @return The data stack that was removed.
         */
        public D removeStack() {
            return removeStack(0);
        }

        @Override
        public D removeStack(int index) {
            if (index == 0) {
                D old = content;
                content = null;
                return old;
            }
            return null;
        }

        @Override
        public boolean isIndexEmpty(int index) {
            return index != 0 || isContentEmpty();
        }

        public abstract boolean isContentEmpty();

        @Override
        public boolean matchesWithIndex(int index, D reference) {
            return index == 0 && matchesWithContent(reference);
        }

        public abstract boolean matchesWithContent(D reference);
    }
}
