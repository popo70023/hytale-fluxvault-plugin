/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.util;

import com.hypixel.hytale.protocol.BlockFace;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Set;

public final class BlockFaceUtil {

    private BlockFaceUtil() {
    }

    @NonNullDecl
    public static BlockFace getOpposite(@NullableDecl BlockFace face) {
        if (face == null) return BlockFace.None;

        return switch (face) {
            case Up -> BlockFace.Down;
            case Down -> BlockFace.Up;
            case North -> BlockFace.South;
            case South -> BlockFace.North;
            case East -> BlockFace.West;
            case West -> BlockFace.East;
            default -> BlockFace.None;
        };
    }

    @NonNullDecl
    public static BlockFace rotateYClockwise(@NullableDecl BlockFace face) {
        if (face == null) return BlockFace.None;
        return switch (face) {
            case North -> BlockFace.East;
            case East -> BlockFace.South;
            case South -> BlockFace.West;
            case West -> BlockFace.North;
            default -> face;
        };
    }

    @NonNullDecl
    public static BlockFace rotateYCounterClockwise(@NullableDecl BlockFace face) {
        if (face == null) return BlockFace.None;
        return switch (face) {
            case North -> BlockFace.West;
            case West -> BlockFace.South;
            case South -> BlockFace.East;
            case East -> BlockFace.North;
            default -> face;
        };
    }

    @NonNullDecl
    public static BlockFace rotateY180(@NullableDecl BlockFace face) {
        if (face == null) return BlockFace.None;
        if (isHorizontal(face)) return getOpposite(face);
        return face;
    }

    public static boolean isPhysical(@NullableDecl BlockFace face) {
        return face != null && face != BlockFace.None;
    }

    public static boolean isHorizontal(@NullableDecl BlockFace face) {
        return face == BlockFace.North || face == BlockFace.South ||
                face == BlockFace.East || face == BlockFace.West;
    }

    public static boolean isVertical(@NullableDecl BlockFace face) {
        return face == BlockFace.Up || face == BlockFace.Down;
    }

    enum BlockFaceGroup {
        ALL(Set.of(BlockFace.None, BlockFace.Up, BlockFace.Down, BlockFace.North, BlockFace.South, BlockFace.East, BlockFace.West)),
        ALL_FACE(Set.of(BlockFace.Up, BlockFace.Down, BlockFace.North, BlockFace.South, BlockFace.East, BlockFace.West)),
        HORIZONTAL(Set.of(BlockFace.North, BlockFace.South, BlockFace.East, BlockFace.West)),
        VERTICAL(Set.of(BlockFace.Up, BlockFace.Down)),
        ONLY_NONE(Set.of(BlockFace.None));

        private final Set<BlockFace> faces;

        BlockFaceGroup(Set<BlockFace> faces) {
            this.faces = faces; // Set.of() 已經保證了不可變性
        }

        @NonNullDecl
        public Set<BlockFace> getFaces() {
            return this.faces;
        }

        public boolean contains(@NullableDecl BlockFace face) {
            return face != null && this.faces.contains(face);
        }
    }
}