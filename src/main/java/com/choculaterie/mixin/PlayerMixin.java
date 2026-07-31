package com.choculaterie.mixin;

import com.choculaterie.history.BlockHistory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {

	@Inject(method = "attack", at = @At("HEAD"))
	private void ctrlz$beginAttack(Entity target, CallbackInfo ci) {
		Player self = (Player) (Object) this;
		if (!self.level().isClientSide()) {
			BlockHistory.begin();
		}
	}

	@Inject(method = "attack", at = @At("RETURN"))
	private void ctrlz$endAttack(Entity target, CallbackInfo ci) {
		Player self = (Player) (Object) this;
		if (!self.level().isClientSide()) {
			BlockHistory.end();
		}
	}
}
