public interface SysConfigMBean {
    public void setThreadCount(int threadCount);
    public int getThreadCount();
    public void setSchemaName(String schemaName);
    public String getSchemaName();
    public String doConfig();
    public int sum(int x, int y);
}
