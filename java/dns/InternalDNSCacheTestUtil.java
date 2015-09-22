package jindongh.dns;

import java.net.InetSocketAddress;
import java.net.URI;



public class InternalDNSCacheTestUtil {
	static final String SAMPLE_HOST = "queue.amazonaws.com";
	static final int SAMPLE_PORT_1 = 8080;
	static final int SAMPLE_PORT_2 = 8081;
	static final int CYCLE_SECONDS = 5;
	static final int EXPIRE_SECONDS = 10;
	static final int TIMEOUT_MILLIS = 500;
	static final int MAX_RETRY = 2;
	static InternalDNSCacheStore store = newStore();

	static final URI SAMPLE_URI = URI.create("http://queue.amazonaws.com:8080");
	static final InetSocketAddress SAMPLE_ADDRESS = new InetSocketAddress(SAMPLE_HOST, SAMPLE_PORT_1);
	static InternalDNSCacheStore newStore() {
		return new InternalDNSCacheStore(CYCLE_SECONDS, EXPIRE_SECONDS, TIMEOUT_MILLIS, MAX_RETRY);
	}
}

