package com.choculaterie.mixin;

import com.choculaterie.history.BlockHistory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {

	@Inject(method = "addFreshEntity", at = @At("RETURN"))
	private void ctrlz$captureAdd(Entity entity, CallbackInfoReturnable<Boolean> cir) {
		if (!cir.getReturnValue() || !BlockHistory.isTracking()) {
			return;
		}
		ServerLevel self = (ServerLevel) (Object) this;
		BlockHistory.recordEntityAdded(self, entity);
	}

	@Inject(method = "blockEvent", at = @At("HEAD"))
	private void ctrlz$watchBlockEvent(BlockPos pos, Block block, int paramA, int paramB, CallbackInfo ci) {
		if (BlockHistory.isTracking()) {
			BlockHistory.watchBlock(pos);
		}
	}
}
