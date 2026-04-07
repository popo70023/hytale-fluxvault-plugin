/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.payload.liquid.interaction.ui;

import com.google.gson.JsonObject;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.window.WindowType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.entities.player.windows.BlockWindow;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.popo70023.fluxvault.api.IFluxContainer;
import io.github.popo70023.fluxvault.payload.liquid.Liquid;
import io.github.popo70023.fluxvault.payload.liquid.LiquidStack;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

import javax.annotation.Nonnull;

//TODO: 未完成
public class LiquidContainerWindow extends BlockWindow {
    @Nonnull
    private final JsonObject windowData;

    public LiquidContainerWindow(int x, int y, int z, int rotationIndex, @NonNullDecl BlockType blockType, @NonNullDecl IFluxContainer<LiquidStack> fluxContainers) {
        super(WindowType.Container, x, y, z, rotationIndex, blockType);
        this.windowData = new JsonObject();
        Item item = blockType.getItem();
        assert item != null;
        this.windowData.addProperty("blockItemId", item.getId());
        long capacity = fluxContainers.getCapacity();
        this.windowData.addProperty("containerCapacity", capacity);
        for (int i = 0; i < fluxContainers.getContainerMaxSize(); i++) {
            LiquidStack content = fluxContainers.getContent(i);
            if (content != null && !content.isEmpty()) {
                long quantity = content.getQuantity();
                this.windowData.addProperty("content_" + i + "_liquidName", content.getLiquidId());
                this.windowData.addProperty("content_" + i + "_quantity", quantity);
                float fillRatio = capacity > 0 ? (float) quantity / capacity : 0.0f;
                this.windowData.addProperty("content_" + i + "_fillRatio", fillRatio);
            } else {
                this.windowData.addProperty("content_" + i + "_liquidName", Liquid.EMPTY_ID);
                this.windowData.addProperty("content_" + i + "_quantity", 0);
                this.windowData.addProperty("content_" + i + "_fillRatio", 0.0f);
            }
        }
    }

    @NonNullDecl
    @Override
    public JsonObject getData() {
        return this.windowData;
    }

    @Override
    protected boolean onOpen0(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl Store<EntityStore> store) {
        return true;
    }

    @Override
    protected void onClose0(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl ComponentAccessor<EntityStore> componentAccessor) {

    }
}
