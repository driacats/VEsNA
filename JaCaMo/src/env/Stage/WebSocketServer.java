package websocketserver;

import cartago.Artifact;
import cartago.OpFeedbackParam;
import cartago.OPERATION;

import org.glassfish.tyrus.server.Server;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.util.Scanner;

public class WebSocketServer extends Artifact{
	// static String resultString;
	private int PORT = 5002;
	private boolean inited = false;
	private Server server;

	@OPERATION
	public void listen(OpFeedbackParam<String> result) throws Exception{
		Endpoint endpoint = new Endpoint();
		Server server = new Server("localhost", PORT, "/websockets", null, endpoint.getClass());
		try {
			server.start();
			System.out.println("[SOCKET SERVER] Server is running");
			while(endpoint.resultString == null){
				Thread.sleep(10);
			}
			System.out.print("[SOCKET SERVER] Ending loop, resultString:" + endpoint.resultString);
			result.set(endpoint.resultString);
			endpoint.resultString = null;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			server.stop();
		}
	}

}