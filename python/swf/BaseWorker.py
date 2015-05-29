import json
import random
import boto.swf.layer2 as swf
import config
import states

class Context:
    def __init__(self, raw):
        if raw == None or raw=='':
            raw = '{}'
        self.obj=json.loads(raw)
    def set(self, key, value):
        self.obj[key] = value
    def set_result(self, value):
        self.obj['result'] = value
    def get_result(self):
        return self.obj.get('result', '')
    def get(self, key):
        return self.obj[key]
    def get_hostname(self):
        return self.obj.get('hostname', '')
    def set_verbose(self, verbose):
        self.obj['verbose'] = verbose
    def get_verbose(self):
        if 'verbose' in self.obj:
            return self.obj['verbose']
        return False
    def set_dryrun(self, dryrun):
        if dryrun != None:
            self.obj['dryrun'] = json.loads(dryrun)
    def get_dryrun(self, key):
        key = key.replace('Worker', 'Activity')
        result = None
        if self.get_verbose():
            print 'try dryrun for key %s' % key
        if 'dryrun' in self.obj:
            dryrun = self.obj['dryrun']
            if key in dryrun:
                result = dryrun[key]
            elif not key in states.Activities:
                raise 'Invalid Key %s' % key
            else:
                actions = states.Activities[key].keys()
                if len(actions) == 0:
                    result = ''
                else:
                    idx = random.randint(0, len(actions)-1)
                    result = actions[idx]
        if self.get_verbose():
            print 'dryrun %s result %s' % (key, result)
        return result
    def to_string(self):
        return json.dumps(self.obj)

class BaseWorker(swf.ActivityWorker):
    domain = config.DOMAIN
    version = config.VERSION
    def __init__(self):
        super(BaseWorker, self).__init__()
        cls_name = self.__class__.__name__
        self.task_list = cls_name.replace('Worker', '')
    def run(self, verbose=False):
        task = self.poll_task(verbose)
        if None == task:
            return False
        else:
            return self.handle_task(task, verbose)
    def poll_task(self, verbose=False):
        activity_task = self.poll(task_list=self.task_list)
        if not 'activityId' in activity_task:
            return None
        if verbose:
            print 'Got task', activity_task
        return activity_task
    def handle_task(self, activity_task, verbose=False):
        if verbose:
            print 'Handle Task', activity_task
        if not 'activityId' in activity_task:
            print 'Invalid task to handle', activity_task
            return False
        try:
            # unpack the context from input
            raw_result = activity_task.get('input')
            if verbose:
                print 'Get input %s' % raw_result
            context = Context(raw_result)
            # execute specified worker
            result = context.get_dryrun(self.__class__.__name__)
            if verbose:
                print 'Dryrun result %s' % result
            if None==result:
                result = self.activity(context, activity_task)
            context.set_result(result)
            if verbose:
                print 'Get result %s' % result

            # pack the context to string and pass to swf
            self.complete(result=context.to_string())
        except Exception, error:
            self.fail(reason=str(error))
            raise error
        return True

    def activity(self, context, task):
        raise NotImplementedError

