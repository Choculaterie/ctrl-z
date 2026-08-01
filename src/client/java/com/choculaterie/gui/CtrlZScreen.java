package com.choculaterie.gui;

import com.choculaterie.history.HistorySettings;
import com.choculaterie.keybind.KeyCombo;
import com.choculaterie.keybind.KeybindSettings;
import com.choculaterie.network.ClearHistoryPayload;
import com.choculaterie.vanilib.gui.theme.UITheme;
import com.choculaterie.vanilib.gui.widget.CustomButton;
import com.choculaterie.vanilib.gui.widget.CustomTextField;
import com.choculaterie.vanilib.gui.widget.ToggleButton;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class CtrlZScreen extends Screen {
	private static final String TITLE = "Ctrl-Z";
	private static final int LABEL_WIDTH = 150;
	private static final int GAP = 10;
	private static final int CONTROL_WIDTH = 170;
	private static final int ROW_HEIGHT = 20;
	private static final int ROW_SPACING = 32;
	private static final int PADDING = 10;
	private static final int CLOSE_BUTTON_SIZE = 20;

	private static final int HISTORY_FIELD_WIDTH = 100;
	private static final int HISTORY_UNIT_GAP = 6;
	private static final int HISTORY_UNIT_WIDTH = 64;

	private CustomButton closeButton;
	private CustomButton undoButton;
	private CustomButton redoButton;
	private CustomTextField historyField;
	private CustomButton historyUnitButton;
	private CustomButton clearButton;
	private ToggleButton creativeOnlyToggle;
	private KeyCombo capturing;
	private CustomButton capturingButton;
	private final Set<Integer> capturingKeys = new LinkedHashSet<>();

	private int contentX;
	private int titleY;

	public CtrlZScreen() {
		super(Component.literal(TITLE));
	}

	@Override
	protected void init() {
		super.init();

		closeButton = new CustomButton(this.width - PADDING - CLOSE_BUTTON_SIZE, PADDING, CLOSE_BUTTON_SIZE, CLOSE_BUTTON_SIZE,
			Component.literal("X"), b -> this.onClose());
		closeButton.setRenderAsXIcon(true);
		this.addRenderableWidget(closeButton);

		int totalWidth = LABEL_WIDTH + GAP + CONTROL_WIDTH;
		contentX = (this.width - totalWidth) / 2;
		int controlX = contentX + LABEL_WIDTH + GAP;

		int rowsBlockHeight = ROW_SPACING * 4 + ROW_HEIGHT;
		int rowsTop = (this.height - rowsBlockHeight) / 2;
		titleY = Math.max(10, rowsTop - 40);

		int rowY = rowsTop;

		undoButton = new CustomButton(controlX, rowY, CONTROL_WIDTH, ROW_HEIGHT,
			Component.literal(KeybindSettings.UNDO.display()), b -> startCapture(KeybindSettings.UNDO, undoButton));
		this.addRenderableWidget(undoButton);

		rowY += ROW_SPACING;
		redoButton = new CustomButton(controlX, rowY, CONTROL_WIDTH, ROW_HEIGHT,
			Component.literal(KeybindSettings.REDO.display()), b -> startCapture(KeybindSettings.REDO, redoButton));
		this.addRenderableWidget(redoButton);

		rowY += ROW_SPACING;
		historyField = new CustomTextField(this.minecraft, controlX, rowY, HISTORY_FIELD_WIDTH, ROW_HEIGHT, Component.literal("History"));
		historyField.setValue(String.valueOf(HistorySettings.getValue()));
		historyField.setOnChanged(this::applyHistoryValue);
		this.addRenderableWidget(historyField);

		historyUnitButton = new CustomButton(controlX + HISTORY_FIELD_WIDTH + HISTORY_UNIT_GAP, rowY, HISTORY_UNIT_WIDTH, ROW_HEIGHT,
			Component.literal(unitLabel(HistorySettings.getUnit())), b -> cycleHistoryUnit());
		this.addRenderableWidget(historyUnitButton);

		rowY += ROW_SPACING;
		clearButton = new CustomButton(controlX, rowY, CONTROL_WIDTH, ROW_HEIGHT,
			Component.literal("Clear History"), b -> clearHistory());
		this.addRenderableWidget(clearButton);

		rowY += ROW_SPACING;
		creativeOnlyToggle = new ToggleButton(controlX, rowY, KeybindSettings.isCreativeOnly(), KeybindSettings::setCreativeOnly);
		this.addRenderableWidget(creativeOnlyToggle);
	}

	private void applyHistoryValue() {
		try {
			HistorySettings.setValue(Integer.parseInt(historyField.getValue().trim()));
		} catch (NumberFormatException ignored) {
		}
	}

	private void cycleHistoryUnit() {
		HistorySettings.Unit[] units = HistorySettings.Unit.values();
		HistorySettings.Unit next = units[(HistorySettings.getUnit().ordinal() + 1) % units.length];
		HistorySettings.setUnit(next);
		historyUnitButton.setMessage(Component.literal(unitLabel(next)));
	}

	private static String unitLabel(HistorySettings.Unit unit) {
		return switch (unit) {
			case ACTIONS -> "Actions";
			case SECONDS -> "Seconds";
			case MINUTES -> "Minutes";
		};
	}

	private void clearHistory() {
		if (ClientPlayNetworking.canSend(ClearHistoryPayload.TYPE)) {
			ClientPlayNetworking.send(new ClearHistoryPayload());
		}
	}

	private void startCapture(KeyCombo combo, CustomButton button) {
		capturing = combo;
		capturingButton = button;
		capturingKeys.clear();
		button.setMessage(Component.literal("> Press keys <"));
	}

	@Override
	public boolean keyPressed(KeyEvent event) {
		if (capturing != null) {
			if (event.isEscape()) {
				cancelCapture();
			} else {
				capturingKeys.add(event.key());
			}
			return true;
		}
		return super.keyPressed(event);
	}

	@Override
	public boolean keyReleased(KeyEvent event) {
		if (capturing != null && !capturingKeys.isEmpty()) {
			finishCapture();
			return true;
		}
		return super.keyReleased(event);
	}

	private void cancelCapture() {
		capturingButton.setMessage(Component.literal(capturing.display()));
		capturing = null;
		capturingButton = null;
		capturingKeys.clear();
	}

	private void finishCapture() {
		int[] keys = new int[capturingKeys.size()];
		int i = 0;
		for (int key : capturingKeys) {
			keys[i++] = key;
		}
		capturing.set(keys);
		KeybindSettings.save();
		capturingButton.setMessage(Component.literal(capturing.display()));
		capturing = null;
		capturingButton = null;
		capturingKeys.clear();
	}

	@Override
	public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
		boolean handled = super.mouseClicked(event, doubleClick);
		if (!historyField.isMouseOver(event.x(), event.y())) {
			this.setFocused(null);
		}
		return handled;
	}

	@Override
	public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
		context.fill(0, 0, this.width, this.height, UITheme.Colors.PANEL_BG);
		super.render(context, mouseX, mouseY, delta);

		int titleX = (this.width - this.font.width(TITLE)) / 2;
		context.drawString(this.font, TITLE, titleX, titleY, UITheme.Colors.TEXT_PRIMARY);

		context.drawString(this.font, "Undo key:", contentX, undoButton.getY() + 6, UITheme.Colors.TEXT_PRIMARY);
		context.drawString(this.font, "Redo key:", contentX, redoButton.getY() + 6, UITheme.Colors.TEXT_PRIMARY);
		context.drawString(this.font, "History (" + unitLabel(HistorySettings.getUnit()).toLowerCase(Locale.ROOT) + "):",
			contentX, historyField.getY() + 6, UITheme.Colors.TEXT_PRIMARY);
		context.drawString(this.font, "Creative only:", contentX, creativeOnlyToggle.getY() + 6, UITheme.Colors.TEXT_PRIMARY);
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	@Override
	public void onClose() {
		CustomTextField.restoreMinecraftCharCallback();
		if (this.minecraft != null) {
			this.minecraft.setScreen(null);
		}
	}
}
