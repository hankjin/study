from BaseWorker import BaseWorker

class CutTicketWorker(BaseWorker):
    def activity(self, context, task):
        ticket_api = TicketAPI()
        hostname = context.get_hostname()
        host_status = context.get_host_status()
        ticket = ticket_api.cut_ticket(hostname, host_status)
        context.set_dco_ticket(ticket)
        return 'Succeed'
