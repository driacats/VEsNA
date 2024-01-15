package stage;

import org.json.JSONObject;

// JaCaMo
import jason.asSyntax.Literal;
import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import websocket.WsClient;

// Websockets
import javax.websocket.ContainerProvider;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import java.net.URI;

// Java Sets
import java.util.Arrays; 
import java.util.Set;

import java.util.HashSet;

public class Stage extends Artifact{

    // Global default variables
    private final String addObjectIntentName = "add_object";
    private final String addRelativeObjectIntentName = "add_relative_object";
    private final String removeObjectIntentName = "remove_object";
    private final String addActorIntentName = "add_actor";
    private final String address = "127.0.0.1";
    private final int port = 9080;
    Set<String> relatives = new HashSet<>(Arrays.asList("left of", "right of", "front of", "behind of", "on"));
    Infos infos = new Infos();

    // WsClient client;

    // public Stage() throws Exception{
        // System.out.println("Ci siamo");
        // client = new WsClient();
        // System.out.println("Ancora");
        // client.startConnection(address, port);
        // System.out.println("[STAGE] Connection on 127.0.0.1:9080");
    // }

    public String sendRequest(Infos newInfos) throws Exception {
        String payload = newInfos.buildPayload();
        System.out.println("[STAGE] Payload: " + payload);
        WsClient client = new WsClient();
        client.startConnection(address, port);
        StringBuilder answer = new StringBuilder(client.sendMessage(payload));
        // client.sendMessage(payload);
        System.out.println("[STAGE] Get Answer: " + answer.toString());
        client.stopConnection();
        return answer.toString();
        // return "Sent";
    }

    Infos organizeInfo(String infos){
        System.out.println("[STAGE] organizeInfo");
        Infos newInfos = new Infos();
        JSONObject objInfo = new JSONObject(infos);
        System.out.println("[STAGE] organizeInfo, intent");
        if (objInfo.has("intent")){
            switch (objInfo.getString("intent")){
                case addObjectIntentName:
                    newInfos.globAddition = true;
                    break;
                case addRelativeObjectIntentName:
                    newInfos.relAddition = true;
                    break;
                case removeObjectIntentName:
                    newInfos.removal = true;
                    break;
                case addActorIntentName:
                    newInfos.actorAddition = true;
                    break;
                default:
                    System.out.println("[STAGE] ERROR: Intent name not available.");
            }
        } else{
            System.out.println("[STAGE] ERROR: Intent name not provided");
        }
        if (objInfo.has("obj") && !objInfo.isNull("obj"))
            newInfos.objName = objInfo.getString("obj");
        if (objInfo.has("posX") && !objInfo.isNull("posX"))
            newInfos.posX = objInfo.getString("posX");
        if (objInfo.has("posY") && !objInfo.isNull("posY"))
            newInfos.posY = objInfo.getString("posY");
        if (objInfo.has("relative") && !objInfo.isNull("relative"))
            newInfos.posRel = objInfo.getString("relative");
        if (objInfo.has("relName") && !objInfo.isNull("relName"))
            newInfos.objRel = objInfo.getString("relName");
        if (objInfo.has("actorName") && !objInfo.isNull("actorName"))
            newInfos.actorName = objInfo.getString("actorName");
        if (objInfo.has("port") && !objInfo.isNull("port"))
            newInfos.port = objInfo.getInt("port");
        return newInfos;
    }
        
    @OPERATION
    void addObjectGlobal(String obj, String posX, String posY, OpFeedbackParam<Literal> result){
        JSONObject JsonInfo = new JSONObject();
        JsonInfo.put("intent", addObjectIntentName);
        JsonInfo.put("obj", obj);
        JsonInfo.put("posX", posX);
        JsonInfo.put("posY", posY);
        Infos newInfos = organizeInfo(JsonInfo.toString());
        try{
            result.set(Literal.parseLiteral(sendRequest(newInfos).replaceAll("\\P{Print}", "")));
        }catch(Exception e){
            System.out.println(e);
        }
    }

    @OPERATION
    void addObjectRelative(String obj, String posRel, String objRel, OpFeedbackParam<Literal> result){
        JSONObject JsonInfo = new JSONObject();
        JsonInfo.put("intent", addRelativeObjectIntentName);
        JsonInfo.put("obj", obj);
        JsonInfo.put("relative", posRel);
        JsonInfo.put("relName", objRel);
        Infos newInfos = organizeInfo(JsonInfo.toString());
        try{
            result.set(Literal.parseLiteral(sendRequest(newInfos).replaceAll("\\P{Print}", "")));
        }catch(Exception e){
            System.out.println(e);
        }
    }

    @OPERATION
    void addActor(String actorName, String posX, String posY, int port, OpFeedbackParam<Literal> result){
        JSONObject JsonInfo = new JSONObject();
        JsonInfo.put("intent", addActorIntentName);
        JsonInfo.put("posX", posX);
        JsonInfo.put("posY", posY);
        JsonInfo.put("port", port);
        JsonInfo.put("actorName", actorName);
        Infos newInfos = organizeInfo(JsonInfo.toString());
        System.out.println("[STAGE] newInfos: " + newInfos.buildPayload());
        try{
            result.set(Literal.parseLiteral(sendRequest(newInfos).replaceAll("\\P{Print}", "")));
        }catch(Exception e){
            System.out.println(e);
        }
    }

    @OPERATION
    void removeObject(String objRel, OpFeedbackParam<Literal> result){
        System.out.println("[STAGE] removeObject");
        JSONObject JsonInfo = new JSONObject();
        JsonInfo.put("intent", removeObjectIntentName);
        JsonInfo.put("relName", objRel);
        System.out.println("[STAGE] json created: " + JsonInfo.toString());
        Infos newInfos = organizeInfo(JsonInfo.toString());
        System.out.println("[STAGE] info organized");
        try{
            System.out.println("[STAGE] sending request");
            result.set(Literal.parseLiteral(sendRequest(newInfos).replaceAll("\\P{Print}", "")));
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
}
