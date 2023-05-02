:- module('spec', [trace_expression/2, match/2]).
:- use_module(monitor('deep_subdict')).
match(_event, msg_user_to_bot) :- deep_subdict(_{'receiver':"bot",'sender':"user"}, _event).
match(_event, msg_bot_to_user) :- deep_subdict(_{'receiver':"user",'sender':"bot"}, _event).
match(_event, add_object) :- deep_subdict(_{'slots':_{'session_started_metadata':_,'relName':"None",'relative':"None",'object':_,'vertical':_,'horizontal':_},'intent':_{'confidence':_,'name':"add_object"}}, _event).
match(_event, add_object_rel(Obj)) :- deep_subdict(_{'slots':_{'session_started_metadata':_,'relName':Obj,'relative':_,'horizontal':_,'vertical':_,'object':_},'intent':_{'confidence':_,'name':"add_relative_object"}}, _event).
match(_event, remove_object(Obj)) :- deep_subdict(_{'slots':_{'session_started_metadata':_,'relName':Obj,'relative':_,'horizontal':_,'vertical':_,'object':_},'intent':_{'confidence':_,'name':"remove_object"}}, _event).
match(_event, object_added(Obj)) :- deep_subdict(_{'name':Obj,'next_action':"utter_add_object"}, _event).
match(_event, object_added_rel(Obj)) :- deep_subdict(_{'name':Obj,'next_action':"utter_add_relative_object"}, _event).
match(_event, object_removed) :- deep_subdict(_{'next_action':"utter_remove_object"}, _event).
match(_event, irrelevant) :- deep_subdict(_{'next_action':"send_info"}, _event).
match(_event, irrelevant) :- deep_subdict(_{'next_action':"action_reset_all_slots"}, _event).
match(_event, irrelevant) :- deep_subdict(_{'next_action':"send_relative_info"}, _event).
match(_event, irrelevant) :- deep_subdict(_{'next_action':"send_remove_info"}, _event).
match(_event, relevant) :- not(match(_event, irrelevant)).
match(_, any).
trace_expression('Main', Main) :- Main=((relevant>>AddObject);1), AddObject=var(obj, (((msg_user_to_bot:eps)/\(add_object:eps))*(((msg_bot_to_user:eps)/\(object_added(var(obj)):eps))*(app(AddObjectRel, [var('obj')])|optional(AddObject))))), AddObjectRel=gen(['obj'], ((((msg_user_to_bot:eps)/\(remove_object(var(obj)):eps))*((msg_bot_to_user:eps)/\(object_removed:eps)))\/var(obj1, (((msg_user_to_bot:eps)/\(add_object_rel(var(obj)):eps))*(((msg_bot_to_user:eps)/\(object_added_rel(var(obj1)):eps))*(optional(app(AddObjectRel, [var('obj1')]))|optional(app(AddObjectRel, [var('obj')])))))))).
