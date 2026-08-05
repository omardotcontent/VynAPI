package studio.meraki.vynapi.handler.script;

import me.abdelaziz.main.VynMain;
import me.abdelaziz.parser.Parser;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.nat.NativeFunction;
import me.abdelaziz.util.NativeBinder;
import net.minecraft.client.Minecraft;
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

@SuppressWarnings("unused")
public final class ScriptHandler {

    private static final Player PLAYER_VAR = new Player(Minecraft.getInstance().player, Minecraft.getInstance());
    private static final Key KEY = new Key();
    private static final ModLoader MOD_LOADER = ModLoader.INSTANCE;

    private static final Map<String, PackScripts> packScripts = new ConcurrentHashMap<>();

    private static final Set<String> eventNames = ConcurrentHashMap.newKeySet();
    private static final Set<VynAddon> stdAddons = ConcurrentHashMap.newKeySet();
    private static final Set<Script> globalScripts = ConcurrentHashMap.newKeySet();

    private static final NativeFunction DEBUG_TEXT_FUNCTION = new DebugText();
    private static final NativeFunction IMPORT_SCRIPT_FUNCTION = new ImportScript(packScripts);
    private static final NativeFunction EXCLUDE_SCRIPT_FUNCTION = new ExcludeScript(packScripts);

    private ScriptHandler() {
    }

    public static void setPlayerVar() {
        PLAYER_VAR.setPlayer(Minecraft.getInstance().player);
    }

    public static void init() {
        VynMain.init(true);

        eventNames.addAll(List.of("onTick", "onSwingHand", "onPlaySound"));

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

    public static void fireEvent(final String eventName, final Object... values) {
        if (containsEvent(eventName))
            for (final Script script : globalScripts)
                script.fireEvent(PLAYER_VAR, eventName, toValues(script, values));
    }

    public static void addScript(final String packId, final String name, final String content) {
        packScripts.computeIfAbsent(packId, k -> new PackScripts())
                .addScript(content, name);
    }

    public static void registerSTD(final VynAddon addon) {
        stdAddons.add(addon);
        eventNames.addAll(addon.getEvents());
        System.out.println("BROOO, REGISTERED " + addon.getEvents());
        PLAYER_VAR.sendMessage("BROOO, REGISTERED " + addon.getEvents());
    }

    public static void unregisterSTD(final VynAddon addon) {
        stdAddons.remove(addon);
        eventNames.removeAll(addon.getEvents());
    }

    public static Set<String> getEventNames() {
        return eventNames;
    }

    public static boolean containsEvent(final String eventName) {
        return eventNames.contains(eventName);
    }

    public static void tick(final Minecraft client) {
        if (client.player != null
                && client.player != PLAYER_VAR.getPlayer())
            PLAYER_VAR.setPlayer(client.player);

        if (PLAYER_VAR.getPlayer() == null
                || client.isPaused() || client.level == null)
            return;

        if (PLAYER_VAR.getLivingEntity() == null)
            PLAYER_VAR.setLivingEntity(client.player.getControllingPassenger());

        fireEvent("onTick");
    }

    private static List<Value> toValues(final Script script, final Object... values) {
        if (values == null) return Collections.emptyList();

        final List<Value> result = new ArrayList<>();
        for (final Object value : values)
            result.add(NativeBinder.toValue(script.getEnvironment(), value));

        return result;
    }

}
