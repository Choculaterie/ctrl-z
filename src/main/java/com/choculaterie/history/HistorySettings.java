package com.choculaterie.history;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public final class HistorySettings {
	public enum Unit {
		ACTIONS,
		SECONDS,
		MINUTES
	}

	private static final String CONFIG_FILE = "ctrl-z-settings.json";
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final int MIN_VALUE = 1;
	private static final int MAX_VALUE = 5000;

	private static int value = 100;
	private static Unit unit = Unit.ACTIONS;

	static {
		load();
	}

	private HistorySettings() {}

	public static int getValue() {
		return value;
	}

	public static void setValue(int newValue) {
		value = Math.max(MIN_VALUE, Math.min(MAX_VALUE, newValue));
		save();
	}

	public static Unit getUnit() {
		return unit;
	}

	public static void setUnit(Unit newUnit) {
		unit = newUnit;
		save();
	}

	public static boolean isTimeBased() {
		return unit != Unit.ACTIONS;
	}

	public static long getWindowMillis() {
		return unit == Unit.MINUTES ? value * 60_000L : value * 1_000L;
	}

	private static File getConfigFile() {
		File configDir = FabricLoader.getInstance().getConfigDir().toFile();
		return new File(configDir, CONFIG_FILE);
	}

	private static void load() {
		File file = getConfigFile();
		if (!file.exists()) {
			return;
		}
		try (FileReader reader = new FileReader(file)) {
			JsonObject json = GSON.fromJson(reader, JsonObject.class);
			if (json != null) {
				if (json.has("value")) {
					value = Math.max(MIN_VALUE, Math.min(MAX_VALUE, json.get("value").getAsInt()));
				}
				if (json.has("unit")) {
					try {
						unit = Unit.valueOf(json.get("unit").getAsString());
					} catch (IllegalArgumentException ignored) {
					}
				}
			}
		} catch (IOException | RuntimeException ignored) {
		}
	}

	private static void save() {
		try {
			File file = getConfigFile();
			File parent = file.getParentFile();
			if (parent != null && !parent.exists()) {
				parent.mkdirs();
			}
			JsonObject json = new JsonObject();
			json.addProperty("value", value);
			json.addProperty("unit", unit.name());
			try (FileWriter writer = new FileWriter(file)) {
				GSON.toJson(json, writer);
			}
		} catch (IOException ignored) {
		}
	}
}
