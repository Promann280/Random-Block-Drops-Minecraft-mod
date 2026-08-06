package com.proman280.randomblockdrops.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/** Exposes a UI only when Mod Menu is installed on the local client. */
public final class ModMenuIntegration implements ModMenuApi {
	// ===== ModMenu API Implementation =====
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> new RandomBlockDropsScreen(parent);
	}

	// ===== Command Helper =====
	static void command(String command) {
		if (Minecraft.getInstance().player != null) Minecraft.getInstance().player.connection.sendCommand(command);
	}
}
