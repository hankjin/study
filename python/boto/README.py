This is used to create pipeline to backup ddb to s3

Usage:

# vi config.py to config region and tables to backup.
# python ddb.py create to create tables if you don't have(optional)
# python ddb.py put to fake data to tables (optional)
# python s3.py create to create buckets to backup
# python pipe.py create to create pipe in data pipeline
# python pipe.py run to activate the data pipeline.
