package com.benchenssever.fluxvault.api;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * Base implementation for a Flux carrier backed by a list.
 * <p>
 * This class handles the common list operations (storage, iteration, access) required by {@link IFlux}.
 * Concrete implementations (e.g., LiquidFlux) only need to define resource-specific logic.
 * </p>
 *
 * @param <D> The data type.
 */
public abstract class AbstractFlux<D> implements IFlux<D> {
    @Nonnull
    protected Predicate<D> validator = _ -> true;
    private long transferLimit = Long.MAX_VALUE;

    @Override
    public int findIndexOfFirstMatchesStack(Predicate<D> validator) {
        for (int i = 0; i < getStackSize(); i++) {
            if (!isIndexEmpty(i) && validator.test(getStack(i))) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public long getTransferLimit() {
        return this.transferLimit;
    }

    @Override
    public void setTransferLimit(long limit) {
        this.transferLimit = Math.max(0, limit);
    }

    public AbstractFlux<D> withLimit(long limit) {
        setTransferLimit(limit);
        return this;
    }

    @Override
    @Nonnull
    public Predicate<D> getValidator() {
        return validator;
    }

    @Override
    public void setValidator(Predicate<D> validator) {
        this.validator = (validator != null) ? validator : _ -> true;
    }

    public AbstractFlux<D> withValidator(Predicate<D> validator) {
        setValidator(validator);
        return this;
    }

    @Override
    public Iterator<D> iterator() {
        return getStacks().iterator();
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
         * Constructs from an array of stacks.
         */
        @SafeVarargs
        public Bundle(D... stacks) {
            this.stacks = new ArrayList<>(List.of(stacks));
        }

        /**
         * Constructs from a list of stacks.
         */
        public Bundle(List<D> stacks) {
            this.stacks = new ArrayList<>(stacks);
        }

        @Override
        public int getStackSize() {
            return stacks.size();
        }

        @Override
        public List<D> getStacks() {
            return Collections.unmodifiableList(stacks);
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
        public int getStackSize() {
            return 1;
        }

        @Override
        public List<D> getStacks() {
            return content == null ? Collections.emptyList() : Collections.singletonList(content);
        }

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

        public void setContent(D content) {
            setStack(0, content);
        }

        @Override
        public D setStack(int index, D stack) {
            if (index == 0) {
                this.content = stack;
            } else {
                throw new IndexOutOfBoundsException("Packet only supports index 0!");
            }
            return stack;
        }

        public D removeStack() {
            return removeStack(0);
        }

        @Override
        public D removeStack(int index) {
            if (index == 0) {
                D old = content;
                this.content = null;
                return old;
            }
            return null;
        }
    }
}
