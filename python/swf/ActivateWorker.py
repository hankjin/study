from BaseWorker import BaseWorker
from util import RemoteCommand

class ActivateActivity(BaseWorker):

    def __init__(self):
        super.__init__(self)
        self.util = RemoteCommand()
    def activate(self, hostname):
        # activate DFDD
        ret,output = self.util.run(hostname, "sudo /apollo/bin/runCommand -a Activate -e SQSDFDD")
        if 0 != ret:
            print 'Failed to Activate SQSDFDD', output
            return 'Fail'
        # status active
        ret,output = self.util.run("/apollo/bin/runCommand --status Activate %s" % hostname)
        if 0 != ret:
            print 'Failed to set host Activate', output
            return 'Fail'
        # health check
        # TODO
        return 'Succeed'

    def activity(self, context, task):
        return self.activate(context.get_hostname())
