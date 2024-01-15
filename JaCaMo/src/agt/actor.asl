// Actions available:
// - [ ] move
// - [ ] rotate
// - [ ] grab
// - [ ] jump

// Actions "funny" (only to try in place for the moment):
// - [ ] look around
// - [ ] dance

// To connect artifacts:
// focus(ArtId);
// lookupArtifact();
// linkArtifacts().
// Facendo così posso prendere metodi da altri artifact per costruire un altro artifact.
// Bisogna implementare un metodo dentro all'artifact che sappia cosa prendere.
// signal("nome", valore) in Java manda un segnale agli agenti che hanno il focus su di ess
// Triggera eventi ma non salva nulla nei belief.

+!start
    :   port(Port)
    <-  .print("Starting actor");
        makeArtifact("actor", "stage.Actor", [], ArtId);
        focus(ArtId);
        .wait(2000);
        !lookaround;
        .wait(1000);
        !move(forward);
        .wait(1000);
        !move(right);
        .wait(1000);
        !move(back);
        .wait(1000);
        !move(left);
        .wait(1000);
        !move(forward).

// TODO: move position in beliefs
+!move(Direction)
    :   port(Port)
    <-  .print("I will move ", Direction);
        move(Direction, Port, NewPosition).

+!lookaround
    :   port(Port)
    <-  perform(lookaround, Port, _);
        .wait(6000);
        request(eyes, Port, Objects);
        !get_beliefs_from_list(Objects).


+!get_beliefs_from_list([]).
+!get_beliefs_from_list({}).
+!get_beliefs_from_list([Belief|BeliefTail])
    <-  +Belief;
        !get_beliefs_from_list(BeliefTail).