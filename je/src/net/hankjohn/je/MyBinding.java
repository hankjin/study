package net.hankjohn.je;

import java.io.File;
import java.nio.charset.StandardCharsets;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.tuple.TupleBinding;
import com.sleepycat.bind.tuple.TupleInput;
import com.sleepycat.bind.tuple.TupleOutput;
import com.sleepycat.je.Cursor;

class Student {
    public String name;
    public int age;
}
class StudentBinding extends TupleBinding {
    @Override
        public Object entryToObject(TupleInput input) {
            String name = input.readString();
            int age = input.readInt();
            Student student = new Student();
            student.name = name;
            student.age = age;
            return student;
        }
    @Override
        public void objectToEntry(Object obj, TupleOutput output) {
            Student student = (Student)obj;
            output.writeString(student.name);
            output.writeInt(student.age);
        }
};

public class MyBinding {
    public static void main(String args[]) throws Exception {
        EnvironmentConfig envConfig = new EnvironmentConfig();
        Environment env = new Environment(new File("db"), envConfig);
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setAllowCreate(true);
        Database db = env.openDatabase(null, "mybinding.db", dbConfig);
        EntryBinding binding = new StudentBinding();

        DatabaseEntry key = new DatabaseEntry();
        key.setData("No1".getBytes(StandardCharsets.UTF_8));
        DatabaseEntry value = new DatabaseEntry();
        Student student = new Student();
        student.name = "hank";
        student.age = 30;
        binding.objectToEntry(student, value);
        db.put(null, key, value);
        key.setData("No2".getBytes(StandardCharsets.UTF_8));
        student.name = "John";
        student.age = 60;
        binding.objectToEntry(student, value);
        db.put(null, key, value);

        Cursor cursor = db.openCursor(null, null);
        while (cursor.getNext(key, value, LockMode.DEFAULT) == OperationStatus.SUCCESS) {
            student = (Student)binding.entryToObject(value);
            System.out.println("Student: " + student.name + " Age:" + student.age);
        }

        // cleanup 
        cursor.close();
        db.close();
        env.cleanLog();
        env.close();
    }
}
