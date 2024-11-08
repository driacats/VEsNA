package vesna;

import jason.architecture.AgArch;
import jason.asSemantics.*;
import jason.asSyntax.*;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import jason.bb.BeliefBase;
import jason.mas2j.ClassParameters;
import jason.runtime.Settings;

import java.net.URI;
import java.net.URISyntaxException;

public class VesnaAgent extends Agent implements WsClientMsgHandler{

    private WsClient client;

    @Override
    public void loadInitialAS(String asSrc) throws Exception{

        super.loadInitialAS(asSrc);

        Unifier addrUnifier = new Unifier();
        Unifier portUnifier = new Unifier();
        believes(Literal.parseLiteral("address(X)"), addrUnifier);
        String address = addrUnifier.get("X").toString();
        believes(Literal.parseLiteral("port(X)"), portUnifier);
        int port = (int)((NumberTerm)portUnifier.get("X")).solve();
        System.out.println("ADDRESS: " + address + "\t PORT: " + port);
        
        URI full_addr = new URI("ws://" + address + ":" + port);
        client = new WsClient(full_addr);
        client.setMsgHandler(this::handleMsg);
        client.connect();
    }

    public void act(String action) {
        client.send(action);
    }

    private void handle_event(TransitionSystem ts, Unifier un, JSONObject event) {
        String event_type = event.getString("type");
        String event_status = event.getString("status");
        String event_reason = event.getString("reason");
        try {
            InternalAction signal = getIA(".signal");
            Term[] event_list = {ASSyntax.createString("+prova")};
            signal.execute(ts, un, event_list);
        } catch( Exception e ){
            e.printStackTrace();
        }
    }

    private void handle_sight(TransitionSystem ts, Unifier un, JSONObject sight) {
        String object = sight.getString("sight");
        Literal sight_belief = ASSyntax.createLiteral("sight", ASSyntax.createLiteral(object));
        try {
            addBel(sight_belief);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    private void handle_rcc(TransitionSystem ts, Unifier un, JSONObject rcc) {
        int region = rcc.getInt("current");
        Literal rcc_belief = ASSyntax.createLiteral("rcc", ASSyntax.createNumber(region));
        try {
            addBel( rcc_belief );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleMsg(String msg){
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
        } else if ( type.equals("rcc") ){
            handle_rcc(ts, un, data);
        }
    }

}