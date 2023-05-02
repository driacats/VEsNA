:- module('spec', [trace_expression/2, match/2]).
:- use_module(monitor('deep_subdict')).
match(_event, msg_user_to_bot) :- deep_subdict(_{'receiver':"bot",'sender':"user"}, _event).
match(_event, msg_bot_to_user) :- deep_subdict(_{'receiver':"user",'sender':"bot"}, _event).
match(_event, add_object(X, Y)) :- deep_subdict(_{'slots':_{'session_started_metadata':_,'relName':_,'relative':_,'object':_,'vertical':Y,'horizontal':X},'intent':_{'confidence':_,'name':"add_object"}}, _event).
match(_event, not_add_object(X, Y)) :- not(match(_event, add_object(X, Y))).
match(_event, object_added) :- deep_subdict(_{'next_action':"utter_add_object"}, _event).
match(_event, irrelevant) :- deep_subdict(_{'next_action':"send_info"}, _event).
match(_event, irrelevant) :- deep_subdict(_{'next_action':"action_reset_all_slots"}, _event).
match(_event, relevant) :- not(match(_event, irrelevant)).
match(_, any).
trace_expression('Main', Main) :- Main=((relevant>>AddObject);1), AddObject=var(x, var(y, (((msg_user_to_bot:eps)/\(add_object(var(x), var(y)):eps))*(((msg_bot_to_user:eps)/\(object_added:eps))*(app(ObjectNotAlreadyAdded, [var('x'), var('y')])/\optional(AddObject)))))), ObjectNotAlreadyAdded=gen(['x', 'y'], star((not_add_object(var(x), var(y)):eps))).
