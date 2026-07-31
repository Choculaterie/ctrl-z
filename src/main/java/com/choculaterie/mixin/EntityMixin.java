package com.choculaterie.mixin;

import com.choculaterie.history.BlockHistory;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

	@Unique
	private boolean ctrlz$tickTracking;

	@Inject(method = "remove", at = @At("HEAD"))
	private void ctrlz$captureRemoval(Entity.RemovalReason reason, CallbackInfo ci) {
		Entity self = (Entity) (Object) this;
		if (reason.shouldDestroy() && BlockHistory.isTracking() && self.level() instanceof ServerLevel level) {
			BlockHistory.recordEntityRemoved(level, self);
		}
		BlockHistory.unwatch(self.getUUID());
	}

	@Inject(method = "tick", at = @At("HEAD"))
	private void ctrlz$beginTick(CallbackInfo ci) {
		Entity self = (Entity) (Object) this;
		ctrlz$tickTracking = !self.level().isClientSide() && BlockHistory.isWatched(self.getUUID());
		if (ctrlz$tickTracking) {
			BlockHistory.continueEntityChain();
		}
	}

	@Inject(method = "tick", at = @At("RETURN"))
	private void ctrlz$endTick(CallbackInfo ci) {
		if (ctrlz$tickTracking) {
			BlockHistory.end();
			ctrlz$tickTracking = false;
		}
	}
}
