:- module('spec', [trace_expression/2, match/2]).
:- use_module(monitor('deep_subdict')).
match(_event, reliable_msg) :- deep_subdict(_{'intent':_{'confidence':Value,'name':_}}, _event), >=(Value, 0.6).
match(_event, relevant) :- deep_subdict(_{'intent':_}, _event).
match(_, any).
trace_expression('Main', Main) :- Main=((relevant>>ReliableMsg);1), ReliableMsg=star((reliable_msg:eps)).
