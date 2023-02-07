package ac.udsm.coict.gui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DbConnectionImpl implements DbConnection {

    @Override
    public Connection connection() throws SQLException {
        String url = "jdbc:sqlite::resource:is611.db";
        return DriverManager.getConnection(url);
    }
}
