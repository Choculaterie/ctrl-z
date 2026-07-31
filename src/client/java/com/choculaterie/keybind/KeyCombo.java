package com.choculaterie.keybind;

import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public final class KeyCombo {
	private int[] keys;
	private boolean wasActive;

	public KeyCombo(int... keys) {
		set(keys);
	}

	public void set(int... keys) {
		this.keys = keys.clone();
		this.wasActive = false;
	}

	public int[] keys() {
		return keys;
	}

	public boolean containsKey(int key) {
		for (int k : keys) {
			if (k == key) {
				return true;
			}
		}
		return false;
	}

	public boolean consumeClick(long windowHandle) {
		boolean active = keys.length > 0 && allDown(windowHandle);
		boolean fire = active && !wasActive;
		wasActive = active;
		return fire;
	}

	public boolean allDown(long windowHandle) {
		for (int k : keys) {
			if (GLFW.glfwGetKey(windowHandle, k) != GLFW.GLFW_PRESS) {
				return false;
			}
		}
		return true;
	}

	public String display() {
		if (keys.length == 0) {
			return "Unbound";
		}
		StringBuilder text = new StringBuilder();
		for (int i = 0; i < keys.length; i++) {
			if (i > 0) {
				text.append(" + ");
			}
			text.append(InputConstants.Type.KEYSYM.getOrCreate(keys[i]).getDisplayName().getString());
		}
		return text.toString();
	}
}
