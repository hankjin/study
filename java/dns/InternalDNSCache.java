package jindongh.dns;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
/**
 * Internal DNS Cache, used to increase efficiency and fault tolerant.
 * 
 * Why do we need this while we already have system level solution(jvm cache and local dns cache)?
 * 
 * What is the advantage of this class?
 * 1. increase efficiency: 
 *    when the dns cache expire, the new InetSocketAddress function will generate UDP request, which
 *    is fast in most cases while it may be slow when the load is too high (every service has P100).
 * 2. fault tolerant: lwresd, unbound and dns server are all UDP services which may fail.
 *    when the load is high, it is possible that the packet is lost, and we will get
 *    UnresolvedAddressException.
 *
 * What is the payload of this class?
 * 1. We will have extra 3 threads.
 *    1st is used to iterate the cache items, clear the expired item and create task to update existing
 *    2nd is used to call new InetSocketAddress to resolve dns
 *    3rd is used to cancel the InetSocketAddress when the new InetSocketAddress timed out.
 * 2. We will have extra memory space.
 *    every cached item will use about 800Byte.
 * 
 * How to use it?
 * 1. Initialize(optional but strongly recommended): add all the URI you need to access at start up.
 *    Then you will never get high latency and UnresolvedAddressException.
 * 2. Replace new InetSocketAddress(xx) with InternalDNSCache.getInstance().getAddress()
 * @author hankjohn
 *
 */
public class InternalDNSCache {

    /** Interval to update the cache. */
    private static final int REFRESH_CYCLE_MINUTES = 1;

    /** Expire period, after X hours, the item will be cleaned up from cache. */
    private static final int EXPIRE_HOURS = 24;

    /** Retry to access DNS if failed. */
    public static final int MAX_RETRIES = 3;

    /** DNS resolve timeout, only used when update cache. */
    public static final int RESOLVE_TIMEOUT_SECONDS = 3;

    /** singleton instance. */
    private static final InternalDNSCache INSTANCE = new InternalDNSCache();

    /**
     * Singleton method to get instance.
     * @return singleton of InternalDNSCache.
     */
    public static InternalDNSCache getInstance() {
        return INSTANCE;
    }

    /** singleton instance. */
    private InternalDNSCache() {
        store = new InternalDNSCacheStore(
                TimeUnit.MINUTES.toSeconds(REFRESH_CYCLE_MINUTES),
                TimeUnit.HOURS.toSeconds(EXPIRE_HOURS),
                TimeUnit.SECONDS.toMillis(3),
                MAX_RETRIES
                );
        setInternalDNSCacheStore(store);
    }

    /**
     * change store.
     * @param store customized store.
     */
    public void setInternalDNSCacheStore(final InternalDNSCacheStore store) {
        if (store != null) {
            store.onDestroy();
        }
        this.store = store;
    }
    /**
     * On destroy, close the cache refresh thread.
     */
    public void onDestroy() {
        store.onDestroy();
    }

    /**
     * DNS cache store.
     */
    private InternalDNSCacheStore store;

    /**
     * Get address from cache, it will fall back to DNS when cache miss.
     * @param uri uri which include hostname and port, eg: http://queue.amazonaws.com:8080/.
     * @return resolved ip and port. eg: queue.amazonaws.com/1.2.3.4:8080
     */
    public InetSocketAddress getAddress(final URI uri) {
        return store.getAddress(uri);
    }

    /**
     * Get batch of addresses from cache, it can be used to initialize cache during start up.
     * @param uris URIs of DNS.
     * @return
     */
    public Map<URI, InetSocketAddress> getAddresses(final List<URI> uris) {
        Map<URI, InetSocketAddress> addresses = new HashMap<URI, InetSocketAddress>();
        for (URI uri : uris) {
            addresses.put(uri,  store.getAddress(uri));
        }
        return addresses;
    }
}
