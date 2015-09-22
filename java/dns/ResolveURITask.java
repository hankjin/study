package jindongh.dns;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Callable;

class ResolveURITask implements Callable<InetSocketAddress> {
	private URI uri;
	private boolean started;
	private InternalDNSCacheStore store;
	private DNSResolver resolver;
	public ResolveURITask(InternalDNSCacheStore store, URI uri, DNSResolver resolver) {
		this.store = store;
		this.uri = uri;
		started = false;
		setResolver(resolver);
	}
	public InetSocketAddress call() {
		started = true;
		InetSocketAddress address = resolver.getAddressFromDNS(uri, store.getMaxRetries());
		if (address != null && !address.isUnresolved()) {
			store.refreshCache(uri, address);
		}
		return address;
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
	public void setResolver(DNSResolver resolver) {
		this.resolver = resolver;
	}
};
