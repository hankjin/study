#!/bin/env python

from util import VIPUtil
from BaseWorker import BaseWorker

'''
Check the vip configuration is righ
'''
class CheckVIPWorker(BaseWorker):
    def activity(self, context, task):
        util = VIPUtil()
        ok = util.is_ok(context.get_hostname())
        if ok:
            return 'Succeed'
        else:
            return 'Fail'

