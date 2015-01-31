#!/usr/bin/python

import sys
import os
import glob
import importlib

CMD_SUBDIR='apollo'
# command file prefix: eg command=hello, command file=cmd_hello
CMD_PREFIX = 'cmd_'
# command file suffix: eg command=hello, command file=hello.py
CMD_SUFFIX = '.py'
# Directory where commands located
CMD_DIR='%s/%s' % (os.path.dirname(os.path.abspath(__file__)), CMD_SUBDIR)

'''
Print Usage help message.
'''
def print_usage(prog):
    '''
    >>> print_usage('hello')
    Usage: hello command [options]
    Available commands:
     hello
    '''
    cmd_file_pattern = '%s/%s*%s' % (CMD_DIR, CMD_PREFIX, CMD_SUFFIX)
    cmd_files = glob.glob(cmd_file_pattern)
    cmds=[]
    for cmd_file in cmd_files:
        cmds.append(os.path.basename(cmd_file)[len(CMD_PREFIX) : -len(CMD_SUFFIX)])
    sys.stdout.write('Usage: %s command [options]\nAvailable commands:\n %s\n' % (prog, '\n\t'.join(cmds)))

'''
execute command
'''
def invoke_command(cmd, args):
    '''
    >>> invoke_command('not_exists', [])
    False
    >>> invoke_command('hello', [])
    None
    8
    []
    True
    '''
    cmd_path = '%s.%s%s' % (CMD_SUBDIR, CMD_PREFIX, cmd)
    cmd_module = None
    try:
        cmd_module = importlib.import_module(cmd_path)
        if not hasattr(cmd_module, 'execute'):
            sys.stderr.write('Command %s has no "execute" method\n' % cmd_path)
            sys.exit(1)
    except Exception, e:
        sys.stderr.write('Invalid command "%s" "%s"\n' % (cmd, e))
        return False

    return cmd_module.execute(cmd, args)
    
if __name__ == '__main__':
    if len(sys.argv) < 2:
        print_usage()
        sys.exit(1)

    cmd = sys.argv[1]
    args = sys.argv[2:]
    
    if invoke_command(cmd, args):
        sys.exit(0)
    else:
        sys.exit(1)

