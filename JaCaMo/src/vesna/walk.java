package vesna;

import jason.asSemantics.*;
import jason.asSyntax.*;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.json.JSONObject;

public class walk extends DefaultInternalAction {

    private static Set<String> available_actions = Set.of("random", "stop", "door");
    
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {

        if ( !available_actions.contains(args[0].toString()) )
            throw new Exception("Available arguments for functor 'walk' are: " + available_actions.toString() + ", given: " + args[0] );
        
        long id;
        if ( args[0].toString().equals("random") )
            id = 0;
        else
            id = (long)((NumberTerm)args[1]).solve();

        VesnaAgent ag = (VesnaAgent) ts.getAg();

        JSONObject action = new JSONObject();
        action.put( "sender", "agent" );
        action.put( "receiver", "body" );
        action.put( "type", "walk");
        JSONObject data = new JSONObject();
        data.put( "target", args[0] );
        data.put( "id", id);
        action.put( "data", data );

        ag.act(action.toString());
        return true;
    }

}