package com.proman280.randomblockdrops;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LeavesBlock;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

// Thread-local storage for tracking last break context
class BreakContext {
	private static final ThreadLocal<BlockPos> lastPos = new ThreadLocal<>();
	private static final ThreadLocal<Boolean> lastWasCreative = new ThreadLocal<>();

	static void set(BlockPos pos, boolean isCreative) {
		lastPos.set(pos);
		lastWasCreative.set(isCreative);
	}

	static BlockPos getLastPos() {
		return lastPos.get();
	}

	static boolean getLastWasCreative() {
		Boolean val = lastWasCreative.get();
		return val != null && val;
	}

	static void clear() {
		lastPos.remove();
		lastWasCreative.remove();
	}
}

/** Saved once per world. IDs, rather than raw registry objects, make the data portable. */
public final class RandomDropState extends SavedData {
	// ===== Constants =====
	private static final String DATA_NAME = "randomblockdrops";
	private static final Set<String> OPERATOR_BLOCKS = Set.of(
		"minecraft:command_block", "minecraft:chain_command_block", "minecraft:repeating_command_block",
		"minecraft:structure_block", "minecraft:structure_void", "minecraft:barrier", "minecraft:jigsaw", "minecraft:light",
		"minecraft:water", "minecraft:flowing_water", "minecraft:lava", "minecraft:flowing_lava");

	// ===== Instance Fields =====
	private final Map<ResourceLocation, ResourceLocation> mappings = new HashMap<>();
	private long seed;
	private boolean enabled = true;

	// ===== Factory Methods =====
	public static RandomDropState get(MinecraftServer server) {
		RandomDropState state = server.overworld().getDataStorage().computeIfAbsent(
			new SavedData.Factory<>(RandomDropState::new, RandomDropState::load, null), DATA_NAME);
		state.ensureCurrentPool();
		return state;
	}

	// ===== Constructors =====
	private RandomDropState() { regenerate(RandomSource.create().nextLong()); }

	// ===== Loading/Saving =====
	private static RandomDropState load(CompoundTag tag, HolderLookup.Provider registries) {
		RandomDropState state = new RandomDropState();
		state.seed = tag.getLong("seed");
		state.enabled = !tag.contains("enabled") || tag.getBoolean("enabled");
		state.mappings.clear();
		for (Tag entry : tag.getList("mappings", Tag.TAG_COMPOUND)) {
			CompoundTag pair = (CompoundTag) entry;
			ResourceLocation source = ResourceLocation.tryParse(pair.getString("source"));
			ResourceLocation target = ResourceLocation.tryParse(pair.getString("target"));
			if (source != null && target != null) state.mappings.put(source, target);
		}
		return state;
	}

	@Override
	public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
		tag.putLong("seed", seed);
		tag.putBoolean("enabled", enabled);
		ListTag pairs = new ListTag();
		mappings.forEach((source, target) -> { CompoundTag pair = new CompoundTag(); pair.putString("source", source.toString()); pair.putString("target", target.toString()); pairs.add(pair); });
		tag.put("mappings", pairs);
		return tag;
	}

	// ===== Public Query Methods =====
	public boolean shouldRandomize(Block block) { return enabled && mappings.containsKey(BuiltInRegistries.BLOCK.getKey(block)); }
	public ItemStack dropFor(Block block) {
		ResourceLocation target = mappings.get(BuiltInRegistries.BLOCK.getKey(block));
		Block targetBlock = target == null ? null : BuiltInRegistries.BLOCK.get(target);
		return targetBlock == null ? ItemStack.EMPTY : new ItemStack(targetBlock.asItem());
	}
	public boolean enabled() { return enabled; }
	public long seed() { return seed; }
	public int size() { return mappings.size(); }
	public Map<ResourceLocation, ResourceLocation> mappings() { return Collections.unmodifiableMap(mappings); }

	// ===== Public Mutator Methods =====
	public void setEnabled(boolean value) { enabled = value; setDirty(); }
	public static void setLastBreak(BlockPos pos, boolean isCreative) {
		BreakContext.set(pos, isCreative);
	}
	public static boolean wasCreativeBreak(BlockPos pos) {
		return pos.equals(BreakContext.getLastPos()) && BreakContext.getLastWasCreative();
	}
	public static void clearBreak() {
		BreakContext.clear();
	}
	public void regenerate(long newSeed) {
		seed = newSeed;
		List<ResourceLocation> ids = eligibleIds();
		List<ResourceLocation> shuffled = new ArrayList<>(ids);
		Collections.shuffle(shuffled, new java.util.Random(seed));
		mappings.clear();
		for (int i = 0; i < ids.size(); i++) mappings.put(ids.get(i), shuffled.get(i));
		setDirty();

	}

	// ===== Private Helper Methods =====
	private void ensureCurrentPool() {
		Set<ResourceLocation> eligible = new HashSet<>(eligibleIds());
		if (!eligible.equals(mappings.keySet()) || !eligible.equals(new HashSet<>(mappings.values()))) regenerate(seed);
	}
	private List<ResourceLocation> eligibleIds() {
		List<ResourceLocation> result = new ArrayList<>();
		for (Block block : BuiltInRegistries.BLOCK) {
			ResourceLocation id = BuiltInRegistries.BLOCK.getKey(block);
			if (block.asItem() == Items.AIR || OPERATOR_BLOCKS.contains(id.toString())) continue;
			result.add(id);
		}
		Collections.sort(result);
		return result;
	}
}
