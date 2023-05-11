+!start
    :   true
    <-  .print("Starting prompter");
        +prompter(listening).

// The agent listens the user and adds a belief on the instruction given by the user
+prompter(listening)
    <-  .print("I am listening");
        listen(Intent, ObjName, PosX, PosY, PosRel, ObjRel, Direction);
        +instruction(Intent, ObjName, PosX, PosY, PosRel, ObjRel, Direction);
        -prompter(listening);
        +prompter(waiting).

// The agent waits (for the moment useless)
+prompter(waiting)
    <-  .print("I am waiting");
        -prompter(waiting);
        +prompter(listening).

// This goal is triggered when an instruction with intent "add_object" is added to the beliefs
+instruction(add_object, ObjName, PosX, PosY, PosRel, ObjRel, Direction)
    <- .print("Intent add_object");
        addObjectGlobal(ObjName, PosX, PosY, Result).

// This goal is triggered when an instruction with intent "add_relative_object" is added to the beliefs
+instruction(add_relative_object, ObjName, PosX, PosY, PosRel, ObjRel, Direction)
    <- .print("Intent add_relative_object");
        addObjectRelative(ObjName, PosRel, ObjRel, Result).

// This goal is triggered when an instruction with intent "remove_object" is added to the beliefs
+instruction(remove_object, ObjName, PosX, PosY, PosRel, ObjRel, Direction)
    <- .print("Intent remove_object");
        removeObject(ObjRel, Result).

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }