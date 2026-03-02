package me.sh1zaexe.evoasell;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.sh1zaexe.evoasell.gui.EvoAsellScreen;

/**
 * ModMenu integration for EvoAsell.
 *
 * This class is client-only because it references {@link EvoAsellScreen},
 * which lives in the client source set. By placing this file under
 * {@code src/client/java}, we avoid mixing client code into the common
 * (server-capable) source set while still exposing the config screen
 * to ModMenu.
 */
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return EvoAsellScreen::new;
    }
}

