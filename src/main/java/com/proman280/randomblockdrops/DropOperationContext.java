package com.proman280.randomblockdrops;

/** Thread-local guard used while Minecraft removes one player-broken multi-block structure. */
public final class DropOperationContext {
	// ===== Thread-local State =====
	private static final ThreadLocal<Integer> DEPTH = ThreadLocal.withInitial(() -> 0);
	private static final ThreadLocal<Boolean> EMITTED = ThreadLocal.withInitial(() -> false);

	// ===== Constructor =====
	private DropOperationContext() { }

	// ===== Lifecycle Methods =====
	public static void begin() { 
		if (DEPTH.get() == 0) EMITTED.set(false);
		DEPTH.set(DEPTH.get() + 1);
	}
	public static void end() { 
		DEPTH.set(DEPTH.get() - 1);
		if (DEPTH.get() == 0) {
			DEPTH.remove();
			EMITTED.remove();
		}
	}

	// ===== Query Methods =====
	public static boolean active() { return DEPTH.get() > 0; }
	public static boolean emitted() { return EMITTED.get(); }

	// ===== State Mutator =====
	public static void markEmitted() { EMITTED.set(true); }
}
