package studio.meraki.vynapi;

import net.fabricmc.api.ModInitializer;
import studio.meraki.vynapi.model.variable.ModLoader;
import studio.meraki.vynapi.platform.Services;

public final class VynAPI implements ModInitializer {

    @Override
    public void onInitialize() {
        Services.LOG.info("Hello from VynAPI on Fabric!");
        new ModLoader(new FabricModLoaderHandler());
    }
}
