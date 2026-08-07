package studio.meraki.vynapi;

import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.fml.common.Mod;
import studio.meraki.vynapi.handler.script.ScriptLoader;
import studio.meraki.vynapi.model.variable.ModLoader;
import studio.meraki.vynapi.platform.Services;

@Mod(Services.MOD_ID)
public class VynAPI {

    public VynAPI() {
        Services.LOG.info("Hello from VynAPI on Forge!");
        new ModLoader(new ForgeModLoaderHandler());

        ScriptLoader.init();
        RegisterClientReloadListenersEvent.BUS.addListener(this::registerReloadListeners);
    }

    private void registerReloadListeners(final RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(ScriptLoader.getInstance());
    }
}