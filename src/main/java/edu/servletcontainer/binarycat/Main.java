package edu.servletcontainer.binarycat;


import edu.servletcontainer.binarycat.network.NetworkServer;
import edu.servletcontainer.binarycat.servlet.ServletContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        log.info("Starting server");
        log.info("Creating Servlet Container");
        ServletContainer servletContainer = new ServletContainer();
        log.info("Creating network server");
        NetworkServer server = new NetworkServer(servletContainer);
        server.startServer();
    }
}
