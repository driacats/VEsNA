// Author: Andrea Gatti
// This package implements the communication between the JaCaMo agents and Unity.
//* I make use of VsCode BetterComments syntax.

package stage;

// Java Connection
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.Inet4Address;
import java.nio.charset.StandardCharsets;
import java.io.OutputStream;

import org.json.JSONObject;

// JaCaMo
import jason.asSyntax.Literal;
import static jason.asSyntax.ASSyntax.*;
import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import cartago.util.agent.Agent;
import jason.bb.BeliefBase;

// Java Sets
import java.util.Arrays; 
import java.util.Set;

import javax.json.JsonObject;

import java.util.HashSet;

public class Stage extends Artifact{

    // Global default variables
    // OLD VARIABLES
    public static String exitResponse = "Disconnected!";
    public static String notAvailableResponse = "The place is not available!";
    public static String notFoundResponse = "The chosen object is not in the scene";
    public static String notCorrectPlace = "This object cannot be placed in this position";
    public static String error = "Error in adding object.";

    // NEW VARIABLES
    private final String addObjectIntentName = "add_object";
    private final String addRelativeObjectIntentName = "add_relative_object";
    private final String removeObjectIntentName = "remove_object";
    private final String addActorIntentName = "add_actor";

    public BeliefBase bb;

    Set<String> relatives = new HashSet<>(Arrays.asList("left of", "right of", "front of", "behind of", "on"));

    // Infos are transmitted all inside an info string and then subdivided into an array
    public String info = "";
    Infos infos = new Infos();

    // Function that returns the message with the new object id
    private static String doneResponse(String res){
        return "Object added, you can refer to it as " + res;
    }

    public String sendRequest(Infos newInfos) throws Exception {
        String payload = newInfos.buildPayload();
        System.out.println("[STAGE] Connection on 127.0.0.1:9080");
        Client client = new Client();
        client.startConnection("127.0.0.1", 9080);
        StringBuilder answer = new StringBuilder(client.sendMessage(payload));
        System.out.println("[STAGE] Get Answer: " + answer.toString());
        client.stopConnection();
        return answer.toString();
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
        System.out.println("[STAGE] organizeInfo, obj");
        if (objInfo.has("obj") && !objInfo.isNull("obj"))
            newInfos.objName = objInfo.getString("obj");
        System.out.println("[STAGE] organizeInfo, posX");
        if (objInfo.has("posX") && !objInfo.isNull("posX"))
            newInfos.posX = objInfo.getString("posX");
        System.out.println("[STAGE] organizeInfo, posY");
        if (objInfo.has("posY") && !objInfo.isNull("posY"))
            newInfos.posY = objInfo.getString("posY");
        System.out.println("[STAGE] organizeInfo, relative");
        if (objInfo.has("relative") && !objInfo.isNull("relative"))
            newInfos.posRel = objInfo.getString("relative");
        System.out.println("[STAGE] organizeInfo, relName");
        if (objInfo.has("relName") && !objInfo.isNull("relName"))
            newInfos.objRel = objInfo.getString("relName");
        System.out.println("[STAGE] organizeInfo, actorName");
        if (objInfo.has("actorName") && !objInfo.isNull("actorName"))
            newInfos.actorName = objInfo.getString("actorName");
        
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
    void addActor(String actorName, String posX, String posY, OpFeedbackParam<Literal> result){
        JSONObject JsonInfo = new JSONObject();
        JsonInfo.put("intent", addActorIntentName);
        JsonInfo.put("posX", posX);
        JsonInfo.put("posY", posY);
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
