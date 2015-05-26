import sys

Activities={
        'WorkflowExecutionStarted':{
            '':'PingActivity',
            },
        'PingActivity':{
            'Up': 'ToasterActivity',
            'Down': 'RebootActivity',
            #'UnknownHost': 'FreeIpActivity',
            },
        'FreeIpActivity':{
            'Confirm': 'ResolveTicketsActivity',
            'Mistake': 'FreeIpHumanActivity',
            },
        'FreeIpHumanActivity': {
            'Confirm': 'ResolveTicketsActivity',
            'Mistake': 'PingActivity',
            },
        'ToasterActivity':{
            'Healthy': 'CheckVIPActivity',
            'UnHealthy':'CutTicketActivity',
            },
        'RebootActivity':{
            'Succeed': 'ToasterActivity',
            'Failed': 'CutTicketActivity',
            },
        'CutTicketActivity': {
            '': 'WaitDCOActivity',
            },
        'WaitDCOActivity': {
            'Resolved': 'ToasterActivity',
            'NeedReply': 'ReplyHumanActivity',
            'Timeout': 'ReplyHumanActivity',
            },
        'ReplyHumanActivity':{
            '': 'WaitDCOActivity',
            },
        'ActivateActivity': {
            'Succeed': 'ResolveTicketsActivity',
            'Fail': 'ActivateFailHumanActivity',
            },
        'ActivateFailHumanActivity': {
            'Succeed': 'ResolveTicketsActivity',
            'Fail': 'PingActivity',
            },
        'CheckVIPActivity': {
            'Succeed': 'ActivateActivity',
            'Fail': 'FixVIPHumanActivity',
            },
        'FixVIPHumanActivity': {
            '': 'CheckVIPActivity',
            },
        'ResolveTicketsActivity':{
                },
        }
def validate():
    has_error=False
    for activity,nexts in Activities.items():
        worker=activity.replace('Activity','Worker')
        try:
            if not worker == 'WorkflowExecutionStarted':
                __import__(worker, fromlist=[worker])
        except ImportError as e:
            print 'Check %s fail %s' % (worker, e)
            has_error = True
        for key, act in nexts.items():
            worker=act.replace('Activity','Worker')
            try:
                __import__(worker, fromlist=[worker])
            except ImportError as e:
                print 'Check %s fail %s' % (worker, e)
                has_error = True
    return has_error
def generate_dot(fp):
    fp.write('digraph G{\n')
    for name,activity in Activities.items():
        for action,state in activity.items():
            fp.write('    %s->%s[label="%s"];\n' % (name, state, action))
    fp.write('}\n')
