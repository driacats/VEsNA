{ include ("orienteering.asl") }
{ include ("motion.asl") }
{ include ("sight.asl") }

rotation(up).

// landmark(position, previous)
// landmark(0, empty).
first_step.
start.

// path(Landmark1, Landmark2, [Direction1, Direction2, ..., DirectionN]).

// actual_landmark(0).

+!start
    :   true
    <-  .print("Starting actor");.
        //  .wait(5000);
        //  body.walk(random);
        //  .wait(2000);
        //  body.walk(region0).
        // .my_name(Me);
        // .concat(Me, "actor", ArtName);
        // makeArtifact(ArtName, "stage.Actor", [], ArtId);
        // focus(ArtId);
        // .wait(2000);
        // !update_landmark;
        // !lookaround;
        // !walk.
        // !lookaround;
        // .wait(4000);
        // ?actual_landmark(Landmark);
        // !actual_position(Landmark);
        // ?next_landmark(NewLandmark);
        // .wait(2000);
        // !move(up);
        // .wait(2000);
        // ?actual_landmark(Landmark);
        // !actual_position(Landmark);
        // !move(up);
        // .wait(2000);
        // ?actual_landmark(Landmark);
        // !actual_position(Landmark).

+!walk
    :   actual_landmark(Landmark) & way(Landmark, Direction)
    <-  !move(Direction);
        !walk.

+!walk
    :   actual_landmark(Landmark) & not way(Landmark, Direction)
    <-  !update_landmark;
        !lookaround;
        !walk.

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }