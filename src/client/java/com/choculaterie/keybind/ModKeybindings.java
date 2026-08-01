package com.choculaterie.keybind;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public final class ModKeybindings {
	public static KeyMapping OPEN_MENU_KEY_BINDING;

	public static void initialize() {
		OPEN_MENU_KEY_BINDING = KeyBindingHelper.registerKeyBinding(
			new KeyMapping(
				"key.ctrl-z.open_menu",
				GLFW.GLFW_KEY_Z,
				KeyMapping.Category.MISC
			)
		);
	}

	private ModKeybindings() {}
}
