package body;

import jason.asSemantics.*;
import jason.asSyntax.*;
import java.net.URI;

import org.json.JSONObject;
import vesna.EmbodiedAgent;

public class walk extends DefaultInternalAction {
    
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {

        EmbodiedAgent ag = (EmbodiedAgent) ts.getAg();
        System.out.println(ag.getBB());
        JSONObject action = new JSONObject();
        action.put( "sender", "agent" );
        action.put( "receiver", "body" );
        action.put( "type", "walk");
        JSONObject data = new JSONObject();
        data.put( "target", args[0] );
        action.put( "data", data );

        WebSocketSingleton.getInstance().sendMessage( action.toString() );
        return true;
    }

}