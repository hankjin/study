#!/bin/env python
from HumanWorker import HumanWorker

'''
Verify This host is unknown host
'''

class FreeIpHumanWorker(HumanWorker):
    def __init__(self):
        super.__init__(self,
                'Please confirm free ip',
                '',
                20,
                5)
    def update_notify_msg(self, context, task):
        self.body = '''
        Hostname:{hostname}
        Ticket: {ticket}
        Please confirm, if this host is freeip, please reply TTSWF:Confirm in the ticket; if the host is not freeip, please reply TTSWF:Mistake in the ticket'''.format(
                hostname=context.get_hostname(),
                ticket = context.get_ticket())
