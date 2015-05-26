#!/bin/env python
from BaseWorker import BaseWorker

class WaitDCOWorker(BaseWorker):
    TMO = 7 * 24 * 3600 # 7 days
    STEP = 2 * 3600 # check every 6 hours
    def activity(self, context, task):
        ticket_api = TicketAPI()
        while time.time() - task['eventTimestamp'] < TMO:
            status = ticket_api.get_status()
            # TODO resolved
            if status.resolved:
                return 'Resolved'
            if status.assigned != 'DCO':
                return 'NeedReply'
            time.sleep(STEP)
