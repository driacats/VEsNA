current_region(undefined).

+!start
    :   true
    <-  .print("Starting actor");
        //vesna.walk(random).
        !find(prova).

+!find(Object)
    :   current_region(Region) & ntpp(Object, Region)
    <-  .print("I found ", Object, " here!").

+!find(Object)
    :   current_region(Region)
    <-  vesna.walk(random);
        .wait({+done(walk)});.
        //!find(Object).

+sight(door)
    :   current_region(Region)
    <-  -sight(door);
        +po(Region, door);
        +po(door, new_region).

+sight(Object)
    :   current_region(Region)
    <-  -sight(Object);
        +ntpp(Object, Region).

+rcc(SubRegion)
    :   current_region(Region)
    <-  -rcc(SubRegion);
        +ntpp(SubRegion, Region).

+movement(Status, Reason)
    :   true
    <-  .print("Movement is ", Status, " with reason ", Reason).

+movement
    <- .print("Movement").

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }