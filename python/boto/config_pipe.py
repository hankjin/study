import config
import json

SCHEDULE = {
        "fields": [
            {
                "stringValue": "FIRST_ACTIVATION_DATE_TIME", 
                "key": "startAt"
                }, 
            {
                "stringValue": "1 days", 
                "key": "period"
                }, 
            {
                "stringValue": "Schedule", 
                "key": "type"
                }
            ], 
        "id": "DefaultSchedule", 
        "name": "Every 1 day"
        } 
DEFAULT_CONFIG =  {
            "fields": [
                {
                    "stringValue": "CASCADE", 
                    "key": "failureAndRerunMode"
                }, 
                {
                    "stringValue": "cron", 
                    "key": "scheduleType"
                }, 
                {
                    "refValue": "DefaultSchedule", 
                    "key": "schedule"
                }, 
                {
                    "stringValue": "s3://ddb.backup.log/", 
                    "key": "pipelineLogUri"
                }, 
                {
                    "stringValue": "DataPipelineDefaultRole", 
                    "key": "role"
                }, 
                {
                    "stringValue": "DataPipelineDefaultResourceRole", 
                    "key": "resourceRole"
                }
            ], 
            "id": "Default", 
            "name": "Default"
        }
EMR_CLUSTER = {
            "fields": [
                {
                    "stringValue": "2 Hours", 
                    "key": "terminateAfter"
                }, 
                {
                    "stringValue": "3.3.2", 
                    "key": "amiVersion"
                }, 
                {
                    "stringValue": "m1.medium", 
                    "key": "masterInstanceType"
                }, 
                {
                    "stringValue": "m1.medium", 
                    "key": "coreInstanceType"
                }, 
                {
                    "stringValue": "1", 
                    "key": "coreInstanceCount"
                }, 
                {
                    "stringValue": "EmrCluster", 
                    "key": "type"
                }
            ], 
            "id": "EmrClusterForBackup", 
            "name": "EmrClusterForBackup"
        } 
DATA_FORMAT = {
            "fields": [
                {
                    "stringValue": "DynamoDBExportDataFormat", 
                    "key": "type"
                }
            ], 
            "id": "DDBExportFormat", 
            "name": "DDBExportFormat"
        }

TABLE_SOURCE = '''{
            "fields": [
                {
                    "stringValue": "{TABLENAME}", 
                    "key": "tableName"
                }, 
                {
                    "refValue": "DDBExportFormat", 
                    "key": "dataFormat"
                }, 
                {
                    "stringValue": "DynamoDBDataNode", 
                    "key": "type"
                }, 
                {
                    "stringValue": "0.2", 
                    "key": "readThroughputPercent"
                }
            ], 
            "id": "DDBSource{TABLENAME}", 
            "name": "DDBSource{TABLENAME}"
        }'''
TABLE_DEST = '''{
            "fields": [
                {
                    "stringValue": "s3://{S3PREFIX}.{REGION}.{tablename}/#{format(@scheduledStartTime, 'YYYY-MM-dd-HH-mm-ss')}",
                    "key": "directoryPath"
                }, 
                {
                    "refValue": "DDBExportFormat", 
                    "key": "dataFormat"
                }, 
                {
                    "stringValue": "S3DataNode", 
                    "key": "type"
                }
            ], 
            "id": "S3BackupLocation{TABLENAME}", 
            "name": "S3BackupLocation{TABLENAME}"
        }'''
TABLE_ACTIVITY = '''{
            "fields": [
                {
                    "stringValue": "true", 
                    "key": "resizeClusterBeforeRunning"
                }, 
                {
                    "refValue": "DDBSource{TABLENAME}", 
                    "key": "input"
                }, 
                {
                    "refValue": "EmrClusterForBackup", 
                    "key": "runsOn"
                }, 
                {
                    "refValue": "ActionId_{TABLENAME}_SUCCESS", 
                    "key": "onSuccess"
                }, 
                {
                    "refValue": "ActionId_{TABLENAME}_FAIL", 
                    "key": "onFail"
                }, 
                {
                    "refValue": "S3BackupLocation{TABLENAME}", 
                    "key": "output"
                }, 
                {
                    "stringValue": "HiveCopyActivity", 
                    "key": "type"
                }
            ], 
            "id": "{TABLENAME}BackupActivity", 
            "name": "{TABLENAME}BackupActivity"
        }'''

TABLE_NOTIFY_SUCCESS = '''{
        "fields": [
            {
                "stringValue": "we did it", 
                "key": "message"
                }, 
            {
                "stringValue": "DDB Backup {REGION} {TABLENAME} success", 
                "key": "subject"
                }, 
            {
                "stringValue": "{SNSURL}",
                "key": "topicArn"
                }, 
            {
                "stringValue": "DataPipelineDefaultRole", 
                "key": "role"
                }, 
            {
                "stringValue": "SnsAlarm", 
                "key": "type"
                }
            ], 
        "id": "ActionId_{TABLENAME}_SUCCESS", 
        "name": "{TABLENAME}_Success_Action"
        } '''
TABLE_NOTIFY_FAIL = '''{
            "fields": [
                {
                    "stringValue": "we failed", 
                    "key": "message"
                }, 
                {
                    "stringValue": "DDBBackup {REGION} {TABLENAME} Failed", 
                    "key": "subject"
                }, 
                {
                    "stringValue": "{SNSURL}",
                    "key": "topicArn"
                }, 
                {
                    "stringValue": "DataPipelineDefaultRole", 
                    "key": "role"
                }, 
                {
                    "stringValue": "SnsAlarm", 
                    "key": "type"
                }
            ], 
            "id": "ActionId_{TABLENAME}_FAIL", 
            "name": "{TABLENAME}_Fail_Action"
        }'''

def getTableObjects(tableName):
    templates = [
            TABLE_SOURCE,
            TABLE_DEST,
            TABLE_ACTIVITY,
            TABLE_NOTIFY_SUCCESS,
            TABLE_NOTIFY_FAIL,
            ]
    result = []
    for item_template in templates:
        item = item_template \
            .replace('{TABLENAME}', tableName) \
            .replace('{tablename}', tableName.lower()) \
            .replace('{S3PREFIX}', config.S3PREFIX) \
            .replace('{SNSURL}', config.SNSURL) \
            .replace('{REGION}', config.REGION)
        result.append(json.loads(item))
    return result
