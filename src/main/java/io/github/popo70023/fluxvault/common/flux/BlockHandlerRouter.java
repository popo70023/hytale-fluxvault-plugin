/*
 * FluxVault - The Ultimate ECS Resource Storage & Capability Framework for Hytale.
 * Copyright (c) 2026 Ben (popo70023)
 * Licensed under the MIT License.
 */
package io.github.popo70023.fluxvault.common.flux;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockFace;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import io.github.popo70023.fluxvault.api.IFluxHandlerProvider.FluxAccess;
import io.github.popo70023.fluxvault.payload.resource.FluxResource;
import io.github.popo70023.fluxvault.util.BlockFaceUtil;
import io.github.popo70023.fluxvault.util.BlockFaceUtil.RelativeFace;
import io.github.popo70023.fluxvault.util.BlockFaceUtil.RelativeFaceGroup;

import javax.annotation.Nullable;
import java.util.*;

public class BlockHandlerRouter {
    public static final String ROUTING_KEY = "Routing";
    private static final String WILDCARD_RESOURCE = "*";
    private RoutingRule[] routingRule = new RoutingRule[0];
    private volatile Rotation rotation = Rotation.None;
    private final Map<RelativeFace, Map<FluxAccess, Map<String, String>>> exactRouting = new EnumMap<>(RelativeFace.class);

    public BlockHandlerRouter() {
    }

    public BlockHandlerRouter(BlockHandlerRouter other) {
        this.rotation = other.rotation;
        this.setRoutingRule(other.routingRule.clone());
    }

    public RoutingRule[] getRoutingRules() {
        return this.routingRule.clone();
    }

    public void setRoutingRule(RoutingRule[] rules) {
        this.routingRule = rules != null ? rules.clone() : new RoutingRule[0];
        compileRouting();
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public void setRotation(Rotation rotation) {
        this.rotation = rotation;
    }

    public void compileRouting() {
        this.exactRouting.clear();
        if (this.routingRule.length == 0) return;

        List<RoutingRule> sortedRules = new ArrayList<>(Arrays.asList(routingRule));
        sortedRules.sort((r1, r2) -> Integer.compare(r2.getTargetFaces().size(), r1.getTargetFaces().size()));

        for (RoutingRule rule : sortedRules) {
            Set<RelativeFace> faces = rule.getTargetFaces();
            String ruleResourceId = (rule.resourceId == null || rule.resourceId.isEmpty()) ? WILDCARD_RESOURCE : rule.resourceId;

            for (RelativeFace face : faces) {
                exactRouting.putIfAbsent(face, new EnumMap<>(FluxAccess.class));
                Map<FluxAccess, Map<String, String>> intents = exactRouting.get(face);

                if (rule.target != null && !rule.target.isEmpty()) {
                    if (rule.access == RoutingAccess.Both || rule.access == RoutingAccess.Fill) {
                        intents.putIfAbsent(FluxAccess.FILL, new HashMap<>());
                        intents.get(FluxAccess.FILL).put(ruleResourceId, rule.target);
                    }
                    if (rule.access == RoutingAccess.Both || rule.access == RoutingAccess.Drain) {
                        intents.putIfAbsent(FluxAccess.DRAIN, new HashMap<>());
                        intents.get(FluxAccess.DRAIN).put(ruleResourceId, rule.target);
                    }
                }
            }
        }
    }

    @Nullable
    public String getTargetSlot(RelativeFace face, FluxAccess access, String requestedResourceId) {
        Map<FluxAccess, Map<String, String>> intents = exactRouting.get(face);
        if (intents == null) return null;

        Map<String, String> payloadMap = intents.get(access);
        if (payloadMap == null) return null;

        if (requestedResourceId != null && payloadMap.containsKey(requestedResourceId)) {
            return payloadMap.get(requestedResourceId);
        }

        return payloadMap.get(WILDCARD_RESOURCE);
    }

    @Nullable
    public String getTargetSlot(BlockFace incomingAbsoluteFace, FluxAccess access, String requestedResourceId) {
        Set<RelativeFace> incomingRelativeFaces = BlockFaceUtil.getRelativeFaces(this.rotation, incomingAbsoluteFace);

        for (RelativeFace relFace : incomingRelativeFaces) {
            String slot = getTargetSlot(relFace, access, requestedResourceId);
            if (slot != null) {
                return slot;
            }
        }
        return null;
    }

    public enum RoutingAccess {
        Both, Fill, Drain
    }

    public static class RoutingRule {
        public RelativeFaceGroup faces = RelativeFaceGroup.Disabled;
        public RoutingAccess access = RoutingAccess.Both;
        public String resourceId = "*";
        public String target = "";

        public static final BuilderCodec<RoutingRule> CODEC = BuilderCodec.builder(RoutingRule.class, RoutingRule::new)
                .append(new KeyedCodec<>("Faces", new EnumCodec<>(RelativeFaceGroup.class)), (r, v) -> r.faces = v, r -> r.faces).add()
                .append(new KeyedCodec<>("Access", new EnumCodec<>(RoutingAccess.class)), (r, v) -> r.access = v != null ? v : RoutingAccess.Both, r -> r.access).add()
                .append(new KeyedCodec<>(FluxResource.RESOURCE_ID_KEY, Codec.STRING), (rule, v) -> rule.resourceId = v, rule -> rule.resourceId)
                .documentation("Optional. Leave blank or use '*' to accept all resources. Specify an ResourceId (e.g., 'FluxVault.Flux_Energy') to restrict this route to a specific resource type.").add()
                .append(new KeyedCodec<>("Target", Codec.STRING), (r, v) -> r.target = v, r -> r.target).add()
                .build();

        public Set<RelativeFace> getTargetFaces() {
            if (faces != null) return faces.getFaces();
            return EnumSet.noneOf(RelativeFace.class);
        }
    }
}
