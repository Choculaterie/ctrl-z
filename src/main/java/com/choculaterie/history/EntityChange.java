package com.choculaterie.history;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

import java.util.UUID;

record EntityChange(
	ResourceKey<Level> dimension,
	UUID entityId,
	CompoundTag oldNbt,
	CompoundTag newNbt
) {
}
