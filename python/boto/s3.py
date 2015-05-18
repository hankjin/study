#!/bin/env python
import sys
import boto.s3
import config

conn = boto.s3.connect_to_region(config.REGION)
def createS3():
    for tableName in config.TABLES:
        s3url='%s.%s' % (config.S3PREFIX, tableName.lower())
        conn.create_bucket(s3url)
        print 'Create S3 bucket %s' % s3url
def listS3():
    buckets = conn.get_all_buckets()
    for bucket in buckets:
        print bucket.name

if __name__ == '__main__':
    if len(sys.argv) == 1:
        print 'Usage: %s [list|create]' % sys.argv[0]
    if 'create' in sys.argv:
        createS3()
    if 'list' in sys.argv:
        listS3()

