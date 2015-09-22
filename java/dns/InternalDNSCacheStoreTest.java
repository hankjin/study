package jindongh.dns;


import java.net.InetSocketAddress;
import java.net.URI;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;



public class InternalDNSCacheStoreTest {
    InternalDNSCacheStore store = InternalDNSCacheTestUtil.store;
    @Test
    public void testRefreshCache() {
        store.getItems().clear();
        // 1st item
        URI uri1 = InternalDNSCacheTestUtil.SAMPLE_URI;
        URI uri2 = URI.create("http://queue.amazonaws.com:8888");
        InetSocketAddress address1 = InternalDNSCacheTestUtil.SAMPLE_ADDRESS;
        InetSocketAddress address2 = new InetSocketAddress("queue.amazonaws.com", 8888);
        // put uri 1
        store.refreshCache(uri1, address1);
        Assert.assertEquals(address1, store.getAddressFromCache(uri1));
        // update uri 1
        store.refreshCache(uri1, address2);
        Assert.assertEquals(address2, store.getAddressFromCache(uri1));
        // put uri 2
        store.refreshCache(uri2, address1);
        Assert.assertEquals(address1, store.getAddressFromCache(uri2));
        // update uri 2
        store.refreshCache(uri2, address2);
        Assert.assertEquals(address2, store.getAddressFromCache(uri2));
    }

    @Test
    public void testStartStop() {
        InternalDNSCacheStore store = InternalDNSCacheTestUtil.newStore();
        Assert.assertTrue(store.getExecutor().getActiveCount() == 0);
        store.start();
        Assert.assertTrue(store.getExecutor().getActiveCount() == 2);
        store.onDestroy();
        Assert.assertTrue(store.getExecutor().isShutdown());
    }

    @Test
    public void testGetAddressFromCache() {
        store.getItems().clear();
        // cache miss
        InetSocketAddress address = store.getAddressFromCache(InternalDNSCacheTestUtil.SAMPLE_URI);
        Assert.assertNull(address);
        InternalDNSCacheItem item = new InternalDNSCacheItem(InternalDNSCacheTestUtil.SAMPLE_ADDRESS);
        store.getItems().put(InternalDNSCacheTestUtil.SAMPLE_URI, item);
        // cache hit
        address = store.getAddressFromCache(InternalDNSCacheTestUtil.SAMPLE_URI);
        Assert.assertEquals(InternalDNSCacheTestUtil.SAMPLE_ADDRESS, address);
    }

    @Test
    public void testGetAddress() {
        store.getItems().clear();
        DNSResolver resolver = Mockito.spy(DNSResolver.getInstance());
        store.setResolver(resolver);
        InetSocketAddress address = store.getAddressFromCache(InternalDNSCacheTestUtil.SAMPLE_URI);
        Assert.assertNull(address);
        // get address
        address = store.getAddress(InternalDNSCacheTestUtil.SAMPLE_URI);
        Assert.assertEquals(InternalDNSCacheTestUtil.SAMPLE_ADDRESS, address);
        Mockito.verify(resolver, Mockito.times(1))
            .getAddressFromDNS(InternalDNSCacheTestUtil.SAMPLE_URI, store.getMaxRetries());
        // cache item
        Assert.assertEquals(1, store.getItems().size());
    }
}
