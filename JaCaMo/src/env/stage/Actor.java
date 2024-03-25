package stage;

import org.json.JSONObject;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import jason.asSyntax.Literal;

import java.util.List;
import java.util.ArrayList;

import stage.websocket.WsServer;
import stage.websocket.WsClient;
import java.net.URI;
import java.net.URISyntaxException;

public class Actor extends Artifact{

    // private WsClient client = new WsClient();
    private int port = 9080;
    private String host = "localhost";
    private WsClient conn;

    public Actor() throws URISyntaxException {

		// WsServer server = new WsServer(new InetSocketAddress(host, port));
		// server.run();
        conn = new WsClient(new URI("ws://localhost:9080"));
        conn.connect();
	}

    private void send(JSONObject json){
        conn.send(json.toString());
        System.out.println("[ACTOR] Message sent");
    }

    @OPERATION
    public void act(){
        System.out.println("[ACTOR] Sending message");
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
     
    // public Actor(){
    //     try{
    //         client.startConnection("127.0.0.1", port);
    //         System.out.println("[ACTOR] Client initialized.");
    //         StringBuilder answer = new StringBuilder(client.sendMessage("Hello from the actor"));
    //     } catch(Exception e){
    //         System.out.println("[ACTOR] " + e);
    //     }
    // }
// 
    // private String send(JSONObject msg){
    //     StringBuilder  answer = new StringBuilder("Error.");
    //     try{
    //         answer = new StringBuilder(client.sendMessage(msg.toString()));
    //     } catch(Exception e){
    //         System.out.println("[ACTOR] " + e);
    //     }
    //     return answer.toString();
    // }
    // // The actor class connects the actor mind with its body.
    // // It has two main methods:
    // // - perform: asks the body to perform an action;
    // // - request: asks the body a kwnoledge.
// 
    // // //! ATTENTION: perform is for in place operations. In order to move use the move function.
    // // @OPERATION
    // // public void perform(String action, int port, OpFeedbackParam<Literal> result){
    // //     // The function takes as parameters:
    // //     // - the action to be performed;
    // //     // - the port to contact the body;
    // //     // - the feedback parameter for the output.
// 
    // //     // We declare a new JSON object in which we add:
    // //     // - the type of the message
    // //     // - the action
    // //     JSONObject action_json = new JSONObject();
    // //     action_json.put("type", "perform");
    // //     action_json.put("action", action.toString());
    //     
    // //     // We send it to the actor body and get the result.
    // //     WsClient client = new WsClient();
    // //     try{
    // //         client.startConnection("127.0.0.1", port);
    // //         StringBuilder answer = new StringBuilder(client.sendMessage(action_json.toString()));
    // //         System.out.println("[ACTOR] Get Answer: " + answer.toString());
    // //         client.stopConnection();
    // //     }catch(Exception e){
    // //         System.out.println(e);
    // //     }
    // // }
// 
    // @OPERATION
    // public void act(String type, int port, JSONObject action, OpFeedbackParam<Literal> result){
    //     JSONObject action_json = new JSONObject();
    //     action_json.put("type", type);
    //     action_json.put("action", action);
    //     String answer = send(action_json);
    //     System.out.println("[ACTOR] Answer: " + answer);
    // }
// 
    // @OPERATION
    // public void move(String direction, int port, OpFeedbackParam<Literal> result){
    //     JSONObject action_json = new JSONObject();
    //     action_json.put("type", "perform");
    //     action_json.put("action", "move");
    //     action_json.put("direction", direction);
// 
    //     WsClient client = new WsClient();
    //     try{
    //         client.startConnection("127.0.0.1", port);
    //         StringBuilder answer = new StringBuilder(client.sendMessage(action_json.toString()));
    //         System.out.println("[ACTOR] Get Answer: " + answer.toString());
    //         client.stopConnection();
    //     }catch(Exception e){
    //         System.out.println(e);
    //     }
    // }
// 
    // @OPERATION
    // public void request(String datum, int port, OpFeedbackParam<Literal[]> result){
    //     // The function takes as parameters:
    //     // - the datum we want to know;
    //     // - the port to contact the body;
    //     // - the feedback parameter for the output (as list).
// 
    //     // We declare a new JSON object in which we add:
    //     // - the type of the message
    //     // - the request
    //     JSONObject request_json = new JSONObject();
    //     request_json.put("type", "request");
    //     request_json.put("request", "eyes");
// 
    //     // We send it to the actor body and get the result.
    //     WsClient client = new WsClient();
    //     try{
    //         client.startConnection("127.0.0.1", port);
    //         StringBuilder answer = new StringBuilder(client.sendMessage(request_json.toString()));
    //         System.out.println("[ACTOR] Get Answer: " + answer.toString());
    //         JSONObject answer_json = new JSONObject(answer.toString().replaceAll("\\P{Print}", "").substring(1));
    //         List<Literal> raw_result = new ArrayList<Literal>();
    //         answer_json.keySet().forEach(keyStr ->
    //             {
    //                 String item = keyStr + answer_json.getString(keyStr);
    //                 raw_result.add(Literal.parseLiteral(item));
    //             }
    //         );
    //         System.out.println(raw_result.toString());
    //         Literal[] raw_result_array = new Literal[raw_result.size()];
    //         raw_result.toArray(raw_result_array);
    //         result.set(raw_result_array);
    //         client.stopConnection();
    //     }catch(Exception e){
    //         System.out.println(e);
    //     }
    // }
    
}
