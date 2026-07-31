package com.choculaterie.mixin;

import com.choculaterie.history.BlockHistory;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelChunk.class)
public abstract class LevelChunkMixin {

	@Inject(method = "setBlockEntity", at = @At("RETURN"))
	private void ctrlz$watchPistonMove(BlockEntity blockEntity, CallbackInfo ci) {
		if (blockEntity instanceof PistonMovingBlockEntity && BlockHistory.isTracking()) {
			BlockHistory.watchBlock(blockEntity.getBlockPos());
		}
	}
}
