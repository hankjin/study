import sys

class FSM_State:
    States={}
    def __init__(self, name):
        self.name=name
        self.action=None
        if name in FSM_State.States:
            sys.stderr.write('State %s duplicate\n' % self.name)
        FSM_State.States[name]=self
    def set_action(self, action):
        self.action=action
    @staticmethod
    def register(states):
        for state in states:
            setattr(FSM_State, state, FSM_State(state))

class FSM_Activity:
    Activities={}
    def __init__(self,name):
        self.name = name
        self.actions={}
        FSM_Activity.Activities[name]=self
    def add_action(self, name, state):
        if name=='Input':
            state.set_action(self)
        else:
            self.actions[name] = state

    @staticmethod
    def register(activities):
        for activity,actions in activities.items():
            obj = FSM_Activity(activity)
            setattr(FSM_Activity, activity, obj) 
            for action,state in actions.items():
                obj.add_action(action,state)

# States
FSM_State.register([
        'Init',
        'End',
        'HostDown',
        'WaitReboot',
        'Rebooting',
        'HostUp',
        'HostDown',
        'NeedDCO',
        'VerifyDCO',
        'DCO',
        'DCOCheck',
        'NeedReply',
        'WaitReply',
        'ReplyTMO',
        'HostReady',
        'WrongVIP',
        'ServiceReady',
        'WaitVIP',
        'ServiceWrong',
        'ServiceWrongTmo',
        'WaitServiceUp',
        'ServiceUp',
        'ServiceHealthy',
        ])
# Activities
FSM_Activity.register({
    'Ping':{
        'Input':FSM_State.Init,
        'Success': FSM_State.HostUp,
        'Fail': FSM_State.HostDown,
        },
    'RetryReboot':{
        'Input':FSM_State.HostDown,
        'Yes':FSM_State.WaitReboot,
        'No':FSM_State.NeedDCO,
        },
    'Reboot':{
        'Input':FSM_State.WaitReboot,
        'Succeed':FSM_State.Rebooting,
        'Failed':FSM_State.NeedDCO,
        },
    'Rebooting':{
        'Input':FSM_State.Rebooting,
        'Yes':FSM_State.Init,
        'No':FSM_State.Rebooting,
        },
    'CutTicket':{
        'Input':FSM_State.NeedDCO,
        'Yes':FSM_State.DCO,
        },
    'Resolved':{
        'Input':FSM_State.DCO,
        'Yes':FSM_State.Init,
        'No':FSM_State.DCOCheck,
        },
    'NeedReplyDCO':{
        'Input':FSM_State.DCOCheck,
        'Yes':FSM_State.NeedReply,
        'No':FSM_State.DCO,
        },
    'EmailOnCallReply':{
        'Input':FSM_State.NeedReply,
        'Yes':FSM_State.WaitReply,
        },
    'FindReply':{
        'Input':FSM_State.WaitReply,
        'Yes':FSM_State.DCO,
        'No':FSM_State.ReplyTMO,
        },
    'ReplyTimeout':{
        'Input':FSM_State.ReplyTMO,
        'Yes':FSM_State.NeedReply,
        'No':FSM_State.WaitReply,
        },
    'Toaster':{
        'Input':FSM_State.HostUp,
        'Success':FSM_State.HostReady,
        'Fail':FSM_State.NeedDCO,
        },
    'IsVIPOK':{
        'Input':FSM_State.HostReady,
        'Yes':FSM_State.ServiceReady,
        'No':FSM_State.WrongVIP,
        },
    'EmailFixVIP':{
        'Input':FSM_State.WrongVIP,
        'Yes':FSM_State.WaitVIP,
        },
    'VIPTimeout':{
        'Input':FSM_State.WaitVIP,
        'Yes':FSM_State.HostReady,
        'No':FSM_State.WaitVIP,
        },
    'Activate':{
        'Input':FSM_State.ServiceReady,
        'NotWorkTime':FSM_State.ServiceReady,
        'Yes':FSM_State.ServiceHealthy,
        'No':FSM_State.ServiceWrong,
        },
    'ServiceWrongTMO':{
        'Input':FSM_State.ServiceWrong,
        'Yes':FSM_State.ServiceWrongTmo,
        'No':FSM_State.ServiceWrong,
        },
    'EmailServiceWrong':{
        'Input':FSM_State.ServiceWrongTmo,
        'Yes':FSM_State.WaitServiceUp,
        },
    'WaitServiceUpTmo':{
        'Input':FSM_State.WaitServiceUp,
        'Timeout':FSM_State.ServiceWrong,
        'IsUp':FSM_State.ServiceHealthy,
        },
    'ResolveTickets':{
        'Input':FSM_State.ServiceHealthy,
        'Yes':FSM_State.End,
        }
    })


if __name__=='__main__':
    print 'digraph G {'
    for name,state in FSM_State.States.items():
        if not state.action==None:
            print '%s->%s;' % (name, state.action.name)
    for name,activity in FSM_Activity.Activities.items():
        for action,state in activity.actions.items():
            print '%s->%s;' % (name, state.name)
    print '}'
