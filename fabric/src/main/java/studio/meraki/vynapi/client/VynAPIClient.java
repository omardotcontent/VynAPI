package studio.meraki.vynapi.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.server.packs.PackType;
import studio.meraki.vynapi.FabricScriptReloadListener;
import studio.meraki.vynapi.handler.script.ScriptLoader;

public final class VynAPIClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScriptLoader.init();
        ResourceLoader.get(PackType.CLIENT_RESOURCES)
                .registerReloadListener(FabricScriptReloadListener.ID, FabricScriptReloadListener.getInstance());
    }
}
