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

import static net.minecraft.server.command.CommandManager.literal;

public class ResourceWorldCommands {

    public static void register() {
        CommandRegistrationCallback.EVENT.register(ResourceWorldCommands::registerCommands);
    }

    private static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher,
                                         CommandRegistryAccess registryAccess,
                                         CommandManager.RegistrationEnvironment env) {

        dispatcher.register(
                literal("resourceworld")
                        .requires(src -> src.hasPermissionLevel(2))
                        .then(literal("tp")
                                .then(CommandManager.argument("world", StringArgumentType.word())
                                        .executes(ctx -> {
                                            String worldId = StringArgumentType.getString(ctx, "world");
                                            return tpCommand(ctx.getSource(), worldId);
                                        })))
                        .then(literal("delete")
                                .then(CommandManager.argument("world", StringArgumentType.word())
                                        .executes(ctx -> {
                                            String worldId = StringArgumentType.getString(ctx, "world");
                                            return deleteCommand(ctx.getSource(), worldId);
                                        })))
        );
    }

    private static int tpCommand(ServerCommandSource source, String worldId) {
        ServerPlayerEntity player;
        try {
            player = source.getPlayer();
        } catch (Exception e) {
            source.sendError(Text.literal("Must be a player."));
            return 0;
        }

        MinecraftServer server = source.getServer();

        if (worldId.equalsIgnoreCase("overworld")) {
            var world = server.getWorld(ResourceWorldManager.RESOURCE_OVERWORLD);
            if (world == null) {
                source.sendError(Text.literal("Resource overworld not found."));
                return 0;
            }
            player.teleport(world, 0.5, world.getTopY(), 0.5, player.getYaw(), player.getPitch());
            source.sendFeedback(() -> Text.literal("Teleported to resource overworld."), false);
            return 1;
        }

        if (worldId.equalsIgnoreCase("nether")) {
            var world = server.getWorld(ResourceWorldManager.RESOURCE_NETHER);
            if (world == null) {
                source.sendError(Text.literal("Resource nether not found."));
                return 0;
            }
            player.teleport(world, 0.5, 80, 0.5, player.getYaw(), player.getPitch());
            source.sendFeedback(() -> Text.literal("Teleported to resource nether."), false);
            return 1;
        }

        source.sendError(Text.literal("Unknown world. Use 'overworld' or 'nether'."));
        return 0;
    }

    private static int deleteCommand(ServerCommandSource source, String worldId) {
        MinecraftServer server = source.getServer();

        boolean success;
        if (worldId.equalsIgnoreCase("overworld")) {
            success = ResourceWorldManager.deleteResourceWorldFolder(server, ResourceWorldManager.RESOURCE_OVERWORLD);
        } else if (worldId.equalsIgnoreCase("nether")) {
            success = ResourceWorldManager.deleteResourceWorldFolder(server, ResourceWorldManager.RESOURCE_NETHER);
        } else {
            source.sendError(Text.literal("Unknown world. Use 'overworld' or 'nether'."));
            return 0;
        }

        if (success) {
            source.sendFeedback(() -> Text.literal(
                    "Deleted resource " + worldId + " folder. Restart the server to regenerate with a new seed."
            ), true);
            return 1;
        } else {
            source.sendError(Text.literal("Folder not found or could not be deleted."));
            return 0;
        }
    }
}
