cli framework
======

command line interface

Development
------
in order to add a command hello.
1. add a cmd_hello.py to apollo
1. implement execute(prog, args) to cmd_hello.py
1. run python -m doctest apollo/cmd_hello.py -v to test
1. run ./main.py will show hello is already there
1. run ./main.py hello to execute the new command

Usage
-------
./main.py command args


