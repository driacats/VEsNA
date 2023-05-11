package websocketserver;

import cartago.Artifact;
import cartago.OpFeedbackParam;
import cartago.OPERATION;

import org.glassfish.tyrus.server.Server;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Scanner;
import jason.asSyntax.Literal;

import org.json.JSONObject;

public class WebSocketServer extends Artifact{
	// static String resultString;
	private int PORT = 5002;
	private boolean inited = false;
	private Server server;

	@OPERATION
	public void listen(OpFeedbackParam<Literal> intent, OpFeedbackParam<Literal> obj, OpFeedbackParam<Literal> posX, OpFeedbackParam<Literal> posY, OpFeedbackParam< Literal> posRel, OpFeedbackParam<Literal> objRel, OpFeedbackParam<Literal> direction)  throws Exception{
		Endpoint endpoint = new Endpoint();
		Server server = new Server("localhost", PORT, "/websockets", null, endpoint.getClass());
		try {
			server.start();
			System.out.println("[SOCKET SERVER] Server is running");
			while(endpoint.resultString == null){
				Thread.sleep(10);
			}
			System.out.print("[SOCKET SERVER] Ending loop, resultString:" + endpoint.resultString);
			JSONObject info = new JSONObject(endpoint.resultString);
			if (info.has("intent"))
				intent.set(Literal.parseLiteral(info.getString("intent")));
			if (info.has("obj"))
				obj.set(Literal.parseLiteral(info.getString("obj")));
			if (info.has("posX"))
				posX.set(Literal.parseLiteral(info.getString("posX")));
			if (info.has("posY"))
				posY.set(Literal.parseLiteral(info.getString("posY")));
			if (info.has("relative"))
				posRel.set(Literal.parseLiteral(info.getString("relative").replace(" ", "_")));
			if (info.has("relName"))
				objRel.set(Literal.parseLiteral(info.getString("relName")));
			if (info.has("direction"))
				direction.set(Literal.parseLiteral(info.getString("direction")));
			endpoint.resultString = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			server.stop();
		}
	}

}