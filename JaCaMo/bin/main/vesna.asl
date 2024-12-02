current_region(region0).
region_counter(0).

+!start
    :   true
    <-  .wait(2000);
        .print("Starting actor");
        !find(prova).

+!find(Object)
    :   current_region(Region) & ntpp(Object, Region)
    <-  .print("I found ", Object, " here!").

+!find(Object)
    :   current_region(Region) & full_explored(Region)
    <-  .print("I explored completely the room, let's go back to the other room");
        // TODO: meglio implementare tutto questo con DFS o BFS!
        if ( po( door(Id), Region ) ){
            ?po(OtherRegion, door(Id));
            vesna.walk(door, Id);
            .wait({+movement(completed, destination_reached)});
            -+current_region(OtherRegion);
        } else {
            if ( po(Region, door(Id))){
                ?po(door(Id), OtherRegion);
                vesna.walk(door, Id);
                .wait({+movement(completed, destination_reached)});
                -+current_region(OtherRegion);
            } else {
                .print("I really do not know where to go!");
            };
        };
        !find(Object).

+!find(Object)
    :   current_region(Region) & po(Region, door(Id)) & po(door(Id), OtherRegion) & not iAmAtDoor
    <-  .print("I found a door, go for it");
        vesna.walk(door, Id);
        .wait({+movement(completed, destination_reached)});
        +iAmAtDoor;
        -+current_region(OtherRegion);
        !find(Object).

+!find(Object)
    :   current_region(Region)
    <-  .print("I don't know where to go, random move!");
        if (vesna.walk(triangle)){
            .wait({+movement(Status, Reason)});
        };
        !find(Object).

+all_triangles_explored
    :   current_region(Region)
    <-  +full_explored(Region).

+!add_portal(Id)
    :   portals(_, _)
    <-  .findall(X, portal(_, X), Portals);
        .print(Portals);
        .max(Portals, Max);
        .print(Max);
        +portal(Id, Max+1).

+!add_portal(Id)
    :   region_counter(N)
    <-  +portal(Id, N+1).

+sight(door, Id)
    :   current_region(Region) & ( po(Region, door(Id)) | po(door(Id), Region) )
    <-  true.

+sight(door, Id)
    :   current_region(Region) & region_counter(N)
    <-  -sight(door, Id);
        +po(Region, door(Id));
        !add_portal(Id);
        ?portal(Id, NNew);
        .concat(region, NNew, NewRegionString);
        .term2string(NewRegion, NewRegionString);
        +po(door(Id), NewRegion).

+sight(Object, Id)
    :   current_region(Region)
    <-  -sight(Object, Id);
        +ntpp(obj(Object, Id), Region).

+rcc(SubRegion)
    :   current_region(Region)
    <-  -rcc(SubRegion);
        +ntpp(SubRegion, Region).

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }