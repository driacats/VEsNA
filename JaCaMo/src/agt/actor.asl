rotation(up).
position(0, 0).

step(X, Y+1, up) :- position(X, Y).
step(X, Y-1, down) :- position(X, Y).
step(X+1, Y, right) :- position(X, Y).
step(X-1, Y, left) :- position(X, Y).

opposite(up, Opposite) :- Opposite = down.
opposite(down, Opposite) :- Opposite = up.
opposite(right, Opposite) :- Opposite = left.
opposite(left, Opposite) :- Opposite = right.

right(up, Right) :- Right = right.
right(down, Right) :- Right = left.
right(right, Right) :- Right = up.
right(left, Right) :- Right = down.

+!start
    :   true
    <-  .print("Starting actor");
        makeArtifact("actor", "stage.Actor", [], ArtId);
        focus(ArtId);
        .wait(2000);
        !walk.

+!whereiam
    :   true
    <-  .print("Where I am?");
        whereiam;
        .wait(3000);
        ?position(X, Y);
        .print("Ok, I am at (", X, ", ", Y, ")").

// walk makes the agent walk around, always looking for a new empty direction
+!walk
    :   position(X, Y) & path(X, Y, Direction, empty) & target(Direction)
    <-  .print("Going direction ", Direction);
        !gostraighton(Direction);
        !walk.

+!walk
    :   position(X, Y) & path(X, Y, Direction, stop) & target(Direction)
    <-  .print("Looking for a new direction to walk");
        !findemptyDirection;
        !walk.

+!walk
    :   position(X, Y) & not target(_)
    <-  .print("No target present, finding a way.");
        !findemptyDirection;
        !walk.

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
    :   position(X, Y) & not rotation(Direction)
    <-  .print("Rotating to ", Direction);
        -+rotation(Direction);
        ?is_path(X, Y, Direction, Condition, Path);
        +Path;
        rotate(Direction);
        .wait(1000).

+!rotate(Direction)
    :   rotation(Direction)
    <-  .print("Already in correct direction: ", Direction).


// Move the agent of one step in the given direction
+!move(Direction)
    :   position(X, Y) & rotation(Direction) & step(X1, Y1, Direction) & not path(X, Y, Direction, stop)
    <-  -+position(X1, Y1);
        ?is_path(X1, Y1, Direction, Condition, Path);
        +Path;
        move;
        .print("Moving to ", X1, ", ", Y1);
        .wait(1500).

// Rotate in the correct direction if not already right
+!move(Direction)
    :   not rotation(Direction)
    <-  !rotate(Direction);
        .wait(1000);
        !move(Direction).

// Check if the path is already in the belief base
+?is_path(X, Y, Direction, Condition, Path)
    :   path(X, Y, Direction, Condition)
    <-  .print("The path in direction ", Direction, " from position (", X, ", ", Y, ") is already ", Condition);
        Path = path(X, Y, Direction, Condition).

+?is_path(X, Y, Direction, Condition, Path)
    :   true
    <-  .print("The path in direction ", Direction, " from position (", X, ", ", Y, ") is not known.");
        Path = path(X, Y, Direction, empty).

// Go in one direction til you find a wall
+!gostraighton(Direction)
    :   position(X, Y) & path(X, Y, Direction, empty)
    <-  .print("I make a step ", Direction);
        !move(Direction);
        !gostraighton(Direction).

+!gostraighton(Direction)
    :   rotation(Direction) & position(X, Y) & path(X, Y, Direction, stop)
    <-  .print("Destination reached!").

+!gostraighton(Direction)
    :   position(X, Y) & not path(X, Y, Direction, _)
    <-  .print("I don't know a path in ", Direction);
        !findemptyDirection.

// Finds an empty direction from position X, Y
+!findemptyDirection
    :   position(X, Y) & rotation(Direction) & path(X, Y, Direction, empty)
    <-  .print("Current direction is empty.").

+!findemptyDirection
    :   position(X, Y) & rotation(Direction)
    <-  !lookaround;
        .wait(1000);
        !newtarget.

// Select a new target direction that can be followed
+!newtarget
    :   position(X, Y) & path(X, Y, Direction, empty) & not step(X, Y, Direction)
    <-  -+target(Direction);
        +decision(X, Y, Direction);
        .print("Selected new target ", Direction).

+!newtarget
    :   position(X, Y) & not path(X, Y, Direction, empty) & decision(X, Y, Direction)
    <-  .print("All targets already tried.").

+!newtarget
    :   position(X, Y)
    <-  .print("No available paths.").

+seen(Object, Direction, Distance)
    :   position(X, Y)
    <-  .print("Got ", Object, " with rotation ", Direction, " at distance ", Distance);
        +saw(X, Y, Direction, Object, Distance).

+saw(X, Y, Direction, wall, Distance)
    :   path(X, Y, Direction, Condition) & (Distance == touch | Distance == near)
    <-  .print("The path in direction ", Direction, " from position (", X, ", ", Y, ") is not empty (updating).");
        -path(X, Y, Direction, empty);
        +path(X, Y, Direction, stop).

+saw(X, Y, Direction, wall, Distance)
    :   Distance == touch | Distance == near
    <-  .print("The path in direction ", Direction, " from position (", X, ", ", Y, ") is not empty (creating).");
        +path(X, Y, Direction, stop).

+saw(X, Y, Drection, door, Distance)
    :   true
    <-  whereiam;
        whereis(door).

+at(X, Y)
    <-  .print("I am at (", X, ", ", Y, ")").

{ include("$jacamo/templates/common-cartago.asl") }
{ include("$jacamo/templates/common-moise.asl") }
{ include("$moise/asl/org-obedient.asl") }