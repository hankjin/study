from HumanWorker import HumanWorker

class FixVIPHumanWorker(HumanWorker):
    def __init__(self):
        HumanWorker.__init__(self,
                title='Please Fix VIP configuration',
                body='',
                timeout=20,
                step=5)
    def update_notify_msg(self, context, task):
        self.body = 'Hostname:%s\nTicket: %s' % (
                context.get_hostname(),
                context.get_ticket(),
                )
