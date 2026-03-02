package me.sh1zaexe.evoasell.gui;

import me.sh1zaexe.evoasell.EvoAsellClient;
import me.sh1zaexe.evoasell.config.EvoAsellConfig;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;

/**
 * In-game settings screen for EvoAsell.
 *
 * Layout (centred, dark panel):
 * ┌─────────────────────────────────────────────┐
 * │            EvoAsell Settings                │
 * │                                             │
 * │  ── Auto Message ─────────────────────────  │
 * │  Message: [____________________________]    │
 * │  Interval (sec): [____]                     │
 * │  Auto Send:  [ ENABLED / DISABLED ]         │
 * │                                             │
 * │  ── Nickname Highlight ────────────────────  │
 * │  Nick Highlight: [ ENABLED / DISABLED ]     │
 * │                                             │
 * │                  [ Save & Close ]           │
 * └─────────────────────────────────────────────┘
 */
public class EvoAsellScreen extends Screen {

    private static final int PANEL_W = 320;
    private static final int PANEL_H = 220;

    // Colours
    private static final int COL_BG        = 0xCC0D0D0D; // semi-transparent black
    private static final int COL_BORDER     = 0xFF5A8AFF; // electric blue border
    private static final int COL_TITLE      = 0xFF5A8AFF;
    private static final int COL_SECTION    = 0xFFAAAAAA;
    private static final int COL_LABEL      = 0xFFFFFFFF;
    private static final int COL_BTN_ON_BG  = 0xFF1C5C1C; // dark green
    private static final int COL_BTN_OFF_BG = 0xFF5C1C1C; // dark red

    private final Screen parent;

    // Widgets
    private TextFieldWidget messageField;
    private TextFieldWidget intervalMinutesField;
    private TextFieldWidget intervalSecondsField;
    private ButtonWidget   autoSendToggle;

    // Local copies of config values (applied only on Save)
    private boolean autoEnabled;

    public EvoAsellScreen(Screen parent) {
        // Заголовок оставляем на английском, как просил пользователь
        super(Text.literal("EvoAsell Settings"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        EvoAsellConfig cfg = EvoAsellConfig.getInstance();
        autoEnabled      = cfg.autoMessageEnabled;

        int px = (width  - PANEL_W) / 2;
        int py = (height - PANEL_H) / 2;

        // ── Auto-message text field ───────────────────────────────────────────
        messageField = new TextFieldWidget(textRenderer,
                px + 10, py + 60, PANEL_W - 20, 18,
                Text.literal("Сообщение"));
        messageField.setMaxLength(256);
        messageField.setText(cfg.autoMessageText);
        messageField.setPlaceholder(Text.literal("Ваше авто-сообщение..."));
        addDrawableChild(messageField);

        // ── Interval fields (minutes + seconds) ───────────────────────────────
        int totalSecs = Math.max(1, cfg.autoMessageInterval);
        int minutes   = totalSecs / 60;
        int seconds   = totalSecs % 60;

        intervalMinutesField = new TextFieldWidget(textRenderer,
                px + 110, py + 96, 40, 18,
                Text.literal("Минуты"));
        intervalMinutesField.setMaxLength(3);
        intervalMinutesField.setText(String.valueOf(minutes));
        intervalMinutesField.setPlaceholder(Text.literal("0"));
        addDrawableChild(intervalMinutesField);

        intervalSecondsField = new TextFieldWidget(textRenderer,
                px + 160, py + 96, 40, 18,
                Text.literal("Секунды"));
        intervalSecondsField.setMaxLength(2);
        intervalSecondsField.setText(String.valueOf(seconds));
        intervalSecondsField.setPlaceholder(Text.literal("1"));
        addDrawableChild(intervalSecondsField);

        // ── Auto-send toggle ──────────────────────────────────────────────────
        autoSendToggle = ButtonWidget.builder(autoSendLabel(), btn -> {
            autoEnabled = !autoEnabled;
            btn.setMessage(autoSendLabel());
        }).dimensions(px + 10, py + 125, 140, 20).build();
        addDrawableChild(autoSendToggle);

        // ── Save & Close ──────────────────────────────────────────────────────
        ButtonWidget saveBtn = ButtonWidget.builder(
                Text.literal("Сохранить и закрыть"), btn -> saveAndClose()
        ).dimensions(px + (PANEL_W / 2) - 60, py + PANEL_H - 28, 120, 20).build();
        addDrawableChild(saveBtn);
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Dim the game world behind the screen
        renderBackground(ctx, mouseX, mouseY, delta);

        int px = (width  - PANEL_W) / 2;
        int py = (height - PANEL_H) / 2;

        // Panel background
        ctx.fill(px, py, px + PANEL_W, py + PANEL_H, COL_BG);

        // Border – 1 px
        ctx.drawBorder(px, py, PANEL_W, PANEL_H, COL_BORDER);

        // ── Title ─────────────────────────────────────────────────────────────
        ctx.drawCenteredTextWithShadow(textRenderer,
                Text.literal("EvoAsell  |  sh1zaExE"),
                width / 2, py + 8, COL_TITLE);

        // ── Section: Auto Message ─────────────────────────────────────────────
        ctx.drawTextWithShadow(textRenderer,
                Text.literal("-- Авто-сообщение --"), px + 10, py + 34, COL_SECTION);
        ctx.drawTextWithShadow(textRenderer,
                Text.literal("Сообщение:"), px + 10, py + 46, COL_LABEL);
        ctx.drawTextWithShadow(textRenderer,
                Text.literal("Интервал:"), px + 10, py + 82, COL_LABEL);

        // Render children (widgets) on top
        super.render(ctx, mouseX, mouseY, delta);
    }

    // ─────────────────────────────────────────────────────────────────────────

    private Text autoSendLabel() {
        return autoEnabled
                ? Text.literal("  ВКЛ  ").withColor(0xFF55FF55)
                : Text.literal("  ВЫКЛ ").withColor(0xFFFF5555);
    }

    private void saveAndClose() {
        EvoAsellConfig cfg = EvoAsellConfig.getInstance();

        boolean wasAutoEnabled = cfg.autoMessageEnabled;

        cfg.autoMessageEnabled  = autoEnabled;
        cfg.autoMessageText     = messageField.getText();

        try {
            int mins = Integer.parseInt(intervalMinutesField.getText().trim());
            int secs = Integer.parseInt(intervalSecondsField.getText().trim());
            int total = mins * 60 + secs;
            cfg.autoMessageInterval = Math.max(1, total); // минимум 1 секунда
        } catch (NumberFormatException ignored) {
            // Если введено что-то нечисловое — оставляем старое значение
        }

        cfg.save();

        // Reset the auto-message countdown so new interval takes effect immediately
        EvoAsellClient.getAutoMessageManager().resetTimer();

        // Если авто-сообщение только что включили — сразу отправляем первое.
        if (!wasAutoEnabled && cfg.autoMessageEnabled) {
            EvoAsellClient.getAutoMessageManager().triggerImmediate();
        }

        close();
    }

    @Override
    public void close() {
        if (client != null) client.setScreen(parent);
    }

    @Override
    public boolean shouldPause() {
        return false; // game keeps running while screen is open
    }
}
