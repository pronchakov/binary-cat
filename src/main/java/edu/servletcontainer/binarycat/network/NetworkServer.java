package edu.servletcontainer.binarycat.network;

import edu.servletcontainer.binarycat.servlet.ServletContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NetworkServer {

    private static final Logger log = LogManager.getLogger(NetworkServer.class);
    private final int port = 8080;
    private ServerSocket serverSocket;
    private ServletContainer servletContainer;

    public NetworkServer(ServletContainer servletContainer) {
        this.servletContainer = servletContainer;
    }

    public void startServer() throws IOException {
        try {
            log.info("Network server port: {}", port);
            serverSocket = new ServerSocket(port);
            log.info("Server is now ready to handle client requests");
            while (true) {
                Socket socket = serverSocket.accept();
                log.info("");
                log.info("New connection from client {}", socket.getRemoteSocketAddress().toString());
                ClientConnectionHandler clientHandler = new ClientConnectionHandler(socket, servletContainer);
                Thread clientThread = new Thread(clientHandler);
                clientThread.start();
            }
        } catch (IOException e) {
            log.error("Cannot start server or get client connection: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Cannot create servlet container: {}", e.getMessage());
        }
    }

}
