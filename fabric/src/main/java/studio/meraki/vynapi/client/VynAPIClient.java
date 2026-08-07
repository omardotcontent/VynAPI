package studio.meraki.vynapi.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ResourceManager;
import studio.meraki.vynapi.FabricScriptReloadListener;
import studio.meraki.vynapi.handler.script.ScriptLoader;

public final class VynAPIClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ScriptLoader.init();
        ResourceLoader.get(PackType.CLIENT_RESOURCES)
                .registerReloader(FabricScriptReloadListener.ID, FabricScriptReloadListener.getInstance());
    }
}
