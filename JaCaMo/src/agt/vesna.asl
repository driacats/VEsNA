+!start
    :   true
    <-  .print("Starting actor");
        .wait(5000);
        vesna.walk(random).

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }