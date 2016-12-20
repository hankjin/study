package jindongh.dns;

import java.net.InetSocketAddress;
import java.net.URI;

import com.google.common.annotations.VisibleForTesting;

public class DNSResolver {
	public static DNSResolver INSTANCE = new DNSResolver();
	
	private DNSResolver() {	
	}
	
	public static DNSResolver getInstance() {
		return INSTANCE;
	}
	
	/**
	 * Get address from dns.
	 * 
	 * it is a blocked call.
	 * @param uri
	 * @return 
	 */
	public InetSocketAddress getAddressFromDNS(URI uri, int maxRetries) {
		InetSocketAddress address = resolveAddress(uri.getHost(), uri.getPort());
		while (address.isUnresolved() && (maxRetries--) > 0) {
			address = resolveAddress(uri.getHost(), uri.getPort());
		}
		return address;
	}

	@VisibleForTesting
    InetSocketAddress resolveAddress(String host, int port) {
		return new InetSocketAddress (host, port);
	}
}
