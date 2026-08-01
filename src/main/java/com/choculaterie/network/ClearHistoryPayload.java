package com.choculaterie.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record ClearHistoryPayload() implements CustomPacketPayload {
	public static final CustomPacketPayload.Type<ClearHistoryPayload> TYPE =
		new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath("ctrl-z", "clear_history"));

	public static final StreamCodec<FriendlyByteBuf, ClearHistoryPayload> CODEC =
		StreamCodec.unit(new ClearHistoryPayload());

	@Override
	public Type<? extends CustomPacketPayload> type() {
		return TYPE;
	}
}
