from HumanWorker import HumanWorker

class ActivateFailHumanWorker(HumanWorker):
    def __init__(self):
        super.__init__(self,
                'Please Fix Activate Fail Error',
                '',
                20,
                5)
    def update_notify_msg(self, context, task):
        self.body = 'Hostname:%s\nTicket: %s' % (
                context.get_hostname(),
                context.get_ticket(),
                )
