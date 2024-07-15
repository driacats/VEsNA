package stage;

import org.json.JSONObject;

import cartago.*;
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
    
    @OPERATION
    public void init() throws URISyntaxException {
        conn = new WsClient(new URI("ws://localhost:9080"));
        conn.setMsgHandler(this::handleMsg);
        conn.connect();
	}


    private void send(JSONObject json){
        conn.send(json.toString());
    }

    @OPERATION
    public void act(){
        JSONObject json = new JSONObject();
        json.put("action", "connection_start");
        send(json);
    }

    @OPERATION
    public void whereiam(){
        JSONObject json = new JSONObject();
        json.put("action", "request");
        JSONObject data = new JSONObject();
        data.put("information", "position");
        data.put("object", "me");
        json.put("data", data);
        send(json);
    }

    @OPERATION
    public void whereis(String object){
        JSONObject json = new JSONObject();
        json.put("action", "request");
        JSONObject data = new JSONObject();
        data.put("information", "position");
        data.put("object", object);
        json.put("data", data);
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

    @INTERNAL_OPERATION
    void handleSight(JSONObject data){

        String object = data.getString("object");
        String direction = data.getString("rotation");
        String side = data.getString("side");

        if (data.has("distance")){
            String distance = data.getString("distance");
            // signal("seen", Literal.parseLiteral(object.toLowerCase()), Literal.parseLiteral(direction), Literal.parseLiteral(distance), Literal.parseLiteral(side));
            defineObsProperty("seen", Literal.parseLiteral(object.toLowerCase()), Literal.parseLiteral(direction), Literal.parseLiteral(distance), Literal.parseLiteral(side));
        } else {
            // signal("seen", Literal.parseLiteral(object.toLowerCase()), Literal.parseLiteral(direction), Literal.parseLiteral(side));
            defineObsProperty("seen", Literal.parseLiteral(object.toLowerCase()), Literal.parseLiteral(direction), Literal.parseLiteral(side));
        }
    }

    private void handlePosition(JSONObject data){
        float x = data.getFloat("x");
        float y = data.getFloat("y");
        signal("at", x, y);
    }
     
    @Override
    public void handleMsg(String msg){
        System.out.println("[env] received " + msg);
        JSONObject json = new JSONObject(msg);
        if (json.getString("perception").equals("sight")){
            JSONObject data = json.getJSONObject("data");
            handleSight(data);
        }
        if (json.getString("perception").equals("position")){
            JSONObject data = json.getJSONObject("data");
            handlePosition(data);
        }
    }
}
