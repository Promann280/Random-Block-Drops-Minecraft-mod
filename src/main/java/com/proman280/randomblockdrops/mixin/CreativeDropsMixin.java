package com.proman280.randomblockdrops.mixin;

import com.proman280.randomblockdrops.DropOperationContext;
import com.proman280.randomblockdrops.RandomBlockDrops;
import com.proman280.randomblockdrops.RandomDropState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Creative destruction bypasses Block.playerDestroy, so handle the successful operation here. */
@Mixin(ServerPlayerGameMode.class)
public abstract class CreativeDropsMixin {
	// ===== Shadowed Fields =====
	@Shadow protected net.minecraft.server.level.ServerLevel level;
	@Shadow protected net.minecraft.server.level.ServerPlayer player;
	@Shadow public abstract boolean isCreative();

	// ===== Unique State =====
	@Unique private BlockState randomblockdrops$pending;

	// ===== Injection Points =====
	@Inject(method = "destroyBlock", at = @At("HEAD"))
	private void randomblockdrops$capture(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		randomblockdrops$pending = isCreative() ? level.getBlockState(pos) : null;
	}
	@Inject(method = "destroyBlock", at = @At("RETURN"))
	private void randomblockdrops$cleanup(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
		// ===== In creative mode, prevent any drops from occurring =====
		if (isCreative() && cir.getReturnValue() && randomblockdrops$pending != null) {
			DropOperationContext.begin();
			DropOperationContext.markEmitted();
			DropOperationContext.end();
		}
		// ===== Cleanup =====
		randomblockdrops$pending = null;
	}
}
