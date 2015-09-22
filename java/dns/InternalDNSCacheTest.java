package jindongh.dns;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;

public class InternalDNSCacheTest {

    static InternalDNSCache dns = InternalDNSCache.getInstance();
    static InternalDNSCacheStore store;
    static DNSResolver resolver;
    private static final int CYCLE_SECONDS = 1;
    private static final int EXPIRE_SECONDS = 3;
    @BeforeClass
    public static void setup() {
        resolver = Mockito.spy(DNSResolver.getInstance());
        store = Mockito.spy(InternalDNSCacheTestUtil.store);
        store.setResolver(resolver);
        store.setCycleSeconds(CYCLE_SECONDS);
        store.setExpireSeconds(EXPIRE_SECONDS);
        store.setResolveTimeoutMillis(TimeUnit.SECONDS.toMillis(1));
        dns.setInternalDNSCacheStore(store);
        dns.start();
    }
    public void shutdown() {
        dns.onDestroy();
    }
    @Test
    public void testNormal() throws Exception {
        URI uri1 = URI.create("http://queue.amazonaws.com:80");
        InetSocketAddress address1 = new InetSocketAddress("queue.amazonaws.com", 80);
        URI uri2 = URI.create("http://s3.amazonaws.com:80");
        InetSocketAddress address2 = new InetSocketAddress("s3.amazonaws.com", 80);
        URI uri3 = URI.create("https://dynamodb.us-east-1.amazonaws.com:443");
        InetSocketAddress address3 = new InetSocketAddress("dynamodb.amazonaws.com", 443);

        // miss cache
        InetSocketAddress address = dns.getAddress(uri1);
        Mockito.verify(store, Mockito.times(1)).getAddressFromCache(uri1);
        Mockito.verify(resolver, Mockito.atLeastOnce()).getAddressFromDNS(uri1, store.getMaxRetries());
        Assert.assertEquals(address1, address);
        final long firstUpdateTime1 = store.getItems().get(uri1).getLastUpdateTime();

        // hit cache, so it works fine when DNS is down.
        address = dns.getAddress(uri1);
        Mockito.verify(store, Mockito.times(2)).getAddressFromCache(uri1);
        Assert.assertEquals(address1, address);

        // another item
        address = dns.getAddress(uri2);
        Assert.assertEquals(address2, address);
        Mockito.verify(store, Mockito.timeout(1)).getAddressFromCache(uri2);
        Mockito.verify(resolver, Mockito.times(1)).getAddressFromDNS(uri2, store.getMaxRetries());
        final long firstUpdateTime2 = store.getItems().get(uri2).getLastUpdateTime();

        // 3rd item
        address = dns.getAddress(uri3);
        Assert.assertEquals(3, store.getItems().size());

        // wait for background update task
        Thread.sleep(TimeUnit.SECONDS.toMillis(CYCLE_SECONDS + 2));

        // verify background update is working
        // uri1: last update time is updated
        final long currentUpdateTime1 = store.getItems().get(uri1).getLastUpdateTime();
        Assert.assertTrue(firstUpdateTime1 != currentUpdateTime1);
        address = dns.getAddress(uri1);
        // uri1: address is not updated
        Assert.assertEquals(address1, address);
        // uri2: last update time is updated
        final long currentUpdateTime2 = store.getItems().get(uri2).getLastUpdateTime();
        Assert.assertTrue(firstUpdateTime2 != currentUpdateTime2);
        // uri2: address is not changed
        address = dns.getAddress(uri2);
        Assert.assertEquals(address2, address);

        // wait until address 3 expire
        Thread.sleep(TimeUnit.SECONDS.toMillis(EXPIRE_SECONDS - CYCLE_SECONDS));
        Assert.assertEquals(2, store.getItems().size());

        // we can get address 3 back
        address = dns.getAddress(uri3);
        Assert.assertEquals(address3, address);
        Assert.assertEquals(3, store.getItems().size());
    }
}
