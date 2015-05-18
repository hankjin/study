
TABLES=[
        'TableA',
        'TableB',
        'TableC',
        'TableD',
        'TableE',
        'TableF',
        ]
# Region
REGION='us-east-1'
# Share EMR or one EMR for each table
SHARE_EMR=True
# Name of the data pipeline
PIPE_NAME='BackupDDB'
# S3 prefix of the backup tables.
S3PREFIX='ddb.backup'
# SNS to publish notification of success or fail the backup
SNSURL='arn:aws:sns:us-east-1:282091767217:DDBBackupSucceed'
# Pipe ID to identify the unique pipe
PIPE_ID='%s_%s' % (REGION,PIPE_NAME)
