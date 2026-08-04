package studio.meraki.vynapi.handler.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@SuppressWarnings("unused")
public final class BackgroundLoopHandler {

    private static final Logger log = LoggerFactory.getLogger(BackgroundLoopHandler.class);

    private static final Map<String, BackgroundLoop> loops = new ConcurrentHashMap<>();

    private BackgroundLoopHandler() {
    }

    public static void tickAll() {
        loops.values().forEach(BackgroundLoop::tick);
    }

    public static void startLoop(final String name, final Runnable task, final int tickDelay) {
        if (loops.containsKey(name))
            endLoop(name);

        loops.put(name, new BackgroundLoop(task, tickDelay));
    }

    public static void waitTicks(final String id, final int ticks, final Runnable task) {
        if (isLoopRunning(id))
            return;

        startLoop(id, () -> {
            try {
                task.run();
            } finally {
                endLoop(id);
            }
        }, ticks);
    }

    public static void pauseLoop(final String name) {
        final BackgroundLoop loop = loops.get(name);
        if (loop != null)
            loop.pause();
    }

    public static void resumeLoop(final String name) {
        final BackgroundLoop loop = loops.get(name);
        if (loop != null)
            loop.resume();
    }

    public static void endLoop(final String name) {
        final BackgroundLoop loop = loops.remove(name);
        if (loop != null)
            loop.stop();
    }

    public static boolean isLoopRunning(final String name) {
        return loops.containsKey(name);
    }

    public static void clearAll() {
        loops.values().forEach(BackgroundLoop::stop);
        loops.clear();
    }

    private static final class BackgroundLoop {

        private int tickCounter;
        private final Runnable task;
        private final int tickDelay;
        private final AtomicBoolean running, paused;

        private BackgroundLoop(final Runnable task, final int tickDelay) {
            this.task = task;
            this.tickCounter = 0;
            this.tickDelay = tickDelay;
            this.running = new AtomicBoolean(true);
            this.paused = new AtomicBoolean(false);
        }

        public void tick() {
            if (!running.get() || paused.get())
                return;

            if (++tickCounter < tickDelay)
                return;

            tickCounter = 0;

            try {
                task.run();
            } catch (final Throwable t) {
                log.error(t.getMessage(), t);
            }
        }

        public void pause() {
            paused.set(true);
        }

        public void resume() {
            paused.set(false);
        }

        public void stop() {
            running.set(false);
        }
    }
}