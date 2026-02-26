package studio.meraki.vynapi.model.script;

import me.abdelaziz.main.VynMain;
import me.abdelaziz.runtime.Environment;
import me.abdelaziz.runtime.Value;
import me.abdelaziz.runtime.function.VynCallable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import studio.meraki.vynapi.model.exception.DelayedScriptException;
import studio.meraki.vynapi.model.variable.Player;

import java.util.*;

public final class Script {

    private static final Logger log = LoggerFactory.getLogger(Script.class);

    private State state;
    private Environment environment;
    private final String code, filename;
    private final Map<FunctionType, VynCallable> callables;

    public Script(final String code, final String fileName) {
        this.code = code;
        this.filename = fileName;
        this.callables = new HashMap<>();
    }

    public void load(final Player playerVar, final Set<Script> globalScripts, final Deque<Script> delayedScripts) {
        try {
            environment = VynMain.loadLines(code);

            for (final FunctionType type : FunctionType.values()) {
                try {
                    callables.put(type, environment.getFunction(type.getName()));
                } catch (final Exception ignored) {
                }
            }

            if (callables.isEmpty()) {
                state = State.IMPORTABLE;
                return;
            }

            state = State.LOADED;
            globalScripts.add(this);
        } catch (final Exception e) {
            if (e instanceof DelayedScriptException) {
                delayedScripts.add(this);
                return;
            }

            state = State.ERROR;
            playerVar.sendMessage("§6(" + filename + ") §cError in script during loading: " + e.getMessage());
        }

        System.out.println("Loaded .vyn script: " + filename + " (state=" + state + ")");
    }

    public void call(final Player playerVar) {
        call(playerVar, FunctionType.ON_TICK);
    }

    public void call(final Player playerVar, final FunctionType functionType) {
        call(playerVar, functionType, Collections.emptyList());
    }

    public void call(final Player playerVar, final FunctionType functionType, final List<Value> values) {
        if (state != State.LOADED)
            return;

        try {
            final VynCallable callable = callables.get(functionType);
            if (callable != null)
                callable.call(environment, values);
        } catch (final Exception e) {
            state = State.ERROR;
            if (playerVar != null)
                playerVar.sendMessage("§6(" + filename + ") §cError in script during " + functionType.getName() + ": " + e.getMessage());

            log.error(e.getMessage(), e);
        }
    }

    void setState(final State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public String getCode() {
        return code;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public String getFilename() {
        return filename;
    }

    public enum State {
        LOADED,
        IMPORTABLE,
        ERROR,
        EXCLUDED
    }

    public enum FunctionType {
        ON_TICK("onTick"),
        ON_DAMAGE("onDamage"),
        ON_SWING_HAND("onSwingHand"),
        ON_PLAY_SOUND("onPlaySound");

        private final String name;

        FunctionType(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}