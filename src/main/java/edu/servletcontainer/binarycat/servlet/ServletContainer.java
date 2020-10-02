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
                    WebServlet annotation = (WebServlet) clazz.getAnnotation(WebServlet.class);
                    String name = annotation.name();
                    String urlPattern = annotation.urlPatterns()[0];

                    log.info("Registering servlet {}, with pattern {}", name, urlPattern);
                    HttpServlet o = (HttpServlet) clazz.getConstructor().newInstance();
                    servlets.put(urlPattern, o);
                }
            }
        }
    }

    public String processRequest(List<String> request) throws ServletException, IOException {
        String resource = request.get(0).replace("GET ", "").replace(" HTTP/1.1", "");
        String[] split = resource.split("/");
        if (split.length != 3) {
            log.warn("Not a servlet request");
            throw new ServletException();
        }

        String contextPath = split[1];
        String urlPattern = "/" + split[2];

        BinaryCatRequest httpServletRequest = new BinaryCatRequest();
        BinaryCatResponse httpServletResponse = new BinaryCatResponse();
        HttpServlet servlet = servlets.get(urlPattern);

        if (servlet == null) {
            log.warn("There is no servlet for processing {}", urlPattern);
            throw new ServletException();
        }

        String servletClassName = servlet.getClass().getCanonicalName();
        log.info("Request will be handled with servlet {}", servletClassName);
        servlet.service(httpServletRequest, httpServletResponse);
        String result = httpServletResponse.getResult();
        log.info("Request was handled by {} with result: {}", servletClassName, result);

        return result;
    }

}
