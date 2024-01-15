package stage;

import org.json.JSONObject;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import jason.asSyntax.Literal;
import websocket.WsClient;

import java.util.List;
import java.util.ArrayList;

public class Actor extends Artifact{

    // The actor class connects the actor mind with its body.
    // It has two main methods:
    // - perform: asks the body to perform an action;
    // - request: asks the body a kwnoledge.

    //! ATTENTION: perform is for in place operations. In order to move use the move function.
    @OPERATION
    public void perform(String action, int port, OpFeedbackParam<Literal> result){
        // The function takes as parameters:
        // - the action to be performed;
        // - the port to contact the body;
        // - the feedback parameter for the output.

        // We declare a new JSON object in which we add:
        // - the type of the message
        // - the action
        JSONObject action_json = new JSONObject();
        action_json.put("type", "perform");
        action_json.put("action", action.toString());
        
        // We send it to the actor body and get the result.
        WsClient client = new WsClient();
        try{
            client.startConnection("127.0.0.1", port);
            StringBuilder answer = new StringBuilder(client.sendMessage(action_json.toString()));
            System.out.println("[ACTOR] Get Answer: " + answer.toString());
            client.stopConnection();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    @OPERATION
    public void move(String direction, int port, OpFeedbackParam<Literal> result){
        JSONObject action_json = new JSONObject();
        action_json.put("type", "perform");
        action_json.put("action", "move");
        action_json.put("direction", direction);

        WsClient client = new WsClient();
        try{
            client.startConnection("127.0.0.1", port);
            StringBuilder answer = new StringBuilder(client.sendMessage(action_json.toString()));
            System.out.println("[ACTOR] Get Answer: " + answer.toString());
            client.stopConnection();
        }catch(Exception e){
            System.out.println(e);
        }
    }

    @OPERATION
    public void request(String datum, int port, OpFeedbackParam<Literal[]> result){
        // The function takes as parameters:
        // - the datum we want to know;
        // - the port to contact the body;
        // - the feedback parameter for the output (as list).

        // We declare a new JSON object in which we add:
        // - the type of the message
        // - the request
        JSONObject request_json = new JSONObject();
        request_json.put("type", "request");
        request_json.put("request", "eyes");

        // We send it to the actor body and get the result.
        WsClient client = new WsClient();
        try{
            client.startConnection("127.0.0.1", port);
            StringBuilder answer = new StringBuilder(client.sendMessage(request_json.toString()));
            System.out.println("[ACTOR] Get Answer: " + answer.toString());
            JSONObject answer_json = new JSONObject(answer.toString().replaceAll("\\P{Print}", "").substring(1));
            List<Literal> raw_result = new ArrayList<Literal>();
            answer_json.keySet().forEach(keyStr ->
                {
                    String item = keyStr + answer_json.getString(keyStr);
                    raw_result.add(Literal.parseLiteral(item));
                }
            );
            System.out.println(raw_result.toString());
            Literal[] raw_result_array = new Literal[raw_result.size()];
            raw_result.toArray(raw_result_array);
            result.set(raw_result_array);
            client.stopConnection();
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
}
