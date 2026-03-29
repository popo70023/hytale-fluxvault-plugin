package io.github.popo70023.fluxvault.liquid.interaction;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import io.github.popo70023.fluxvault.liquid.container.SingleLiquidContainerComponent;
import io.github.popo70023.fluxvault.liquid.interaction.ui.LiquidContainerWindow;
import io.github.popo70023.fluxvault.liquid.interaction.ui.SingleLiquidContainerUIPage;
import io.github.popo70023.fluxvault.util.InteractionUtil;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.UUID;

//TODO: 這個類別的主要目標是實作一個簡單的互動，當玩家與一個具有 SingleLiquidContainerComponent 的方塊互動時，打開一個 GUI 顯示容器內的液體資訊。核心邏輯會在 interactWithBlock 方法裡實作，從容器組件讀取液體資訊並將其發送給玩家。這個互動不會修改容器的內容，因此 simulateInteractWithBlock 方法可以保持空白。
public class OpenSingleLiquidContainerInteraction extends SimpleBlockInteraction {
    public static final String ID = "OpenSingleLiquidContainer";
    public static final BuilderCodec<OpenSingleLiquidContainerInteraction> CODEC = BuilderCodec.builder(OpenSingleLiquidContainerInteraction.class, OpenSingleLiquidContainerInteraction::new, SimpleBlockInteraction.CODEC)
            .documentation("Open the liquid container GUI.")
            .build();

    @Override
    protected void interactWithBlock(@NonNullDecl World world, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext context, @NullableDecl ItemStack itemStack, @NonNullDecl Vector3i pos, @NonNullDecl CooldownHandler cooldownHandler) {
        Ref<EntityStore> ref = context.getEntity();
        Store<EntityStore> store = ref.getStore();
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        PlayerRef playerRef = commandBuffer.getComponent(ref, PlayerRef.getComponentType());
        SingleLiquidContainerComponent containerComp = InteractionUtil.getBlockComponent(world, pos, SingleLiquidContainerComponent.getComponentType());
        UUIDComponent uuidComponent = commandBuffer.getComponent(ref, UUIDComponent.getComponentType());
        BlockType blockType = world.getBlockType(pos.x, pos.y, pos.z);
        UUID uuid = uuidComponent.getUuid();
        SingleLiquidContainerUIPage uiPage = new SingleLiquidContainerUIPage(playerRef);
        LiquidContainerWindow window = new LiquidContainerWindow(pos.getX(), pos.getY(), pos.getZ(), 0, blockType, containerComp.getContainer());

        if (containerComp.getActiveWindows().putIfAbsent(uuid, window) == null) {

            if (player.getPageManager().openCustomPageWithWindows(ref, store, uiPage, window)) {

                window.registerCloseEvent((event) -> {
                    containerComp.getActiveWindows().remove(uuid);
                });

            } else {
                containerComp.getActiveWindows().remove(uuid);
            }
        }
    }

    @Override
    protected void simulateInteractWithBlock(@NonNullDecl InteractionType interactionType, @NonNullDecl InteractionContext interactionContext, @NullableDecl ItemStack itemStack, @NonNullDecl World world, @NonNullDecl Vector3i vector3i) {

    }
}
