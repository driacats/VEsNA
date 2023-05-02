// !start.

+!start
    :   true
    <-  .print("Starting prompter");
        +prompter(listening);
        !listen.

+!listen
    :   prompter(listening)
    <-  .print("I am listening");
        listen(Instructions);
        addObject(Instructions, Result);
        -prompter(listening);
        +prompter(waiting);
        !wait.

+!wait
    :   prompter(waiting)
    <-  .print("I am waiting");
        -prompter(waiting);
        +prompter(listening);
        !listen.



{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }