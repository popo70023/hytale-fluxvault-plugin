package com.benchenssever.fluxvault;

import com.benchenssever.fluxvault.registry.ComponentTypes;
import com.benchenssever.fluxvault.registry.FluxAssetRegistry;
import com.benchenssever.fluxvault.registry.LiquidCapsuleTypeRegistry;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;

public class FluxVaultPlugin extends JavaPlugin {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public FluxVaultPlugin(@Nonnull JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Hello from " + this.getName() + " version " + this.getManifest().getVersion().toString());
    }

    public static HytaleLogger getPluginLogger() {
        return LOGGER;
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up plugin " + this.getName());
        ComponentTypes.registerChunkStore(this.getChunkStoreRegistry());
        FluxAssetRegistry.registerAssets(this.getAssetRegistry());
        FluxAssetRegistry.registerInteraction(this.getCodecRegistry(Interaction.CODEC));
    }

    @Override
    protected void start() {
        LOGGER.atInfo().log("Starting plugin " + this.getName());
        LiquidCapsuleTypeRegistry.registerLiquidCapsuleType();
    }
}