package com.choculaterie.mixin;

import com.choculaterie.history.BlockHistory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FluidState.class)
public abstract class FluidStateMixin {

	@Unique
	private boolean ctrlz$tracking;

	@Inject(method = "tick", at = @At("HEAD"))
	private void ctrlz$begin(ServerLevel level, BlockPos pos, BlockState blockState, CallbackInfo ci) {
		ctrlz$tracking = BlockHistory.isBlockWatched(pos);
		if (ctrlz$tracking) {
			BlockHistory.continueBlockChain(pos);
		}
	}

	@Inject(method = "tick", at = @At("RETURN"))
	private void ctrlz$end(ServerLevel level, BlockPos pos, BlockState blockState, CallbackInfo ci) {
		if (ctrlz$tracking) {
			BlockHistory.end();
			ctrlz$tracking = false;
		}
	}
}
