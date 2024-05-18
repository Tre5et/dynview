package net.treset.adaptiveview;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
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
import net.treset.adaptiveview.distance.ServerHandler;
import net.treset.adaptiveview.distance.ViewDistanceHandler;
import net.treset.adaptiveview.tools.TextTools;
import net.treset.adaptiveview.unlocking.LockManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdaptiveViewMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("adaptiveview");

	private static final Config config = Config.loadOrDefault();
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
		dispatcher.register(CommandManager.literal("adaptiveview")
				.executes(this::status)
				.then(CommandManager.literal("status")
						.executes(this::status)
				)
				.then(CommandManager.literal("notifications")
						.executes(configCommandHandler::notifications)
						.then(CommandManager.literal("subscribe")
								.executes(configCommandHandler::notificationsSubscribe)
						)
						.then(CommandManager.literal("unsubscribe")
								.executes(configCommandHandler::notificationsUnsubscribe)
						)
				)
				.then(CommandManager.literal("config")
						.requires(source -> source.hasPermissionLevel(2))
						.executes(configCommandHandler::list)
						.then(CommandManager.literal("status")
								.executes(configCommandHandler::list)
						)
						.then(CommandManager.literal("reload")
								.executes(configCommandHandler::reload)
						)
						.then(CommandManager.literal("update_rate")
								.executes(configCommandHandler::updateRate)
								.then(CommandManager.argument("ticks", IntegerArgumentType.integer(1, 72000))
										.executes(configCommandHandler::setUpdateRate)
								)
						)
						.then(CommandManager.literal("max_view_distance")
								.executes(configCommandHandler::maxView)
								.then(CommandManager.argument("chunks", IntegerArgumentType.integer(2, 32))
										.executes(configCommandHandler::setMaxView)
								)
						)
						.then(CommandManager.literal("min_view_distance")
								.executes(configCommandHandler::minView)
								.then(CommandManager.argument("chunks", IntegerArgumentType.integer(2, 32))
										.executes(configCommandHandler::setMinView)
								)
						)
						.then(CommandManager.literal("broadcast_to_ops")
								.executes(configCommandHandler::broadcast)
								.then(CommandManager.literal("enable")
										.executes(configCommandHandler::broadcastEnable)
								)
								.then(CommandManager.literal("disable")
										.executes(configCommandHandler::broadcastDisable)
								)
						)
						.then(CommandManager.literal("rules")
								.executes(configCommandHandler::rules)
								.then(CommandManager.argument("index", IntegerArgumentType.integer(1, 100))
										.executes(configCommandHandler::ruleIndex)
										.then(CommandManager.literal("remove")
												.executes(configCommandHandler::ruleRemove)
										)
										.then(CommandManager.literal("condition")
												.executes(configCommandHandler::ruleCondition)
												.then(CommandManager.literal("type")
														.executes(configCommandHandler::ruleType)
														.then(CommandManager.literal("mspt")
																.executes(configCommandHandler::ruleTypeSetMspt)
														)
														.then(CommandManager.literal("memory")
																.executes(configCommandHandler::ruleTypeSetMemory)
														)
														.then(CommandManager.literal("players")
																.executes(configCommandHandler::ruleTypeSetPlayers)
														)
												)
												.then(CommandManager.literal("value")
														.executes(configCommandHandler::ruleValue)
														.then(CommandManager.argument("value", StringArgumentType.word())
																.executes(configCommandHandler::ruleSetValue)
														)
														.then(CommandManager.literal("clear")
																.executes(configCommandHandler::ruleClearValue)
														)
												)
												.then(CommandManager.literal("min")
														.executes(configCommandHandler::ruleMin)
														.then(CommandManager.argument("min", IntegerArgumentType.integer(0))
																.executes(configCommandHandler::ruleSetMin)
														)
														.then(CommandManager.literal("clear")
																.executes(configCommandHandler::ruleClearMin)
														)
												)
												.then(CommandManager.literal("max")
														.executes(configCommandHandler::ruleMax)
														.then(CommandManager.argument("max", IntegerArgumentType.integer(0))
																.executes(configCommandHandler::ruleSetMax)
														)
														.then(CommandManager.literal("clear")
																.executes(configCommandHandler::ruleClearMax)
														)
												)
										)
										.then(CommandManager.literal("action")
												.executes(configCommandHandler::ruleAction)
												.then(CommandManager.literal("update_rate")
														.executes(configCommandHandler::ruleUpdateRate)
														.then(CommandManager.argument("ticks", IntegerArgumentType.integer(1, 72000))
																.executes(configCommandHandler::ruleSetUpdateRate)
														)
														.then(CommandManager.literal("clear")
																.executes(configCommandHandler::ruleClearUpdateRate)
														)
												)
												.then(CommandManager.literal("step")
														.executes(configCommandHandler::ruleStep)
														.then(CommandManager.argument("step", IntegerArgumentType.integer(-32, 32))
																.executes(configCommandHandler::ruleSetStep)
														)
														.then(CommandManager.literal("clear")
																.executes(configCommandHandler::ruleClearStep)
														)
												)
												.then(CommandManager.literal("step_after")
														.executes(configCommandHandler::ruleStepAfter)
														.then(CommandManager.argument("step_after", IntegerArgumentType.integer(1, 100))
																.executes(configCommandHandler::ruleSetStepAfter)
														)
														.then(CommandManager.literal("clear")
																.executes(configCommandHandler::ruleClearStepAfter)
														)
												)
												.then(CommandManager.literal("min_view_distance")
														.executes(configCommandHandler::ruleMinView)
														.then(CommandManager.argument("chunks", IntegerArgumentType.integer(2, 32))
																.executes(configCommandHandler::ruleSetMinView)
														)
														.then(CommandManager.literal("clear")
																.executes(configCommandHandler::ruleClearMinView)
														)
												)
												.then(CommandManager.literal("max_view_distance")
														.executes(configCommandHandler::ruleMaxView)
														.then(CommandManager.argument("chunks", IntegerArgumentType.integer(2, 32))
																.executes(configCommandHandler::ruleSetMaxView)
														)
														.then(CommandManager.literal("clear")
																.executes(configCommandHandler::ruleClearMaxView)
														)
												)
										)
								)
								.then(CommandManager.literal("add")
										.then(CommandManager.literal("mspt")
												.then(CommandManager.literal("min")
														.then(CommandManager.argument("min", IntegerArgumentType.integer(0, 1000))
																.executes(configCommandHandler::addMsptMin)
														)
												)
												.then(CommandManager.literal("max")
														.then(CommandManager.argument("min", IntegerArgumentType.integer(0, 1000))
																.executes(configCommandHandler::addMsptMax)
														)
												)
												.then(CommandManager.literal("range")
														.then(CommandManager.argument("min", IntegerArgumentType.integer(0, 1000))
																.then(CommandManager.argument("max", IntegerArgumentType.integer(0, 1000))
																		.executes(configCommandHandler::addMsptRange)
																)
														)
												)
										)
										.then(CommandManager.literal("memory")
												.then(CommandManager.literal("min")
														.then(CommandManager.argument("min", IntegerArgumentType.integer(0, 100))
																.executes(configCommandHandler::addMemoryMin)
														)
												)
												.then(CommandManager.literal("max")
														.then(CommandManager.argument("min", IntegerArgumentType.integer(0, 100))
																.executes(configCommandHandler::addMemoryMax)
														)
												)
												.then(CommandManager.literal("range")
														.then(CommandManager.argument("min", IntegerArgumentType.integer(0, 100))
																.then(CommandManager.argument("max", IntegerArgumentType.integer(0, 100))
																		.executes(configCommandHandler::addMemoryRange)
																)
														)
												)
										)
										.then(CommandManager.literal("players")
												.then(CommandManager.literal("min")
														.then(CommandManager.argument("min", IntegerArgumentType.integer(0, 1000))
																.executes(configCommandHandler::addPlayersMin)
														)
												)
												.then(CommandManager.literal("max")
														.then(CommandManager.argument("min", IntegerArgumentType.integer(0, 1000))
																.executes(configCommandHandler::addPlayersMax)
														)
												)
												.then(CommandManager.literal("range")
														.then(CommandManager.argument("min", IntegerArgumentType.integer(0, 1000))
																.then(CommandManager.argument("max", IntegerArgumentType.integer(0, 1000))
																		.executes(configCommandHandler::addPlayersRange)
																)
														)
												)
												.then(CommandManager.literal("name")
														.then(CommandManager.argument("name", StringArgumentType.word())
																.executes(configCommandHandler::addPlayersName)
														)
												)
										)
								)
						)
				)
				.then(CommandManager.literal("lock")
						.executes(lockCommandHandler::status)
						.then(CommandManager.literal("status")
								.executes(lockCommandHandler::status)
						)
						.then(CommandManager.literal("set")
								.requires(source -> source.hasPermissionLevel(2))
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
								.requires(source -> source.hasPermissionLevel(2))
								.executes(lockCommandHandler::unlock)
								.then(CommandManager.literal("clear")
										.executes(lockCommandHandler::clear)
								)
						)
				)
		);
	}

	private int status(CommandContext<ServerCommandSource> ctx) {
		TextTools.replyFormatted(ctx, String.format("The current view distance is $b%s chunks", ViewDistanceHandler.getViewDistance()), false);
		return 1;
	}
}
