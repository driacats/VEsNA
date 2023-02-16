# VEsNA

**VEsNA** is a framework for managing **V**irtual **E**nvironments via **N**atural Language **A**gents, for Factory Automation. The study, design, and engineering of factory automation is not a trivial task. Since robotics is always more and more involved, the final result is not cheap either. Thus, it is vital to understand the best way to exploit such technology, by reducing at the same time the risk of building advanced but eventually useless production lines. Simulation can be a game changer in such scenarios, but it requires advanced programming skills, especially for large scenarios. Unfortunately, the experts of the system may not have such know-how. Thus, in this project, we present a framework for managing Virtual Environments via Natural language Agents (VEsNA) tool, which exploits agent-based technologies and natural language processing to enhance the design of factory automation in industry. VEsNA supports non-expert users in the design of factory automation applications through high-level and user friendly interactions, based completely on natural language texts. In this repository, it is available the full code proposed as example in the presentation paper.



This is a step by step guide to install all the tools.

## Dialogflow

To use Dialogflow go to https://dialogflow.cloud.google.com and sign in to your Google account. Then:

- create a new agent with a name of your choice
- click on the gear on the right of your agent name
- select the *Export and Import* tab
- select *Import zip* and choose the zip provided in this repository

## JaCaMo

Inside the `JaCaMo` folder simply run those two commands from the terminal:

```bash
gradle build
gradle run
```

and the JaCaMo system will start. If not installed, install `gradle`.

## Unity3D

In the `Unity Scripts` folder are present two files:
 - `Listener.cs`
 - `Actor.cs`
Copy those two files to your Unity project.
The `Listener.cs` script must be attached to the floor of the scene GamObject, the `Actor.cs` to the actor.

## Launch the System

To launch the system you have also to install **ngrok** (https://ngrok.com/). Download it and install it, you have to create a free account in order to use this tool.
Then, launch ngrok with

```bash
ngrok http 8080
```

Copy the link provided by ngrok, open the dialogflow page and go to the Fulfillment tab on the left. The first field is `URL`. Paste your url here and save at the bottom of the page.

Now, launch the Unity project and the JaCaMo one as described above and you will be able to use the framework.
