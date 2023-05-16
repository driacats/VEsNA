# This files contains your custom actions which can be used to run
# custom Python code.
#
# See this guide on how to implement these action:
# https://rasa.com/docs/rasa/custom-actions

# TODO: use the result to give different answers.

#from rasa_sdk import Action, Tracker
from rasa_sdk import Action
from rasa_sdk.events import AllSlotsReset
#from rasa_sdk.executor import CollectingDispatcher
#import socket
import json
from websocket import create_connection

class ActionResetAllSlots(Action):

    def name(self):
        return "action_reset_all_slots"

    def run(self, dispatcher, tracker, domain):
        return [AllSlotsReset()]

class SendInfo(Action):

    mas_server = "ws://localhost:5002/websockets/endpoint"

    def name(self):
        return "send_info"
    
    def run(self, dispatcher, tracker, domain):
        instruction = {}
        instruction['intent'] = tracker.latest_message['intent'].get('name')
        instruction['obj'] = tracker.get_slot('object')
        if instruction['intent'] in ('add_object', 'add_actor') and tracker.get_slot('horizontal') is None:
            instruction['posX'] = 'center'
        else:
            instruction['posX'] = tracker.get_slot('horizontal')
        if instruction['intent'] in ('add_object', 'add_actor') and tracker.get_slot('vertical') is None:
            instruction['posY'] = 'center'
        else:
            instruction['posY'] = tracker.get_slot('vertical')
        instruction['relative'] = tracker.get_slot('relative')
        instruction['relName'] = tracker.get_slot('relName')
        instruction['actorName'] = tracker.get_slot('actorName')
        ws = create_connection(self.mas_server)
        print("connection created...")
        ws.send(json.dumps(instruction))
        print("sending...")
        result = ws.recv()
        print("getting the result: ", result)
        ws.close()