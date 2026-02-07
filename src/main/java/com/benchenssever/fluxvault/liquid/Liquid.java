package com.benchenssever.fluxvault.liquid;

import java.util.Set;

public record Liquid(String liquidID, Set<String> tags) {
    public static final String EMPTY_ID = "empty";
    public static final Liquid EMPTY = new Liquid(EMPTY_ID);

    public Liquid(String liquidID, String... tags) {
        this(liquidID, Set.of(tags));
    }
}
