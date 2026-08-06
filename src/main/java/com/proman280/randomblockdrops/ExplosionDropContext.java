package com.proman280.randomblockdrops;

/** Marks the short vanilla loot operation performed while an explosion destroys a block. */
public final class ExplosionDropContext {
	// ===== Thread-local State =====
	private static final ThreadLocal<Integer> DEPTH = ThreadLocal.withInitial(() -> 0);

	// ===== Constructor =====
	private ExplosionDropContext() { }

	// ===== Lifecycle Methods =====
	public static void begin() { DEPTH.set(DEPTH.get() + 1); }
	public static void end() {
		int depth = DEPTH.get() - 1;
		if (depth <= 0) DEPTH.remove();
		else DEPTH.set(depth);
	}

	// ===== Query Method =====
	public static boolean active() { return DEPTH.get() > 0; }
}
