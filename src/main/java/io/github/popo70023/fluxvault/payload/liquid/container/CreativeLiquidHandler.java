/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.liquid.container;

import io.github.popo70023.fluxvault.api.FluxType;
import io.github.popo70023.fluxvault.api.IFluxHandler;
import io.github.popo70023.fluxvault.payload.liquid.Liquid;
import io.github.popo70023.fluxvault.payload.liquid.LiquidFlux;
import io.github.popo70023.fluxvault.payload.liquid.LiquidStack;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.Objects;

public record CreativeLiquidHandler(String liquidId) implements IFluxHandler<LiquidFlux> {

    @Override
    public boolean matchesFluxType(FluxType<?, ?> fluxType) {
        return fluxType == FluxType.LIQUID;
    }

    @NonNullDecl
    @Override
    public LiquidFlux fill(@NonNullDecl LiquidFlux resource, @NonNullDecl FluxAction action) {
        LiquidFlux resultFlux = new LiquidFlux();

        if (!Objects.equals(liquidId, Liquid.EMPTY_ID) || resource.isEmpty()) return resultFlux;

        long simLimit = resource.getTransferLimit();
        long totalCalculatedFill = 0;

        for (int i = 0; i < resource.getStackCount(); i++) {
            if (simLimit <= 0) break;
            LiquidStack reqStack = resource.getStack(i);
            long toFill = Math.min(reqStack.getQuantity(), simLimit);
            if (toFill > 0) {
                totalCalculatedFill += toFill;
                simLimit -= toFill;
            }
        }

        if (totalCalculatedFill <= 0) return resultFlux;

        if (resource.isExact()) {
            long targetExact = Math.min(resource.getAllQuantity(), resource.getTransferLimit());
            if (totalCalculatedFill < targetExact) {
                return resultFlux;
            }
        }

        if (action.simulate()) {
            return resultFlux.addStack(LiquidStack.of(liquidId, totalCalculatedFill));
        }

        long totalActualFilled = 0;
        long execLimit = resource.getTransferLimit();

        for (int i = resource.getStackCount() - 1; i >= 0; i--) {
            if (execLimit <= 0) break;

            LiquidStack reqStack = resource.getStack(i);
            long toFill = Math.min(reqStack.getQuantity(), execLimit);
            if (toFill <= 0) continue;

            totalActualFilled += toFill;
            execLimit -= toFill;

            if (reqStack.addQuantity(-toFill) == 0) {
                resource.removeStack(i);
            }
        }

        return resultFlux.addStack(LiquidStack.of(liquidId, totalActualFilled));
    }

    @NonNullDecl
    @Override
    public LiquidFlux drain(@NonNullDecl LiquidFlux requestResources, @NonNullDecl FluxAction action) {
        LiquidFlux resultFlux = new LiquidFlux();
        if (Objects.equals(liquidId, Liquid.EMPTY_ID) || requestResources.isEmpty()) return resultFlux;

        long remainingLimit = requestResources.getTransferLimit();

        long totalCalculatedDrain = 0;
        long simLimit = remainingLimit;

        for (int pass = 0; pass < 2; pass++) {
            if (simLimit <= 0) break;
            String targetId = (pass == 0) ? liquidId : Liquid.EMPTY_ID;
            for (int i = 0; i < requestResources.getStackCount(); i++) {
                if (simLimit <= 0) break;
                LiquidStack reqStack = requestResources.getStack(i);
                if (reqStack.getLiquidId().equals(targetId)) {
                    long toDrain = Math.min(reqStack.getQuantity(), simLimit);
                    totalCalculatedDrain += toDrain;
                    simLimit -= toDrain;
                }
            }
        }

        if (totalCalculatedDrain <= 0) return resultFlux;

        if (requestResources.isExact()) {
            long targetExact = Math.min(requestResources.getAllQuantity(), requestResources.getTransferLimit());
            if (totalCalculatedDrain < targetExact) {
                return resultFlux;
            }
        }

        if (action.simulate()) {
            return resultFlux.addStack(LiquidStack.of(liquidId, totalCalculatedDrain));
        }

        long totalActualDrained = 0;

        for (int pass = 0; pass < 2; pass++) {
            if (remainingLimit <= 0) break;
            String targetId = (pass == 0) ? liquidId : Liquid.EMPTY_ID;
            for (int i = requestResources.getStackCount() - 1; i >= 0; i--) {
                if (remainingLimit <= 0) break;

                LiquidStack reqStack = requestResources.getStack(i);
                if (reqStack.getLiquidId().equals(targetId)) {
                    long toDrain = Math.min(reqStack.getQuantity(), remainingLimit);
                    if (toDrain <= 0) continue;

                    totalActualDrained += toDrain;
                    remainingLimit -= toDrain;

                    if (targetId.equals(Liquid.EMPTY_ID)) {
                        reqStack = requestResources.setStack(i, LiquidStack.of(liquidId, reqStack.getQuantity()));
                    }
                    if (reqStack.addQuantity(-toDrain) == 0) {
                        requestResources.removeStack(i);
                    }
                }
            }
        }

        return resultFlux.addStack(LiquidStack.of(liquidId, totalActualDrained));
    }
}
