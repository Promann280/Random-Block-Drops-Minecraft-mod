package com.proman280.randomblockdrops.mixin;

import com.proman280.randomblockdrops.ExplosionDropContext;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.BiConsumer;

/** Keeps the shared drop hook from replacing vanilla loot while an explosion destroys a block. */
@Mixin(BlockBehaviour.class)
public abstract class ExplosionDropsMixin {
	// ===== Injection Points =====
	@Inject(method = "onExplosionHit(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Explosion;Ljava/util/function/BiConsumer;)V", at = @At("HEAD"))
	private void randomblockdrops$beginExplosionDrop(BlockState state, Level level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer, CallbackInfo ci) {
		ExplosionDropContext.begin();
	}

	@Inject(method = "onExplosionHit(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/Level;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/Explosion;Ljava/util/function/BiConsumer;)V", at = @At("RETURN"))
	private void randomblockdrops$endExplosionDrop(BlockState state, Level level, BlockPos pos, Explosion explosion, BiConsumer<ItemStack, BlockPos> dropConsumer, CallbackInfo ci) {
		ExplosionDropContext.end();
	}
}
