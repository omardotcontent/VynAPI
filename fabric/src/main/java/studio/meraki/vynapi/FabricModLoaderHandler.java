package studio.meraki.vynapi;

import net.fabricmc.loader.api.FabricLoader;
import studio.meraki.vynapi.handler.other.ModLoader.AbstractModLoaderHandler;

import java.util.Optional;

public final class FabricModLoaderHandler extends AbstractModLoaderHandler {

    @Override
    public boolean isModLoaded(final String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    @Override
    protected Optional<String> getPlatformGameVersion() {
        return FabricLoader.getInstance()
                .getModContainer("minecraft")
                .map(container -> container.getMetadata().getVersion().getFriendlyString());
    }
}