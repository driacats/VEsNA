package websocket;

import cartago.Artifact;
import cartago.OpFeedbackParam;
import cartago.OPERATION;
import jason.asSyntax.Literal;

import org.glassfish.tyrus.server.Server;
import org.json.JSONObject;

public class WsServer extends Artifact{
    private int PORT = 5002;

    // The method listen is called by the agent and sets the literals depending on the user input.
    @OPERATION
    public void listen(OpFeedbackParam<Literal> intent, OpFeedbackParam<Literal> obj, OpFeedbackParam<Literal> posX, OpFeedbackParam<Literal> posY, OpFeedbackParam< Literal> posRel, OpFeedbackParam<Literal> objRel, OpFeedbackParam<Literal> direction, OpFeedbackParam<Literal> actorName)  throws Exception{
        
        // The server creates an endpoint that will manage the connections
        Endpoint endpoint = new Endpoint();
        Server server = new Server("localhost", PORT, "/websockets", null, endpoint.getClass());

        try {
            // The server is started and waits for a message from the user
            server.start();
            System.out.println("[SOCKET SERVER] Server is running");
            while(endpoint.resultString == null){
                Thread.sleep(1);
            }
            // Create a json object from the string received in input
            JSONObject info = new JSONObject(endpoint.resultString);
            // Set all the literal values
            if (info.has("intent"))
                intent.set(Literal.parseLiteral(info.getString("intent")));
            if (info.has("obj") && !info.isNull("obj"))
                obj.set(Literal.parseLiteral(info.getString("obj")));
            if (info.has("posX") && !info.isNull("posX"))
                posX.set(Literal.parseLiteral(info.getString("posX")));
            if (info.has("posY") && !info.isNull("posY"))
                posY.set(Literal.parseLiteral(info.getString("posY")));
            if (info.has("relative") && !info.isNull("relative"))
                posRel.set(Literal.parseLiteral(info.getString("relative").replaceAll(" ", "_")));
            if (info.has("relName") && !info.isNull("relName"))
                objRel.set(Literal.parseLiteral(info.getString("relName")));
            if (info.has("direction") && !info.isNull("direction"))
                direction.set(Literal.parseLiteral(info.getString("direction")));
            if (info.has("actorName") && !info.isNull("actorName"))
                actorName.set(Literal.parseLiteral(info.getString("actorName").toLowerCase()));

            // reset the user string
            endpoint.resultString = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            server.stop();
        }
    }

}
