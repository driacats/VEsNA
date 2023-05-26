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

	@OPERATION
	public void performAction(String action, int port, OpFeedbackParam<Literal> result){
		JSONObject action_json = new JSONObject();
		action_json.put("type", "perform");
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

	@OPERATION
	public void request(String datum, int port, OpFeedbackParam<Literal[]> result){
		JSONObject request_json = new JSONObject();
		request_json.put("type", "request");
		request_json.put("request", "eyes");
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
