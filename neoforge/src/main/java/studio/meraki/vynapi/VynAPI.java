package studio.meraki.vynapi;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.AddClientReloadListenersEvent;
import studio.meraki.vynapi.handler.script.ScriptLoader;
import studio.meraki.vynapi.model.variable.ModLoader;
import studio.meraki.vynapi.platform.Services;

@Mod(Services.MOD_ID)
public class VynAPI {

    public VynAPI(IEventBus eventBus) {
        Services.LOG.info("Hello from VynAPI on NeoForge!");
        new ModLoader(new NeoForgeModLoaderHandler());

        ScriptLoader.init();
        eventBus.addListener(this::registerReloadListeners);
    }

    private void registerReloadListeners(final AddClientReloadListenersEvent event) {
        event.addListener(
                net.minecraft.resources.Identifier.fromNamespaceAndPath(Services.MOD_ID, "script_loader"),
                ScriptLoader.getInstance()
        );
    }
}