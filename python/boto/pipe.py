#!/bin/env python
import config
import config_pipe
import sys
import json
import boto.datapipeline

conn = boto.datapipeline.connect_to_region(config.REGION)
resp=conn.create_pipeline( config.PIPE_NAME, config.PIPE_ID,)
pipe_id = resp['pipelineId']
def createPipe():
    pipe_def = []
    pipelineObjects = [
                config_pipe.SCHEDULE, # run once per day
                config_pipe.DEFAULT_CONFIG, # role, log location
                config_pipe.DATA_FORMAT, # format to do copy
                ]
    for tableName in config.TABLES:
        pipelineObjects.extend(config_pipe.getTableObjects(tableName))
    pipelineObjects.extend(config_pipe.getEMRObjects(config.TABLES))
    
    open('backup.json','w').write(json.dumps(pipelineObjects, indent=4))
    conn.put_pipeline_definition(
            pipelineObjects,
            pipe_id,
            )
    print 'Pipe %s is created' % (pipe_id)

def deletePipe():
    conn.delete_pipeline(pipe_id)
    print 'PIPE %s deleted' % (pipe_id)

def getPipe():
    resp = conn.get_pipeline_definition(pipe_id)
    print json.dumps(resp, indent=4)

def runPipe():
    conn.activate_pipeline(pipe_id)
    print 'Pipe %s is activated' % pipe_id

if __name__ == '__main__':
    if len(sys.argv) == 1:
        print 'Usage: %s [create|delete|run|get]' % sys.argv[0]
    if 'create' in sys.argv:
        createPipe()
    if 'delete' in sys.argv:
        deletePipe()
    if 'get' in sys.argv:
        getPipe()
    if 'run' in sys.argv:
        runPipe()


