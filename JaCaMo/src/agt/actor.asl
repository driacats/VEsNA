// Actions available:
// - [ ] move
// - [ ] rotate
// - [ ] grab
// - [ ] jump

// Actions "funny" (only to try in place for the moment):
// - [ ] look around
// - [ ] dance

+!start
    :   port(Port)
    <-  .print("Starting actor");
        makeArtifact("actor", "stage.Actor", [], ArtId);
        focus(ArtId);
        .wait(2000);
        perform(lookaround, Port, _);
        .wait(8000);
        request(eyes, Port, Objects);
        .print(Objects);
        !get_beliefs_from_list(Objects);
        .wait(4000);
        move(forward, Port, _);
        .wait(2000);
        move(right, Port, _);
        .wait(2000);
        move(back, Port, _);
        .wait(2000);
        move(left, Port, _);
        .wait(2000);
        move(forward, Port, _).

+!get_beliefs_from_list([]).
+!get_beliefs_from_list({}).
+!get_beliefs_from_list([Belief|BeliefTail])
    <-  +Belief;
        !get_beliefs_from_list(BeliefTail).