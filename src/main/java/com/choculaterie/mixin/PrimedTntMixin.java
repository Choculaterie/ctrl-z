package com.choculaterie.mixin;

import com.choculaterie.history.BlockHistory;
import net.minecraft.world.entity.item.PrimedTnt;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PrimedTnt.class)
public abstract class PrimedTntMixin {

	@Unique
	private boolean ctrlz$tracking;

	@Inject(method = "tick", at = @At("HEAD"))
	private void ctrlz$begin(CallbackInfo ci) {
		PrimedTnt self = (PrimedTnt) (Object) this;
		ctrlz$tracking = !self.level().isClientSide() && BlockHistory.isWatched(self.getUUID());
		if (ctrlz$tracking) {
			BlockHistory.continueEntityChain();
		}
	}

	@Inject(method = "tick", at = @At("RETURN"))
	private void ctrlz$end(CallbackInfo ci) {
		if (ctrlz$tracking) {
			BlockHistory.end();
			ctrlz$tracking = false;
		}
	}
}
