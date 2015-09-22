package jindongh.dns;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class RefreshCacheThreadTest {

    InternalDNSCacheStore store = InternalDNSCacheTestUtil.store;
    @Test
    public void testExpire() throws Exception {
        long expireSeconds = 3;
        store.getItems().clear();
        store.setExpireSeconds(expireSeconds);
        store.setCycleSeconds(1);
        InternalDNSCacheItem item = new InternalDNSCacheItem(InternalDNSCacheTestUtil.SAMPLE_ADDRESS);
        store.getItems().put(InternalDNSCacheTestUtil.SAMPLE_URI, item);
        // not expire
        RefreshCacheThread thread = new RefreshCacheThread(store);
        thread.reallyRun();
        Assert.assertTrue(store.getItems().size() == 1);
        System.out.println("Item" + item.getLastAccessTime());
        // expired
        Thread.sleep(TimeUnit.SECONDS.toMillis(expireSeconds + 1));
        System.out.println("expired " + System.currentTimeMillis());
        thread.reallyRun();
        Assert.assertTrue(store.getItems().isEmpty());
    }

    @Test
    public void testTimeout() {
        store.getItems().clear();
        InternalDNSCacheItem item = new InternalDNSCacheItem(InternalDNSCacheTestUtil.SAMPLE_ADDRESS);
        store.getItems().put(InternalDNSCacheTestUtil.SAMPLE_URI, item);
        final long lastUpdateTime = item.getLastUpdateTime();
        store.setCycleSeconds(1); // really run finish in 1 seconds
        store.setExpireSeconds(1000);
        store.setResolveTimeoutMillis(100); // resolve timeout
        // scan
        RefreshCacheThread thread = new RefreshCacheThread(store);
        thread.reallyRun();
        final long newLastUpdateTime = item.getLastUpdateTime();
        Assert.assertTrue(newLastUpdateTime > lastUpdateTime);
        //
        DNSResolver resolver = Mockito.spy(DNSResolver.getInstance());
        store.setResolver(resolver);
        // mock error
        Mockito.doReturn(InetSocketAddress.createUnresolved("", 2))
        .when(resolver).getAddressFromDNS(InternalDNSCacheTestUtil.SAMPLE_URI, store.getMaxRetries());
        thread.reallyRun();
        Assert.assertEquals(newLastUpdateTime, item.getLastUpdateTime());
    }

    @Test
    public void testRateLimit() {
        store.getItems().clear();
        for (int i = 0; i < 10; i++) {
            store.getItems().put(URI.create("http://www."+i+".com:80"),
                    new InternalDNSCacheItem(InternalDNSCacheTestUtil.SAMPLE_ADDRESS));
        }
        long cycleSeconds = 5;
        store.setCycleSeconds(cycleSeconds);
        final long startTime = System.currentTimeMillis();
        RefreshCacheThread thread = new RefreshCacheThread(store);
        thread.reallyRun();
        final long endTime = System.currentTimeMillis();
        final double realMillis = endTime - startTime;
        final double expectedMillis = TimeUnit.SECONDS.toMillis(cycleSeconds);
        final double diffRate = (realMillis - expectedMillis) / expectedMillis;
        final double errorRate = 0.2;
        Assert.assertTrue(diffRate > -1 * errorRate);
        Assert.assertTrue(diffRate < errorRate);
    }
}
