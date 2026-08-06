package com.proman280.randomblockdrops.mixin;

import com.proman280.randomblockdrops.DropOperationContext;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public abstract class BreakOperationMixin {
	// ===== Injection Points =====
	@Inject(method = "destroyBlock", at = @At("HEAD"))
	private void randomblockdrops$begin(BlockPos pos, CallbackInfoReturnable<Boolean> cir) { DropOperationContext.begin(); }
	@Inject(method = "destroyBlock", at = @At("RETURN"))
	private void randomblockdrops$end(BlockPos pos, CallbackInfoReturnable<Boolean> cir) { DropOperationContext.end(); }
}
