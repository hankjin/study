
TABLES=[
        'TableA',
        'TableB',
        'TableC'
        ]
REGION='us-east-1'
PIPE_NAME='BackupDDB'
PIPE_ID='%s_%s' % (REGION,PIPE_NAME)
S3PREFIX='ddb.backup'
SNSURL='arn:aws:sns:us-east-1:282091767217:DDBBackupSucceed'
EMAIL='jindongh.gmail.com'
