package studio.meraki.vynapi.handler.script;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ScriptLoader extends SimplePreparableReloadListener<Map<ResourceLocation, ScriptLoader.LoadedScript>> {

    private static final Logger log = LoggerFactory.getLogger(ScriptLoader.class);
    private static final ScriptLoader INSTANCE = new ScriptLoader();

    private ScriptLoader() {
    }

    public static ScriptLoader getInstance() {
        return INSTANCE;
    }

    public static void init() {
        ScriptHandler.init();
    }

    @Override
    protected @NotNull Map<ResourceLocation, LoadedScript> prepare(final ResourceManager manager, final @NotNull ProfilerFiller profiler) {
        final Map<ResourceLocation, LoadedScript> scripts = new LinkedHashMap<>();

        for (final ResourceLocation id : manager.listResources(
                "scripts",
                candidate -> candidate.getNamespace().equals("minecraft")
                        && candidate.getPath().endsWith(".vyn")
        ).keySet()) {
            try {
                for (final Resource resource : manager.getResourceStack(id)) {
                    try (final InputStream stream = resource.open()) {
                        final String path = id.getPath();
                        final String fileName = path.substring(path.lastIndexOf('/') + 1);
                        final String fileVariable = fileName.substring(0, fileName.length() - ".vyn".length());
                        final String packId = resource.sourcePackId();

                        scripts.put(id, new LoadedScript(packId, fileVariable,
                                new String(stream.readAllBytes(), StandardCharsets.UTF_8)));
                    }
                }
            } catch (final Exception e) {
                log.error("Failed to load script: {}", id, e);
            }
        }

        return scripts;
    }

    @Override
    protected void apply(final Map<ResourceLocation, LoadedScript> prepared, final @NotNull ResourceManager manager, final @NotNull ProfilerFiller profiler) {
        ScriptHandler.clearScripts();
        for (final LoadedScript script : prepared.values()) {
            ScriptHandler.addScript(script.packId(), script.variableName(), script.source());
        }
        ScriptHandler.loadScripts();
    }

    public record LoadedScript(String packId, String variableName, String source) {
    }
}