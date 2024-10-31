package body;

import java.net.URI;
import java.net.URISyntaxException;

import jason.asSemantics.*;
import jason.asSyntax.*;

public class WebSocketSingleton {
    private static WebSocketSingleton instance = null;
    private static WsClient client;

    private WebSocketSingleton() {
        try {
            URI uri = new URI("ws://localhost:9080");
            client = new WsClient(uri);
            client.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static WebSocketSingleton getInstance() {
        if (instance == null) {
            instance = new WebSocketSingleton();
        }
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return instance;
    }

    public void sendMessage(String message) {
        client.send(message);
    }
}