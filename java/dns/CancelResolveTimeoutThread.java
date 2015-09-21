package jindongh.dns;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.log4j.Logger;


/**
 * Cancel the task if DNS resolve is too slow.
 * This condition can be simulated with sudo iptables -A INPUT -p udp --sport 53 -j DROP
 * @author hankjohn
 *
 */
class CancelResolveTimeoutThread extends Thread {
	private static final Logger LOG = Logger.getLogger(CancelResolveTimeoutThread.class);
	private InternalDNSCacheStore store;
	
	public CancelResolveTimeoutThread(InternalDNSCacheStore store) {
		this.store = store;
	}
	/**
	 * Wait for the task for <link>resolveTimeoutMillis</link> milliseconds, cancel it if it time out.
	 * @param task task to wait for.
	 */
	private void handleTask(ResolveURITask task, Future<InetSocketAddress> future) {
		try {
			future.get(store.getResolveTimeoutMillis(), TimeUnit.MILLISECONDS);
		} catch (TimeoutException ie) {
			LOG.error("DNSTimeout for " + task.getURI());
			future.cancel(true);
		} catch (Exception e) {
			LOG.error("DNSResolve error", e);
		}
	}
	/**
	 * poll task from <link>tasks</link> BlockingQueue.
	 */
	public void run() {
		while (true) {
			Map.Entry<ResolveURITask, Future<InetSocketAddress>> entry = null;
			try {
				entry = store.getTasks().poll(store.getCycleSeconds(), TimeUnit.SECONDS);				
			} catch (InterruptedException e) {
				continue;
			}
			if (entry == null) {
				continue;
			} else if (!entry.getKey().isStarted()) {
				store.getTasks().add(entry);
				continue;
			} else {
				handleTask(entry.getKey(), entry.getValue());
			}
		}
	}
}