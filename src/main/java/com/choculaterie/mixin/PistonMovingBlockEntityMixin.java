package com.choculaterie.mixin;

import com.choculaterie.history.BlockHistory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PistonMovingBlockEntity.class)
public abstract class PistonMovingBlockEntityMixin {

	@Unique
	private static boolean ctrlz$tracking;

	@Inject(method = "tick", at = @At("HEAD"))
	private static void ctrlz$begin(Level level, BlockPos pos, BlockState state, PistonMovingBlockEntity entity, CallbackInfo ci) {
		ctrlz$tracking = !level.isClientSide() && BlockHistory.isBlockWatched(pos);
		if (ctrlz$tracking) {
			BlockHistory.continueBlockEntityChain();
		}
	}

	@Inject(method = "tick", at = @At("RETURN"))
	private static void ctrlz$end(Level level, BlockPos pos, BlockState state, PistonMovingBlockEntity entity, CallbackInfo ci) {
		if (ctrlz$tracking) {
			if (entity.isRemoved()) {
				BlockHistory.unwatchBlock(pos);
			}
			BlockHistory.end();
			ctrlz$tracking = false;
		}
	}
}
