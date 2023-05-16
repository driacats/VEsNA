+!start
    :   true
    <-  .print("Starting prompter");
        // These lines create a new agent called actor with the file actor.asl
        // And sends to actor a goal.
        // .create_agent(actor, "actor.asl");
        // .send(actor, achieve, start);
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
    : not object(_, PosX, PosY)
    <- .print("Intent add_object");
        addObjectGlobal(ObjName, PosX, PosY, Result);
        -instruction(add_object, ObjName, POsX, PosY, PosRel, ObjRel, Direction);
        +object(Result, PosX, PosY).

// This goal is triggered when an instruction with intent "add_relative_object" is added to the beliefs
+instruction(add_relative_object, ObjName, PosX, PosY, PosRel, ObjRel, Direction)
    : not object(_, PosRel, ObjRel)
    <- .print("Intent add_relative_object");
        addObjectRelative(ObjName, PosRel, ObjRel, Result);
        -instruction(add_relative_object, ObjName, PosX, PosY, PosRel, ObjRel, Direction);
        +object(Result, PosRel, ObjRel).

// This goal is triggered when an instruction with intent "remove_object" is added to the beliefs
+instruction(remove_object, ObjName, PosX, PosY, PosRel, ObjRel, Direction)
    : object(ObjRel, _, _)
    <- .print("Intent remove_object");
        removeObject(ObjRel, Result);
        -instruction(remove_object, ObjName, PosX, PosY, PosRel, ObjRel, Direction);
        -object(ObjRel, _, _).

+instruction(add_actor, ObjName, PosX, PosY, PosRel, ObjRel, Direction)
    <- .print("Intent add actor");
        +actor(PosX, PosY).

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }