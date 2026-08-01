package com.choculaterie.mixin;

import com.choculaterie.history.BlockHistory;
import net.minecraft.world.level.ServerExplosion;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerExplosion.class)
public abstract class ServerExplosionMixin {

	@Inject(method = "explode", at = @At("HEAD"))
	private void ctrlz$begin(CallbackInfo ci) {
		BlockHistory.begin();
	}

	@Inject(method = "explode", at = @At("RETURN"))
	private void ctrlz$end(CallbackInfo ci) {
		BlockHistory.end();
	}
}
