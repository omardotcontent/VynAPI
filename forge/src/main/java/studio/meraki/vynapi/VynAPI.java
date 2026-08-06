package studio.meraki.vynapi;

import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import studio.meraki.vynapi.handler.script.ScriptLoader;
import studio.meraki.vynapi.model.variable.ModLoader;
import studio.meraki.vynapi.platform.Services;

@Mod(Services.MOD_ID)
public class VynAPI {

    public VynAPI(final FMLJavaModLoadingContext context) {
        final IEventBus eventBus = context.getModEventBus();

        Services.LOG.info("Hello from VynAPI on Forge!");
        new ModLoader(new ForgeModLoaderHandler());

        ScriptLoader.init();
        eventBus.addListener(this::registerReloadListeners);
    }

    private void registerReloadListeners(final RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(ScriptLoader.getInstance());
    }
}
