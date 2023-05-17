package stage;

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
        this.actorName = "";
        this.direction = "";
    }

    public String buildPayload(){
        String payload = "{";
        payload += "\"relAddition\":  \""   + this.relAddition  + "\",\n";
        payload += "\"globAddition\":  \""  + this.globAddition + "\",\n";
        payload += "\"actorAddition\":  \"" + this.actorAddition + "\",\n";
        payload += "\"removal\":  \""       + this.removal      + "\",\n";
        payload += "\"motion\":  \""        + this.motion       + "\",\n";
        payload += "\"posX\":  \""          + this.posX         + "\",\n";
        payload += "\"posY\":  \""          + this.posY         + "\",\n";
        payload += "\"posRel\":  \""        + this.posRel       + "\",\n";
        payload += "\"objRel\":  \""        + this.objRel       + "\",\n";
        payload += "\"objName\":  \""       + this.objName      + "\",\n";
        payload += "\"actorName\":  \""     + this.actorName    + "\",\n";
        payload += "\"direction\":  \""     + this.direction    + "\"}";
        return payload;
    }
}