package jindongh.dns;

import java.net.InetSocketAddress;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class ResolveURITaskTest {

	@Test
	public void testSucceed() {
		InternalDNSCacheStore store = Mockito.mock(InternalDNSCacheStore.class);
		ResolveURITask task = new ResolveURITask(store, InternalDNSCacheTestUtil.SAMPLE_URI, DNSResolver.getInstance());
		InetSocketAddress expectedAddress = new InetSocketAddress(
				InternalDNSCacheTestUtil.SAMPLE_URI.getHost(),
				InternalDNSCacheTestUtil.SAMPLE_URI.getPort());
		InetSocketAddress address = task.call();
		Assert.assertEquals(expectedAddress, address);
		Mockito.verify(store, Mockito.times(1)).refreshCache(InternalDNSCacheTestUtil.SAMPLE_URI, address);
	}

	private void testFailure(InetSocketAddress invalidAddress) {
		InternalDNSCacheStore store = Mockito.mock(InternalDNSCacheStore.class);
		// fake resolve fail.
		DNSResolver resolver = Mockito.mock(DNSResolver.class);
		Mockito.when(resolver.getAddressFromDNS(InternalDNSCacheTestUtil.SAMPLE_URI, store.getMaxRetries()))
			.thenReturn(invalidAddress);
		ResolveURITask task = new ResolveURITask(store, InternalDNSCacheTestUtil.SAMPLE_URI, resolver);
		// call
		InetSocketAddress address = task.call();
		Assert.assertEquals(invalidAddress, address);
		// refresh cache not called
		Mockito.verify(store, Mockito.never()).refreshCache(InternalDNSCacheTestUtil.SAMPLE_URI, address);
	}

	@Test
	public void testUnresolvedAddress() {
		testFailure(InetSocketAddress.createUnresolved("", 1234));
	}

	@Test
	public void testNullAddress() {
		testFailure(null);
	}
}
