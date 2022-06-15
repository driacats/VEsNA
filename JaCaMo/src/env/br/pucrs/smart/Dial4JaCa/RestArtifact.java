/*
	Original file: https://github.com/jacamo-lang/jacamo-rest/blob/master/src/main/java/jacamo/rest/JCMRest.java
	Changed by: Débora Engelmann
	May 3, 2020
*/

package br.pucrs.smart.Dial4JaCa;


import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;

import javax.ws.rs.core.UriBuilder;

import org.apache.commons.io.FileUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.zookeeper.server.NIOServerCnxnFactory;
import org.apache.zookeeper.server.ServerCnxnFactory;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import jacamo.platform.DefaultPlatformImpl;

public class RestArtifact extends DefaultPlatformImpl {

    public static String JaCaMoZKAgNodeId = "/jacamo/agents";
    public static String JaCaMoZKDFNodeId = "/jacamo/df";
    
    protected HttpServer restHttpServer = null;

    protected static URI restServerURI = null;
    
    protected ServerCnxnFactory zkFactory = null;
    protected static String zkHost = null;
    protected static CuratorFramework zkClient;
    
    static public String getRestHost() {
        if (restServerURI == null)
            return null;
        else
            return restServerURI.toString();
    }
    
    static public String getZKHost() {
        return zkHost;
    }
    
    static {
        confLog4j();
    }
    
    @Override
    public void init(String[] args) throws Exception {
                        
        int restPort = 3280;
        int zkPort   = 2181;
        boolean useZK = false;

        // Used when deploying on heroku
        String webPort = System.getenv("PORT");
        if (webPort == null || webPort.isEmpty()) {
            restPort = 8080;
        } else {
            restPort = Integer.parseInt(webPort);
        }
        
        if (args.length > 0) {
            String la = "";
            for (String a: args[0].split(" ")) {
                if (la.equals("--restPort"))
                    try {
                        restPort = Integer.parseInt(a);
                    } catch (Exception e) {
                        System.err.println("[Dial4JaCa] The argument for restPort is not a number.");
                    }

                if (a.equals("--main")) {
                    useZK = true;
                }
                if (la.equals("--main"))
                    try {
                        zkPort = Integer.parseInt(a);
                    } catch (Exception e) {
                        System.err.println("[Dial4JaCa] The argument for restPort is not a number.");
                    }

                if (la.equals("--connect")) {
                    zkHost = a;
                    useZK = true;
                }
                la = a;
            }           
        }
        
        restHttpServer = startRestServer(restPort);
        
        if (useZK) {
            if (zkHost == null) {
                zkFactory  = startZookeeper(zkPort);
                System.out.println("[Dial4JaCa] Platform (zookeeper) started on "+zkHost);
            } else {
                System.out.println("[Dial4JaCa] Platform (zookeeper) running on "+zkHost);
            }
        }
        
    }

   
    @Override
    public void stop() {
        if (restHttpServer != null)
            try {
                restHttpServer.shutdown();
            } catch (Exception e) {}
        restHttpServer = null;

        if (zkFactory != null)
            try {
                zkFactory.shutdown();
            } catch (Exception e) {}
        zkFactory = null;

        if (zkClient != null)
            zkClient.close();
    }
    
    static void confLog4j() {
        try {
            ConsoleAppender console = new ConsoleAppender(); //create appender
            //configure the appender
            String PATTERN = "%d [%p|%c|%C{1}] %m%n";
            console.setLayout(new PatternLayout(PATTERN)); 
            console.setThreshold(Level.WARN);
            console.activateOptions();
            //add appender to any Logger (here is root)
            Logger.getRootLogger().addAppender(console);

            FileAppender fa = new FileAppender();
            fa.setName("FileLogger");
            fa.setFile("log/zk.log");
            fa.setLayout(new PatternLayout("%d %-5p [%c{1}] %m%n"));
            fa.setThreshold(Level.WARN);
            fa.setAppend(true);
            fa.activateOptions();
            
            //add appender to any Logger (here is root)
            Logger.getRootLogger().addAppender(fa);
            //repeat with all other desired appenders
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public HttpServer startRestServer(int port) {
        try {
            restServerURI = UriBuilder.fromUri("http://0.0.0.0/").port(port).build();
            
            RestAppConfig rc = new RestAppConfig();
            
            // get a server from factory
            HttpServer s = GrizzlyHttpServerFactory.createHttpServer(restServerURI, rc);
            
            System.out.println("[Dial4JaCa] JaCaMo Rest API is running on "+restServerURI);
            return s;
        } catch (javax.ws.rs.ProcessingException e) {           
            System.out.println("[Dial4JaCa] trying next port for rest server "+(port+1)+". e="+e);
            return startRestServer(port+1);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    File zkTmpDir = null;
    static String zkTmpFileName = "jcm-zookeeper";
    
    public ServerCnxnFactory startZookeeper(int port) {
        int numConnections = 500;
        int tickTime = 2000;

        try {
            cleanZKFiles();
            
            zkHost = InetAddress.getLocalHost().getHostAddress()+":"+port;

            zkTmpDir = Files.createTempDirectory(zkTmpFileName).toFile(); 
            ZooKeeperServer server = new ZooKeeperServer(zkTmpDir, zkTmpDir, tickTime);
            server.setMaxSessionTimeout(4000);
            
            ServerCnxnFactory factory = new NIOServerCnxnFactory();
            factory.configure(new InetSocketAddress(port), numConnections);
            factory.startup(server); // start the server.   

            getZKClient().create().creatingParentsIfNeeded().forPath(JaCaMoZKAgNodeId);
            getZKClient().create().creatingParentsIfNeeded().forPath(JaCaMoZKDFNodeId);
            return factory;
        } catch (java.net.BindException e) {
            System.err.println("[Dial4JaCa] Cannot start zookeeper, port "+port+" already used!");
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    void cleanZKFiles() {
        for (File f: FileUtils.getTempDirectory().listFiles()) {
            if (f.getName().startsWith(zkTmpFileName)) {
                try {
                    FileUtils.deleteDirectory(f);
                } catch (IOException e) {
                }
            }
        }
    }

    public static CuratorFramework getZKClient() {
        if (zkClient == null) {
            zkClient = CuratorFrameworkFactory.newClient(getZKHost(), new ExponentialBackoffRetry(1000, 3));
            zkClient.start();
        }
        return zkClient;
    }

}
