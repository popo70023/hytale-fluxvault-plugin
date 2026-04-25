/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.util;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockFace;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Set;

public final class BlockFaceUtil {

    private BlockFaceUtil() {
    }

    public static RotationTuple getRotationTuple(Ref<ChunkStore> blockRef) {
        BlockModule.BlockStateInfo stateInfo = blockRef.getStore().getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());

        if (stateInfo != null) {
            Ref<ChunkStore> chunkRef = stateInfo.getChunkRef();
            if (chunkRef.isValid()) {
                BlockChunk blockChunk = blockRef.getStore().getComponent(chunkRef, BlockChunk.getComponentType());
                if (blockChunk != null) {
                    int index = stateInfo.getIndex();
                    int x = ChunkUtil.xFromBlockInColumn(index);
                    int y = ChunkUtil.yFromBlockInColumn(index);
                    int z = ChunkUtil.zFromBlockInColumn(index);

                    return blockChunk.getSectionAtBlockY(y).getRotation(x, y, z);
                }
            }
        }
        return null;
    }

    @Nullable
    public static BlockFace getAbsoluteFace(@Nullable Rotation yaw, @Nullable RelativeFace relativeFace) {
        if (yaw == null) yaw = Rotation.None;
        if (relativeFace == null) return null;
        BlockFace baseFace = BlockFace.VALUES[relativeFace.getValue()];
        return BlockFace.rotate(baseFace, yaw, Rotation.None);
    }

    @Nonnull
    public static Set<RelativeFace> getRelativeFaces(@Nullable Rotation yaw, @Nullable BlockFace absoluteFace) {
        if (yaw == null) yaw = Rotation.None;

        Set<RelativeFace> resultSet = EnumSet.noneOf(RelativeFace.class);
        if (absoluteFace == null) {
            resultSet.add(RelativeFace.None);
            return resultSet;
        }
        Rotation inverseYaw = yaw.toInverse();

        BlockFace[] components = absoluteFace.getComponents();

        if (components.length == 0) {
            components = new BlockFace[]{absoluteFace};
        }

        for (BlockFace baseFace : components) {
            BlockFace unrotatedFace = BlockFace.rotate(baseFace, inverseYaw, Rotation.None);
            RelativeFace relFace = RelativeFace.fromValue(unrotatedFace.ordinal());
            if (relFace != null) {
                resultSet.add(relFace);
            }
        }

        return resultSet;
    }

    public enum RelativeFace {
        Top(0),
        Bottom(1),
        Back(2),
        Right(3),
        Front(4),
        Left(5),
        None(-1);

        private static final RelativeFace[] VALUES = values();
        private final int value;

        RelativeFace(int value) {
            this.value = value;
        }

        public int getValue() {
            return this.value;
        }

        public static RelativeFace fromValue(int value) {
            if (value >= 0 && value <= 5) return VALUES[value];
            return None;
        }
    }

    public enum RelativeFaceGroup {
        Disabled(EnumSet.noneOf(RelativeFace.class)),

        All(EnumSet.allOf(RelativeFace.class)),
        AllFace(EnumSet.of(RelativeFace.Top, RelativeFace.Bottom, RelativeFace.Front, RelativeFace.Back, RelativeFace.Left, RelativeFace.Right)),
        Horizontal(EnumSet.of(RelativeFace.Front, RelativeFace.Back, RelativeFace.Left, RelativeFace.Right)),
        Vertical(EnumSet.of(RelativeFace.Top, RelativeFace.Bottom)), Top(EnumSet.of(RelativeFace.Top)),

        Bottom(EnumSet.of(RelativeFace.Bottom)),
        Front(EnumSet.of(RelativeFace.Front)),
        Back(EnumSet.of(RelativeFace.Back)),
        Left(EnumSet.of(RelativeFace.Left)),
        Right(EnumSet.of(RelativeFace.Right)),
        None(EnumSet.of(RelativeFace.None));

        private final Set<BlockFaceUtil.RelativeFace> faces;

        RelativeFaceGroup(Set<BlockFaceUtil.RelativeFace> faces) {
            this.faces = faces;
        }

        @Nonnull
        public Set<BlockFaceUtil.RelativeFace> getFaces() {
            return this.faces;
        }

        public boolean contains(@Nullable BlockFaceUtil.RelativeFace face) {
            return face != null && this.faces.contains(face);
        }
    }
}