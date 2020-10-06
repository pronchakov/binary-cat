package edu.servletcontainer.binarycat.servlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ServletContainer {

    private static final Logger log = LogManager.getLogger(ServletContainer.class);

    private Map<String, HttpServlet> servlets = new HashMap<>();

    public ServletContainer() throws Exception {
        File deployDir = new File("deploy/");
        File[] deployFiles = deployDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".war");
            }
        });
        // TODO: Periodically look for new files in deploy folder to proccess

        log.info("Found {} applications ready for deploy:", deployFiles.length);
        for (File deployFile : deployFiles) {
            log.info("File: {}", deployFile.getName());
            JarFile jarDeployFile = new JarFile(deployFile);
            Enumeration<JarEntry> jerEntries = jarDeployFile.entries();

            URL[] urls = { new URL("jar:file:" + deployFile.getAbsolutePath() +"!/WEB-INF/classes/") };
            URLClassLoader cl = URLClassLoader.newInstance(urls);

            while (jerEntries.hasMoreElements()) {
                JarEntry jarEntry = jerEntries.nextElement();
                if(jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")){
                    continue;
                }
                String className = jarEntry.getName().substring(16,jarEntry.getName().length()-6);
                className = className.replace('/', '.');
                Class clazz = cl.loadClass(className);
                if (clazz.getSuperclass().equals(HttpServlet.class)) {
                    // TODO: Support web.xml settings for servlet along with WebServlet annotation
                    WebServlet annotation = (WebServlet) clazz.getAnnotation(WebServlet.class);
                    String name = annotation.name();
                    String urlPattern = annotation.urlPatterns()[0]; // TODO: Use all url patterns instead of first only

                    log.info("Registering servlet {}, with pattern {}", name, urlPattern);
                    HttpServlet o = (HttpServlet) clazz.getConstructor().newInstance();
                    servlets.put(urlPattern, o);
                    // TODO: Separate servlets by applications using context path
                    // TODO: Add check for servlets that already exist
                }
            }
        }
    }

    public String processRequest(List<String> request) throws ServletException, IOException {

        // TODO: This is not the best way to parse HTTP Request ever. You can redesign it completely.

        String resource = request.get(0).replace("GET ", "").replace(" HTTP/1.1", "");
        String[] split = resource.split("/");
        if (split.length != 3) {
            log.warn("Not a servlet request");
            throw new ServletException();
        }

        String contextPath = split[1]; // TODO: Context path is never user used
        String urlPattern = "/" + split[2];

        BinaryCatRequest httpServletRequest = new BinaryCatRequest();
        for (int i = 1; i < request.size(); i++) {
            String headerLine = request.get(i);
            String[] splitHeader = headerLine.split(": ");
            String headerName = splitHeader[0];
            String headerValue = splitHeader[1];
            httpServletRequest.addHeader(headerName, headerValue);
        }

        BinaryCatResponse httpServletResponse = new BinaryCatResponse();
        HttpServlet servlet = servlets.get(urlPattern);

        if (servlet == null) {
            log.warn("There is no servlet for processing {}", urlPattern);
            throw new ServletException(); // TODO: Think about better way to handle exceptions at all
        }

        String servletClassName = servlet.getClass().getCanonicalName();
        log.info("Request will be handled with servlet {}", servletClassName);
        servlet.service(httpServletRequest, httpServletResponse);
        String result = httpServletResponse.getResult();
        log.info("Request was handled by {} with result: {}", servletClassName, result);

        return result;
    }

}
