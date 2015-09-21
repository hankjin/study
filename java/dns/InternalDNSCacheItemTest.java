package jindongh.dns;

import java.net.InetSocketAddress;

import org.junit.Assert;
import org.junit.Test;

public class InternalDNSCacheItemTest {

	private static final String SAMPLE_HOST = "queue.amazonaws.com";
	private static final int SAMPLE_PORT_1 = 8080;
	private static final int SAMPLE_PORT_2 = 8081;
	@Test
	public void test() {
		InetSocketAddress address = new InetSocketAddress(SAMPLE_HOST, SAMPLE_PORT_1);
		final long beforeCreate = System.currentTimeMillis();
		InternalDNSCacheItem item = new InternalDNSCacheItem(address);
		final long afterCreate = System.currentTimeMillis();
		Assert.assertTrue(beforeCreate <= item.getLastAccessTime());
		Assert.assertTrue(item.getLastUpdateTime() <= afterCreate);
		Assert.assertTrue(beforeCreate <= item.getLastAccessTime());
		Assert.assertTrue(item.getLastUpdateTime() <= afterCreate);
		// get 
		Assert.assertEquals(address, item.getAddress());
		final long afterGet = System.currentTimeMillis();
		// access time is updated
		Assert.assertTrue(item.getLastUpdateTime() <= afterCreate);
		Assert.assertTrue(item.getLastAccessTime() >= afterCreate);
		Assert.assertTrue(item.getLastAccessTime() <= afterGet);
		
		// set 
		InetSocketAddress address_2 = new InetSocketAddress(SAMPLE_HOST, SAMPLE_PORT_2);
		item.setAddress(address);
		final long afterSet = System.currentTimeMillis();
		// access time is updated
		Assert.assertTrue(item.getLastAccessTime() <= afterGet);
		Assert.assertTrue(item.getLastUpdateTime() <= afterSet);
		Assert.assertTrue(item.getLastUpdateTime() >= afterCreate);
		Assert.assertEquals(address_2, item.getAddress());
	}

}
