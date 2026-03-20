package com.asterith.resourceworld;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

import static net.minecraft.server.command.CommandManager.literal;

public class ResourceWorldCommands {

    private static final int BORDER = 10000;
    private static final Random RANDOM = new Random();

    public static void register() {
        CommandRegistrationCallback.EVENT.register(ResourceWorldCommands::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher,
                                         CommandRegistryAccess registryAccess,
                                         CommandManager.RegistrationEnvironment env) {

        dispatcher.register(
                literal("resourceworld")
                        // PLAYER COMMANDS (NO PERMISSION REQUIRED)
                        .then(literal("rtp")
                                .then(CommandManager.argument("world", StringArgumentType.word())
                                        .executes(ctx -> rtp(ctx.getSource(),
                                                StringArgumentType.getString(ctx, "world")))))
                        .then(literal("home")
                                .executes(ctx -> home(ctx.getSource())))

                        // ADMIN COMMANDS (OP REQUIRED)
                        .then(literal("reset")
                                .requires(src -> src.hasPermissionLevel(2))
                                .then(CommandManager.argument("world", StringArgumentType.word())
                                        .executes(ctx -> reset(ctx.getSource(),
                                                StringArgumentType.getString(ctx, "world")))))
                        .then(literal("delete")
                                .requires(src -> src.hasPermissionLevel(2))
                                .then(CommandManager.argument("world", StringArgumentType.word())
                                        .executes(ctx -> delete(ctx.getSource(),
                                                StringArgumentType.getString(ctx, "world")))))
        );
    }

    // -------------------------
    // PLAYER COMMANDS
    // -------------------------

    private static int rtp(ServerCommandSource source, String worldId) {
        ServerPlayerEntity player = source.getPlayer();
        MinecraftServer server = source.getServer();

        var world = switch (worldId.toLowerCase()) {
            case "overworld" -> server.getWorld(ResourceWorldManager.RESOURCE_OVERWORLD);
            case "nether" -> server.getWorld(ResourceWorldManager.RESOURCE_NETHER);
            default -> null;
        };

        if (world == null) {
            source.sendError(Text.literal("Unknown world. Use overworld or nether."));
            return 0;
        }

        // Random X/Z inside border
        int x = RANDOM.nextInt(BORDER * 2) - BORDER;
        int z = RANDOM.nextInt(BORDER * 2) - BORDER;

        // Find safe Y
        int y = world.getTopY();
        BlockPos pos = new BlockPos(x, y, z);

        // Drop down to ground
        while (y > world.getBottomY() && world.getBlockState(pos).isAir()) {
            y--;
            pos = new BlockPos(x, y, z);
        }

        // Move up to safe spot
        y += 2;

        player.teleport(world, x + 0.5, y, z + 0.5, player.getYaw(), player.getPitch());
        source.sendFeedback(() -> Text.literal("Randomly teleported!"), false);
        return 1;
    }

    private static int home(ServerCommandSource source) {
        ServerPlayerEntity player = source.getPlayer();
        MinecraftServer server = source.getServer();

        // Player bed/anchor
        var spawnPos = player.getSpawnPointPosition();
        var spawnDim = player.getSpawnPointDimension();

        if (spawnPos != null && spawnDim != null) {
            var world = server.getWorld(spawnDim);
            if (world != null) {
                player.teleport(world,
                        spawnPos.getX() + 0.5,
                        spawnPos.getY(),
                        spawnPos.getZ() + 0.5,
                        player.getYaw(),
                        player.getPitch());
                return 1;
            }
        }

        // Fallback: overworld spawn
        var overworld = server.getOverworld();
        BlockPos spawn = overworld.getSpawnPos();

        player.teleport(overworld,
                spawn.getX() + 0.5,
                spawn.getY(),
                spawn.getZ() + 0.5,
                player.getYaw(),
                player.getPitch());

        return 1;
    }

    // -------------------------
    // ADMIN COMMANDS
    // -------------------------

    private static int reset(ServerCommandSource source, String worldId) {
        return delete(source, worldId); // delete now, regenerate on restart
    }

    private static int delete(ServerCommandSource source, String worldId) {
        MinecraftServer server = source.getServer();

        boolean ok = switch (worldId.toLowerCase()) {
            case "overworld" -> ResourceWorldManager.deleteResourceWorldFolder(server, ResourceWorldManager.RESOURCE_OVERWORLD);
            case "nether" -> ResourceWorldManager.deleteResourceWorldFolder(server, ResourceWorldManager.RESOURCE_NETHER);
            default -> false;
        };

        if (!ok) {
            source.sendError(Text.literal("Could not delete world folder."));
            return 0;
        }

        source.sendFeedback(() -> Text.literal("Deleted. Restart server to regenerate."), true);
        return 1;
    }
}
