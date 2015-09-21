package jindongh.dns;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;

class FakeInternalDNSCacheStore extends InternalDNSCacheStore {
	Map<Map.Entry<String,Integer>, InetSocketAddress> mockAddress
		= new HashMap<Map.Entry<String, Integer>, InetSocketAddress>();
	public FakeInternalDNSCacheStore(final long cycleSeconds, final long expireSeconds, final long resolveTimeoutMillis, final int maxRetries) {
		super(cycleSeconds, expireSeconds, resolveTimeoutMillis, maxRetries);
	}
	InetSocketAddress resolveAddress(String host, int port) {
		return mockAddress.get(Maps.immutableEntry(host, port));
	}
	void mockAddress(String host, int port, InetSocketAddress address) {
		mockAddress.put(Maps.immutableEntry(host, port), address);
	}
}
class DelayedResolveURITask extends ResolveURITask {
	long delayMillis;
	public DelayedResolveURITask(InternalDNSCacheStore store, URI uri, long delayMillis) {
		super(store, uri);
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

public class InternalDNSCacheTestUtil {
	static final String SAMPLE_HOST = "queue.amazonaws.com";
	static final int SAMPLE_PORT_1 = 8080;
	static final int SAMPLE_PORT_2 = 8081;
	static final int CYCLE_SECONDS = 5;
	static final int EXPIRE_SECONDS = 10;
	static final int TIMEOUT_MILLIS = 500;
	static final int MAX_RETRY = 2;
	static FakeInternalDNSCacheStore store = new FakeInternalDNSCacheStore(CYCLE_SECONDS, EXPIRE_SECONDS, TIMEOUT_MILLIS, MAX_RETRY);
	static final URI SAMPLE_URI = URI.create("http://queue.amazonaws.com:8080");
}

