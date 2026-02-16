package com.benchenssever.fluxvault.item;

import com.benchenssever.fluxvault.api.IFluxContainer;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import java.util.ArrayList;
import java.util.List;

//TODO: 這個類別的主要目標是將原生的 ItemContainer 包裝成一個符合 IFluxContainer<ItemFlux, ItemStack> 介面的適配器，讓我們可以用統一的方式來操作原生箱子裡的物品。核心邏輯會在 fill 和 drain 方法裡實作，確保它們能正確處理物品的堆疊和空位，同時尊重模擬模式的要求。
public class VanillaContainerAdapter implements IFluxContainer<ItemFlux, ItemStack> {

    private final ItemContainer vanillaState;

    public VanillaContainerAdapter(ItemContainerState state) {
        this.vanillaState = state.getItemContainer();
    }

    @Override
    public int getContainerMaxSize() {
        return vanillaState.getCapacity();
    }

    @Override
    public ItemStack getContent(int index) {
        return vanillaState.getItemStack((short) index);
    }

    @Override
    public void setContent(int index, ItemStack content) {
        this.vanillaState.setItemStackForSlot((short) index, content);
    }

    @Override
    public ItemStack addContent(ItemStack content) {
        return this.vanillaState.addItemStack(content).getRemainder();
    }

    //TODO
    @Override
    public int getFirstContentIndex() {
        return 0;
    }

    //TODO
    @Override
    public int findContentIndex(ItemStack content) {
        return 0;
    }

    @Override
    public List<ItemStack> getContents() {
        return this.vanillaState.dropAllItemStacks();
    }

    @NonNullDecl
    @Override
    public ItemFlux fill(@NonNullDecl ItemFlux resource, @NonNullDecl FluxAction action) {
        if (resource.isEmpty()) return resource;

        List<ItemStack> remainders = new ArrayList<>();
        for (ItemStack stackToInsert : resource) {
            ItemStack remainder = insertIntoVanilla(stackToInsert, action);
            if (!remainder.isEmpty()) {
                remainders.add(remainder);
            }
        }

        return new ItemFlux(remainders);
    }

    //TODO: 這裡的邏輯會比較麻煩，因為要考慮到堆疊優先、空位次之、模擬模式不修改原狀態等等
    @NonNullDecl
    @Override
    public ItemFlux drain(@NonNullDecl ItemFlux maxDrainResource, @NonNullDecl FluxAction action) {
        // 實作從原生箱子找東西並扣除數量的邏輯
        // 類似 fill，如果是 SIMULATE 記得不要真的扣除
        return new ItemFlux(); // 暫位符
    }

    @Override
    public void onContentsChanged() {
    }

    // --- 內部輔助邏輯 ---

    private ItemStack insertIntoVanilla(ItemStack stack, FluxAction action) {
        // 這裡實作標準的「堆疊優先，空位次之」的邏輯
        // 1. 遍歷 vanillaState 找相同的物品堆疊 -> 加進去
        // 2. 如果還有剩，找空格子 -> 放進去
        // 3. 根據 action 決定是真的修改 vanillaState 還是只計算結果
        return ItemStack.EMPTY; // 假設全部塞進去了
    }

    @Override
    public long getContainerCapacity() {
        return getContainerMaxSize();
    }

    @Override
    public long getAllContentQuantity() {
        long count = 0;
        for (int i = 0; i < getContainerMaxSize(); i++) {
            ItemStack s = getContent(i);
            if (s != null) count += s.getQuantity();
        }
        return count;
    }
}