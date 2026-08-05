package studio.meraki.vynapi;

import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.NotNull;
import studio.meraki.vynapi.handler.script.ScriptLoader;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class FabricScriptReloadListener implements IdentifiableResourceReloadListener {

    private static final FabricScriptReloadListener INSTANCE = new FabricScriptReloadListener();

    private FabricScriptReloadListener() {
    }

    public static FabricScriptReloadListener getInstance() {
        return INSTANCE;
    }

    @Override
    public ResourceLocation getFabricId() {
        return ResourceLocation.fromNamespaceAndPath("vynapi", "script_loader");
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(final PreparableReloadListener.PreparationBarrier barrier,
                                                   final ResourceManager manager,
                                                   final Executor prepareExecutor,
                                                   final Executor applyExecutor) {
        return ScriptLoader.getInstance().reload(barrier, manager, prepareExecutor, applyExecutor);
    }
}