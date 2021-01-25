package ex01.pyrmont;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * The server started listening 8080 port.
 *
 * the resource dir is /project/webroot, you can ask for http://localhost:8080/index.html
 * Also you can request the url that not exit,then the server response error.
 *
 */
public class HttpServer {
	public static final String WEB_ROOT = System.getProperty("user.dir") + File.separator + "webroot";
	private static final String SHUT_DOWN = "/SHUTDOWN";
	public boolean shutdown = false;

	public void await() {
		ServerSocket serverSocket = null;
		int port = 8080;

		try {
			serverSocket = new ServerSocket(port, 1, InetAddress.getByName("127.0.0.1"));
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}

		while (!shutdown) {
			try {
				Socket socket = serverSocket.accept();
				InputStream inputStream = socket.getInputStream();
				OutputStream outputStream = socket.getOutputStream();

				Request request = new Request(inputStream);
				request.parse();

				Response response = new Response(outputStream);
				response.setRequest(request);
				response.sendStaticResource();

				socket.close();

				shutdown = request.getUri().equals(SHUT_DOWN);
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
	}

	public static void main(String[] args) {
		HttpServer httpServer = new HttpServer();
		httpServer.await();
	}
}
