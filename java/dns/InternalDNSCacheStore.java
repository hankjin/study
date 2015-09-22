package jindongh.dns;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Internal DNS Cache Store.
 * Data:
 *  1. Map<URI, InetSocketAddress> dns cache items.
 *  2. BlockingQueue<Future> dns resolve tasks
 * Threads:
 *  1. Cancel resolve timeout.
 *  2. Refresh items
 *  3. Resolve DNS
 * @author hankjohn
 *
 */
public class InternalDNSCacheStore {
    private Map<URI, InternalDNSCacheItem> items;
    private long cycleSeconds;
    private long expireSeconds;
    private long resolveTimeoutMillis;
    private int maxRetries;
    private ThreadPoolExecutor executor;
    private DNSResolver resolver;
    private BlockingQueue<Map.Entry<ResolveURITask,Future<InetSocketAddress>>> tasks;
    /**
     * refresh items + timeout check + resolve(*2)
     */
    private static final int INTERNAL_DNS_CACHE_THREAD_NUM = 4;
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
        this.cycleSeconds = cycleSeconds;
        this.expireSeconds = expireSeconds;
        this.resolveTimeoutMillis = resolveTimeoutMillis;
        this.maxRetries = maxRetries;
        this.resolver = DNSResolver.getInstance();
        final BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(DNS_RESOLVE_REQUEST_QUEUE);
        ThreadFactory factory = new ThreadFactoryBuilder()
            .setNameFormat("internal-dns-cache-%d")
            .build();
        tasks = new LinkedBlockingDeque<Map.Entry<ResolveURITask,Future<InetSocketAddress>>>();
        executor = new ThreadPoolExecutor(INTERNAL_DNS_CACHE_THREAD_NUM,
                INTERNAL_DNS_CACHE_THREAD_NUM,
                0, TimeUnit.SECONDS,
                queue, factory);
    }
    public void start() {
        executor.submit(new CancelResolveTimeoutThread(this));
        executor.submit(new RefreshCacheThread(this));
    }
    /**
     * destroy.
     */
    public void onDestroy() {
        executor.shutdownNow();
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
            address = resolver.getAddressFromDNS(uri, maxRetries);
            refreshCache(uri, address);
        }
        return address;
    }
    /**
     * Get address from cache.
     * @param uri
     * @return null if it doesn't exist, otherwise a resolved address.
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
     * Refresh/add cache item, if it is not null and resolved.
     * @param uri
     * @param address it can be null or unresolved, in which cases it will be ignored.
     */
    public void refreshCache(final URI uri, final InetSocketAddress address) {
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
    public long getResolveTimeoutMillis() {
        return this.resolveTimeoutMillis;
    }
    public int getMaxRetries() {
        return this.maxRetries;
    }
    BlockingQueue<Map.Entry<ResolveURITask,Future<InetSocketAddress>>> getTasks() {
        return tasks;
    }
    Map<URI, InternalDNSCacheItem> getItems() {
        return items;
    }
    ThreadPoolExecutor getExecutor() {
        return executor;
    }
    long getCycleSeconds() {
        return cycleSeconds;
    }
    void setCycleSeconds(long cycleSeconds) {
        this.cycleSeconds = cycleSeconds;
    }
    long getExpireSeconds() {
        return expireSeconds;
    }
    void setExpireSeconds(long expireSeconds) {
        this.expireSeconds = expireSeconds;
    }
    DNSResolver getResolver() {
        return resolver;
    }
    void setResolver(DNSResolver resolver) {
        this.resolver = resolver;
    }
    void setResolveTimeoutMillis(long resolveTimeoutMillis) {
        this.resolveTimeoutMillis = resolveTimeoutMillis;
    }
}
