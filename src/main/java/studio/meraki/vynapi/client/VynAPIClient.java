package studio.meraki.vynapi.client;

import net.fabricmc.api.ClientModInitializer;
import studio.meraki.vynapi.handler.script.ScriptLoader;

public final class VynAPIClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScriptLoader.init();
    }

}
