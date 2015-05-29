import boto.swf.layer2 as swf
from boto.swf.exceptions import SWFTypeAlreadyExistsError, SWFDomainAlreadyExistsError
import config
import states

registerables = []
registerables.append(swf.Domain(name=config.DOMAIN))
for workflow_type in (config.WorkFlowType,):
    registerables.append(swf.WorkflowType(
        domain=config.DOMAIN,
        name=workflow_type,
        version=config.VERSION,
        task_list=config.TASKLIST))

for activity_type in states.Activities:
    if activity_type.endswith('Activity'):
        registerables.append(swf.ActivityType(
            domain=config.DOMAIN,
            name=activity_type,
            version=config.VERSION,
            task_list=config.TASKLIST))

for swf_entity in registerables:
    try:
        swf_entity.register()
        print swf_entity.name, 'registered successfully'
    except (SWFDomainAlreadyExistsError, SWFTypeAlreadyExistsError):
        print swf_entity.__class__.__name__, swf_entity.name, 'already exists'
