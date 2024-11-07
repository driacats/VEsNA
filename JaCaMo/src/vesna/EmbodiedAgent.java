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

public class EmbodiedAgent extends Agent implements WsClientMsgHandler{

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

    @Override
    public void handleMsg(String msg){
        JSONObject log = new JSONObject(msg);
        String sender = log.getString("sender");
        String receiver = log.getString("receiver");
        String type = log.getString("type");
        TransitionSystem ts = getTS();
        Unifier un = new Unifier();
        if ( type.equals( "signal" ) ) {
            JSONObject sig = log.getJSONObject("msg");
            String functor = sig.getString("functor");
            Literal signal = Literal.parseLiteral(functor);
            JSONArray terms = sig.getJSONArray("terms");
            for (int i=0; i<terms.length(); i++){
                String t = terms.getString(i);
                Literal t_n = Literal.parseLiteral(t);
                signal.addTerm(t_n);
            }
            Literal s1 = Literal.parseLiteral("self");
            Literal s2 = Literal.parseLiteral("signal");
            Term[] signal_list = {s1, s2, signal};
            try{
                InternalAction signal_f = getIA(".send");
                signal_f.execute(ts, un, signal_list);
            } catch (Exception e){
                e.printStackTrace();
            }
        } else if ( type.equals("sight") ){
            JSONObject m = log.getJSONObject("msg");
            JSONArray sights = m.getJSONArray("sights");
            for ( int i=0; i<sights.length(); i++ ){
                String sight = sights.getString(i);
                Literal sight_literal = Literal.parseLiteral("sight");
                sight_literal.addTerm(Literal.parseLiteral(sight));
                try {
                    addBel(sight_literal);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        } else if ( type.equals("rcc") ){
            JSONObject data = log.getJSONObject("data");
            int region = data.getInt("current");
            Literal rcc = Literal.parseLiteral("rcc");
            rcc.addTerm(ASSyntax.parseNumber(Integer.toString(region)));
            try {
                addBel( rcc );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}