/*
 * FluxVault - A universal transport protocol for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.util;

import com.hypixel.hytale.protocol.BlockFace;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

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
}