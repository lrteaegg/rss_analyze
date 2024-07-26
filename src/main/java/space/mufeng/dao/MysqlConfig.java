package space.mufeng.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MysqlConfig {
    public static String url;
    public static String username;
    public static String password;

    public static Connection connection;
    public MysqlConfig() {

    }

    public static Statement getStatement() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        Connection connection = getConnection();
        return connection.createStatement();
    }

    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");
        if (connection == null || connection.isClosed()) {
            connection = null;
            connection = DriverManager.getConnection(url, username, password);
        }
        return connection;
    }

}
