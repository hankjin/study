package jindongh.dns;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.Maps;

public class CancelResolveTimeoutThreadTest {
	class DelayedResolveURITask extends ResolveURITask {
		long delayMillis;
		public DelayedResolveURITask(InternalDNSCacheStore store, URI uri, long delayMillis) {
			super(store, uri, DNSResolver.getInstance());
			this.delayMillis = delayMillis;
		}
		public InetSocketAddress call() {
			this.setStarted(true);
			try {
				Thread.sleep(delayMillis);
			} catch (InterruptedException e) {}
			return super.call();
		}
	}
	private Map.Entry<ResolveURITask, Future<InetSocketAddress>> fakeTaskWithDelay(long delayMillis) throws Exception {
		// fake a task which an extra timeout
		ResolveURITask task = new DelayedResolveURITask(
				InternalDNSCacheTestUtil.store,
				InternalDNSCacheTestUtil.SAMPLE_URI,
				delayMillis);
		Future<InetSocketAddress> future = InternalDNSCacheTestUtil.store.getExecutor().submit(task);
		Map.Entry<ResolveURITask, Future<InetSocketAddress>> entry = Maps
				.immutableEntry(task, future);
		return entry;
	}

	@Test
	public void test() throws Exception {
		Map.Entry<ResolveURITask, Future<InetSocketAddress>> entry;
		// no delay
		entry = fakeTaskWithDelay(0);
		InternalDNSCacheTestUtil.store.getTasks().add(entry);
		Thread.sleep(TimeUnit.SECONDS.toMillis(1)
				+ InternalDNSCacheTestUtil.store.getResolveTimeoutMillis());
		Assert.assertTrue(entry.getValue().isDone());
		Assert.assertTrue(!entry.getValue().get().isUnresolved());

		// timeout, sleep 1+timeout to make sure it will timout
		entry = fakeTaskWithDelay(TimeUnit.SECONDS.toMillis(5) + InternalDNSCacheTestUtil.store.getResolveTimeoutMillis());
		InternalDNSCacheTestUtil.store.getTasks().add(entry);
		Thread.sleep(TimeUnit.SECONDS.toMillis(1)
				+ InternalDNSCacheTestUtil.store.getResolveTimeoutMillis());
		Assert.assertTrue(entry.getValue().isCancelled());
	}

}
