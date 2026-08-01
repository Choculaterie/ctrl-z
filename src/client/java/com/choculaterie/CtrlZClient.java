package com.choculaterie;

import com.choculaterie.gui.CtrlZScreen;
import com.choculaterie.keybind.KeyCombo;
import com.choculaterie.keybind.KeybindSettings;
import com.choculaterie.keybind.ModKeybindings;
import com.choculaterie.network.UndoRedoPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.input.KeyEvent;
import org.lwjgl.glfw.GLFW;

public class CtrlZClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		ModKeybindings.initialize();
		ClientTickEvents.END_CLIENT_TICK.register(CtrlZClient::onClientTick);
	}

	private static void onClientTick(Minecraft client) {
		while (ModKeybindings.OPEN_MENU_KEY_BINDING.consumeClick()) {
			if (!matchesOpenMenuKey(KeybindSettings.UNDO) && !matchesOpenMenuKey(KeybindSettings.REDO)) {
				toggleScreen(client);
			}
		}

		if (client.screen != null || client.player == null) {
			return;
		}
		if (KeybindSettings.isCreativeOnly() && !client.player.isCreative()) {
			return;
		}

		long windowHandle = GLFW.glfwGetCurrentContext();
		if (windowHandle == 0 || !ClientPlayNetworking.canSend(UndoRedoPayload.TYPE)) {
			return;
		}

		if (KeybindSettings.UNDO.consumeClick(windowHandle)) {
			ClientPlayNetworking.send(new UndoRedoPayload(false));
		}
		if (KeybindSettings.REDO.consumeClick(windowHandle)) {
			ClientPlayNetworking.send(new UndoRedoPayload(true));
		}
	}

	private static boolean matchesOpenMenuKey(KeyCombo combo) {
		int[] keys = combo.keys();
		if (keys.length == 0) {
			return false;
		}
		boolean containsOpenMenuKey = false;
		for (int key : keys) {
			if (ModKeybindings.OPEN_MENU_KEY_BINDING.matches(new KeyEvent(key, 0, 0))) {
				containsOpenMenuKey = true;
				break;
			}
		}
		if (!containsOpenMenuKey) {
			return false;
		}
		long windowHandle = GLFW.glfwGetCurrentContext();
		return windowHandle != 0 && combo.allDown(windowHandle);
	}

	private static void toggleScreen(Minecraft client) {
		if (client.screen instanceof CtrlZScreen) {
			client.setScreen(null);
		} else {
			client.setScreen(new CtrlZScreen());
		}
	}
}
