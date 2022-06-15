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