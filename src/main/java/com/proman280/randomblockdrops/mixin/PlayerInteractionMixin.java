package com.proman280.randomblockdrops.mixin;

import com.proman280.randomblockdrops.RandomBlockDrops;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayerGameMode;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerPlayerGameMode.class)
public class PlayerInteractionMixin {

	@Inject(
			method = "destroyBlock",
			at = @At("HEAD")
	)
	private void randomblockdrops$trackBlockBreak(
			BlockPos pos,
			CallbackInfoReturnable<Boolean> cir
	) {
		ServerPlayerGameMode gameMode = (ServerPlayerGameMode)(Object)this;

		RandomBlockDrops.setLastBreak(
				pos,
				gameMode.isCreative()
		);
	}
}