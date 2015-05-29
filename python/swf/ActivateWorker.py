from BaseWorker import BaseWorker
from util import RemoteCommand

class ActivateWorker(BaseWorker):

    def __init__(self):
        BaseWorker.__init__(self)
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

if __name__ == '__main__':
    ActivateWorker().run()
