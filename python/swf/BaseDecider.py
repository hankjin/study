import time
import json
import config
import states
from util import Notify
from BaseWorker import Context
import boto.swf.layer2 as swf

class BaseDecider(swf.Decider):
    domain = config.DOMAIN
    task_list = config.TASKLIST
    version = config.VERSION
    
    def run(self, verbose=False):
        if verbose:
            print 'Try to poll task'
        decision_task = self.poll()

        if not 'events' in decision_task:
            if verbose:
                print 'No events to do'
            return
        events = decision_task['events']
        while 'nextPageToken' in decision_task:
            decision_task = self.poll(next_page_token=decision_task['nextPageToken'])
            if 'events' in decision_task:
                events += decision_task['events']
        workflow_events = [e for e in events
                if e['eventId'] > decision_task['previousStartedEventId']]
        if verbose:
            print '{task}: Got {todo} events after {eventid} to handle from {total}'.format(
                    task = decision_task,
                    todo = len(workflow_events),
                    eventid = decision_task['previousStartedEventId'],
                    total = len(events))
        decisions = swf.Layer1Decisions()
        for event in workflow_events:
            event_type = event['eventType']
            if verbose:
                print 'Handle event {type} with {event}'.format(
                        type = event_type,
                        event = event,
                        )
            if event_type == 'WorkflowExecutionStarted':
                # Schedule the first activity.
                raw_result = event['workflowExecutionStartedEventAttributes'].get('input')
                activity_name = states.Activities['WorkflowExecutionStarted'].values()[0]
                activity_id = '%s-%i' % (activity_name, time.time())
                task_list = activity_name.replace('Activity', '')
                decisions.schedule_activity_task(activity_id,
                        activity_name,
                        self.version,
                        task_list=task_list,
                        input=raw_result,
                        )
                if verbose:
                    print 'Schedule activity {id} {name} {tasklist} {input}'.format(
                            id=activity_id,
                            name=activity_name,
                            tasklist=task_list,
                            input=raw_result)
            elif event_type == 'ActivityTaskCompleted':
                raw_result = event['activityTaskCompletedEventAttributes'].get('result')
                context = Context(raw_result)
                event_attrs = event['activityTaskCompletedEventAttributes']
                completed_activity_id = event_attrs['scheduledEventId'] - 1
                # 2) Extract its name.
                activity_data = events[completed_activity_id]
                activity_attrs = activity_data['activityTaskScheduledEventAttributes']
                activity_name = activity_attrs['activityType']['name']
                result = context.get_result()
                actions = states.Activities[activity_name]
                if len(actions) == 0:
                    decisions.complete_workflow_execution()
                    if verbose:
                        print 'Complete workflow because no more actions for %s' % activity_name
                elif not result in actions:
                    details='Unknow resulth %s for activity %s' % (result, activity_name)
                    decisions.cancel_workflow_executions(details=details)
                    if verbose:
                        print 'Cancel workflow because %s' % details
                else:
                    next_activity = actions[result]
                    activity_id = '%s-%i' % (next_activity, time.time())
                    task_list = next_activity.replace('Activity','')
                    decisions.schedule_activity_task(
                            activity_id,
                            next_activity,
                            self.version,
                            task_list=task_list,
                            input=raw_result)
                    print '{host} finish {task} with {output} begin {next}'.format(
                            host = context.get_hostname(),
                            task = activity_name,
                            output = context.get_result(),
                            next = next_activity)
                    if verbose:
                        print 'Schedule activity {id} {name} {tasklist} {input}'.format(
                                id=activity_id,
                                name=activity_name,
                                tasklist=task_list,
                                input=raw_result)
            elif event_type == 'ActivityTaskFailed':
                details = json.dumps(event)
                decisions.cancel_workflow_executions(details=details)
                if verbose:
                    print 'Cancel workflow because %s' % details
                Notify().developer(json.dumps(event))
        self.complete(decisions=decisions)
        return True

if __name__ == '__main__':
    while True:
        BaseDecider().run(verbose=True)
