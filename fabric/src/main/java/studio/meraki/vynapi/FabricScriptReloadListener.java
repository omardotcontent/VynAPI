package studio.meraki.vynapi;


import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jspecify.annotations.NonNull;
import studio.meraki.vynapi.handler.script.ScriptLoader;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public final class FabricScriptReloadListener implements PreparableReloadListener {

    public static final Identifier ID = Identifier.fromNamespaceAndPath("vynapi", "script_loader");

    private static final FabricScriptReloadListener INSTANCE = new FabricScriptReloadListener();

    private FabricScriptReloadListener() {
    }

    public static FabricScriptReloadListener getInstance() {
        return INSTANCE;
    }

    @Override
    public @NonNull CompletableFuture<Void> reload(final PreparableReloadListener.@NonNull SharedState sharedState,
                                                   final @NonNull Executor prepareExecutor,
                                                   final PreparableReloadListener.@NonNull PreparationBarrier barrier,
                                                   final @NonNull Executor applyExecutor) {
        return ScriptLoader.getInstance().reload(sharedState, prepareExecutor, barrier, applyExecutor);
    }
}