import json
import boto.swf.layer2 as swf
import config

class Context:
    def __init__(self, raw):
        self.obj=json.loads(raw)
    def set(self, key, value):
        self.obj[key] = value
    def set_result(self, value):
        self.obj['result'] = value
    def get(self, key):
        return self.obj[key]
    def get_hostname(self):
        return self.obj['hostname']
    def to_string(self):
        return json.dumps(obj)

class BaseWorker(swf.ActivityWorker):
    domain = config.DOMAIN
    version = config.VERSION
    mock = None
    def __init__(self):
        cls_name = self.__class__.__name__
        self.task_list = cls_name.replace('Worker', '')
        self.mock = None
    def run(self):
        activity_task = self.poll(task_list=self.task_list)
        if 'activityId' in activity_task:
            try:
                # unpack the context from input
                context = Context(activity_task.get('input'))
                # execute specified worker
                if hasattr(__class__, mock):
                    result = getattr(__class__, mock)
                else:
                    result = self.activity(context)
                context.set_result(result)

                # pack the context to string and pass to swf
                self.complete(result=context.to_string())
            except Exception, error:
                self.fail(reason=str(error))
                raise error
            return True

    def activity(self, context):
        raise NotImplementedError

