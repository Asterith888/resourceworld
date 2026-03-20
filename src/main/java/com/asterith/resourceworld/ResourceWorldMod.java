package com.asterith.resourceworld;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceWorldMod implements ModInitializer {
    public static final String MOD_ID = "resourceworld";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        ResourceWorldManager.init();
        ResourceWorldCommands.register();
        LOGGER.info("[ResourceWorld] Initialized");
    }
}
