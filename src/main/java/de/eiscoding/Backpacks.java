package de.eiscoding;

import de.eiscoding.registry.BackpackRegistries;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Backpacks implements ModInitializer {
    public static final String MOD_ID = "backpacks";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        BackpackRegistries.registerAll();
        LOGGER.info("Loaded Backpacks mod");
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }
}
