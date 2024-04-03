package stage;

import org.json.JSONObject;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
// import cartago.*;
import jason.asSyntax.Literal;

import java.util.List;
import java.util.ArrayList;

import stage.websocket.WsServer;
import stage.websocket.WsClient;
import java.net.URI;
import java.net.URISyntaxException;

import stage.websocket.WsClientMsgHandler;

public class Actor extends Artifact implements WsClientMsgHandler{

    private int port = 9080;
    private String host = "localhost";
    private WsClient conn;

    // public Actor() throws URISyntaxException {
    //     conn = new WsClient(new URI("ws://localhost:9080"));
    //     conn.setMsgHandler(this::handleMsg);
    //     conn.connect();
    //     defineObsProperty("sight", "none");
	// }
    
    @OPERATION
    public void init() throws URISyntaxException {
        conn = new WsClient(new URI("ws://localhost:9080"));
        conn.setMsgHandler(this::handleMsg);
        conn.connect();
        defineObsProperty("sight", "none");
        defineObsProperty("distance", "none");
        defineObsProperty("veRotation", "up");
	}


    private void send(JSONObject json){
        conn.send(json.toString());
        // System.out.println("[actor] Message sent");
    }

    @OPERATION
    public void act(){
        System.out.println("[actor] Sending message");
        JSONObject json = new JSONObject();
        json.put("action", "connection_start");
        send(json);
    }

    @OPERATION
    public void rotate(String direction){
        JSONObject json = new JSONObject();
        json.put("action", "rotate");
        JSONObject data = new JSONObject();
        data.put("direction", direction);
        json.put("data", data);
        send(json);
    }

    @OPERATION
    public void move(){
        JSONObject json = new JSONObject();
        json.put("action", "move");
        send(json);
    }
     
    @Override
    public void handleMsg(String msg){
        System.out.println("[actor] received " + msg);
        JSONObject json = new JSONObject(msg);
        if (json.getString("perception").equals("sight")){
            JSONObject data = json.getJSONObject("data");
            String object = data.getString("object");
            
            String rotationStr = data.getString("rotation");
            String[] stringValues = rotationStr.substring(1, rotationStr.length() - 1).split(",\\s*");
        
            // Converti le stringhe in numeri float
            float[] rotation = new float[stringValues.length];
            for (int i = 0; i < stringValues.length; i++) {
                rotation[i] = Float.parseFloat(stringValues[i]);
            }
            String rotLit = "";
             if (rotation[1] < 4.0)
                rotLit = "down";
            if (rotation[1] < 2.0)
                rotLit = "left";
            if (rotation[1] < 1.0)
                rotLit = "up";
            if (rotation[1] < 0.0)
                rotLit = "right";
            // else
            //     rotLit = "invalid";
            // else
            //     rotLit = "invalid";
            getObsProperty("veRotation").updateValue(Literal.parseLiteral(rotLit));

            // System.out.println("[actor] DEBUG: I saw " + object);
            float distance = Float.parseFloat(data.getString("distance"));
            if (distance < 3.5)
                getObsProperty("distance").updateValue(Literal.parseLiteral("touch"));
            else if (distance < 5.0)
                getObsProperty("distance").updateValue(Literal.parseLiteral("near"));
            else if (distance < 8.0)
                getObsProperty("distance").updateValue(Literal.parseLiteral("medium"));
            else
                getObsProperty("distance").updateValue(Literal.parseLiteral("far"));

            if (object.toLowerCase().contains("wall"))
                getObsProperty("sight").updateValue(Literal.parseLiteral("wall"));
            else
                getObsProperty("sight").updateValue(Literal.parseLiteral(object));
        }
        else if (json.getString("perception").equals("rotation")){
            String rotationStr = json.getString("rotation");
            String[] stringValues = rotationStr.substring(1, rotationStr.length() - 1).split(",\\s*");
        
            // Converti le stringhe in numeri float
            float[] rotation = new float[stringValues.length];
            for (int i = 0; i < stringValues.length; i++) {
                rotation[i] = Float.parseFloat(stringValues[i]);
            }
            System.out.println("[rotation] " + rotation[1]);
            String rotLit = "";
            if (rotation[1] < 4.0)
                rotLit = "down";
            if (rotation[1] < 2.0)
                rotLit = "left";
            if (rotation[1] < 1.0)
                rotLit = "up";
            if (rotation[1] < 0.0)
                rotLit = "right";
            else
                rotLit = "invalid";
            getObsProperty("veRotation").updateValue(Literal.parseLiteral(rotLit));
        }
    }
}
