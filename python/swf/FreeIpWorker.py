from BaseWorker import BaseWorker

'''
Confirm the hostname is really freeip.
'''
class FreeIpWorker(BaseWorker):
    # TODO
    def activity(self, context):
        print 'Free ip %s' % context.get_hostname()
        return 'Confirm'
