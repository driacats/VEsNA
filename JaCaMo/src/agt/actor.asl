path(0, 0, up, empty).
// path(0, 0, down, empty).
// path(0, 0, right, empty).
// path(0, 0, left, empty).
rotation(up).
position(0, 0).
target(up).
// decision(0, 0, up).

+!start
    :   true
    <-  .print("Starting actor");
        makeArtifact("actor", "stage.Actor", [], ArtId);
        focus(ArtId);
        .wait(1000);
        !walk.
        // !lookaround;
        // .wait(1000);
        // !gostraighton(up);
        // .wait(1000);
        // !rotate(right);
        // .wait(1000);
        // !gostraighton(right).

+!walk
    :   position(X, Y) & path(X, Y, Direction, empty) & target(Direction)
    <-  !gostraighton(Direction);
        !walk.

+!walk
    :   position(X, Y) & path(X, Y, Direction, stop_condition) & target(Direction)
    <-  !findemptyDirection;
        !walk.

+!lookaround
    :   true
    <-  !rotate(right);
        .wait(1000);
        !rotate(down);
        .wait(1000);
        !rotate(left);
        .wait(1000);
        !rotate(up);
        .wait(1000).

+!rotate(Direction)
    :   rotation(OldDirection) & position(X, Y)
    <-  .print("rotating to ", Direction);
        -rotation(OldDirection);
        +rotation(Direction);
        +path(X, Y, Direction, empty);
        rotate(Direction);
        .wait(500).

+!rotate(Direction)
    :   rotation(Direction)
    <-  .print("actor alreay in position.").

+!move(Direction)
    :   not rotation(Direction)
    <-  rotate(Direction);
        .wait(1000);
        !move(Direction).

+!move(Direction)
    :   Direction == up & position(X, Y) & rotation(Direction)
    <-  -position(X, Y);
        +position(X, Y+1);
        +path(X, Y+1, Direction, empty);
        move;
        .wait(1500).

+!move(Direction)
    :   Direction == down & position(X, Y) & rotation(Direction)
    <-  -position(X, Y);
        +position(X, Y-1);
        +path(X, Y-1, Direction, empty);
        move;
        .wait(1500).

+!move(Direction)
    :   Direction == right & position(X, Y) & rotation(Direction)
    <-  -position(X, Y);
        +position(X+1, Y);
        +path(X+1, Y, Direction, empty);
        move;
        .wait(1500).

+!move(Direction)
    :   Direction == left & position(X, Y) & rotation(Direction)
    <-  -position(X, Y);
        +position(X-1, Y);
        +path(X-1, Y, Direction, empty);
        move;
        .wait(1500).

+!gostraighton(Direction)
    :   rotation(Direction) & position(X, Y) & path(X, Y, Direction, empty)
    <-  .print("I make a step ", Direction);
        !move(Direction);
        !gostraighton(Direction).

+!gostraighton(Direction)
    :   rotation(Direction) & position(X, Y) & path(X, Y, Direction, stop_condition)
    <-  .print("Destination reached!").

+!gostraighton(Direction)
    :   position(X, Y) & path(X, Y, Direction, empty)
    <-  !rotate(Direction);
        !move(Direction);
        !gostraighton(Direction).

+!findemptyDirection
    :   position(X, Y) & rotation(Direction) & path(X, Y, Direction, empty)
    <-  .print("Current direction is empty.").

+!findemptyDirection
    :   position(X, Y) & rotation(Direction) & path(X, Y, Direction, stop_condition)
    <-  !lookaround;
        .wait(1000);
        !newtarget;
        .print("Selected new target").

+!newtarget
    :   position(X, Y) & rotation(Direction) & path(X, Y, OtherDirection, empty) & target(OldTarget) & not (OldTarget == OtherDirection) & not decision(X, Y, OtherDirection)
    <-  -target(OldTarget);
        +target(OtherDirection);
        +decision(X, Y, OtherDirection);
        .print("new target: ", Direction).

+!newtarget
    :   position(X, Y) & path(X, Y, Direction, empty) & decision(X, Y, Direction)
    <-  .print("All targets already tried.").

+sight(Object)
    :   position(X, Y) & veRotation(Direction) & distance(Distance)
    <-  .print("I saw ", Object, " ", Distance, " in position (", X, ", ", Y, ") and rotation ", Direction);
        +saw(X, Y, Direction, Object, Distance).

+saw(X, Y, Direction, Object, Distance)
    :   path(X, Y, Direction, Condition) & (Distance == touch | Distance == near)
    <-  -path(X, Y, Direction, Condition);
        +path(X, Y, Direction, stop_condition).