package com.proman280.randomblockdrops.mixin;

import com.proman280.randomblockdrops.RandomBlockDrops;
import com.proman280.randomblockdrops.RandomDropState;
import com.proman280.randomblockdrops.DropOperationContext;
import com.proman280.randomblockdrops.ExplosionDropContext;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.BedPart;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(Block.class)
public abstract class BlockDropsMixin {

	@Inject(
			method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)V",
			at = @At("HEAD"),
			cancellable = true
	)
	private static void randomblockdrops$replaceLoot(
			BlockState state,
			Level level,
			BlockPos pos,
			BlockEntity blockEntity,
			Entity entity,
			ItemStack tool,
			CallbackInfo ci
	) {

		if (level.isClientSide() || level.getServer() == null) {
			return;
		}

		if (ExplosionDropContext.active()) {
			return;
		}

		if (entity instanceof Player player && player.isCreative()) {
			ci.cancel();
			return;
		}

		if (RandomDropState.wasCreativeBreak(pos)) {
			RandomDropState.clearBreak();
			ci.cancel();
			return;
		}

		if (state.getBlock() instanceof net.minecraft.world.level.block.LeavesBlock
				&& !(entity instanceof Player)) {
			return;
		}

		if (state.getBlock() instanceof DoorBlock
				&& state.getValue(DoorBlock.HALF) == DoubleBlockHalf.UPPER) {
			ci.cancel();
			return;
		}

		if (state.getBlock() instanceof DoublePlantBlock
				&& state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
			ci.cancel();
			return;
		}

		if (state.getBlock() instanceof BedBlock
				&& state.getValue(BedBlock.PART) == BedPart.FOOT) {
			ci.cancel();
			return;
		}

		if (DropOperationContext.active() && DropOperationContext.emitted()) {
			ci.cancel();
			return;
		}

		if (tool != null &&
				EnchantmentHelper.getItemEnchantmentLevel(
						level.registryAccess()
								.lookupOrThrow(Registries.ENCHANTMENT)
								.getOrThrow(Enchantments.SILK_TOUCH),
						tool) > 0) {
			return;
		}

		RandomDropState random = RandomBlockDrops.state(level.getServer());

		if (!random.shouldRandomize(state.getBlock())) {
			return;
		}

		ItemStack drop = random.dropFor(state.getBlock());

		if (!drop.isEmpty()) {
			Block.popResource(level, pos, drop);
		}

		DropOperationContext.markEmitted();
		ci.cancel();
	}
}