package net.treset.adaptiveview;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.treset.adaptiveview.commands.ConfigCommandHandler;
import net.treset.adaptiveview.commands.LockCommandHandler;
import net.treset.adaptiveview.commands.NotificationCommandHandler;
import net.treset.adaptiveview.config.Config;
import net.treset.adaptiveview.distance.ServerHandler;
import net.treset.adaptiveview.distance.ViewDistanceHandler;
import net.treset.adaptiveview.tools.TextTools;
import net.treset.adaptiveview.unlocking.LockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdaptiveViewMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("adaptiveview");

	private static final Config config = Config.loadOrDefault();
	private static final NotificationCommandHandler notificationCommandHandler = new NotificationCommandHandler(config);
	private static final ConfigCommandHandler configCommandHandler = new ConfigCommandHandler(config);
	private static final ViewDistanceHandler viewDistanceHandler = new ViewDistanceHandler(config);
	private static final LockManager lockManager = new LockManager(config, viewDistanceHandler);
	private static final LockCommandHandler lockCommandHandler = new LockCommandHandler(config, lockManager);
	private static final ServerHandler serverHandler = new ServerHandler(config, lockManager, viewDistanceHandler);
	private static MinecraftServer server;

	private static boolean client = false;

	public static Config getConfig() {
		return config;
	}

	public static MinecraftServer getServer() {
		return server;
	}

	public static boolean isClient() {
		return client;
	}

	public static void setClient(boolean client) {
		AdaptiveViewMod.client = client;
	}

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> this.registerCommands(dispatcher, environment));

		ServerLifecycleEvents.SERVER_STARTED.register((s) -> server = s);
		ServerTickEvents.END_SERVER_TICK.register(serverHandler::onTick);
	}

	private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandManager.RegistrationEnvironment environment) {
		if(!environment.dedicated && !config.isAllowOnClient()) return;
		LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal("adaptiveview")
				.executes(this::status)
				.then(CommandManager.literal("status")
						.executes(this::status)
				);
		notificationCommandHandler.registerCommands(builder);
		configCommandHandler.registerCommands(builder);
		lockCommandHandler.registerCommands(builder);

		dispatcher.register(builder);
	}

	private int status(CommandContext<ServerCommandSource> ctx) {
		TextTools.replyFormatted(ctx, "View Distance: $b%s chunks", ViewDistanceHandler.getViewDistance());
		TextTools.replyFormatted(ctx, "Simulation Distance: $b%s chunks", ViewDistanceHandler.getSimDistance());
        TextTools.replyFormatted(ctx, "Chunk-Ticking Distance: $b%s chunks", ViewDistanceHandler.getChunkTickingDistance());
		return 1;
	}
}
