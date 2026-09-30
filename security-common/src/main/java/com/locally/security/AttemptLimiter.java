package com.locally.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.util.HashMap;
import java.util.Map;

/** Bounded per-instance protection. Add an edge/distributed limiter when scaling replicas. */
public final class AttemptLimiter {
    private record Window(long expires, int attempts) {}
    private static final Map<String, Window> WINDOWS = new HashMap<>();
    private AttemptLimiter() {}
    public static synchronized void check(String key, int maximum, int seconds) {
        long now = System.currentTimeMillis();
        Window window = WINDOWS.get(key);
        if (window == null || window.expires() <= now) {
            if (WINDOWS.size() >= 10000) WINDOWS.entrySet().removeIf(e -> e.getValue().expires() <= now);
            if (WINDOWS.size() >= 10000) throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS);
            window = new Window(now + seconds * 1000L, 0);
        }
        if (window.attempts() >= maximum) throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS);
        WINDOWS.put(key, new Window(window.expires(), window.attempts() + 1));
    }
    public static synchronized void clear(String key) { WINDOWS.remove(key); }
}
