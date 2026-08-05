package studio.meraki.vynapi;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import studio.meraki.vynapi.model.variable.ModLoader;
import studio.meraki.vynapi.platform.Services;

@Mod(Services.MOD_ID)
public class VynAPI {

    public VynAPI(IEventBus eventBus) {
        // TODO: Bootstrap the mod from here for NeoForge
        // CommonClass.init();
        Services.LOG.info("Hello from VynAPI on NeoForge!");
        ModLoader modLoaderApi = new ModLoader(new NeoForgeModLoaderHandler());
    }
}
