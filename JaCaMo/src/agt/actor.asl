// Actions available:
// - [ ] move
// - [ ] rotate
// - [ ] grab
// - [ ] jump

// Actions "funny" (only to try in place for the moment):
// - [ ] look around
// - [ ] dance

+!start
    :   true
    <-  .print("Starting actor");
        makeArtifact("actor", "stage.Actor", [], ArtId);
        focus(ArtId).