package jindongh.dns;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.RateLimiter;

public class RefreshCacheThread extends Thread {
    private InternalDNSCacheStore store;
    private RateLimiter rateLimiter;
    public RefreshCacheThread(InternalDNSCacheStore store) {
        this.store = store;
        rateLimiter = RateLimiter.create(1);
    }

    /**
     * update cache.
     */
    @VisibleForTesting
    void reallyRun() {
        int itemSize = store.getItems().size();
        if (itemSize > 0) {
            rateLimiter.setRate(itemSize / store.getCycleSeconds());
        }
        final long expire = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(store.getExpireSeconds());

        for (Map.Entry<URI, InternalDNSCacheItem> entry : store.getItems().entrySet()) {
            // expire
            if (entry.getValue().getLastAccessTime() < expire) {
                store.getItems().remove(entry.getKey());
                continue;
            }
            // speed limit
            rateLimiter.acquire();
            ResolveURITask task = new ResolveURITask(store, entry.getKey(), store.getResolver());
            Future<InetSocketAddress> future = store.getExecutor().submit(task);
            // register timeout check task.
            Map.Entry<ResolveURITask, Future<InetSocketAddress>> item
                = Maps.immutableEntry(task, future);
            store.getTasks().add(item);
        }
        emicMetrics();
    }

    private void emicMetrics() {
        //TODO
    }

    /**
     * update cache thread.
     */
    public void run() {
        while (true) {
            reallyRun();
        }
    }
}
