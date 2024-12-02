package vesna;

import jason.asSemantics.*;
import jason.asSyntax.*;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;

public class VesnaAgent extends Agent implements WsClientMsgHandler{

    private WsClient client;
    // public RCCMap rccMap;
    public TriangleMap tm;

    // We override the loadInitialAS to create the WebSocket connection with the body.
    // With the super function we load the beliefs (containing the address and the port)
    // and only next we connect to address and port.
    @Override
    public void loadInitialAS(String asSrc) throws Exception{

        super.loadInitialAS(asSrc);

        // Get the address
        Unifier addrUnifier = new Unifier();
        believes(Literal.parseLiteral("address(X)"), addrUnifier);
        String address = addrUnifier.get("X").toString();
        // Get the port
        Unifier portUnifier = new Unifier();
        believes(Literal.parseLiteral("port(X)"), portUnifier);
        int port = (int)((NumberTerm)portUnifier.get("X")).solve();
        System.out.println("ADDRESS: " + address + "\t PORT: " + port);
        
        // Creation of the URI and of the connection
        URI full_addr = new URI("ws://" + address + ":" + port);
        client = new WsClient(full_addr);
        client.setMsgHandler(this::handleMsg);
        client.connect();

//        rccMap = new RCCMap();
        tm = new TriangleMap();
    }

    public void act(String action) {
        client.send(action);
    }

    public void sense( Literal perception ){
        try {
            InternalAction signal = getIA(".signal");
            StringTerm type = ASSyntax.createString("+" + perception.toString());
            Unifier un = new Unifier();
            Term[] event_list = new Term[] {type};
            signal.execute(ts, un, event_list);
            System.out.println("I sent the signal: " + type.toString());
        } catch( Exception e ){
            e.printStackTrace();
        }
    }

    private void handle_event(TransitionSystem ts, Unifier un, JSONObject event) {
        String event_type = event.getString("type");
        String event_status = event.getString("status");
        String event_reason = event.getString("reason");
        try {
            // int current_t = rccMap.getCurrent();
            int current_t = tm.getCurrent();
            // int target_t = rccMap.getTarget();
            int target_t = tm.getTarget();
            if ( current_t != target_t ){
                System.out.println("Current target " + target_t + " is different from current triangle " + current_t );
                // rccMap.addNotReachedTriangle(target_t);
                tm.setVNotReachable(target_t);
            }

            InternalAction signal = getIA(".signal");
            Literal event_literal = ASSyntax.createLiteral(event_type, ASSyntax.createLiteral(event_status), ASSyntax.createLiteral(event_reason));
            StringTerm type = ASSyntax.createString("+" + event_literal.toString());
            Term[] event_list = new Term[] {type};
            signal.execute(ts, un, event_list);
            System.out.println("I sent the signal!");
        } catch( Exception e ){
            e.printStackTrace();
        }
    }

    private void handle_sight(TransitionSystem ts, Unifier un, JSONObject sight) {
        String object = sight.getString("sight");
        long id = sight.getLong("id");
        Literal sight_belief = ASSyntax.createLiteral("sight", ASSyntax.createLiteral(object), ASSyntax.createNumber(id));
        try {
            addBel(sight_belief);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handle_triangle(TransitionSystem ts, Unifier un, JSONObject rcc) {
        int current = rcc.getInt("current");
        tm.setCurrent( current );
        JSONArray adjs = rcc.getJSONArray("adjs");
        ArrayList<Integer> adjs_int = new ArrayList<Integer>();
        for (int i = 0; i<adjs.length(); i++ )
            adjs_int.add(adjs.getInt(i));
        // rccMap.addAdjsToTriangle(region, adjs_int);
        tm.addEdges(current, adjs_int);

        Unifier regionUnifier = new Unifier();
        try{
            System.out.println("[HANDLE RCC] entering");
            believes(Literal.parseLiteral("current_region(X)"), regionUnifier);
            System.out.println("[HANDLE RCC] got belief");
            String current_region = regionUnifier.get("X").toString();
            ////  System.out.println("[HANDLE RCC] got value");
            ////  System.out.println(current_region);
            ////  System.out.println(rccMap.isTriangleExplored(ASSyntax.parseLiteral(current_region), region));
            ////  System.out.println("Triangle contained in region: " + rccMap.getRegionFromTriangle(region));
            ////  rccMap.addTriangle(ASSyntax.parseLiteral(current_region), region);
            ////  rccMap.updateCurrent(region);
            ////  rccMap.printMap();
            tm.setCurrentRegion( ASSyntax.createLiteral( current_region ) );
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void handleMsg(String msg){
        System.out.println(msg);
        JSONObject log = new JSONObject(msg);
        String sender = log.getString("sender");
        String receiver = log.getString("receiver");
        String type = log.getString("type");
        TransitionSystem ts = getTS();
        Unifier un = new Unifier();
        JSONObject data = log.getJSONObject("data");
        if ( type.equals( "signal" ) ) {
            handle_event(ts, un, data);
        } else if ( type.equals("sight") ){
            handle_sight(ts, un, data);
        } else if ( type.equals("triangle") ){
            handle_triangle(ts, un, data);
        }
    }

}