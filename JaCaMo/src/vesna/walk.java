package vesna;

import jason.asSemantics.*;
import jason.asSyntax.*;
import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

import org.json.JSONObject;

public class walk extends DefaultInternalAction {

    private static Set<String> available_actions = Set.of("random", "stop", "door", "triangle");
    
    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {

        // Check if action from agent is an available one
        if ( !available_actions.contains(args[0].toString()) )
            throw new Exception("Available arguments for functor 'walk' are: " + available_actions.toString() + ", given: " + args[0] );
        
        // Get the agent object
        VesnaAgent ag = (VesnaAgent) ts.getAg();

        long id = -1;
        switch ( args[0].toString() ) {
            case "random":
                id = 0;
                break;
            case "triangle":
                id = ag.tm.nextTriangle();
                if ( id != -1 ){
                    if (! ag.tm.setTarget( (int) id ))
                        System.out.println("It's the second time I try this target.");
                }
                break;
            case "door":
                id = (long)((NumberTerm)args[1]).solve();
                break;
        
            default:
                break;
        }

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