package com.choculaterie.history;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

record BlockChange(
	ResourceKey<Level> dimension,
	BlockPos pos,
	BlockState oldState,
	CompoundTag oldNbt,
	BlockState newState,
	CompoundTag newNbt
) {
}
