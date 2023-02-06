package stage;

public class Infos{

    // Fields
    private boolean relAddition = false;
    private boolean globAddition = false;
    private boolean removal = false;
    public String posX;     // position on horizontal axis (left, center, right)
    public String posY;     // position on vertical axis (in front, center, behind)
    public String posRel;   // relative position (left of, right of, behind of, in front of, on)
    public String objRel;   // relative system object (left of objRel)
    public String objName;  // name of the object to add
    public String direction;      // direction of the motion

    private String toLowerCamelCase(String pos){
        if (pos.equals("on"))
            return pos;
        pos = pos.split(" ")[0];
        pos = pos.concat("Of");
        return pos;
    }

    // Reset all the values to empty string
    public void reset(){
        this.relAddition = false;
        this.globAddition = false;
        this.removal = false;
        this.posX = "";
        this.posY = "";
        this.posRel = "";
        this.objRel = "";
        this.objName = "";
        this.direction = "";
    }

    // Set values for adding with global positioning
    public void setGlobalPosition(String objName, String posX, String posY){
        reset();
        if (posX.equals(""))
            posX = "center";
        if (posY.equals(""))
            posY = "center";
        this.globAddition = true;
        this.objName = objName;
        this.posX = posX;
        this.posY = posY;
    }

    // Set values for adding with relative positioning
    public void setRelativePosition(String objName, String posRel, String objRel){
        reset();
        this.relAddition = true;
        this.objName = objName;
        this.posRel = toLowerCamelCase(posRel);
        this.objRel = objRel;
    }

    // Set values for removing object
    public void setRemoval(String objRel){
        reset();
        this.removal = true;
        this.objRel = objRel;
    }

    public void setMotion(String objRel, String direction){
        reset();
        this.objRel = objRel;
        this.direction = direction;
    }

    public String getRequest(){
        if (this.globAddition)
            return "/" + this.objName + "/" + this.posX + "/" + this.posY;
        else if (this.relAddition)
            return "/" + this.objName + "/" + this.posRel + "/" + this.objRel;
        else if (this.removal)
            return "/remove/" + this.objRel;
        return "ERROR";
    }
}