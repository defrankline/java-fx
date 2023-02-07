package ac.udsm.coict.gui;

import javafx.fxml.Initializable;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MemberService implements Initializable {
    private static final String SQL_CREATE_TABLE_MEMBER = "CREATE TABLE IF NOT EXISTS members ( id INTEGER PRIMARY KEY, name TEXT NOT NULL, number TEXT NOT NULL UNIQUE)";
    private static final String SQL_INSERT_MEMBER = "INSERT INTO members (name,number) VALUES (?,?)";

    private static final String SQL_UPDATE = "UPDATE members SET name = ?, number = ? WHERE id = ?";

    public static final String SELECT_FROM_MEMBERS = "SELECT * FROM members order by id asc";

    public static final String SEARCH_MEMBERS = "SELECT * FROM members WHERE name LIKE ? or number like ?";
    public static final String SQL_GET = "select * from members where id= ?";
    public static final String SQL_GET_BY_NUMBER = "select * from members where number= ?";
    public static final String SQL_DELETE = "DELETE FROM members where id = ?";

    public static Connection getConnection() throws SQLException {
        DbConnectionImpl db = new DbConnectionImpl();
        return db.connection();
    }

    public static void initTableMember() throws SQLException {
        Connection c = getConnection();
        Statement stmt;
        try {
            stmt = c.createStatement();
            stmt.executeUpdate(SQL_CREATE_TABLE_MEMBER);
            stmt.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Users table created successfully");
    }

    public int create(Member member) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_INSERT_MEMBER)) {
            preparedStatement.setString(1, member.getName());
            preparedStatement.setString(2, member.getNumber());
            preparedStatement.executeUpdate();
            return 1;
        } catch (SQLException e) {
            return 0;
        }
    }

    public int update(Member member) {
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(SQL_UPDATE)) {
            preparedStatement.setString(1, member.getName());
            preparedStatement.setString(2, member.getNumber());
            preparedStatement.setInt(3, member.getId());
            preparedStatement.executeUpdate();
            return 1;
        } catch (SQLException e) {
            return 0;
        }
    }

    public Member get(int id) {
        PreparedStatement stmt;
        try (Connection connection = getConnection()) {
            stmt = connection.prepareStatement(SQL_GET);
            stmt.setInt(1, id);
            return getMember(stmt);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }

    private Member getMember(PreparedStatement stmt) throws SQLException {
        Member member = new Member();
        ResultSet rs = stmt.executeQuery();
        boolean check = false;
        while (rs.next()) {
            check = true;
            member.setId(rs.getInt("id"));
            member.setName(rs.getString("name"));
            member.setNumber(rs.getString("number"));
        }
        if (check) {
            return member;
        } else {
            return null;
        }
    }


    public Member get(String number) {
        PreparedStatement stmt;
        try (Connection connection = getConnection()) {
            stmt = connection.prepareStatement(SQL_GET_BY_NUMBER);
            stmt.setString(1, number);
            return getMember(stmt);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
            return null;
        }
    }

    public List<Member> processMembers(String query) throws SQLException {
        List<Member> members = new ArrayList<>();
        try (Connection connection = getConnection()) {
            PreparedStatement ps = connection.prepareStatement(SELECT_FROM_MEMBERS);
            if (!query.isEmpty()) {
                ps = connection.prepareStatement(SEARCH_MEMBERS);
                ps.setString(1, "%" + query + "%");
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Member member = new Member();
                member.setId(rs.getInt("id"));
                member.setName(rs.getString("name"));
                member.setNumber(rs.getString("number"));
                members.add(member);
            }
            rs.close();
            ps.close();
        }
        return members;
    }

    public List<MemberStringProperty> findAll(String query) throws SQLException {
        List<Member> members = this.processMembers(query);
        List<MemberStringProperty> properties = new ArrayList<>();
        if (members.size() > 0) {
            for (Member member : members) {
                MemberStringProperty property = new MemberStringProperty();
                property.setId(member.getId().toString());
                property.setName(member.getName());
                property.setNumber(member.getNumber());
                properties.add(property);
            }
        }
        return properties;
    }

    public int delete(int id) {
        PreparedStatement stmt;
        try (Connection connection = getConnection()) {

            PaymentService paymentService = new PaymentService();
            paymentService.deleteAllByMemberId(id);

            stmt = connection.prepareStatement(SQL_DELETE);
            stmt.setInt(1, id);
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
            initTableMember();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
