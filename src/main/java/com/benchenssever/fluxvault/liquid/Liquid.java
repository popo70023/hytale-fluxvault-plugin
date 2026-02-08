package com.benchenssever.fluxvault.liquid;

import java.util.Set;

//TODO: 這個類別的主要目標是定義一種液體類型，包含一個唯一的識別符（liquidID）和一組相關的標籤（tags）。核心邏輯會在構造函數裡實作，確保它能正確初始化 liquidID 和 tags，並提供必要的方法來訪問這些屬性。
public record Liquid(String liquidID, Set<String> tags) {
    public static final String EMPTY_ID = "empty";
    public static final Liquid EMPTY = new Liquid(EMPTY_ID);

    public Liquid(String liquidID, String... tags) {
        this(liquidID, Set.of(tags));
    }
}
