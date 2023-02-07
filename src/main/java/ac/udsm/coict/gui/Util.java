package ac.udsm.coict.gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Util {

    public static void changeScene(ActionEvent event, double width, double height, String fxmlFile, String title, String username) {
        Parent root = null;
        if (username != null) {
            try {
                FXMLLoader loader = new FXMLLoader(Util.class.getResource(fxmlFile));
                root = loader.load();
                HomeController homeController = loader.getController();
                homeController.setLoggedInUser(username);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                root = FXMLLoader.load(Objects.requireNonNull(Util.class.getResource(fxmlFile)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setResizable(false);
        assert root != null;
        stage.setScene(new Scene(root, width, height));
        stage.show();
    }

    public static void signIn(ActionEvent event, double width, double height, String username, String password) {
        AuthService authService = new AuthService();
        boolean login = authService.login(username, password);
        if (login) {
            changeScene(event, width, height, "home.fxml", "Welcome!", username);
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Wrong credentials!");
            alert.show();
        }
    }

    public static Integer getPrimaryKey() {
        return (int) (Math.random() * 100000000);
    }
}
