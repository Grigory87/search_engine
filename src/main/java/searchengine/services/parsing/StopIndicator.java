package searchengine.services.parsing;

import java.util.concurrent.atomic.AtomicBoolean;

public class StopIndicator {
    private static AtomicBoolean stop;

    public static boolean getStop() {
        return stop.get();
    }
    public static void stop() {
        stop.set(true);
    }
    public static void newStopIndicator() {
        stop = new AtomicBoolean(false);
    }
}
