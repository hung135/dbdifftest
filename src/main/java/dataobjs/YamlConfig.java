package dataobjs;

public class YamlConfig {
    private String dbtype;
    private String host;
    private int port;
    private String user;
    private String password_envar;
    private String database_name;

    // public YamlConfig() {
    // }

    // public YamlConfig(String dbtype, String host, int port, String user, String password_envar, String database_name) {
    //     this.dbtype = dbtype;
    //     this.host = host;
    //     this.port = port;
    //     this.user = user;
    //     this.password_envar = password_envar;
    //     this.database_name = database_name;
    // }

    public String getDbType() {
        return this.dbtype;
    }

    public void setDbType(String dbtype) {
        this.dbtype = dbtype;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return this.port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getUser() {
        return this.user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword_envar() {
        return this.password_envar;
    }

    public void setPassword_envar(String password_envar) {
        this.password_envar = password_envar;
    }

    public String getDatabase_name() {
        return this.database_name;
    }

    public void setDatabase_name(String database_name) {
        this.database_name = database_name;
    }

    @Override
    public String toString() {
        return "{" +
            " DbType='" + getDbType() + "'" +
            ", host='" + getHost() + "'" +
            ", port='" + getPort() + "'" +
            ", user='" + getUser() + "'" +
            ", password_envar='" + getPassword_envar() + "'" +
            ", database_name='" + getDatabase_name() + "'" +
            "}";
    }

}