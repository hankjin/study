import boto.sns

from BaseWorker import BaseWorker

''' Human Worker
notify operator
wait for operator response
notify operator again if no response after timeout
'''
class HumanWorker(BaseWorker):
    def __init__(self, title, body, timeout, step):
        BaseWorker.__init__(self)
        self.title = title
        self.body = body
        self.timeout = timeout
        self.step = step
        self.sns=boto.sns.connect_to_region('us-east-1')
    '''
    Read tickets to get response.
    '''
    def get_status(self, context, task):
        pass
    '''
    Send email to customer
    '''
    def notify(self, context, task):
        print 'Send Notification to %s' % config.SNS
        self.publish(topic=config.SNS,
                message=self.body,
                subject=self.title)
    def update_notify_msg(self, context, task):
        pass
    '''
    '''
    def activity(self, context, task):
        self.update_notify_msg(context, task)
        self.notify(context, task)
        now=time.time()
        while time.time() - now < self.timeout:
            status = self.get_status(context, task)
            if status:
                break
            time.sleep(self.step)
        if status == 'Succeed':
            return 'Succeed'
        else:
            return 'Failed'
