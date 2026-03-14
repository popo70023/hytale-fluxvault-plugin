package com.benchenssever.fluxvault.item;

import com.benchenssever.fluxvault.api.IFluxHandler;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.universe.world.meta.state.ItemContainerState;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

//TODO: 這個類別的主要目標是將原生的 ItemContainer 包裝成一個符合 IFluxHandler<ItemFlux> 介面的適配器，讓我們可以用統一的方式來操作原生箱子裡的物品。核心邏輯會在 fill 和 drain 方法裡實作，確保它們能正確處理物品的堆疊和空位，同時尊重模擬模式的要求。
public class VanillaFluxHandlerAdapter implements IFluxHandler<ItemFlux> {

    private final ItemContainer vanillaState;

    public VanillaFluxHandlerAdapter(ItemContainerState state) {
        this.vanillaState = state.getItemContainer();
    }

    //TODO
    @NonNullDecl
    @Override
    public ItemFlux fill(@NonNullDecl ItemFlux resource, @NonNullDecl FluxAction action) {
        return resource; // 暫位符
    }

    //TODO
    @NonNullDecl
    @Override
    public ItemFlux drain(@NonNullDecl ItemFlux maxDrainResource, @NonNullDecl FluxAction action) {
        // 實作從原生箱子找東西並扣除數量的邏輯
        return new ItemFlux(); // 暫位符
    }
}