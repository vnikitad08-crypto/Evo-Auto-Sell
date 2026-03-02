package me.sh1zaexe.evoasell.mixin;

import net.minecraft.client.gui.hud.ChatHud;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Пустой миксин. Оставляем класс, чтобы сохранить структуру конфигурации,
 * но не внедряемся в методы ChatHud (во избежание крашей на новых версиях).
 *
 * Логика звука по триггеру "» Я:" перенесена в Fabric-эвент чата
 * внутри {@code EvoAsellClient}.
 */
@Mixin(ChatHud.class)
public abstract class ChatHudMixin {
}
