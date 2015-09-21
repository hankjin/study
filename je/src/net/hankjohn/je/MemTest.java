package net.hankjohn.je;

import java.nio.charset.StandardCharsets;
import java.io.File;
import java.io.Serializable;

import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.LockMode;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.bind.EntryBinding;

class MemObj implements Serializable {
    public String name;
    public String auther;
};
public class MemTest {
    static Database openDB(int i, Environment env, DatabaseConfig dbConfig) {
        String name = String.valueOf(i) + ".db";
        Database db = env.openDatabase(null, name, dbConfig);
        StoredClassCatalog classCatalog = new StoredClassCatalog(db);
        EntryBinding binding = new SerialBinding(classCatalog, MemObj.class);
        return db;
    }
    public static void main(String args[]) throws Exception {
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        Environment env = new Environment(new File("db"), envConfig);
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        int total = 80000;
        int step = 1000;
        Runtime runtime = Runtime.getRuntime();
        Database []dbs = new Database[total+1];
        long unit = 1024*1024;
        for (int i = 0; i <= total; i++) {
            if (i%step == 0) {
                System.gc();
                long tm = runtime.totalMemory()/unit;
                long fm = runtime.freeMemory()/unit;
                long used = tm - fm;
                System.out.println(String.format("%1$d %2$d %3$d %4$d", i, used, tm, fm));
            }
            dbs[i] = openDB(i, env, dbConfig);
        }
        Thread.sleep(10 * 60);
    }
}
