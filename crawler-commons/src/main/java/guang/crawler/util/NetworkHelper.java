package guang.crawler.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class NetworkHelper {

	public static String getIPAddress() throws UnknownHostException {
		return InetAddress.getLocalHost().getHostAddress();
	}

	public static String getLocalHostName() throws UnknownHostException {
		return InetAddress.getLocalHost().getCanonicalHostName();
	}

	public static synchronized boolean isPortAvailable(int port) {
		try (Socket socket = new Socket()) {
			socket.bind(new InetSocketAddress(port));
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	public synchronized static int nextAvailablePort(int from, int to) {
		for (int i = from; i < to; i++) {
			int port = i;
			if (NetworkHelper.isPortAvailable(port)) {
				return port;
			}
		}
		return -1;
	}
}
