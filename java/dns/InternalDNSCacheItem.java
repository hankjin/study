package jindongh.dns;

import java.net.InetSocketAddress;

/**
 * Cache item.
 * @author hankjohn
 *
 */
class InternalDNSCacheItem {
	public InternalDNSCacheItem(InetSocketAddress address) {
		this.address = address;
		lastAccessTime = System.currentTimeMillis();
		lastUpdateTime = System.currentTimeMillis();
	}
	/**
	 * Address.
	 */
	private InetSocketAddress address;
	/**
	 * last access time.
	 */
	private long lastAccessTime;
	/**
	 * last update time.
	 */
	private long lastUpdateTime;

	public InetSocketAddress getAddress() {
		lastAccessTime = System.currentTimeMillis();
		return address;
	}
	
	public void setAddress(InetSocketAddress address) {
		this.address = address;
		this.lastUpdateTime = System.currentTimeMillis();
	}
	
	public long getLastAccessTime() {
		return lastAccessTime;
	}
	
	public long getLastUpdateTime() {
		return lastUpdateTime;
	}
}