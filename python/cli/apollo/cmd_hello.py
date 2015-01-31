#!/usr/bin/python

import sys
from optparse import OptionParser

'''
parse arguments
'''
def parse(prog, args):
    '''
    >>> o,a = parse('wow', [])
    >>> o.debug
    >>> o.number
    8
    '''
    usage = 'Usage: %s hello [options] name' % prog
    parser = OptionParser(usage = usage)
    # dest to specify variable to store
    parser.add_option("-d", "--debug", action="store_true", dest='debug',
            help="Print debug information")
    # metavar to set default value in help message
    parser.add_option('-f', '--file', action='store', metavar='FFFF', type='string', dest='file',
            help='File Name')
    # set default value
    parser.add_option('-n', '--number', action='store', type='int', dest='number',
            help='Number to use', default=8)
    (options, args) = parser.parse_args(args)
    return options, args

'''
Entrance of this command
'''
def execute(prog, args):
    '''
    >>> sys.argv=['hello']
    >>> execute('hank', [])
    None
    8
    []
    True
    >>> execute('hank', ['--file', 'myf', '-n', '9', 'hi'])
    myf
    9
    ['hi']
    True
    '''
    options, args = parse(prog, args)
    if options.debug:
        print 'Debuging'
    print options.file
    print options.number
    print args
    return True

