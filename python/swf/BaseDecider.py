import time
import config
import boto.swf.layer2 as swf

class SerialDecider(swf.Decider):
    domain = config.DOMAIN
    task_list = config.TASKLIST
    version = config.VERSION
    
    def run(self):
        decision_task = self.poll()

        if not 'events' in decision_task:
            return
        events = decision_task['events']
        workflow_events = [e for e in events
                if e['eventId'] > decision_task['previousStartedEventId']]
        decisions = swf.Layer1Decisions()
        for event in workflow_events:
            event_type = event['eventType']
            if event_type == 'WorkflowExecutionStarted':
                # Schedule the first activity.
                activityName = states['WorkflowExecutionStarted'].values()[0]
                decisions.schedule_activity_task('%s-%i' % (activityName, time.time()),
                        activityName, self.version, task_list=activityName.replace('Activity',''))
            elif event_type == 'ActivityTaskCompleted':
                event_attrs = event['activityTaskCompletedEventAttributes']
                completed_activity_id = event_attrs['scheduledEventId'] - 1
                # 2) Extract its name.
                activity_data = decision_task['events'][completed_activity_id]
                activity_attrs = activity_data['activityTaskScheduledEventAttributes']
                activity_name = activity_attrs['activityType']['name']
                raw_result = event['activityTaskCompletedEventAttributes'].get('result')
                context = Context(raw_result)
                result = context.get_result()
                actions = states[activity_name]
                if len(actions) == 0:
                    decisions.complete_workflow_execution()
                for key,value in actions.items():
                    if key in result:
                        decisions.schedule_activity_task('%s-%i' % (value, time.time()),
                                value, self.version, task_list=value.replace('Activity',''), input=raw_result)
                        break
            else:
                print 'WOWOWOW',event_type
        self.complete(decisions=decisions)
        return True
