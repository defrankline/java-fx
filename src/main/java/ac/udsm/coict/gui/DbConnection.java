package ac.udsm.coict.gui;

import java.sql.Connection;
import java.sql.SQLException;

public interface DbConnection {
    Connection connection() throws SQLException;
}
