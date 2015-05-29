import boto.swf.layer2 as swf

WorkFlowType = 'FETicketWorkflow'
DOMAIN = 'sqs_ticket_fe'
VERSION = '0.9'
TASKLIST = 'default'

SNS='arn:aws:sns:us-east-1:282091767217:TicketWorkFlowNotify'
