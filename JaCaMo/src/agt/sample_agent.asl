!start.

+request(RequestedBy, ResponseId, IntentName, Params, Contexts)
	:	true
	<-	.print("Request received ", IntentName, " of Dialogflow");
		!answer(RequestedBy, ResponseId, IntentName, Params, Contexts);.

+!answer(RequestedBy, ResponseId, IntentName, Params, Contexts)
	:	(IntentName == "Call Jason Agent")
	<- 	reply("Hello, I am your Jason agent, how can I help you?");.

+!answer(RequestedBy, ResponseId, IntentName, Params, Contexts)
	:	(IntentName == "AddObject")
	<- 	!getParameters(Params);
		addObject(Result);
		reply(Result);.

+!answer(RequestedBy, ResponseId, IntentName, Params, Contexts)
	:	(IntentName == "RemoveObject")
	<-	!getParameters(Params);
		removeObject(Result);
		reply(Result).

+!answer(RequestedBy, ResponseId, IntentName, Params, Contexts)
	:	(IntentName == "MoveActor")
	<-	!getParameters(Params);
		moveActor(Result);
		reply(Result).

+!answer(RequestedBy, ResponseId, IntentName, Params, Contexts)
	:	(IntentName == "Exit")
	<-	exit(Result);
		reply(Result).

+!answer(RequestedBy, ResponseId, IntentName, Params, Contexts)
	:	(IntentName == "Call Intent By Event")
	<- 	replyWithEvent("Answering with an event", "testEvent");.

+!answer(RequestedBy, ResponseId, IntentName, Params, Contexts)
	:	(IntentName == "Intent Called By Event")
	<- 	reply("Answering to an intention called by an event");.

+!answer(RequestedBy, ResponseId, IntentName, Params, Contexts)
	:	(IntentName == "Call With Contexts and Parameters")
	<- 	.print("The contexts and parameters will be listed below.");
		!printContexts(Contexts);
		!printParameters(Params);
		reply("Hello, I'm your Jason agent, I received your contexts and parameters");.

+!answer(RequestedBy, ResponseId, IntentName, Params, Contexts)
	:	(IntentName == "Call With Contexts")
	<-	.print("The contexts will be listed below.");
		!printContexts(Contexts);
		reply("Hello, I am your Jason agent, I received your contexts");.

+!answer(RequestedBy, ResponseId, IntentName, Params, Contexts)
	:	(IntentName == "Reply With Context")
	<-	.print("The context will be created next.");
		contextBuilder(ResponseId, "test context", "1", Context);
		.print("Context created: ", Context);
		replyWithContext("Hello, I am your Jason agent, and I am responding with context", Context);.

+!answer(RequestedBy, ResponseId, IntentName, Params, Contexts)
	:	true
	<-	reply("Sorry, I do not recognize this intention");
		.print(IntentName).

+!printContexts([]).
+!printContexts([Context|List])
	<-	.print(Context);
		!printContexts(List);.

+!printParameters([]).
+!printParameters([Param|List])
	<-	.print(Param)
		!printParameters(List).

+!getParameters([]).
+!getParameters([Param|List])
	<-	getInfo(Param);
		!getParameters(List).

+!hello
	:	true
	<-	.print("hello world");.

+!start
	:	true
	<-	.print("Sample agent enabled.").

{ include("$jacamoJar/templates/common-cartago.asl") }
{ include("$jacamoJar/templates/common-moise.asl") }

// uncomment the include below to have an agent compliant with its organisation
//{ include("$moiseJar/asl/org-obedient.asl") }
