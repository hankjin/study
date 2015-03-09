package net.hankjohn.je;

import java.io.File;
import java.nio.charset.StandardCharsets;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.TupleBinding;

public class Primitive {
    public static void main(String args[]) throws Exception {
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        Environment env = new Environment(new File("db"), envConfig);
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        Database db = env.openDatabase(null, "primitive.db", dbConfig);
        DatabaseEntry key = new DatabaseEntry();
        String strKey = "mykey";
        key.setData(strKey.getBytes(StandardCharsets.UTF_8));

        DatabaseEntry value = new DatabaseEntry();
        Long longValue = new Long("88888");
        EntryBinding myBinding = TupleBinding.getPrimitiveBinding(Long.class);
        myBinding.objectToEntry(longValue, value);

        db.put(null, key, value);
        DatabaseEntry result = new DatabaseEntry();
        db.get(null, key, result, LockMode.DEFAULT);
        Long number = (Long)myBinding.entryToObject(result);
        System.out.println(number);
        db.close();
        env.cleanLog();
        env.close();
    }
}
