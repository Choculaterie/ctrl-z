package com.choculaterie.mixin;

import com.choculaterie.history.BlockHistory;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.ticks.LevelTicks;
import net.minecraft.world.ticks.ScheduledTick;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelTicks.class)
public abstract class LevelTicksMixin {

	@Inject(method = "schedule", at = @At("HEAD"))
	private void ctrlz$captureSchedule(ScheduledTick<?> tick, CallbackInfo ci) {
		if (BlockHistory.isTracking() && tick.type() instanceof Block) {
			BlockHistory.watchBlock(tick.pos());
		}
	}
}
