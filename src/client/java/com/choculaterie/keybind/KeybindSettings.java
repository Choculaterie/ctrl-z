package com.choculaterie.keybind;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;
import org.lwjgl.glfw.GLFW;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class KeybindSettings {
	private static final String CONFIG_FILE = "ctrl-z-keybinds.json";
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static final KeyCombo UNDO = new KeyCombo(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_Z);
	public static final KeyCombo REDO = new KeyCombo(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_Y);

	private static boolean creativeOnly = false;

	static {
		load();
	}

	private KeybindSettings() {}

	public static boolean isCreativeOnly() {
		return creativeOnly;
	}

	public static void setCreativeOnly(boolean creativeOnly) {
		KeybindSettings.creativeOnly = creativeOnly;
		save();
	}

	public static void save() {
		try {
			File file = getConfigFile();
			File parent = file.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			JsonObject json = new JsonObject();
			json.add("undo", toJson(UNDO));
			json.add("redo", toJson(REDO));
			json.addProperty("creativeOnly", creativeOnly);
			try (FileWriter writer = new FileWriter(file)) {
				GSON.toJson(json, writer);
			}
		} catch (IOException ignored) {
		}
	}

	private static void load() {
		File file = getConfigFile();
		if (!file.exists()) {
			return;
		}
		try (FileReader reader = new FileReader(file)) {
			JsonObject json = GSON.fromJson(reader, JsonObject.class);
			if (json == null) {
				return;
			}
			if (json.has("undo")) {
				fromJson(json.get("undo"), UNDO);
			}
			if (json.has("redo")) {
				fromJson(json.get("redo"), REDO);
			}
			if (json.has("creativeOnly")) {
				creativeOnly = json.get("creativeOnly").getAsBoolean();
			}
		} catch (IOException | RuntimeException ignored) {
		}
	}

	private static JsonArray toJson(KeyCombo combo) {
		JsonArray array = new JsonArray();
		for (int key : combo.keys()) {
			array.add(key);
		}
		return array;
	}

	private static void fromJson(com.google.gson.JsonElement element, KeyCombo combo) {
		if (element.isJsonArray()) {
			JsonArray array = element.getAsJsonArray();
			int[] keys = new int[array.size()];
			for (int i = 0; i < keys.length; i++) {
				keys[i] = array.get(i).getAsInt();
			}
			combo.set(keys);
		} else if (element.isJsonObject()) {
			JsonObject legacy = element.getAsJsonObject();
			if (legacy.has("key")) {
				java.util.List<Integer> keys = new java.util.ArrayList<>();
				if (legacy.has("ctrl") && legacy.get("ctrl").getAsBoolean()) {
					keys.add(GLFW.GLFW_KEY_LEFT_CONTROL);
				}
				if (legacy.has("shift") && legacy.get("shift").getAsBoolean()) {
					keys.add(GLFW.GLFW_KEY_LEFT_SHIFT);
				}
				if (legacy.has("alt") && legacy.get("alt").getAsBoolean()) {
					keys.add(GLFW.GLFW_KEY_LEFT_ALT);
				}
				keys.add(legacy.get("key").getAsInt());
				combo.set(keys.stream().mapToInt(Integer::intValue).toArray());
			}
		}
	}

	private static File getConfigFile() {
		File configDir = FabricLoader.getInstance().getConfigDir().toFile();
		return new File(configDir, CONFIG_FILE);
	}
}
