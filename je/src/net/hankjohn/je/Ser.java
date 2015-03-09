package net.hankjohn.je;

import java.io.File;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.bind.serial.SerialBinding;

class Book implements Serializable {
    public String name;
    public String auther;
}
public class Ser {
    public static void main(String args[]) throws Exception {
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setAllowCreate(true);
        Environment env = new Environment(new File("db"), envConfig);
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        Database db = env.openDatabase(null, "ser.db", dbConfig);

        DatabaseEntry key = new DatabaseEntry();
        String strKey = "myKey";
        key.setData(strKey.getBytes(StandardCharsets.UTF_8));

        StoredClassCatalog classCatalog = new StoredClassCatalog(db);
        EntryBinding binding = new SerialBinding(classCatalog, Book.class);

        DatabaseEntry value = new DatabaseEntry();
        Book book = new Book();
        book.name = "MyName";
        book.auther = "MyAuther";
        binding.objectToEntry(book, value);

        OperationStatus status = db.put(null, key, value);
        System.out.println(status);

        DatabaseEntry result = new DatabaseEntry();
        db.get(null, key, result, LockMode.DEFAULT);
        Book newBook = (Book)binding.entryToObject(result);
        System.out.println("Name:" + newBook.name + " Author:" + newBook.auther);
        db.close();
        env.cleanLog();
        env.close();
    }
}
