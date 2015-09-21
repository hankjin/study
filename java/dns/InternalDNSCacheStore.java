package jindongh.dns;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.RateLimiter;


public class InternalDNSCacheStore extends Thread {
    private Map<URI, InternalDNSCacheItem> items;
    private RateLimiter rateLimiter;
    private long cycleSeconds;
    private long expireSeconds;
    private long resolveTimeoutMillis;
    private int maxRetries;
    private boolean keepRunning;
    private ExecutorService executor;
    private BlockingQueue<Future<InetSocketAddress>> tasks;
    private static final int DNS_RESOLVE_THREAD_NUM = 2;
    private static final int DNS_RESOLVE_REQUEST_QUEUE = 100;
    /**
     * Create Internal DNS store.
     * @param cycleSeconds internal to update
     * @param expireSeconds
     * @param resolveTimeoutMillis
     * @param maxRetries
     */
    public InternalDNSCacheStore(final long cycleSeconds, final long expireSeconds, final long resolveTimeoutMillis, final int maxRetries) {
        items = new ConcurrentHashMap<URI, InternalDNSCacheItem>();
        rateLimiter = RateLimiter.create(1);
        keepRunning = true;
        this.cycleSeconds = cycleSeconds;
        this.expireSeconds = expireSeconds;
        this.resolveTimeoutMillis = resolveTimeoutMillis;
        this.maxRetries = maxRetries;
        final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(DNS_RESOLVE_REQUEST_QUEUE);
        int threadNum = DNS_RESOLVE_THREAD_NUM + 1; // extra one thread to cancel timeout task.
        executor = new ThreadPoolExecutor(threadNum, threadNum, 0, TimeUnit.SECONDS, queue);
        tasks = new LinkedBlockingDeque<Future<InetSocketAddress>>();
        executor.submit(new DNSTimeoutChecker());
    }
    /**
     * Get address.
     * First check cache, return if it exists, otherwise access DNS and return. 
     * @param uri
     * @return not null, but maybe unresolved if it doesn't exist in cache and dns resolve fail.
     */
    public InetSocketAddress getAddress(URI uri) {
        InetSocketAddress address = getAddressFromCache(uri);
        if (address != null) {
            return address;
        } else {
            return getAddressFromDNS(uri);
        }
    }
    /**
     * Get address from cache.
     * @param uri 
     * @return null if it doesn't exist, otherwise it is 
     */
    public InetSocketAddress getAddressFromCache(URI uri) {
        InternalDNSCacheItem item = items.get(uri);
        if (null == item) {
            return null;
        } else {
            return item.getAddress();
        }
    }

    /**
     * Get address from dns.
     * 
     * it is a blocked call.
     * @param uri
     * @return 
     */
    public InetSocketAddress getAddressFromDNS(URI uri) {
        InetSocketAddress address = new InetSocketAddress (uri.getHost(), uri.getPort());
        while (address.isUnresolved() && (maxRetries--) > 0) {
            address = new InetSocketAddress (uri.getHost(), uri.getPort());
        }
        refreshCache(uri, address);
        return address;
    }

    /**
     * Refresh/add cache item, if it is not null and resolved.
     * @param uri
     * @param address it can be null or unresolved, in which cases it will be ignored.
     */
    private void refreshCache(final URI uri, final InetSocketAddress address) {
        if (address == null || address.isUnresolved()) {
            return ;
        }
        InternalDNSCacheItem item = items.get(uri);
        if (item == null) {
            items.put(uri, new InternalDNSCacheItem(address));
        } else {
            item.setAddress(address);
        }
    }

    /**
     * update cache.
     * @throws InterruptedException when the thread is closed.
     */
    private void reallyRun() throws InterruptedException {
        int itemSize = items.size();
        if (itemSize > 0) {
            rateLimiter.setRate(itemSize / cycleSeconds);
        }
        final long expire = System.currentTimeMillis() - TimeUnit.SECONDS.toMillis(expireSeconds);
        for (Map.Entry<URI, InternalDNSCacheItem> entry : items.entrySet()) {
            // expire
            if (entry.getValue().getLastAccessTime() < expire) {
                items.remove(entry.getKey());
                continue;
            }
            // speed limit
            rateLimiter.acquire();
            Future<InetSocketAddress> task = executor.submit(new Callable<InetSocketAddress>() {
                public InetSocketAddress call() {
                    InetSocketAddress address = getAddressFromDNS(entry.getKey());
                    return address;
                }
            });
            // register timeout check task.
            tasks.add(task);
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
        while (keepRunning) {
            try {
                reallyRun();
            } catch (InterruptedException e) {
                executor.shutdownNow();
            }
        }
    }

    /**
     * Cancel the task if DNS resolve is too slow.
     * This condition can be simulated with sudo iptables -A INPUT -p udp --sport 53 -j DROP
     * @author hankjohn
     *
     */
    final class DNSTimeoutChecker extends Thread {

        /**
         * Wait for the task for <link>resolveTimeoutMillis</link> milliseconds, cancel it if it time out.
         * @param task task to wait for.
         */
        private void handleTask(final Future<InetSocketAddress> task) {
            try {
                task.wait(resolveTimeoutMillis);
            } catch (InterruptedException ie) {
                task.cancel(true);
            }
        }
        /**
         * poll task from <link>tasks</link> BlockingQueue.
         */
        public void run() {
            while (keepRunning) {
                try {
                    Future<InetSocketAddress> task = tasks.poll(resolveTimeoutMillis, TimeUnit.MILLISECONDS);
                    handleTask(task);
                } catch (InterruptedException e) {
                    continue;
                }
            }
        }
    }
    /**
     * Cache item.
     * @author hankjohn
     *
     */
    final class InternalDNSCacheItem {
        public InternalDNSCacheItem(InetSocketAddress address) {
            this.address = address;
            lastAccessTime = System.currentTimeMillis();
            lastUpdateTime = System.currentTimeMillis();
        }
        /**
         * Address.
         */
        private InetSocketAddress address;
        /**
         * last access time.
         */
        private long lastAccessTime;
        /**
         * last update time.
         */
        private long lastUpdateTime;

        public InetSocketAddress getAddress() {
            final long now = System.currentTimeMillis();
            lastAccessTime = now;
            return address;
        }

        public void setAddress(InetSocketAddress address) {
            this.address = address;
            this.lastUpdateTime = System.currentTimeMillis();
        }

        public long getLastAccessTime() {
            return lastAccessTime;
        }

        public long getLastUpdateTime() {
            return lastUpdateTime;
        }
    }
}
