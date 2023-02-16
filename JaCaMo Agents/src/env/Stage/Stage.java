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
import java.util.HashSet;

public class Stage extends Artifact{

    // Global default variables
    public static String exitResponse = "Disconnected!";
    public static String notAvailableResponse = "The place is not available!";
    public static String notFoundResponse = "The chosen object is not in the scene";
    public static String notCorrectPlace = "This object cannot be placed in this position";
    public static String error = "Error in adding object.";
    
    public BeliefBase bb;

    Set<String> relatives = new HashSet<>(Arrays.asList("left of", "right of", "front of", "behind of", "on"));

    // Infos are transmitted all inside an info string and then subdivided into an array
    public String info = "";
    Infos infos = new Infos();

    // Function that returns the message with the new object id
    private static String doneResponse(String name){
        return "Object added, you can refer to it as " + name.split("!")[1];
    }

    // Function that performs the html request to add the new object
    // It sends a request to "http://address-of-localhost-in-lan:8081/object/posX/posY"
    // Takes in input the object with its position, returns the result string from Unity
    public String sendRequest() throws Exception {
        StringBuilder result = new StringBuilder();
        // URL url = new URL(Inet4Address.getLocalHost().getHostAddress() + ":8081");
        URL url = new URL("http://127.0.0.1:8081");
        System.out.println("[STAGE] Connection on " + url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // Connect to the url
        conn.setRequestMethod("POST"); // Set the request method to GET
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        String payload = infos.buildPayload();
        byte[] out = payload.getBytes(StandardCharsets.UTF_8);
        OutputStream stream = conn.getOutputStream();
        stream.write(out);
        try (BufferedReader reader = new BufferedReader( // try to connect and read answer
            new InputStreamReader(conn.getInputStream()))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    result.append(line);
                }
        }
        return result.toString(); // Return the Unity answer
    }

    // Function called by the agent used to get the desired position parameters
    @OPERATION 
    void getInfo(String newInfo){
        info += newInfo;
    }
    
    // Function that given the pos string sets the corresponding posX and posY
    // TODO: regex
    void setInfo(){
        // Perform some preprocess on position string
        info = info.replace("param", ",");
        info = info.replace("(", "");
        info = info.replace("[", "");
        info = info.replace(")", "");
        info = info.replace("]", "");
        String[] infoList = info.split(","); // Split string on commas
        
        if (infoList[0].equals("remove"))
            infos.setRemoval(infoList[4]);
        else if (infoList[0].equals("move"))
            infos.setMotion(infoList[4], infoList[6]);
        else {
            if (relatives.contains(infoList[0]))
                infos.setRelativePosition(infoList[6], infoList[2], infoList[4]);
            else
                infos.setGlobalPosition(infoList[6], infoList[2], infoList[4]);
        }
    }

    // Function that adds the specified object to the stage
    // Takes in input the object, gets the position, tries to add it and returns the result
    @OPERATION
    void addObject(OpFeedbackParam<String> result){
        setInfo(); // Set position, after that we will have in posX and posY global variables the right values
        System.out.println("[STAGE] Add " + infos.objName + " function");
        try{
            String response = sendRequest();//infos.getRequest()); // performs the request and get the response
            // set the result string depending on Unity answer
            if (response.contains("DONE")){
                result.set(doneResponse(response));
                defineObsProperty(infos.objName); // add position
            }
            else if (response.contains("PLACE NOT AVAILABLE"))
                result.set(notAvailableResponse);
            else
                result.set(error);
        }catch(Exception e){
            System.out.println(e);
            result.set(error);
        }
        infos.reset(); // resets the positions
    }

    @OPERATION
    void removeObject(OpFeedbackParam<String> result){
        setInfo();
        System.out.println("[STAGE] Remove " + infos.objRel + " function");
        try{
            String response = sendRequest(); //("/remove/" + infos.objRel);
            System.out.println("[REMOVE] " + response);
            if (response.contains("DONE"))
                result.set("Object removed correctly");
            else if (response.contains("OBJECT NOT FOUND"))
                result.set(notFoundResponse);
            else
                result.set(error);
        }catch(Exception e){
            System.out.println(e);
            result.set(error);
        }
        infos.reset();
    }

    @OPERATION
    void moveActor(OpFeedbackParam<String> result){
        setInfo();
        System.out.println("[STAGE] Move Actor function");
        try{
            String response = sendRequest(); //"/move/" + infos.objRel + "/" + infos.direction);
        }catch(Exception e){
            System.out.println(e);
        }
    }

    // Function to exit
    @OPERATION String exit(OpFeedbackParam<String> result){
        System.out.println("[STAGE] Exit");
        try{
            String response = sendRequest();//"/exit");
            if (response.contains("EXIT"))
                result.set("Goodbye!");
            else
                result.set("Problem!");
        }catch(Exception e){
            System.out.println(e);
        }
        return error;
    }
}
