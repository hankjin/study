#!/bin/env python
from optparse import OptionParser
import boto.swf.layer2 as swf
import config
from BaseWorker import Context

def getInputFromOpt():
    parser = OptionParser()
    parser.add_option('-i', '--input', dest='input',
            help='input to the work flow', metavar='INPUT')
    parser.add_option('-v', '--verbose', dest='verbose',
            action='store_true', default=False,
            help="print status message to stdout")
    parser.add_option('-d', '--dryrun', dest='dryrun',
            action='store', default=None,
            help='dryrun mode, eg{"XActivity":"Succeed","BActivity":"Fail"}')
    (options, args) = parser.parse_args()
    context = Context(options.input)
    context.set_verbose(options.verbose)
    context.set_dryrun(options.dryrun)
    return context.to_string()

if __name__ == '__main__':
    userInput=getInputFromOpt()
    execution = swf.WorkflowType(
            name=config.WorkFlowType,
            domain=config.DOMAIN,
            version=config.VERSION,
            task_list = config.TASKLIST,
            ).start(
            input=userInput
            )
    print execution.history()
