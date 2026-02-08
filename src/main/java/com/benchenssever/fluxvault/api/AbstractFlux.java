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
 * @param <T> The concrete implementation type.
 * @param <D> The data type.
 */
public abstract class AbstractFlux<T extends IFlux<T, D>, D> implements IFlux<T, D> {

    @Nonnull
    protected Predicate<D> validator = _ -> true;

    @Override
    @Nonnull
    public Predicate<D> getValidator() {
        return validator;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T withValidator(Predicate<D> validator) {
        this.validator = (validator != null) ? validator : _ -> true;
        return (T) this;
    }

    // =================================================================================
    // Strategy: Bundle (List-backed)
    // =================================================================================

    public abstract static class Bundle<T extends IFlux<T, D>, D> extends AbstractFlux<T, D> {

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
        public int getIndexOf(D stack) {
            if (stack == null) return -1;

            for (int i = 0; i < stacks.size(); i++) {
                D current = stacks.get(i);
                // 這裡的 matches 需要你根據你的 D 類型來定義
                // 或是依賴子類別覆寫此方法
                if (matcheStack(current, stack)) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public void setStack(int index, D stack) {
            if (index >= 0 && index < stacks.size()) {
                stacks.set(index, stack);
            }
        }

        @Override
        public Iterator<D> iterator() {
            return stacks.iterator();
        }
    }

    // =================================================================================
    // Strategy: Packet (Single-Value optimized)
    // =================================================================================

    public abstract static class Packet<T extends IFlux<T, D>, D> extends AbstractFlux<T, D> {

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

        @Override
        public D getStack(int index) {
            return index == 0 ? content : null;
        }

        @Override
        public int getIndexOf(D stack) {
            if (stack == null) return -1;
            return matcheStack(content, stack) ? 0 : -1;
        }

        @Override
        public void setStack(int index, D stack) {
            if (index == 0) {
                this.content = stack;
            }
        }

        @Override
        public Iterator<D> iterator() {
            return getStacks().iterator();
        }
    }
}
