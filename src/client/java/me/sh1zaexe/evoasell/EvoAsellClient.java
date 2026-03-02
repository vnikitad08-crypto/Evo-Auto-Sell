package me.sh1zaexe.evoasell;

import me.sh1zaexe.evoasell.feature.AutoMessageManager;
import me.sh1zaexe.evoasell.gui.EvoAsellScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

/**
 * Client-side entry point for EvoAsell.
 *
 * Registers:
 *  - The keybinding (default: Right Shift) that opens the settings screen.
 *  - The client-tick listener that drives {@link AutoMessageManager}.
 */
public class EvoAsellClient implements ClientModInitializer {

    /** Keybinding to open the EvoAsell settings screen (default: Right Shift). */
    private static KeyBinding openScreenKey;

    /** Singleton auto-message manager, accessed from the screen. */
    private static final AutoMessageManager AUTO_MESSAGE = new AutoMessageManager();

    // ─────────────────────────────────────────────────────────────────────────

    @Override
    public void onInitializeClient() {
        // Register keybinding
        openScreenKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.evoasell.open_screen",          // translation key
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,           // default key: Right Shift
                "category.evoasell"                  // category in Controls screen
        ));

        // Tick listener – drives AutoMessageManager + keybinding check
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Auto-message countdown
            AUTO_MESSAGE.tick();

            // Open settings screen when the keybind is pressed
            while (openScreenKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new EvoAsellScreen(null));
                }
            }
        });

        // Chat listener – звук по триггерам "» Я:" или "ЛС |" в любом месте сообщения.
        // Используем ALLOW_CHAT, так как CHAT может не вызываться для всех типов сообщений
        ClientReceiveMessageEvents.ALLOW_CHAT.register((message, signedMessage, sender, params, timestamp) -> {
            EvoAsell.LOGGER.info("[EvoAsell] ALLOW_CHAT event triggered!");
            
            if (message == null) {
                EvoAsell.LOGGER.warn("[EvoAsell] Message is null!");
                return true; // Разрешаем сообщение
            }

            String plain = message.getString();
            EvoAsell.LOGGER.info("[EvoAsell] Message text: '{}'", plain);
            
            // Проверяем оба триггера
            boolean containsTrigger1 = plain.contains("» Я:");
            boolean containsTrigger2 = plain.contains("ЛС |");
            
            EvoAsell.LOGGER.info("[EvoAsell] Contains '» Я:': {}, Contains 'ЛС |': {}", containsTrigger1, containsTrigger2);
            
            if (containsTrigger1 || containsTrigger2) {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client != null && client.player != null) {
                    EvoAsell.LOGGER.info("[EvoAsell] Trigger found! Playing sound.");
                    // Используем SoundManager напрямую для надёжности
                    if (client.getSoundManager() != null) {
                        client.getSoundManager().play(
                            net.minecraft.client.sound.PositionedSoundInstance.master(
                                SoundEvents.UI_BUTTON_CLICK.value(), 1.0f, 1.1f
                            )
                        );
                    }
                }
            }

            return true; // Всегда разрешаем сообщение
        });

        EvoAsell.LOGGER.info("[EvoAsell] Client initialized.");
    }

    // ─────────────────────────────────────────────────────────────────────────

    /** Expose the manager so the settings screen can reset its timer on save. */
    public static AutoMessageManager getAutoMessageManager() {
        return AUTO_MESSAGE;
    }
}
