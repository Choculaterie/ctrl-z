package com.choculaterie.mixin;

import com.choculaterie.history.BlockHistory;
import net.minecraft.world.level.Explosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Explosion.class)
public abstract class ServerExplosionMixin {

	@Inject(method = "explode", at = @At("HEAD"))
	private void ctrlz$begin(CallbackInfo ci) {
		BlockHistory.begin();
	}

	@Inject(method = "finalizeExplosion", at = @At("RETURN"))
	private void ctrlz$end(boolean particles, CallbackInfo ci) {
		BlockHistory.end();
	}
}
