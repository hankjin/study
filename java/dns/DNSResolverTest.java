package jindongh.dns;

import java.net.InetSocketAddress;
import java.net.URI;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class DNSResolverTest {

	@Test
	public void testRetry() {
		InetSocketAddress address;
		InetSocketAddress expectAddress = new InetSocketAddress("queue.amazonaws.com", 8080);
		InetSocketAddress fail0 = InetSocketAddress.createUnresolved("a", 1);
		InetSocketAddress fail1 = InetSocketAddress.createUnresolved("a", 2);
		InetSocketAddress fail2 = InetSocketAddress.createUnresolved("a", 3);
		// retry 3 times, get expected
		DNSResolver mockResolver = Mockito.spy(DNSResolver.getInstance());
		Mockito.doReturn(fail0)
			.doReturn(fail1)
			.doReturn(fail2)
			.doReturn(expectAddress)
			.when(mockResolver).resolveAddress("a", 3);
		address = mockResolver.getAddressFromDNS(URI.create("http://a:3"), 3);
		Assert.assertEquals(expectAddress, address);
		
		// retry 2 times, got fail2
		Mockito.doReturn(fail0)
		.doReturn(fail1)
		.doReturn(fail2)
		.doReturn(expectAddress)
		.when(mockResolver).resolveAddress("a", 3);
		address = mockResolver.getAddressFromDNS(URI.create("http://a:3"), 2);
		Assert.assertEquals(fail2, address);
		
		// retry 1 times, got fail 1
		Mockito.doReturn(fail0)
		.doReturn(fail1)
		.doReturn(fail2)
		.doReturn(expectAddress)
		.when(mockResolver).resolveAddress("a", 3);
		address = mockResolver.getAddressFromDNS(URI.create("http://a:3"), 1);
		Assert.assertEquals(fail1, address);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testNullHostname() {
		DNSResolver.getInstance().resolveAddress(null, 1234);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidPort() {
		DNSResolver.getInstance().resolveAddress(InternalDNSCacheTestUtil.SAMPLE_HOST, -1);
	}
}
