package com.proman280.randomblockdrops.command;

import com.mojang.brigadier.CommandDispatcher;
import com.proman280.randomblockdrops.RandomBlockDrops;
import com.proman280.randomblockdrops.RandomDropState;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;

/** Native commands are the administration fallback for clients without the optional UI. */
public final class RandomBlockDropsCommands {
	// ===== Constructor =====
	private RandomBlockDropsCommands() { }

	// ===== Registration =====
	public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
		var root = Commands.literal("randomblockdrops")
			.then(Commands.literal("randomize").requires(source -> source.hasPermission(2)).executes(ctx -> randomize(ctx.getSource())))
			.then(Commands.literal("toggle").requires(source -> source.hasPermission(2)).executes(ctx -> toggle(ctx.getSource())));
		dispatcher.register(root);
		dispatcher.register(Commands.literal("rbd").redirect(root.build()));
	}

	// ===== Command Handlers =====
	private static int randomize(CommandSourceStack source) {
		RandomDropState state = RandomBlockDrops.state(source.getServer());
		state.regenerate(RandomSource.create().nextLong());
		source.sendSuccess(() -> Component.literal("Random Block Drops loot pool randomized."), true);
		return 1;
	}
	private static int toggle(CommandSourceStack source) {
		RandomDropState state = RandomBlockDrops.state(source.getServer());
		boolean enabled = !state.enabled();
		state.setEnabled(enabled);
		source.sendSuccess(() -> Component.literal("Random Block Drops: " + (enabled ? "enabled" : "disabled") + "."), true);
		return 1;
	}
}
