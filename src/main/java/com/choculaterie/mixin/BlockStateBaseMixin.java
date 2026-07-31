package com.choculaterie.mixin;

import com.choculaterie.history.BlockHistory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {

	@Unique
	private boolean ctrlz$tracking;

	@Unique
	private boolean ctrlz$triggerTracking;

	@Unique
	private static boolean ctrlz$beginIfWatched(BlockPos pos) {
		boolean tracking = BlockHistory.isBlockWatched(pos);
		if (tracking) {
			BlockHistory.continueBlockChain(pos);
		}
		return tracking;
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void ctrlz$begin(ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
		ctrlz$tracking = ctrlz$beginIfWatched(pos);
	}

	@Inject(method = "tick", at = @At("RETURN"))
	private void ctrlz$end(ServerLevel level, BlockPos pos, RandomSource random, CallbackInfo ci) {
		if (ctrlz$tracking) {
			BlockHistory.end();
			ctrlz$tracking = false;
		}
	}

	@Inject(method = "triggerEvent", at = @At("HEAD"))
	private void ctrlz$beginTrigger(Level level, BlockPos pos, int b0, int b1, CallbackInfoReturnable<Boolean> cir) {
		ctrlz$triggerTracking = ctrlz$beginIfWatched(pos);
	}

	@Inject(method = "triggerEvent", at = @At("RETURN"))
	private void ctrlz$endTrigger(Level level, BlockPos pos, int b0, int b1, CallbackInfoReturnable<Boolean> cir) {
		if (ctrlz$triggerTracking) {
			BlockHistory.end();
			ctrlz$triggerTracking = false;
		}
	}
}
