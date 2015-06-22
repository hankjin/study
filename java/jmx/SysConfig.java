public class SysConfig implements SysConfigMBean {
    private int threadCount;
    private String schemaName;
    public SysConfig(int threadCount, String schemaName) {
        this.threadCount = threadCount;
        this.schemaName = schemaName;
    }
    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }
    public int getThreadCount() {
        return this.threadCount;
    }
    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }
    public String getSchemaName() {
        return this.schemaName;
    }
    public String doConfig() {
        return "Thread=" + this.threadCount + " schema=" + this.schemaName;
    }
    public int sum(int x, int y) {
        return x + y;
    }
}
