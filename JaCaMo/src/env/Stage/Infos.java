package stage;

import org.json.JSONObject;

public class Infos{

    // Fields
    public boolean relAddition = false;
    public boolean globAddition = false;
    public boolean actorAddition = false;
    public boolean removal = false;
    public boolean motion = false;
    public String posX;         // position on horizontal axis (left, center, right)
    public String posY;         // position on vertical axis (in front, center, behind)
    public String posRel;       // relative position (left of, right of, behind of, in front of, on)
    public String objRel;       // relative system object (left of objRel)
    public String objName;      // name of the object to add
    public int port;
    public String actorName;
    public String direction;    // direction of the motion

    // Reset all the values to empty string
    public void reset(){
        this.relAddition = false;
        this.globAddition = false;
        this.actorAddition = false;
        this.removal = false;
        this.motion = false;
        this.posX = "";
        this.posY = "";
        this.posRel = "";
        this.objRel = "";
        this.objName = "";
        this.port = -1;
        this.actorName = "";
        this.direction = "";
    }

    public String buildPayload(){
        JSONObject payload_json = new JSONObject();
        payload_json.put("relAddition", this.relAddition);
        payload_json.put("globAddition", this.globAddition);
        payload_json.put("actorAddition", this.actorAddition);
        payload_json.put("removal", this.removal);      
        payload_json.put("motion", this.motion);       
        payload_json.put("posX", this.posX);         
        payload_json.put("posY", this.posY);         
        payload_json.put("posRel", this.posRel);       
        payload_json.put("objRel", this.objRel);       
        payload_json.put("objName", this.objName);      
        payload_json.put("port", this.port);         
        payload_json.put("actorName", this.actorName);    
        payload_json.put("direction", this.direction);
        return payload_json.toString();
    }
}
