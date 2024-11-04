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

different(Dir1, Dir2, Dir3) :- Dir1 \== Dir2 & Dir1 \== Dir3 & Dir2 \== Dir3.

dead_end(Landmark) :- object(_, Landmark, Direction1, _) & object(_, Landmark, Direction2, _) & object(_, Landmark, Direction3, _) & different(Direction1, Direction2, Direction3).
corridor(Landmark) :- object(_, Landmark, Direction1, _) & object(_, Landmark, Direction2, _) & Direction1 \== Direction2.
deviation(Landmark) :- object(_, Landmark, _, _).
room(Landmark) :- not object(_, Landmark, _, _).

is_wall(Object) :- atom_concat('wall', _, Object).

next_landmark(0) :- not actual_landmark(_).
next_landmark(Landmark + 1) :- actual_landmark(Landmark).

previous_landmark(0) :- not actual_landmark(_).
previous(Landmark - 1) :- actual_landmark(Landmark).

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

// +!update_path(Landmark, NextLandmark, Direction)
//     :   path(Landmark, NextLandmark, Path)
//     <-  -path(Landmark, NextLandmark, Path);
//         +path(Landmark, NextLandmark, [Direction|Path]).

// +!update_path(Landmark, NextLandmark, Direction)
//     :   true
//     <-  +path(Landmark, NextLandmark, [Direction]).

// +!update_landmark
//     :   next_landmark(NextLandmark) & actual_landmark(Landmark)
//     <-  +landmark(NextLandmark, Landmark);
//         -+actual_landmark(NextLandmark).

// +!update_landmark
//     :   not actual_landmark(_)
//     <-  ?next_landmark(NextLandmark);
//         +actual_landmark(NextLandmark).