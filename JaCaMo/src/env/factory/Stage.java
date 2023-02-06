// Author: Andrea Gatti
// This package implements the communication between the JaCaMo agents and Unity.
//! I make use of VsCode BetterComments syntax.

package stage;

// Java Connection
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

    // Set<String> relatives = new HashSet<>();
    // relatives.addAll(Arrays.asList("left of", "right of", "front of", "behind of", "on"));
    Set<String> relatives = new HashSet<>(Arrays.asList("left of", "right of", "front of", "behind of", "on"));

    // Infos are transmitted all inside an info string and then subdivided into an array
    public String info = "";
    // // Infos is the array of infomations from the agent.
    // // When adding an object it contains:
    // // [0]: object
    // // [1]: position on x axis
    // // [2]: position on y axis
    // // // In Java we need to initialize the three positions with empty strings in order to access them with index.
    // // public String[] infos = {"", "", ""};
    Infos infos = new Infos();

    // Function that returns the message with the new object id
    private static String doneResponse(String name){
        return "Object added, you can refer to it as " + name.split("!")[1];
    }

    // Function that performs the html request to add the new object
    // It sends a request to "http://address-of-localhost-in-lan:8081/object/posX/posY"
    // Takes in input the object with its position, returns the result string from Unity
    public static String sendRequest(String request) throws Exception {
        StringBuilder result = new StringBuilder();
        // InetAddress localhost = InetAddress.getLocalHost(); // Get the localhost properties
        // URL url = new URL("http://" + localhost.getHostAddress() + ":8081" + obj); // Concatenate the url string
        URL url = new URL("http://192.168.182.217:8081" + request.replace(" ", "%20")); // Concatenate the url string
        System.out.println("[STAGE] " + url);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // Connect to the url
        conn.setRequestMethod("GET"); // Set the request method to GET
        try (BufferedReader reader = new BufferedReader( // try to connect and read answer
            new InputStreamReader(conn.getInputStream()))) {
                for (String line; (line = reader.readLine()) != null; ) {
                    result.append(line);
                }
        }
        return result.toString(); // Return the Unity answer
    }

    // // // Function that simply resests the informations
    // // void resetInfo(){
    // //     info = "";
    // //     infos[0] = infos[1] = infos[2] = "";
    // // }

    // Function called by the agent used to get the desired position parameters
    @OPERATION 
    void getInfo(String newInfo){
        info += newInfo;
    }
    
    // Function that given the pos string sets the corresponding posX and posY
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
        else {
            if (relatives.contains(infoList[0]))
                infos.setRelativePosition(infoList[6], infoList[2], infoList[4]);
            else
                infos.setGlobalPosition(infoList[6], infoList[2], infoList[4]);
        } 
        // // if (infoList.length >= 6){
        // //     infos[0] = infoList[2].replace("\"", ""); // posX is in position 0
        // //     infos[1] = infoList[4].replace("\"", ""); // posY is in position 1
        // //     infos[2] = infoList[6].replace("\"", ""); // object is in position 2
        // // }
        // // else{
        // //     infos[2] = infoList[2].replace("\"", "");
        // // }
    }

    // Function that adds the specified object to the stage
    // Takes in input the object, gets the position, tries to add it and returns the result
    @OPERATION
    void addObject(OpFeedbackParam<String> result){
        setInfo(); // Set position, after that we will have in posX and posY global variables the right values
        System.out.println("[STAGE] Add " + infos.objName + " function");
        // // if(infos[0].equals(""))
        // //     infos[0] = "center";
        // // else if (infos[0].equals("front of"))
        // //     infos[0] = "frontOf";
        // // else if (infos[0].equals("behind of"))
        // //     infos[0] = "behindOf";
        // // else if (infos[0].equals("right of"))
        // //     infos[0] = "rightOf";
        // // else if (infos[0].equals("left of"))
        // //     infos[0] = "leftOf";
        // // if (infos[1].equals(""))
        // //     infos[1] = "center";
        try{
            // // String response = sendRequest("/" + infos[2] + "/" + infos[0] + "/" + infos[1]); // performs the request and get the response
            String response = sendRequest(infos.getRequest()); // performs the request and get the response
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
            String response = sendRequest("/remove/" + infos.objRel);
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
            String response = sendRequest("/move/" + infos.objRel + "/" + infos.direction);
        }catch(Exception e){
            System.out.println(e);
        }
    }

    // Function to exit
    @OPERATION String exit(OpFeedbackParam<String> result){
        System.out.println("[STAGE] Exit");
        try{
            String response = sendRequest("/exit");
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
