package net.hankjohn.je;

import java.nio.charset.StandardCharsets;
import java.io.File;

import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.LockMode;

public class Test {
    public static void main(String args[]) throws Exception {
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        Environment env = new Environment(new File("db"), envConfig);
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        Database db = env.openDatabase(null, "test.db", dbConfig);
        try {
            for (int i = 0; i < 10; i ++) {
                String key = "myKey" + i;
                String value = "myValue" + i;
                OperationStatus status = db.put(null,
                        new DatabaseEntry(key.getBytes(StandardCharsets.UTF_8)),
                        new DatabaseEntry(value.getBytes(StandardCharsets.UTF_8)));
                System.out.println(status == OperationStatus.SUCCESS);
            }
            DatabaseEntry queryKey = new DatabaseEntry();
            DatabaseEntry value = new DatabaseEntry();
            queryKey.setData("myKey2".getBytes(StandardCharsets.UTF_8));
            OperationStatus status = db.get(null, queryKey, value, LockMode.DEFAULT);
            System.out.println(new String(value.getData(), StandardCharsets.UTF_8));
            status = db.delete(null, queryKey);
            System.out.println(status == OperationStatus.SUCCESS);
        }
        finally {
            if (db != null) {
                db.close();
            }
            if (env != null) {
                env.cleanLog();
                env.close();
            }
        }
    }
}
