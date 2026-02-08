package com.benchenssever.fluxvault.liquid;

import com.hypixel.hytale.server.core.inventory.ItemStack;

import java.util.Map;

public class LiquidCapsuleType {
    private final String capsuleID;
    private final int capacity;
    private final String emptyCapsuleID;
    private final Map<Liquid, String> liquidToFilledCapsule;

    //TODO: 這個類別的主要目標是定義一種特殊的物品類型，稱為「液體膠囊」，它可以用來攜帶特定種類的液體。核心邏輯會在 isLiquidCapsule、getLiquidCapsuleType、isEmptyCapsule、getLiquidStackInCapsule 和 getCapsuleWithLiquid 方法裡實作，確保它們能正確識別和處理液體膠囊的狀態和內容。
    public LiquidCapsuleType(String capsuleID, int capacity, String emptyItemID, Map<Liquid, String> fluidToFilledItem) {
        this.capsuleID = capsuleID;
        this.capacity = capacity;
        this.emptyCapsuleID = emptyItemID;
        this.liquidToFilledCapsule = fluidToFilledItem;
    }

    public static boolean isLiquidCapsule(ItemStack itemStack) {
        return getLiquidCapsuleType(itemStack) != null;
    }

    //TODO: 這裡的邏輯會根據 itemStack 的物品 ID 來判斷它是否屬於這個液體膠囊類型，並返回對應的 LiquidCapsuleType 實例。如果 itemStack 的物品 ID 不匹配任何已註冊的液體膠囊類型，則返回 null。
    public static LiquidCapsuleType getLiquidCapsuleType(ItemStack itemStack) {
        return null;
    }

    public int getCapacity() {
        return capacity;
    }

    public boolean isEmptyCapsule(ItemStack itemStack) {
        return itemStack.getItemId().equals(emptyCapsuleID);
    }

    //TODO: 這裡的邏輯會根據 itemStack 的物品 ID 來判斷它所攜帶的液體種類和數量，並返回一個對應的 LiquidStack 實例。如果 itemStack 的物品 ID 不匹配任何已註冊的液體膠囊類型，或者它是空膠囊，則返回 LiquidStack.EMPTY。
    public LiquidStack getLiquidStackInCapsule(ItemStack itemStack) {
        return LiquidStack.EMPTY;
    }

    public ItemStack getEmptyCapsule() {
        return new ItemStack(emptyCapsuleID);
    }

    //TODO: 這裡的邏輯會根據給定的液體類型來查找對應的已填充膠囊的物品 ID，並返回一個新的 ItemStack 實例。如果給定的液體類型沒有對應的已填充膠囊，則返回 ItemStack.EMPTY。
    public ItemStack getCapsuleWithLiquid(Liquid iiquid) {
        return ItemStack.EMPTY;
    }
}
