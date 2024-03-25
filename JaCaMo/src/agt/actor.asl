+!start
    :   true
    <-  .print("Starting actor");
        .wait(10000);
        makeArtifact("actor", "stage.Actor", [], ArtId);
        focus(ArtId);
        .wait(2000);
        act;
        .wait(2000);
        !lookaround;
        !move(forward).

+!lookaround
    :   true
    <-  !rotate(right);
        .wait(1000);
        !rotate(right);
        .wait(1000);
        !rotate(right);
        .wait(1000);
        !rotate(right).

+!rotate(Direction)
    :   true
    <-  rotate(Direction).

//TODO: La direzione deve essere accessibile come precondizione
+!move(Direction)
    :   (Direction == right | Direction == left)
    <-  rotate(Direction);
        .wait(1000);
        move.

+!move(Direction)
    :   true
    <-  move.
// +!see
//     :   true
//     <-  see.
// +!start
//     :   port(Port)
//     <-  .print("Starting actor");
//         makeArtifact("actor", "stage.Actor", [], ArtId);
//         focus(ArtId);
//         .wait(2000);
//         !lookaround;
//         .wait(1000);
//         !move(forward);
//         .wait(1000);
//         !move(right);
//         .wait(1000);
//         !move(back);
//         .wait(1000);
//         !move(left);
//         .wait(1000);
//         !move(forward).

// +!move(Direction)
//     :   port(Port)
//     <-  .print("I will move ", Direction);
//         move(Direction, Port, NewPosition).

// +!lookaround
//     :   port(Port)
//     <-  perform(lookaround, Port, _);
//         .wait(6000);
//         request(eyes, Port, Objects);
//         !get_beliefs_from_list(Objects).


// +!get_beliefs_from_list([]).
// +!get_beliefs_from_list({}).
// +!get_beliefs_from_list([Belief|BeliefTail])
//     <-  +Belief;
//         !get_beliefs_from_list(BeliefTail).