package com.choculaterie.history;

import net.minecraft.world.item.Item;

import java.util.UUID;

record ToolDurabilityChange(
	UUID playerUuid,
	Item item,
	int oldDamage,
	int newDamage,
	boolean broke
) {
}
