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
        performAction(lookaround, Port, Result);
        .wait(8000);
        request(eyes, Port, Objects);
        .print(Objects);
        !get_beliefs_from_list(Objects).

+!get_beliefs_from_list([]).
+!get_beliefs_from_list([Belief|BeliefTail])
    <-  +Belief;
        !get_beliefs_from_list(BeliefTail).


// +!getParameters([]).
// +!getParameters([Param|List])
// 	<-	getInfo(Param);
// 		!getParameters(List).