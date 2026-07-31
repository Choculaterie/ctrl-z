package com.choculaterie.history;

import net.minecraft.world.item.Item;

import java.util.UUID;

record HeldItemChange(
	UUID playerUuid,
	Item consumedItem,
	Item remainderItem
) {
}
