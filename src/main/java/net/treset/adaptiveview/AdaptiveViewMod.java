package net.treset.adaptiveview;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.treset.adaptiveview.commands.ConfigCommandHandler;
import net.treset.adaptiveview.commands.LockCommandHandler;
import net.treset.adaptiveview.config.Config;
import net.treset.adaptiveview.distance.ServerTickHandler;
import net.treset.adaptiveview.distance.ViewDistanceHandler;
import net.treset.adaptiveview.tools.TextTools;
import net.treset.adaptiveview.unlocking.LockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdaptiveViewMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("adaptiveview");

	private static final Config config = Config.load();
	//TODO
	private static final ConfigCommandHandler configCommandHandler = new ConfigCommandHandler(null);
	private static final ViewDistanceHandler viewDistanceHandler = new ViewDistanceHandler(null);
	private static final LockManager lockManager = new LockManager(null, viewDistanceHandler);
	private static final LockCommandHandler lockCommandHandler = new LockCommandHandler(null, lockManager);
	private static final ServerTickHandler serverTickHandler = new ServerTickHandler(null, lockManager, viewDistanceHandler);
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
		ServerTickEvents.END_SERVER_TICK.register(serverTickHandler::onTick);
	}

	private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandManager.RegistrationEnvironment environment) {
		if(!environment.dedicated && !config.isAllowOnClient()) return;
		dispatcher.register(CommandManager.literal("adaptiveview")
				.requires(source -> source.hasPermissionLevel(2))
				.executes(this::adaptiveview)
				.then(CommandManager.literal("config")
						.executes(configCommandHandler::base)
						.then(CommandManager.literal("updateInterval")
								.executes(configCommandHandler::updateInterval)
								.then(CommandManager.argument("interval", IntegerArgumentType.integer(1, 72000))
										.executes(configCommandHandler::updateIntervalInterval)
								)
						)
						.then(CommandManager.literal("targetMspt")
								.executes(configCommandHandler::targetMspt)
								.then(CommandManager.argument("minMspt", IntegerArgumentType.integer(1, 499))
										.then(CommandManager.argument("maxMspt", IntegerArgumentType.integer(2, 500))
												.executes(configCommandHandler::targetMsptMinMsptMaxMspt)
										)
								)
						)
						.then(CommandManager.literal("targetMsptAggressive")
								.executes(configCommandHandler::targetMsptAggressive)
								.then(CommandManager.argument("minMsptAggressive", IntegerArgumentType.integer(1, 499))
										.then(CommandManager.argument("maxMsptAggressive", IntegerArgumentType.integer(2, 500))
												.executes(configCommandHandler::targetMsptAggressiveMinMsptMaxMsptAggressive)
										)
								)
						)
						.then(CommandManager.literal("viewDistanceRange")
								.executes(configCommandHandler::viewDistanceRange)
								.then(CommandManager.argument("minVD", IntegerArgumentType.integer(2, 31))
										.then(CommandManager.argument("maxVD", IntegerArgumentType.integer(3, 32))
												.executes(configCommandHandler::viewDistanceRangeMinVDMaxVD)
										)
								)
						)
				)
				.then(CommandManager.literal("lock")
						.executes(lockCommandHandler::base)
						.then(CommandManager.literal("set")
								.executes(lockCommandHandler::set)
								.then(CommandManager.argument("chunks", IntegerArgumentType.integer(2, 32))
										.executes(lockCommandHandler::setChunks)
										.then(CommandManager.literal("timeout")
												.executes(lockCommandHandler::setChunksTimeout)
												.then(CommandManager.argument("ticks", IntegerArgumentType.integer(1))
														.executes(lockCommandHandler::setChunksTimeoutTicks)
												)
										)
										.then(CommandManager.literal("player")
												.executes(lockCommandHandler::setChunksPlayer)
												.then(CommandManager.argument("player", EntityArgumentType.player())
														.then(CommandManager.literal("disconnect")
																.executes(lockCommandHandler::setChunksPlayerDisconnect)
														)
														.then(CommandManager.literal("move")
																.executes(lockCommandHandler::setChunksPlayerMove)
														)
												)
										)
								)
						)
						.then(CommandManager.literal("unlock")
								.executes(lockCommandHandler::unlock)
								.then(CommandManager.literal("clear")
										.executes(lockCommandHandler::clear)
								)
						)
				)
		);
	}

	private int adaptiveview(CommandContext<ServerCommandSource> ctx) {
		TextTools.replyFormatted(ctx, String.format("?iThe current view distance is ?B%s chunks", viewDistanceHandler.getViewDistance()), false);
		return 1;
	}
}
