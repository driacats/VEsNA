package websocketserver;

import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.OnClose;
import jakarta.websocket.Session;
import jakarta.websocket.CloseReason;
import jakarta.websocket.server.ServerEndpoint;

@ServerEndpoint("/endpoint")
public class Endpoint {
    static String resultString = null;

    @OnOpen
    public void onOpen(Session session){
        resultString = null;
        System.out.println("[ENDPOINT] Connected " + session.getId());
    }
    
    @OnMessage
    public void onMessage(Session session, String message) {
        System.out.println("[ENDPOINT] " + message);
        resultString = message;
        System.out.println("[ENDPOINT] updated resultString: " + resultString);
        try{
            session.getBasicRemote().sendText("Done!");
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason){
        System.out.println("[ENDPOINT] Session: " + session.getId());
        System.out.println("[ENDPOINT] Closing because: " + closeReason);
    }
}