package studio.meraki.vynapi;

import net.neoforged.fml.ModList;
import studio.meraki.vynapi.handler.other.ModLoader.AbstractModLoaderHandler;

import java.util.Optional;

public final class NeoForgeModLoaderHandler extends AbstractModLoaderHandler {

    @Override
    public boolean isModLoaded(final String modid) {
        return ModList.get().isLoaded(modid);
    }

    @Override
    protected Optional<String> getPlatformGameVersion() {
        return ModList.get().getModContainerById("minecraft")
                .map(container -> container.getModInfo().getVersion().toString());
    }
}