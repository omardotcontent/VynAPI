package studio.meraki.vynapi.handler.other;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class DebugTextHandler {

    private static List<String> renderedTexts = Collections.emptyList();
    private static final List<String> textBuffer = new CopyOnWriteArrayList<>();

    private DebugTextHandler() {
    }

    public static List<String> getRenderedTexts() {
        return renderedTexts;
    }

    public static void addText(final String text) {
        textBuffer.add(text);
    }

    public static void onTickEnd() {
        renderedTexts = new ArrayList<>(textBuffer);

        textBuffer.clear();
    }
}