package ac.udsm.coict.gui;

public class AuthService {
    public boolean login(String usernameEntered, String passwordEntered) {
        String username = "is611";
        String password = "is611";
        return username.equals(usernameEntered) && password.equals(passwordEntered);
    }
}
