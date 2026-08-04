package studio.meraki.vynapi.model.function;

import me.abdelaziz.runtime.function.nat.NativeFunction;
import studio.meraki.vynapi.handler.other.DebugTextHandler;

public final class DebugText extends NativeFunction {

    public DebugText() {
        super((env, args) -> {
            if (args.size() != 1)
                throw new RuntimeException("debugText requires 1 arguments: text (String)");

            DebugTextHandler.addText(args.getFirst().toString());
            return null;
        });
    }
}