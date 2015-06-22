import java.lang.management.ManagementFactory;
import javax.management.MBeanServer;
import javax.management.ObjectName;

public class SysConfigMgr {
    public static void main(String args[]) throws Exception {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        SysConfig config = new SysConfig(10, "default");
        ObjectName name = new ObjectName("xman:type=SysConfig");
        server.registerMBean(config, name);
        do {
            Thread.sleep(3000);
            System.out.println(config.doConfig());
        } while (config.getThreadCount() != 0);
    }
}

