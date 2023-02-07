package ac.udsm.coict.gui;

import javafx.fxml.Initializable;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PaymentService implements Initializable {
    private static final String SQL_CREATE_TABLE_PAYMENT = "CREATE TABLE IF NOT EXISTS payments ( id INTEGER PRIMARY KEY, month TEXT NOT NULL, amount REAL NOT NULL, member_id INTEGER NOT NULL, FOREIGN KEY (member_id) REFERENCES members (id))";
    private static final String SQL_INSERT_PAYMENT = "INSERT INTO payments (month,amount,member_id) VALUES (?,?,?)";

    public static final String SELECT_FROM_PAYMENTS = "SELECT * FROM payments where member_id = ? order by id asc";
    public static final String SQL_GET = "select * from payments where id = ?";
    public static final String SQL_DELETE = "DELETE FROM payments where id = ?";

    public static final String SQL_DELETE_BY_MEMBER_ID = "DELETE FROM payments where member_id = ?";

    public static Connection getConnection() throws SQLException {
        DbConnectionImpl db = new DbConnectionImpl();
        return db.connection();
    }

    public static void initTablePayment() throws SQLException {
        Connection c = getConnection();
        Statement stmt;
        try {
            stmt = c.createStatement();
            stmt.executeUpdate(SQL_CREATE_TABLE_PAYMENT);
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Payments table created successfully");
    }

    public int save(Payment payment) {
        try (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_PAYMENT)) {
            preparedStatement.setString(1, payment.getMonth());
            preparedStatement.setBigDecimal(2, payment.getAmount());
            preparedStatement.setInt(3, payment.getMemberId());
            preparedStatement.executeUpdate();
            return 1;
        } catch (SQLException e) {
            return 0;
        }
    }

    public Payment get(int id) {
        PreparedStatement stmt;
        try (Connection connection = getConnection()) {
            stmt = connection.prepareStatement(SQL_GET);
            stmt.setInt(1, id);
            return getPayment(stmt);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }

    private Payment getPayment(PreparedStatement stmt) throws SQLException {
        Payment payment = new Payment();
        ResultSet rs = stmt.executeQuery();
        boolean check = false;
        while (rs.next()) {
            check = true;
            payment.setId(rs.getInt("id"));
            payment.setMonth(rs.getString("month"));
            payment.setAmount(rs.getBigDecimal("amount"));
            payment.setMemberId(rs.getInt("member_id"));
        }
        if (check) {
            return payment;
        } else {
            return null;
        }
    }

    public List<Payment> processPayments(int memberId) throws SQLException {
        List<Payment> payments = new ArrayList<>();
        try (Connection connection = getConnection()) {
            PreparedStatement ps = connection.prepareStatement(SELECT_FROM_PAYMENTS);
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setId(rs.getInt("id"));
                payment.setMonth(rs.getString("month"));
                payment.setAmount(rs.getBigDecimal("amount"));
                payments.add(payment);
            }
            rs.close();
            ps.close();
        }
        return payments;
    }

    public List<PaymentStringProperty> findAll(int id) throws SQLException {
        List<Payment> payments = this.processPayments(id);
        List<PaymentStringProperty> properties = new ArrayList<>();
        if (payments.size() > 0) {
            for (Payment payment : payments) {
                PaymentStringProperty row = new PaymentStringProperty();
                row.setId(payment.getId().toString());
                row.setMonth(payment.getMonth());
                row.setAmount(payment.getAmount().toString());
                properties.add(row);
            }
        }
        return properties;
    }


    public int delete(int id) {
        PreparedStatement stmt;
        try (Connection connection = getConnection()) {
            stmt = connection.prepareStatement(SQL_DELETE);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            return 1;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return 0;
        }
    }

    public int deleteAllByMemberId(int memberId) {
        PreparedStatement stmt;
        try (Connection connection = getConnection()) {
            stmt = connection.prepareStatement(SQL_DELETE_BY_MEMBER_ID);
            stmt.setInt(1, memberId);
            stmt.executeUpdate();
            return 1;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return 0;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            initTablePayment();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
