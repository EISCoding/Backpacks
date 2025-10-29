package de.eiscoding;

import de.eiscoding.client.screen.BackpackScreen;
import de.eiscoding.registry.BackpackRegistries;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class BackpacksClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        HandledScreens.register(BackpackRegistries.BACKPACK_SCREEN_HANDLER, BackpackScreen::new);
    }
}
