package com.choculaterie.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record UndoRedoPayload(boolean redo) implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<UndoRedoPayload> TYPE =
		new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("ctrl-z", "undo_redo"));

	public static final StreamCodec<FriendlyByteBuf, UndoRedoPayload> CODEC =
		StreamCodec.composite(ByteBufCodecs.BOOL, UndoRedoPayload::redo, UndoRedoPayload::new);

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
