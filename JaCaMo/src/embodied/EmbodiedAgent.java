package vesna;

import jason.architecture.AgArch;
import jason.asSemantics.*;
import jason.asSyntax.*;
import java.util.List;
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
        BeliefBase bb = getBB();
        Unifier addrUnifier = new Unifier();
        Unifier portUnifier = new Unifier();
        believes(Literal.parseLiteral("address(X)"), addrUnifier);
        String address = addrUnifier.get("X").toString();
        believes(Literal.parseLiteral("port(X)"), portUnifier);
        int port = (int)((NumberTerm)portUnifier.get("X")).solve();
        System.out.println("ADDRESS: " + address + "\t PORT: " + port);
        
        URI full_addr = new URI("ws://" + address + ":" + port);
        client = new WsClient(full_addr);
        client.connect();
    }

    public void act(String action) {
        client.send(action);
    }

    @Override
    public void handleMsg(String msg){
        System.out.println("Received : " + msg);
    }

}