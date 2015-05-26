import boto.swf.layer2 as swf

DOMAIN = 'boto_tutorial'
VERSION = '1.0'
TASKLIST = 'default'

class HelloWorker(swf.ActivityWorker):
    domain = DOMAIN
    version = VERSION
    task_list = TASKLIST

    def run(self):
        activity_task = self.poll()
        if 'activityId' in activity_task:
            print 'Hello, World!'
            self.complete()
            return True
