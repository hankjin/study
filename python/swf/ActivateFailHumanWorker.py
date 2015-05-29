from HumanWorker import HumanWorker

class ActivateFailHumanWorker(HumanWorker):
    def __init__(self):
        HumanWorker.__init__(
                self,
                title='Please Fix Activate Fail Error',
                body='',
                timeout=20,
                step=5)
    def update_notify_msg(self, context, task):
        self.body = 'Hostname:%s\nTicket: %s' % (
                context.get_hostname(),
                context.get_ticket(),
                )

if __name__ == '__main__':
    ActivateFailHumanWorker().run()
