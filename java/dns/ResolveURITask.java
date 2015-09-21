package jindongh.dns;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Callable;

import com.google.common.annotations.VisibleForTesting;

class ResolveURITask implements Callable<InetSocketAddress> {
	private URI uri;
	private boolean started;
	private InternalDNSCacheStore store;
	public ResolveURITask(InternalDNSCacheStore store, URI uri) {
		this.store = store;
		this.uri = uri;
		started = false;
	}
	public InetSocketAddress call() {
		started = true;
		InetSocketAddress address = getAddressFromDNS(uri, store.getMaxRetries());
		store.refreshCache(uri, address);
		return address;
	}

	/**
	 * Get address from dns.
	 * 
	 * it is a blocked call.
	 * @param uri
	 * @return 
	 */
	static InetSocketAddress getAddressFromDNS(URI uri, int maxRetries) {
		InetSocketAddress address = resolveAddress(uri.getHost(), uri.getPort());
		while (address.isUnresolved() && (maxRetries--) > 0) {
			address = resolveAddress(uri.getHost(), uri.getPort());
		}
		return address;
	}

	@VisibleForTesting
	static InetSocketAddress resolveAddress(String host, int port) {
		return new InetSocketAddress (host, port);
	}

	public URI getURI() {
		return uri;
	}
	public boolean isStarted() {
		return started;
	}
	public void setStarted(boolean started) {
		this.started = started;
	}
};
