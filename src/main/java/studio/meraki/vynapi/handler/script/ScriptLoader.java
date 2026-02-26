package studio.meraki.vynapi.handler.script;

import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.meraki.vynapi.handler.client.BackgroundLoopHandler;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class ScriptLoader {

    private static final Logger log = LoggerFactory.getLogger(ScriptLoader.class);

    private ScriptLoader() {
    }

    public static void init() {
        ScriptHandler.init();
        BackgroundLoopHandler.init();

        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(new SimpleSynchronousResourceReloadListener() {

            @Override
            public Identifier getFabricId() {
                return Identifier.of("vynapi", "script_loader");
            }

            @Override
            public void reload(final ResourceManager manager) {
                ScriptHandler.clearScripts();

                for (final Identifier id : manager.findResources(
                        "scripts",
                        id -> id.getNamespace().equals("minecraft")
                                && id.getPath().endsWith(".vyn")
                ).keySet()) {
                    try {
                        for (final Resource resource : manager.getAllResources(id)) {
                            try (final InputStream stream = resource.getInputStream()) {
                                final String path = id.getPath();
                                final String fileName = path.substring(path.lastIndexOf('/') + 1);
                                final String fileVariable = fileName.substring(0, fileName.length() - ".vyn".length());
                                final String packId = resource.getPackId();

                                ScriptHandler.addScript(packId, fileVariable,
                                        new String(stream.readAllBytes(), StandardCharsets.UTF_8));
                            }
                        }
                    } catch (final Exception e) {
                        log.error("Failed to load script: {}", id, e);
                    }
                }

                ScriptHandler.loadScripts();
            }
        });
    }
}
