# VEsNA Threasure Hunt

JSON formatted messages.

Agent -> Body:
Actions
    - walk
        - reach a region
        - random

{
    'sender': AgentName,
    'receiver': BodyName,
    'action': Action,
    'data': {
        'target': RegionName / random
    }
}

Body -> Agent:
Perceptions
    - sight
    - task accomplished

{
    'sender': BodyName,
    'receiver': AgentName,
    'perception': sight,
    'data': {
        'sights': [...]
    }
}

{
    'sender': BodyName,
    'receiver': AgentName,
    'perception': task,
    'data': {
        'status': 'accomplished'/'ongoing'/'todo'
    }
}