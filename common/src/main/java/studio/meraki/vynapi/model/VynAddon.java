package studio.meraki.vynapi.model;

import me.abdelaziz.runtime.Environment;
import studio.meraki.vynapi.handler.script.ScriptHandler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public abstract class VynAddon {

    private final Set<String> events;
    public abstract Consumer<Environment> onEnable();

    public VynAddon() {
        this(null);
    }

    public VynAddon(final String event, final String... other) {
        events = new HashSet<>();

        if (event != null) {
            events.add(event);
            if (other != null)
                events.addAll(Arrays.asList(other));
        }

        ScriptHandler.registerSTD(this);
    }

    public Set<String> getEvents() {
        return events;
    }
}
