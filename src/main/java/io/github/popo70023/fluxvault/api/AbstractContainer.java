/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.api;

import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public abstract class AbstractContainer<D> implements IFluxContainer<D> {
    public static final String CONTENT_KEY = "Content";
    public static final String CONTENTS_KEY = "Contents";
    public static final String CAPACITY_KEY = "Capacity";
    public static final String CAPACITY_DOCUMENTATION = "The maximum volume of resources/contents this container can hold.";

    protected final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void onContentsChanged() {
    }

    /**
     * Executes the given action under a write lock.
     * <p>
     * Ensures thread-safe state mutation and guarantees lock release.
     * </p>
     *
     * @param action The state-mutating operation to perform.
     */
    protected void writeAction(Runnable action) {
        lock.writeLock().lock();
        try {
            action.run();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Executes the given supplier under a write lock and returns its result.
     *
     * @param action The state-mutating operation yielding a result.
     * @param <T>    The return type.
     * @return The result of the supplier.
     */
    protected <T> T writeAction(Supplier<T> action) {
        lock.writeLock().lock();
        try {
            return action.get();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Executes the given supplier under a read lock and returns its result.
     * <p>
     * Allows multiple threads to read state concurrently, blocking only if a write is in progress.
     * </p>
     *
     * @param action The read-only operation yielding a result.
     * @param <T>    The return type.
     * @return The result of the supplier.
     */
    protected <T> T readAction(Supplier<T> action) {
        lock.readLock().lock();
        try {
            return action.get();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Resolves the appropriate lock type based on the requested FluxAction.
     *
     * @param action The simulated or executed action type.
     * @return A Write lock if executing, or a Read lock if only simulating.
     */
    protected java.util.concurrent.locks.Lock getActiveLock(IFluxHandler.FluxAction action) {
        return action.execute() ? this.lock.writeLock() : this.lock.readLock();
    }
}
