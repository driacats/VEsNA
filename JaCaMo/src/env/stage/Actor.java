package stage;

import org.json.JSONObject;

import cartago.Artifact;
import cartago.OPERATION;
import cartago.OpFeedbackParam;
import cartago.manual.syntax.Literal;
import websocket.WsClient;

public class Actor extends Artifact{

	@OPERATION
	public void performAction(String action, int port, OpFeedbackParam<Literal> result){
		JSONObject action_json = new JSONObject();
		action_json.put("action", action.toString());
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
	
}
