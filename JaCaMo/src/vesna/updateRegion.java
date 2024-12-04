package vesna;

import jason.asSemantics.*;
import jason.asSyntax.*;

import org.json.JSONObject;

public class updateRegion extends DefaultInternalAction {

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {

        // Get the agent object
        VesnaAgent ag = (VesnaAgent) ts.getAg();

        JSONObject json = new JSONObject();
        json.put("sender", "vesna");
        json.put("receiver", "body");
        json.put("type", "region");
        JSONObject data = new JSONObject();
        data.put("region", args[0]);
        json.put("data", data);
        ag.act(json.toString());

        return true;
    }

}