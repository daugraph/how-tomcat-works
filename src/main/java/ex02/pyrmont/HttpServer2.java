package ex02.pyrmont;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 1. package with command:
 *    mvn -s settings.xml clean package -DskipTests
 * 2. run with command:
 *    java -cp target/tiny-tomcat-1.0.0.jar ex02.pyrmont.HttpServer1
 * 3. then you can process servlet resources use curl command:
 *    curl http://localhost:8080/servlet/PrimitiveServlet
 */
public class HttpServer2 {
    public static final String SHUTDOWN = "/shutdown";
    private boolean shutdown = false;

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
            Socket socket = null;
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                socket = serverSocket.accept();
                inputStream = socket.getInputStream();
                outputStream = socket.getOutputStream();

                Request request = new Request(inputStream);
                request.parse();
                Response response = new Response(outputStream);
                response.setRequest(request);

                if (request.getUri().startsWith("/servlet/")) {
                    ServletProcessor2 processor = new ServletProcessor2();
                    processor.process(request, response);
                } else {
                    StaticResourceProcessor staticResourceProcessor = new StaticResourceProcessor();
                    staticResourceProcessor.process(request, response);
                }

                shutdown = request.getUri().equals(SHUTDOWN);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    inputStream.close();
                    outputStream.close();
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static void main(String[] args) {
        HttpServer1 httpServer1 = new HttpServer1();
        httpServer1.await();
    }
}

