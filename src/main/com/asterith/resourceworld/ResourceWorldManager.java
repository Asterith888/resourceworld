package com.asterith.resourceworld;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResourceWorldManager {

    public static final RegistryKey<World> RESOURCE_OVERWORLD =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier(ResourceWorldMod.MOD_ID, "overworld"));
    public static final RegistryKey<World> RESOURCE_NETHER =
            RegistryKey.of(RegistryKeys.WORLD, new Identifier(ResourceWorldMod.MOD_ID, "nether"));

    private static final int BORDER_SIZE = 10000;

    public static void init() {
        ServerLifecycleEvents.SERVER_STARTED.register(ResourceWorldManager::onServerStarted);
    }

    private static void onServerStarted(MinecraftServer server) {
        // Ensure worlds are loaded (datapack dimensions auto-generate if missing)
        ServerWorld overworld = server.getWorld(RESOURCE_OVERWORLD);
        ServerWorld nether = server.getWorld(RESOURCE_NETHER);

        if (overworld == null) {
            ResourceWorldMod.LOGGER.warn("[ResourceWorld] Resource overworld not found (check datapack/JSON).");
        } else {
            applyBorder(overworld);
        }

        if (nether == null) {
            ResourceWorldMod.LOGGER.warn("[ResourceWorld] Resource nether not found (check datapack/JSON).");
        } else {
            applyBorder(nether);
        }
    }

    private static void applyBorder(ServerWorld world) {
        WorldBorder border = world.getWorldBorder();
        border.setCenter(0.0, 0.0);
        border.setSize(BORDER_SIZE);
    }

    public static boolean deleteResourceWorldFolder(MinecraftServer server, RegistryKey<World> worldKey) {
        Path worldDir = server.getSavePath(WorldSavePath.ROOT);
        String dimFolder;

        if (worldKey == RESOURCE_OVERWORLD) {
            dimFolder = "DIM_resourceworld/overworld";
        } else if (worldKey == RESOURCE_NETHER) {
            dimFolder = "DIM_resourceworld/nether";
        } else {
            return false;
        }

        Path target = worldDir.resolve(dimFolder);
        if (!Files.exists(target)) {
            return false;
        }

        try {
            deleteRecursively(target);
            ResourceWorldMod.LOGGER.info("[ResourceWorld] Deleted folder {}", target);
            return true;
        } catch (IOException e) {
            ResourceWorldMod.LOGGER.error("[ResourceWorld] Failed to delete folder {}", target, e);
            return false;
        }
    }

    private static void deleteRecursively(Path path) throws IOException {
        if (Files.notExists(path)) return;
        Files.walk(path)
                .sorted((a, b) -> b.getNameCount() - a.getNameCount())
                .forEach(p -> {
                    try {
                        Files.deleteIfExists(p);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    // Small shim because 1.21 moved save paths into WorldSavePath
    public enum WorldSavePath implements net.minecraft.world.WorldSavePath {
        ROOT(".");

        private final String dirName;

        WorldSavePath(String dirName) {
            this.dirName = dirName;
        }

        @Override
        public String getDirectoryName() {
            return dirName;
        }
    }
}
