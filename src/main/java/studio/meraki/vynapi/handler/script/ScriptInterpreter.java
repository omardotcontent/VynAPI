package studio.meraki.vynapi.handler.script;

import me.abdelaziz.main.VynMain;
import me.abdelaziz.parser.Parser;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.function.nat.NativeFunction;
import me.abdelaziz.util.NativeBinder;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.damage.DamageSource;
import studio.meraki.vynapi.handler.client.BackgroundLoopHandler;
import studio.meraki.vynapi.model.VynAddon;
import studio.meraki.vynapi.model.function.DebugText;
import studio.meraki.vynapi.model.function.ExcludeScript;
import studio.meraki.vynapi.model.function.ImportScript;
import studio.meraki.vynapi.model.script.PackScripts;
import studio.meraki.vynapi.model.script.Script;
import studio.meraki.vynapi.model.statement.wait.WaitHandler;
import studio.meraki.vynapi.model.variable.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public final class ScriptInterpreter {

    private static final Player PLAYER_VAR;
    private static final Key KEY = new Key();
    private static final ModLoader MOD_LOADER = new ModLoader(FabricLoader.getInstance());

    private static final Map<String, PackScripts> packScripts = new ConcurrentHashMap<>();

    private static final Set<Script> globalScripts = ConcurrentHashMap.newKeySet();
    private static final Set<VynAddon> stdAddons = ConcurrentHashMap.newKeySet();

    private static final NativeFunction DEBUG_TEXT_FUNCTION = new DebugText();
    private static final NativeFunction IMPORT_SCRIPT_FUNCTION = new ImportScript(packScripts);
    private static final NativeFunction EXCLUDE_SCRIPT_FUNCTION = new ExcludeScript(packScripts);

    static {
        PLAYER_VAR = new Player(MinecraftClient.getInstance().player, MinecraftClient.getInstance());
        ClientPlayConnectionEvents.JOIN
                .register((handler, sender, client) -> PLAYER_VAR.setPlayer(client.player));
    }

    private ScriptInterpreter() {
    }

    public static void init() {
        VynMain.init(true);
        Parser.register("wait", new WaitHandler());

        VynMain.setStdListener(listener -> {
            NativeBinder.bind(listener, Sound.class);
            NativeBinder.bind(listener, Position.class);

            NativeBinder.defineConstant(listener, "player", PLAYER_VAR);
            NativeBinder.defineConstant(listener, "world", PLAYER_VAR.getWorld());
            NativeBinder.defineConstant(listener, "key", KEY);
            NativeBinder.defineConstant(listener, "modLoader", MOD_LOADER);

            listener.defineFunction("debugText", DEBUG_TEXT_FUNCTION);
            listener.defineFunction("importScript", IMPORT_SCRIPT_FUNCTION);
            listener.defineFunction("excludeScript", EXCLUDE_SCRIPT_FUNCTION);

            for (final VynAddon stdAddon : stdAddons) {
                final Consumer<Environment> consumer = stdAddon.onEnable();
                if (consumer != null)
                    consumer.accept(listener);
            }
        });

        ClientTickEvents.START_CLIENT_TICK.register(ScriptInterpreter::tick);
    }

    public static void loadScripts() {
        final Deque<Script> delayedScripts = new ArrayDeque<>();
        for (final PackScripts packScripts : packScripts.values())
            packScripts.loadScripts(PLAYER_VAR, globalScripts, delayedScripts);

        while (!delayedScripts.isEmpty())
            delayedScripts.pop().load(PLAYER_VAR, globalScripts, delayedScripts);
    }

    public static void clearScripts() {
        packScripts.clear();
        globalScripts.clear();

        BackgroundLoopHandler.clearAll();
    }

    public static void onSwingHand() {
        for (final Script entry : globalScripts)
            entry.call(PLAYER_VAR, Script.FunctionType.ON_SWING_HAND);
    }

    public static void onDamage(final DamageSource source, final float amount, final Boolean returnValue) {
        for (final Script entry : globalScripts) {
            entry.call(PLAYER_VAR, Script.FunctionType.ON_DAMAGE,
                    List.of(NativeBinder.toValue(entry.getEnvironment(), source.toString()),
                            NativeBinder.toValue(entry.getEnvironment(), amount),
                            NativeBinder.toValue(entry.getEnvironment(), returnValue)));
        }
    }

    public static void onPlaySound(final Sound sound) {
        for (final Script entry : globalScripts) {
            entry.call(PLAYER_VAR, Script.FunctionType.ON_PLAY_SOUND,
                    List.of(NativeBinder.toValue(entry.getEnvironment(), sound)));
        }
    }

    public static void addScript(final String packId, final String name, final String content) {
        packScripts.computeIfAbsent(packId, k -> new PackScripts())
                .addScript(content, name);
    }

    public static void registerSTD(final VynAddon addon) {
        stdAddons.add(addon);
    }

    public static void unregisterSTD(final VynAddon addon) {
        stdAddons.remove(addon);
    }

    private static void tick(final MinecraftClient client) {
        if (client.player != null
                && client.player != PLAYER_VAR.getPlayer())
            PLAYER_VAR.setPlayer(client.player);

        if (PLAYER_VAR.getPlayer() == null
                || client.isPaused() || client.world == null)
            return;

        if (PLAYER_VAR.getLivingEntity() == null)
            PLAYER_VAR.setLivingEntity(client.player.getEntity());

        for (final Script entry : globalScripts)
            entry.call(PLAYER_VAR);
    }

}
