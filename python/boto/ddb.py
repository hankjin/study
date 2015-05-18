from boto.dynamodb2.table import Table
from boto.dynamodb2.fields import HashKey
import sys
import boto.dynamodb2
import config

dbconn = boto.dynamodb2.connect_to_region(config.REGION)
def createTables():
    for tableName in config.TABLES:
        table = Table.create(tableName,
                schema=[HashKey(tableName)],
                throughput={
                    'read':100,
                    'write':100,
                    },
                connection=dbconn,
                )
        print 'Create Table %s' % tableName, table

def listTables():
    tables = dbconn.list_tables()
    for table in tables['TableNames']:
        print table

def deleteTables():
    for tableName in config.TABLES:
        table = Table(
                tableName,
                connection=dbconn,
                )
        table.delete()
        print 'Table %s deleted' % tableName

def putData():
    for tableName in config.TABLES:
        table = Table(
                tableName,
                connection=dbconn,
                )
        for i in range(1000):
            table.put_item(data = {
                tableName: '%s_%d' % (tableName, i),
                '%s_not_key' % tableName: 'hankjohn',
                })
        print 'Put Data %d to table %s' % (i, tableName)

if __name__ == '__main__':
    if len(sys.argv) == 1:
        print 'Usage: %s [list|create|delete|put]' % sys.argv[0]
    if 'list' in sys.argv:
        listTables()
    if 'create' in sys.argv:
        createTables()
    if 'delete' in sys.argv:
        deleteTables()
    if 'put' in sys.argv:
        putData()
