package com.proman280.randomblockdrops;

import com.proman280.randomblockdrops.command.RandomBlockDropsCommands;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Common, server-safe entry point. All gameplay state belongs to the server world. */
public final class RandomBlockDrops implements ModInitializer {
	// ===== Constants =====
	public static final String MOD_ID = "random-block-drops";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	// ===== Initialization =====
	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) ->
			RandomBlockDropsCommands.register(dispatcher));
		ServerLifecycleEvents.SERVER_STARTED.register(server -> state(server));
	}

	// ===== Public API =====
	public static RandomDropState state(MinecraftServer server) {
		return RandomDropState.get(server);
	}

	public static void setLastBreak(BlockPos pos, boolean isCreative) {
		RandomDropState.setLastBreak(pos, isCreative);
	}
}
