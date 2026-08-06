package com.proman280.randomblockdrops.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Prevents doors from calling popResource multiple times. */
@Mixin(DoorBlock.class)
public abstract class DoorDropsMixin {
	@Inject(method = "popResource", at = @At("HEAD"), cancellable = true)
	private void randomblockdrops$preventDoubleDrops(Level level, BlockPos pos, BlockState state, CallbackInfo ci) {
		// Let BlockDropsMixin handle the random drop via dropResources
		ci.cancel();
	}
}
