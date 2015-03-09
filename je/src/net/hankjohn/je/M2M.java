package net.hankjohn.je;

import java.io.File;
import java.util.Set;
import java.util.HashSet;

import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.Environment;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.EntityCursor;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.PrimaryIndex;
import com.sleepycat.persist.model.SecondaryKey;
import com.sleepycat.persist.model.Relationship;
import com.sleepycat.persist.model.DeleteAction;
import com.sleepycat.persist.SecondaryIndex;
import com.sleepycat.persist.StoreConfig;
import com.sleepycat.persist.EntityStore;

@Entity
class Claz {
    @PrimaryKey
        String cId;
    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
        String cName;
    @SecondaryKey(relate = Relationship.MANY_TO_MANY, relatedEntity = Teacher.class, onRelatedEntityDelete = DeleteAction.NULLIFY)
        Set<String> setTeacher = new HashSet<String>();
    public Claz() {}
    public Claz (String cId, String cName) {
        this.cId = cId;
        this.cName = cName;
    }
}

@Entity
class Teacher {
    @PrimaryKey
        String tId;
    @SecondaryKey(relate = Relationship.MANY_TO_ONE)
        String tName;
    @SecondaryKey(relate = Relationship.MANY_TO_MANY, relatedEntity = Claz.class, onRelatedEntityDelete = DeleteAction.NULLIFY)
        Set<String> setClaz = new HashSet<String>();
    public Teacher() {
    }
    public Teacher(String tId, String tName) {
        this.tId = tId;
        this.tName = tName;
    }
}

class Accessor {
    PrimaryIndex<String, Claz> primaryClazById;
    SecondaryIndex<String, String, Claz> clazByName;
    SecondaryIndex<String, String, Claz> clazBySetTeacher;

    PrimaryIndex<String, Teacher> primaryTeacherById;
    SecondaryIndex<String, String, Teacher> teacherByName;
    SecondaryIndex<String, String, Teacher> teacherByClaz;

    public Accessor(EntityStore store) throws DatabaseException {
        this.primaryClazById = store.getPrimaryIndex(String.class, Claz.class);
        this.clazByName = store.getSecondaryIndex(this.primaryClazById, String.class, "cName");
        this.clazBySetTeacher = store.getSecondaryIndex(this.primaryClazById, String.class, "setTeacher");
        this.primaryTeacherById = store.getPrimaryIndex(String.class, Teacher.class);
        this.teacherByName = store.getSecondaryIndex(this.primaryTeacherById, String.class, "tName");
        this.teacherByClaz = store.getSecondaryIndex(this.primaryTeacherById, String.class, "setClaz");
    }
}

public class M2M {
    public static void main(String args[]) throws Exception {
        EnvironmentConfig envConfig = new EnvironmentConfig();
        envConfig.setTransactional(true);
        Environment env = new Environment(new File("db"), envConfig);

        StoreConfig storeConfig = new StoreConfig();
        storeConfig.setAllowCreate(true);
        storeConfig.setTransactional(true);
        EntityStore store = new EntityStore(env, "m2m.db", storeConfig);

        Accessor dao = new Accessor(store);
        PrimaryIndex<String, Claz> clazById = dao.primaryClazById;
        PrimaryIndex<String, Teacher> teacherById = dao.primaryTeacherById;
        Transaction txn = env.beginTransaction(null, null);

        Claz c1 = new Claz("c1", "Class A");
        Claz c2 = new Claz("c2", "Class B");
        Claz c3 = new Claz("c3", "Class C");
        Claz c4 = new Claz("c4", "Class D");
        Claz c5 = new Claz("c5", "Class E");
        Claz c6 = new Claz("c6", "Class F");
        Claz c7 = new Claz("c7", "Class G");
        Claz c8 = new Claz("c8", "Class H");
        Claz c9 = new Claz("c9", "Class I");

        Teacher t1 = new Teacher("t1", "TA");
        Teacher t2 = new Teacher("t2", "TB");
        Teacher t3 = new Teacher("t3", "TC");
        Teacher t4 = new Teacher("t4", "TD");
        Teacher t5 = new Teacher("t5", "TE");
        Teacher t6 = new Teacher("t6", "TF");
        Teacher t7 = new Teacher("t7", "TG");
        Teacher t8 = new Teacher("t8", "TH");
        Teacher t9 = new Teacher("t9", "TI");

        Set<String> setT1 = new HashSet<String>();
        setT1.add(t1.tId);
        setT1.add(t2.tId);
        setT1.add(t3.tId);

        Set<String> setT2 = new HashSet<String>();
        setT2.add(t2.tId);
        setT2.add(t3.tId);
        setT2.add(t4.tId);

        Set<String> setT3=new HashSet<String>(); 
        setT3.add(t3.tId); 
        setT3.add(t4.tId); 
        setT3.add(t5.tId); 


        Set<String> setT4=new HashSet<String>(); 
        setT4.add(t5.tId); 
        setT4.add(t6.tId); 
        setT4.add(t7.tId); 

        Set<String> setT5=new HashSet<String>(); 
        setT5.add(t7.tId); 
        setT5.add(t8.tId); 
        setT5.add(t9.tId); 

        c1.setTeacher=setT1; 
        c2.setTeacher=setT2; 
        c3.setTeacher=setT1; 
        c4.setTeacher=setT3; 
        c5.setTeacher=setT4; 
        c6.setTeacher=setT5; 

        teacherById.put(txn, t1);
        teacherById.put(txn, t2);
        teacherById.put(txn, t3);
        teacherById.put(txn, t4);
        teacherById.put(txn, t5);
        teacherById.put(txn, t6);
        teacherById.put(txn, t7);
        teacherById.put(txn, t8);
        teacherById.put(txn, t9);

        clazById.put(txn, c1);
        clazById.put(txn, c2);
        clazById.put(txn, c3);
        clazById.put(txn, c4);
        clazById.put(txn, c5);
        clazById.put(txn, c6);
        clazById.put(txn, c7);
        clazById.put(txn, c8);
        clazById.put(txn, c9);
        txn.commit();

        // get
        txn = env.beginTransaction(null, null);
        EntityCursor<Claz> claz = clazById.entities(txn, null);
        for (Claz c : claz) {
            StringBuffer sb = new StringBuffer();
            sb.append(c.cId).append(" ");
            for (String t : c.setTeacher) {
                sb.append(teacherById.get(txn, t, LockMode.DEFAULT).tName)
                    .append(" ");
            }
            System.out.println(sb.toString());
        }
        claz.close();
        txn.commit();

        // clean up
        store.close();
        env.cleanLog();
        env.close();
    }
}
