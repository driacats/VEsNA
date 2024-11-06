package vesna;

import jason.asSemantics.*;
import jason.asSyntax.*;
import java.net.URI;

import org.json.JSONObject;

public class walk extends DefaultInternalAction {
    
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {

        EmbodiedAgent ag = (EmbodiedAgent) ts.getAg();

        JSONObject action = new JSONObject();
        action.put( "sender", "agent" );
        action.put( "receiver", "body" );
        action.put( "type", "walk");
        JSONObject data = new JSONObject();
        data.put( "target", args[0] );
        action.put( "data", data );

        ag.act(action.toString());
        return true;
    }

}