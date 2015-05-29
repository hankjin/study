from HumanWorker import HumanWorker

class ReplyHumanWorker(HumanWorker):
    def __init__(self):
        HumanWorker.__init__(self,
                title='Please reply DCO Ticket',
                body='',
                timeout=20,
                step=5)
    def get_status(self, context, task):
        util = TicketAPI()
        ticket = context.get_dco_ticket()
        status = util.get_ticket(ticket)
        # TODO last comment newer
        if True:
            return 'Succeed'
        else:
            return None

    def update_notify_msg(self, context, task):
        self.body = 'Hostname:%s\nTicket:%s' % (
                context.get_hostname(),
                context.get_dco_ticket())

if __name__ == '__main__':
    ReplyHumanWorker().run()
