package jindongh.dns;



public class InternalDNSCacheStoreTest {

	private static final String SAMPLE_HOST = "queue.amazonaws.com";
	private static final int SAMPLE_PORT_1 = 8080;
	private static final int SAMPLE_PORT_2 = 8081;
	private static final int CYCLE_SECONDS = 5;
	private static final int EXPIRE_SECONDS = 10;
	private static final int TIMEOUT_MILLIS = 500;
	private static final int MAX_RETRY = 2;
	FakeInternalDNSCacheStore store = new FakeInternalDNSCacheStore(CYCLE_SECONDS, EXPIRE_SECONDS, TIMEOUT_MILLIS, MAX_RETRY);

}
