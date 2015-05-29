from BaseWorker import BaseWorker
from util import TicketAPI

'''
Resolve Tickets after the service is healthy running
'''
class ResolveTicketsWorker(BaseWorker):
    def activity(self, context, task):
        hostname = context.get_hostname()
        ticket_api = TicketAPI()
        # Get related ticket list
        tickets = ticket_api.list_tickets(hostname)
        for ticket in tickets:
            # Resolve ticket
            print 'Resolve ticket %s %s' % (hostname, ticket)
            ticket_api.resolve_ticket(ticket)
        return 'Succeed'
