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
import time
from websocket import create_connection

class ActionResetAllSlots(Action):

    def name(self):
        return "action_reset_all_slots"

    def run(self, dispatcher, tracker, domain):
        print("[RESET ALL SLOTS] time: ", time.time())
        return [AllSlotsReset()]

class SendInfo(Action):

    def name(self):
        return "send_info"

    def run(self, dispatcher, tracker, domain):
        print("[SEND INFO] time: ", time.time())
        obj = tracker.get_slot("object")
        dispatcher.utter_message(text="[SendInfo] object:" + obj)
        posX = tracker.get_slot("horizontal")
        if posX is not None:
            dispatcher.utter_message(text="[SendInfo] horizontal:" + posX)
        else:
            posX = "center"
        posY = tracker.get_slot("vertical")
        if posY is not None:
            dispatcher.utter_message(text="[SendInfo] vertical: " + posY)
        else:
            posY = "center"
        ws = create_connection("ws://localhost:5002/websockets/endpoint")
        print("connection created...")
        ws.send("intent = " + tracker.latest_message['intent'].get('name') + ",obj = " + obj + ",posX = " + posX + ",posY = " + posY)
        print("Sending...")
        result = ws.recv()
        print("getting the result...")
        ws.close()


class SendRelativeInfo(Action):

    def name(self):
        return "send_relative_info"

    def run(self, dispatcher, tracker, domain):
        print("[SEND RELATIVE INFO] time: ", time.time())
        print("Hello from send_relative_info")
        obj = tracker.get_slot("object")
        dispatcher.utter_message(text="[SendRelativeInfo] object:" + obj)
        relPos = tracker.get_slot("relative")
        relName = tracker.get_slot("relName")
        ws = create_connection("ws://localhost:5002/websockets/endpoint")
        ws.send("intent = " + tracker.latest_message['intent'].get('name') + ",obj = " + obj + ",relative = " + relPos + ",relName = " + relName)
        result = ws.recv()
        ws.close()

class SendRemoveInfo(Action):

    def name(self):
        return "send_remove_info"

    def run(self, dispatcher, tracker, domain):
        print("[SEND REMOVE INFO] time: ", time.time())
        relName = tracker.get_slot("relName")
        ws = create_connection("ws://localhost:5002/websockets/endpoint")
        ws.send("intent = " + tracker.latest_message['intent'].get('name') + ",relName = " + relName)
        result = ws.recv()
        ws.close()