package com.benchenssever.fluxvault;

import com.benchenssever.fluxvault.liquid.container.LiquidContainerInteraction;
import com.benchenssever.fluxvault.registry.ComponentTypes;
import com.benchenssever.fluxvault.registry.FluxAssetRegistry;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;

public class FluxVaultPlugin extends JavaPlugin {

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public FluxVaultPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Hello from " + this.getName() + " version " + this.getManifest().getVersion().toString());
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up plugin " + this.getName());
        this.getCodecRegistry(Interaction.CODEC).register("Single_Liquid_Container_Interaction", LiquidContainerInteraction.class, LiquidContainerInteraction.CODEC);
        ComponentTypes.registerChunkStore(this.getChunkStoreRegistry());
        FluxAssetRegistry.registerAssets(this.getAssetRegistry());
    }

    @Override
    protected void start() {
        super.start();
        LOGGER.atInfo().log("Starting plugin " + this.getName());
        FluxAssetRegistry.registerMap();
    }
}