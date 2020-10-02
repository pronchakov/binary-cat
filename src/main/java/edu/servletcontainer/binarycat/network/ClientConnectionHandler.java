package edu.servletcontainer.binarycat.network;

import edu.servletcontainer.binarycat.servlet.ServletContainer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import java.io.*;
import java.net.Socket;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ClientConnectionHandler implements Runnable {

    private static final Logger log = LogManager.getLogger(ClientConnectionHandler.class);
    private Socket socket;
    private ServletContainer servletContainer;

    public ClientConnectionHandler(Socket socket, ServletContainer servletContainer) {
        this.socket = socket;
        this.servletContainer = servletContainer;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String firstLine = reader.readLine();
            if (!firstLine.startsWith("GET") || !firstLine.endsWith("HTTP/1.1")) {
                log.warn("Unknown request: " + firstLine);

                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                writer.write("HTTP/1.1 400 Bad Request\n\n\n");
                writer.flush();

                reader.close();
                writer.close();

                socket.close();
                return;
            }
            List<String> request = new ArrayList<>();
            request.add(firstLine);
            String line;
            while (!(line = reader.readLine()).isEmpty()) {
                request.add(line);
            }

            for (String s : request) {
                log.info("'" + s + "'");
            }

            String responseBody = null;
            try {
                responseBody = servletContainer.processRequest(request);
            } catch (ServletException e) {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                writer.write("HTTP/1.1 500 Internal Server Error\n\n\n");
                writer.flush();

                reader.close();
                writer.close();

                socket.close();
                return;
            }

            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write("HTTP/1.1 200 OK\n");
            writer.write("Date: ");
            writer.write(ZonedDateTime.now().format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss z")));
            writer.write("\n\n");
            writer.write(responseBody);
            writer.write("\n\n");
            writer.close();
            socket.close();
        } catch (IOException e) {
            log.error("Error reading request: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
