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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ScriptLoader extends SimplePreparableReloadListener<List<ScriptLoader.LoadedScript>> {

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
    protected @NotNull List<LoadedScript> prepare(final ResourceManager manager, final @NotNull ProfilerFiller profiler) {
        final List<LoadedScript> scripts = new ArrayList<>();

        // Scripts live in the "scripts" folder of ANY namespace, e.g.
        // assets/interactivestuff/scripts/<file>.vyn in a resource pack.
        // Previously this only accepted the "minecraft" namespace, which is
        // why scripts shipped in packs (like "interactivestuff") never loaded.
        for (final Map.Entry<ResourceLocation, List<Resource>> entry : manager.listResourceStacks(
                "scripts",
                candidate -> candidate.getPath().endsWith(".vyn")
        ).entrySet()) {
            final ResourceLocation id = entry.getKey();
            final String path = id.getPath();
            final String fileName = path.substring(path.lastIndexOf('/') + 1);
            final String fileVariable = fileName.substring(0, fileName.length() - ".vyn".length());

            // The stack is ordered from the highest priority pack to the lowest.
            // Every pack's copy of the script is loaded so that scripts with the
            // same name in different packs don't shadow each other; each copy is
            // registered under its own pack id (usable in importScript/excludeScript).
            for (final Resource resource : entry.getValue()) {
                try (final InputStream stream = resource.open()) {
                    scripts.add(new LoadedScript(resource.sourcePackId(), fileVariable,
                            new String(stream.readAllBytes(), StandardCharsets.UTF_8)));
                } catch (final Exception e) {
                    log.error("Failed to load script {} from pack {}", id, resource.sourcePackId(), e);
                }
            }
        }

        return scripts;
    }

    @Override
    protected void apply(final List<LoadedScript> prepared, final @NotNull ResourceManager manager, final @NotNull ProfilerFiller profiler) {
        ScriptHandler.clearScripts();
        for (final LoadedScript script : prepared) {
            ScriptHandler.addScript(script.packId(), script.variableName(), script.source());
        }
        ScriptHandler.loadScripts();

        log.info("VynAPI loaded {} .vyn script(s) from {} pack(s): {}",
                prepared.size(),
                prepared.stream().map(LoadedScript::packId).distinct().count(),
                prepared.stream().map(script -> script.packId() + "/" + script.variableName() + ".vyn").toList());
    }

    public record LoadedScript(String packId, String variableName, String source) {
    }
}
