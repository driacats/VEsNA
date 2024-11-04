+seen(Object, Direction, Distance, front)
    :   actual_landmark(Landmark) & Object \== empty
    <-  .print("I can see a ", Object, " in front of me (I am watching ", Direction, ") at distance ", Distance);
        -object(Object, Landmark, _, _);
        +object(Object, Landmark, Direction, Distance).

// +seen(Object, Direction, Distance, left)
//     :   actual_landmark(Landmark) & Object \== empty
//     <-  .print("I can see a ", Object, " on my left (I am watching ", Direction, ") at distance ", Distance);
//         ?left(Direction, GlobalDir);
//         -object(Object, Landmark, _, _);
//         +object(Object, Landmark, GlobalDir, Distance).

// +seen(Object, Direction, Distance, right)
//     :   actual_landmark(Landmark) & Object \== empty
//     <-  .print("I can see a ", Object, " on my right (I am watching ", Direction, ") at distance ", Distance);
//         ?right(Direction, GlobalDir);
//         -object(Object, Landmark, _, _);
//         +object(Object, Landmark, GlobalDir, Distance).

+seen(empty, Direction, front)
    :   actual_landmark(Landmark)
    <-  +way(Landmark, Direction);.

+seen(empty, Direction, left)
    :   actual_landmark(Landmark)
    <-  ?left(Direction, GlobalDir);
        +way(Landmark, GlobalDir).

+seen(empty, Direction, right)
    :   actual_landmark(Landmark)
    <-  ?right(Direction, GlobalDir);
        +way(Landmark, GlobalDir).

+seen(Object, Direction, Side)
    :   true
    <-  .print("There is ", Object, " at my ", Side, " in direction ", Direction).

// +object(Object, Landmark, GlobalDir, Distance)
//     :   previous_landmark(PrevLM) & not object(Object, PrevLM, _, _) & PrevLM \== -1
//     <-  !update_landmark.
// +way(Landmark, Direction)
//     :   actual_landmark(Landmark)
//     <-  ?next_landmark(NewLandmark);
//         !update_landmark(NewLandmark).

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