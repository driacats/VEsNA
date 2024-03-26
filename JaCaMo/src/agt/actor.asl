+!start
    :   true
    <-  .print("Starting actor");
        makeArtifact("actor", "stage.Actor", [], ArtId);
        focus(ArtId);
        .wait(5000);
        +position(0, 0);
        +rotation(up);
        .wait(2000);
        act;
        .wait(2000);
        !lookaround;
        .wait(1000);
        !gostraighton(up);
        .wait(1000);
        !rotate(right);
        .wait(1000);
        !gostraighton(right).

+!lookaround
    :   true
    <-  !rotate(right);
        .wait(1000);
        !rotate(down);
        .wait(1000);
        !rotate(left);
        .wait(1000);
        !rotate(up).

+!rotate(Direction)
    :   rotation(OldDirection)
    <-  .print("rotating to ", Direction);
        rotate(Direction);
        -rotation(OldDirection);
        +rotation(Direction).

+!rotate(Direction)
    :   rotation(Direction)
    <-  .print("actor alreay in position.").

+!move(Direction)
    :   not rotation(Direction)
    <-  rotate(Direction);
        .wait(1000);
        !move(Direction).

// +!move(Direction)
//     :   (Direction == right | Direction == left | Direction == up | Direction == down) & not rotation(Direction) & position(X, Y)
//     <-  rotate(Direction);
//         .wait(2000);
//         move;
//         -position(X, Y).

+!move(Direction)
    :   Direction == right & position(X, Y) & rotation(Direction) & not saw(X, Y, right, Object, near) //+saw(X, Y, Rotation, Object, Distance).
    <-  -position(X, Y);
        +position(X+1, Y);
        move;
        .wait(2000).

+!move(Direction)
    :   Direction == left & position(X, Y) & rotation(Direction) & not saw(X, Y, left, Object, near)
    <-  -position(X, Y);
        +position(X-1, Y);
        move;
        .wait(2000).

+!move(Direction)
    :   Direction == up & position(X, Y) & rotation(Direction) & not saw(X, Y, up, Object, near)
    <-  -position(X, Y);
        +position(X, Y+1);
        move;
        .wait(2000).

+!move(Direction)
    :   Direction == down & position(X, Y) & rotation(Direction) & not saw(X, Y, down, Object, near)
    <-  -position(X, Y);
        +position(X, Y-1);
        move;
        .wait(2000).

// +!move(Direction)
//     :   rotation(Direction)
//     <-  move.

+!gostraighton(Direction)
    :   rotation(Direction) & position(X, Y) & not saw(X, Y, Direction, Object, near)
    <-  .print("I make a step.");
        !move(Direction);
        !gostraighton(Direction).

+!gostraighton(Direction)
    :   rotation(Direction) & saw(X, Y, Direction, _, near) & position(X, Y)
    <-  .print("Destination reached!").

// +!findemptyDirection(Direction)
//     :   
//     <-  

+sight(Object)
    :   position(X, Y) & rotation(Rotation) & distance(Distance)
    <-  .print("I saw ", Object, Distance);
        +saw(X, Y, Rotation, Object, Distance).