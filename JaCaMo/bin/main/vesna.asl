current_region(region0).
region_counter(0).

+!start
    :   true
    <-  .print("Starting actor");
        !find(prova).

+!find(Object)
    :   current_region(Region) & ntpp(Object, Region)
    <-  .print("I found ", Object, " here!").

+!find(Object)
    :   current_region(Region) & po(Region, door(Id)) & po(door(Id), OtherRegion)
    <-  vesna.walk(door, Id);
        .wait({+movement(completed, destination_reached)});
        !find(Object).

+!find(Object)
    :   current_region(Region)
    <-  vesna.walk(random);
        .wait({+movement(completed, destination_reached)});
        !find(Object).

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