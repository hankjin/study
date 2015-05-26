from HumanWorker import HumanWorker

class ReplyHumanWorker(HumanWorker):
    def __init__(self):
        super.__init__(self,
                'Please reply DCO Ticket',
                '',
                20,
                5)
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
