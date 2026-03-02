package me.sh1zaexe.evoasell;

import me.sh1zaexe.evoasell.config.EvoAsellConfig;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvoAsell implements ModInitializer {

    public static final String MOD_ID = "evoasell";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        // Load config from disk on startup
        EvoAsellConfig.getInstance();
        LOGGER.info("[EvoAsell] Mod initialized by sh1zaExE");
    }
}
