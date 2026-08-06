package com.proman280.randomblockdrops.client;

import net.minecraft.client.gui.components.Button;
import com.proman280.randomblockdrops.RandomBlockDrops;
import com.proman280.randomblockdrops.RandomDropState;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

/**
 * A deliberately dependency-free Mod Menu screen. Actions use server commands, so the
 * server remains authoritative and the same controls work for clients without this UI.
 */
final class RandomBlockDropsScreen extends Screen {
	// ===== Instance Fields =====
	private final Screen parent;
	private boolean enabled = true;

	// ===== Constructor =====
	RandomBlockDropsScreen(Screen parent) { super(Component.literal("Random Block Drops")); this.parent = parent; }

	// ===== Screen Initialization =====
	@Override protected void init() {
		loadSingleplayerState();
		int left = width / 2 - 110;
		addRenderableWidget(Button.builder(Component.literal("Randomize Loot Pool"), b -> ModMenuIntegration.command("rbd randomize")).bounds(left, 64, 220, 22).build());
		addRenderableWidget(Button.builder(label("Random Drops", enabled), b -> { enabled = !enabled; toggle(b, "Random Drops", enabled, "rbd toggle"); }).bounds(left, 94, 220, 22).build());
	}

	// ===== Helper Methods =====
	private void loadSingleplayerState() { if (minecraft.getSingleplayerServer() != null) { RandomDropState state = RandomBlockDrops.state(minecraft.getSingleplayerServer()); enabled = state.enabled(); } }
	private Component label(String name, boolean on) { return Component.literal(name + ": " + (on ? "ON" : "OFF")).withStyle(on ? ChatFormatting.GREEN : ChatFormatting.RED); }
	private void toggle(Button button, String label, boolean on, String command) { ModMenuIntegration.command(command); button.setMessage(label(label, on)); }
	// ===== Rendering =====
	@Override public void render(net.minecraft.client.gui.GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		renderBackground(graphics, mouseX, mouseY, delta); graphics.drawCenteredString(font, title, width / 2, 18, 0xFFFFFF);
		graphics.drawCenteredString(font, Component.literal("Server-authoritative settings • Operators only"), width / 2, 34, 0xA0A0A0);
		super.render(graphics, mouseX, mouseY, delta);
	}

	// ===== Screen Lifecycle =====
	@Override public void onClose() { minecraft.setScreen(parent); }
}
