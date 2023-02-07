package ac.udsm.coict.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private TextField textFieldUsername;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button btnLogin;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        btnLogin.setOnAction(event -> {
            String email = textFieldUsername.getText();
            String password = passwordField.getText();
            Util.signIn(event, 800, 600, email, password);
        });
    }
}