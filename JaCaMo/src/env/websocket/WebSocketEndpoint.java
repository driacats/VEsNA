import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

public class YourWebSocketEndpoint extends Endpoint {

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        System.out.println("Connessione aperta");

        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                System.out.println("Messaggio ricevuto: " + message);
                // Gestisci il messaggio ricevuto qui
            }
        });

        // Invia un messaggio al server WebSocket
        session.getAsyncRemote().sendText("Ciao, server!");
    }

    @Override
    public void onClose(Session session, javax.websocket.CloseReason closeReason) {
        System.out.println("Connessione chiusa: " + closeReason.getReasonPhrase());
    }

    @Override
    public void onError(Session session, Throwable throwable) {
        System.out.println("Errore: " + throwable.getMessage());
    }
}
