+!start
    :   true
    <-  .print("Starting prompter");
        +prompter(listening).

// The agent listens the user and adds a belief on the instruction given by the user
+prompter(listening)
    <-  .print("I am listening");
        listen(Intent, ObjName, PosX, PosY, PosRel, ObjRel, Direction, ActorName);
        +instruction(Intent, ObjName, PosX, PosY, PosRel, ObjRel, Direction, ActorName);
        -prompter(listening);
        +prompter(waiting).

// The agent waits (for the moment useless)
+prompter(waiting)
    <-  .print("I am waiting");
        -prompter(waiting);
        +prompter(listening).

// This goal is triggered when an instruction with intent "add_object" is added to the beliefs
+instruction(add_object, ObjName, PosX, PosY, PosRel, ObjRel, Direction, ActorName)
    : not object(_, PosX, PosY)
    <- .print("Intent add_object");
        addObjectGlobal(ObjName, PosX, PosY, Result);
        -instruction(add_object, ObjName, POsX, PosY, PosRel, ObjRel, Direction, ActorName);
        +object(Result, PosX, PosY).

// This goal is triggered when an instruction with intent "add_relative_object" is added to the beliefs
+instruction(add_relative_object, ObjName, PosX, PosY, PosRel, ObjRel, Direction, ActorName)
    : not object(_, PosRel, ObjRel)
    <- .print("Intent add_relative_object");
        addObjectRelative(ObjName, PosRel, ObjRel, Result);
        -instruction(add_relative_object, ObjName, PosX, PosY, PosRel, ObjRel, Direction, ActorName);
        +object(Result, PosRel, ObjRel).

// This goal is triggered when an instruction with intent "remove_object" is added to the beliefs
+instruction(remove_object, ObjName, PosX, PosY, PosRel, ObjRel, Direction, ActorName)
    : object(ObjRel, _, _)
    <- .print("Intent remove_object");
        removeObject(ObjRel, Result);
        -instruction(remove_object, ObjName, PosX, PosY, PosRel, ObjRel, Direction, ActorName);
        -object(ObjRel, _, _).

+instruction(add_actor, ObjName, PosX, PosY, PosRel, ObjRel, Direction, ActorName)
    <- .print("Intent add actor");
        addActor(ActorName, PosX, PosY, Result);
        .create_agent(actor, "actor.asl");
        .send(actor, tell, name(ActorName));
        .send(actor, tell, position(PosX, PosY));
        .send(actor, achieve, start);
        -instruction(add_actor, ObjName, PosX, PosY, PosRel, ObjRel, Direction, ActorName);
        +actor(ActorName, PosX, PosY).

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }