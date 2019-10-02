package pro.antonshu.net;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionFactory {

    /*
     * Utility for JDBC Logger.
     */
    private static BasicDataSource dataSource;
    private static String localAddress = "jdbc:mysql://localhost/cloudstorage?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
    private static String inetAddress = "jdbc:mysql://antonshu.pro:3306/cloudstorage?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";

    private ConnectionFactory() {
    }

    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            dataSource = new BasicDataSource();
            dataSource.setUrl(inetAddress);
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUsername("server");
            dataSource.setPassword("123");
        }
        return dataSource.getConnection();
    }
}
