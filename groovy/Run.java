import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import java.io.File;
import java.io.IOException;
 
public class Run {
    public static void main(final String[] args) throws IllegalAccessException, InstantiationException, IOException {
        // Create GroovyClassLoader.
        final GroovyClassLoader classLoader = new GroovyClassLoader();
        // Create a String with Groovy code.
        final StringBuilder groovyScript = new StringBuilder();
        groovyScript.append("class Sample {");
        groovyScript.append("  String sayIt(name) { \"Groovy says: Cool $name!\" }");
        groovyScript.append("}");
        // Load string as Groovy script class.
        //Class groovy = classLoader.parseClass(groovyScript.toString());
        Class groovy = classLoader.parseClass(new File("hjz.groovy"));
        GroovyObject groovyObj = (GroovyObject) groovy.newInstance();
        String output = (String)groovyObj.invokeMethod("sayIt", new Object[] { "mrhaki" });
        System.out.println(output);
    }
}
