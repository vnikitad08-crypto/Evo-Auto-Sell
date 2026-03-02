package me.sh1zaexe.evoasell.feature;

import me.sh1zaexe.evoasell.EvoAsell;
import me.sh1zaexe.evoasell.config.EvoAsellConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

/**
 * Manages the timed auto-chat-message loop on the client side.
 *
 * Call {@link #tick()} every client tick (from the ClientTickEvent).
 * When the configured interval elapses and the feature is enabled,
 * the stored message is sent to the server chat.
 */
public class AutoMessageManager {

    /** Ticks per second in Minecraft. */
    private static final int TPS = 20;

    /** Счётчик до следующего авто-сообщения (в тиках). */
    private int tickCounter = 0;

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Called every client game tick.
     * Sends the configured message when the countdown reaches zero.
     */
    public void tick() {
        EvoAsellConfig cfg = EvoAsellConfig.getInstance();

        if (!cfg.autoMessageEnabled) {
            tickCounter = 0;
            return;
        }

        tickCounter++;
        int targetTicks = cfg.autoMessageInterval * TPS;

        if (tickCounter >= targetTicks) {
            tickCounter = 0;
            sendMessage(cfg.autoMessageText);
        }
    }

    /**
     * Resets the internal countdown.
     * Call this after the user changes the interval so the new setting
     * takes effect immediately without waiting for the old countdown to expire.
     */
    public void resetTimer() {
        tickCounter = 0;
    }

    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Отправить авто-сообщение немедленно (если включено в конфиге),
     * и сбросить таймер, чтобы следующий запуск шёл по интервалу.
     */
    public void triggerImmediate() {
        EvoAsellConfig cfg = EvoAsellConfig.getInstance();
        if (!cfg.autoMessageEnabled) {
            return;
        }
        tickCounter = 0;
        sendMessage(cfg.autoMessageText);
    }

    // ─────────────────────────────────────────────────────────────────────────

    private void sendMessage(String message) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.player == null) return;

        if (message == null || message.isBlank()) {
            EvoAsell.LOGGER.warn("[EvoAsell] AutoMessage text is empty – skipping send.");
            return;
        }

        // Trim to 256 chars to avoid server kick for too-long messages
        String trimmed = message.length() > 256 ? message.substring(0, 256) : message;

        try {
            client.player.networkHandler.sendChatMessage(trimmed);

            // Лёгкий звуковой сигнал.
            if (client.getSoundManager() != null) {
                client.getSoundManager().play(
                        net.minecraft.client.sound.PositionedSoundInstance.master(
                                net.minecraft.sound.SoundEvents.UI_BUTTON_CLICK.value(), 1.0f, 1.1f
                        )
                );
            }

            // Если интервал авто-сообщения 2 минуты и больше — пишем тихое уведомление в чат.
            EvoAsellConfig cfg = EvoAsellConfig.getInstance();
            if (cfg.autoMessageInterval >= 120 && client.inGameHud != null) {
                Text info = Text.empty()
                        .append(Text.literal("EvoAsell ").formatted(Formatting.GREEN))
                        .append(Text.literal("» ").formatted(Formatting.GRAY))
                        .append(Text.literal("Сообщение было успешно отправлено").formatted(Formatting.YELLOW));

                client.inGameHud.getChatHud().addMessage(info);
            }
        } catch (Exception e) {
            EvoAsell.LOGGER.error("[EvoAsell] Failed to send auto-message: {}", e.getMessage());
        }
    }
}
