left(up, left).
left(left, down).
left(down, right).
left(right, up).

right(up, right).
right(left, up).
right(down, left).
right(right, down).

opposite(up, down).
opposite(down, up).
opposite(right, left).
opposite(left, right).

rotation(up).

different(Dir1, Dir2, Dir3) :- Dir1 \== Dir2 & Dir1 \== Dir3 & Dir2 \== Dir3.

// corridor(Landmark) :- Obj1(Landmark, )
dead_end(Landmark) :- object(_, Landmark, Direction1, _) & object(_, Landmark, Direction2, _) & object(_, Landmark, Direction3, _) & different(Direction1, Direction2, Direction3).
corridor(Landmark) :- object(_, Landmark, Direction1, _) & object(_, Landmark, Direction2, _) & Direction1 \== Direction2.
deviation(Landmark) :- object(_, Landmark, _, _).
room(Landmark) :- not object(_, Landmark, _, _).

// landmark(position, previous)
landmark(initial, empty).

actual_landmark(initial).

// TODO dead end

+!start
    :   true
    <-  .print("Starting actor");
        .my_name(Me);
        .concat(Me, "actor", ArtName);
        makeArtifact(ArtName, "stage.Actor", [], ArtId);
        focus(ArtId);
        .wait(2000);
        !lookaround;
        .wait(4000);
        ?actual_landmark(Landmark);
        !actual_position(Landmark);
        .wait(2000);
        move;
        .wait(2000);
        ?actual_landmark(Landmark);
        !actual_position(Landmark);
        move;
        .wait(2000);
        ?actual_landmark(Landmark);
        !actual_position(Landmark).

// Rotate the agent to all directions
+!lookaround
    :   rotation(Direction)
    <-  .print("Looking around...");
        ?opposite(Direction, Opposite);
        !rotate(Opposite);
        ?right(Opposite, Right);
        !rotate(Right);
        ?opposite(Right, RightOpposite);
        !rotate(RightOpposite);
        !rotate(Direction).


// Rotate the agent to the given @direction Direction;
// waits a second if any seen is caught.
+!rotate(Direction)
    :   not rotation(Direction)
    <-  .print("Rotating to ", Direction);
        -+rotation(Direction);
        rotate(Direction);
        .wait(1000).

+!rotate(Direction)
    :   rotation(Direction)
    <-  .print("Already in correct direction: ", Direction).

+!actual_position(Landmark)
    :   dead_end(Landmark)
    <-  .print("Actually I am in a dead end.").

+!actual_position(Landmark)
    :   corridor(Landmark)
    <-  .print("Actually I am in a corridor").

+!actual_position(Landmark)
    :   deviation(Landmark)
    <-  .print("Actually I am at a deviation").

+!actual_position(Landmark)
    :   room(Landmark)
    <-  .print("Actually I am in a room").

+seen(Object, Direction, Distance, front)
    :   actual_landmark(Landmark)
    <-  .print("I can see a ", Object, " on my ", Side, " (I am watching ", Direction, ") at distance ", Distance);
        +object(Object, Landmark, Direction, Distance).

+seen(Object, Direction, Distance, left)
    :   actual_landmark(Landmark)
    <-  ?left(Direction, GlobalDir);
        +object(Object, Landmark, GlobalDir, Distance).

    
+seen(Object, Direction, Distance, right)
    :   actual_landmark(Landmark)
    <-  ?right(Direction, GlobalDir);
        +object(Object, Landmark, GlobalDir, Distance).

+seen(Object, Direction, Side)
    :   true
    <-  .print("There is ", Object, " at my ", Side, " in direction ", Direction).