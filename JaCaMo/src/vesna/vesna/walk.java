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
                int current_t = ag.rccMap.getCurrent();
                List<Integer> adjs = ag.rccMap.getAdjs(current_t);
                if ( adjs == null || adjs.isEmpty() )
                    return false;
                for ( int adj : adjs ) {
                    if ( ag.rccMap.getRegionFromTriangle( adj ) == null ){
                        id = adj;
                        break;
                    }
                }
                if ( id == -1 ) {
                    System.out.println("[ALERT] All triangles are explored");
                    System.out.println("Adjs: " + adjs);
                    ag.rccMap.printMap();
                    Literal ate = ASSyntax.createLiteral("all_triangles_explored");
                    ag.sense(ate);
                    Literal mov_completed = ASSyntax.createLiteral("movement", ASSyntax.createLiteral("stopped"), ASSyntax.createLiteral("error"));
                    ag.sense(mov_completed);
                    return false;
                }
                ag.rccMap.setTarget( id );
                break;
            case "door":
                id = (long)((NumberTerm)args[1]).solve();
                break;
        
            default:
                break;
        }
        // // if ( args[0].toString().equals("random") )
        // //     id = 0;
        // // else if ( args[0].toString().equals("triangle") ){
        // //     System.out.println("Looking for adjancent triangles");
        // //     int current_triangle = ag.rccMap.getCurrent();
        // //     System.out.println("I am currently on triangle " + current_triangle );
        // //     List<Integer> adjs  = ag.rccMap.getAdjs(current_triangle);
        // //     System.out.println("Adjacences are " + adjs);
        // //     if ( adjs == null || adjs.isEmpty() ) {
        // //         System.out.println("Adjs è vuota!");
        // //         return false;
        // //     }
        // //     Unifier regionUnifier = new Unifier();
        // //     ag.believes(Literal.parseLiteral("current_region(X)"), regionUnifier);
        // //     String current_region = regionUnifier.get("X").toString();
        // //     System.out.println("My current region is " + current_region);
        // //     for ( int idx : adjs ) {
        // //         if ( ag.rccMap.getRegionFromTriangle( idx) == null )
        // //             id = idx;
        // //             System.out.println("I found: " + id + " that is not explored!");
        // //             continue;
        // //     }
        // //     if ( id == -1 )
        // //         System.out.println("Near triangles are all visited.");
        // // }
        // // else
        // //     id = (long)((NumberTerm)args[1]).solve();

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