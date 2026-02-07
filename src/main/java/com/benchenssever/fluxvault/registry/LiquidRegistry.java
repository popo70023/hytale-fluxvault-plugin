package com.benchenssever.fluxvault.registry;

import com.benchenssever.fluxvault.liquid.Liquid;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LiquidRegistry {
    private static final Map<String, Liquid> REGISTRY = new HashMap<>();

    static {
        registerLiquid(Liquid.EMPTY);
    }

    public static void registerLiquid(Liquid liquid) {
        if (REGISTRY.containsKey(liquid.liquidID())) {
            System.err.println("Warning: Duplicate liquid ID registered: " + liquid.liquidID());
            return;
        }
        REGISTRY.put(liquid.liquidID(), liquid);
    }

    public static Liquid getLiquid(String id) {
        return REGISTRY.getOrDefault(id, Liquid.EMPTY);
    }

    public static Set<Liquid> getAllLiquids() {
        return Set.copyOf(REGISTRY.values());
    }
}
