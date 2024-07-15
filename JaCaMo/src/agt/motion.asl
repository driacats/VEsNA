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

+!move(Direction)
    :   first_step & not actual_landmark(_)
    <-  ?next_landmark(Landmark);
        +actual_landmark(Landmark);
        -first_step;
        !move(Direction).

+!move(Direction)
    :   first_step
    <-  ?next_landmark(Landmark);
        -+actual_landmark(Landmark);
        -first_step;
        !move(Direction).

+!move(Direction)
    :   actual_landmark(Landmark) & rotation(Direction) & way(Landmark, Direction) & not first_step
    <-  ?next_landmark(NextLandmark);
        !update_path(Landmark, NextLandmark, Direction);
        move;
        .print("Moving in direction ", Direction);
        .wait(1500).

// Rotate in the correct direction if not already right
+!move(Direction)
    :   actual_landmark(Landmark) & not rotation(Direction) & way(Landmark, Direction) & not first_step
    <-  !rotate(Direction);
        .wait(1000);
        !move(Direction).

+!move(Direction)
    :   actual_landmark(Landmark) & not way(Landmark, Direction)
    <-  .print("The way I'm trying to follow is not available!").