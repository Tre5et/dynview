package net.treset.adaptiveview;

import net.fabricmc.api.ClientModInitializer;

public class AdaptiveViewClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        AdaptiveViewMod.setClient(true);
        if(!AdaptiveViewMod.getConfig().isAllowOnClient()) {
            AdaptiveViewMod.LOGGER.warn("Client environment detected, disabling AdaptiveView. Use 'allow_on_client' config option to override.");
        } else {
            AdaptiveViewMod.LOGGER.info("Client environment detected, using 'allow_on_client'. This may cause unexpected behaviour.");
        }
    }
}
