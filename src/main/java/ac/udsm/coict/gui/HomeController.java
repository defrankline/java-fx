package ac.udsm.coict.gui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML
    private Button btnLogout;

    @FXML
    private Label labelLoggedInUser;

    private String query = "";

    @FXML
    private TextField textFieldSearch;

    @FXML
    private Button btnRegister;

    @FXML
    private TableView<MemberStringProperty> tableView;

    @FXML
    private TableColumn<MemberStringProperty, String> tableColumnId;

    @FXML
    private TableColumn<MemberStringProperty, String> tableColumnName;

    @FXML
    private TableColumn<MemberStringProperty, String> tableColumnNumber;

    @FXML
    private TableColumn<MemberStringProperty, Button> tableColumnUpdate;

    @FXML
    private TableColumn<MemberStringProperty, Button> tableColumnDelete;

    @FXML
    private TableColumn<MemberStringProperty, Button> tableColumnPayment;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.table();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        btnLogout.setOnAction(event -> Util.changeScene(event, 600, 400, "login.fxml", "Login In", null));
        btnRegister.setOnAction(event -> createDialog());
        textFieldSearch.textProperty().addListener(new DelayedListener<>() {
            @Override
            public void onChanged(String value) throws SQLException {
                query = value;
                table();
            }
        });
    }

    private void table() throws SQLException {
        MemberService memberService = new MemberService();
        ObservableList<MemberStringProperty> observableList = FXCollections.observableArrayList();
        observableList.addAll(memberService.findAll(this.query));
        tableView.setItems(observableList);
        tableColumnId.setCellValueFactory(f -> f.getValue().idProperty());
        tableColumnName.setCellValueFactory(f -> f.getValue().nameProperty());
        tableColumnNumber.setCellValueFactory(f -> f.getValue().numberProperty());
        tableColumnUpdate.setCellFactory(ActionButtonTableCell.forTableColumn("Edit", this::updateDialog));
        tableColumnDelete.setCellFactory(ActionButtonTableCell.forTableColumn("Delete", this::memberDeleteConfirmation));
        tableColumnPayment.setCellFactory(ActionButtonTableCell.forTableColumn("Payments", this::payments));
    }

    private MemberStringProperty payments(MemberStringProperty p) {
        Parent root = null;
        try {
            FXMLLoader loader = new FXMLLoader(Util.class.getResource("payment.fxml"));
            root = loader.load();
            PaymentController paymentController = loader.getController();
            paymentController.loadCurrentUserPayments(p);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Stage stage = new Stage();
        stage.setTitle("Payments history for " + p.getName());
        stage.centerOnScreen();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setX(800);
        stage.setY(200);
        stage.setResizable(false);
        assert root != null;
        stage.setScene(new Scene(root, 600, 400));
        stage.showAndWait();
        return p;
    }

    private void createDialog() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Member Registration Form");
        dialog.setHeaderText("Fill the form properly!");

        ImageView lockImage = new ImageView(Objects.requireNonNull(this.getClass().getResource("registration.png")).toString());
        lockImage.setFitWidth(50);
        lockImage.setFitHeight(50);
        dialog.setGraphic(lockImage);

        ButtonType buttonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField();
        name.setPromptText("Name");

        TextField number = new TextField();
        number.setPromptText("Number");

        grid.add(new Label("Name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("Number:"), 0, 1);
        grid.add(number, 1, 1);

        Node node = dialog.getDialogPane().lookupButton(buttonType);
        node.setDisable(true);

        name.textProperty().addListener((observable, oldValue, newValue) -> {
            node.setDisable(newValue.trim().isEmpty());
        });

        number.textProperty().addListener((observable, oldValue, newValue) -> {
            node.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(name::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonType) {
                return new Pair<>(name.getText(), number.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(data -> {
            if (result.get().getKey().isEmpty() || result.get().getValue().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Incomplete Form!");
                alert.setHeaderText("Fill the form properly");
                alert.setContentText("Name or Number should not be empty");
                alert.showAndWait();
            } else {
                Member member = new Member(Util.getPrimaryKey(), data.getKey(), data.getValue());
                MemberService memberService = new MemberService();
                memberService.create(member);
                try {
                    table();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private MemberStringProperty updateDialog(MemberStringProperty memberStringProperty) {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Member Info Update");
        dialog.setHeaderText("Fill the form properly!");

        ImageView lockImage = new ImageView(Objects.requireNonNull(this.getClass().getResource("lock.png")).toString());
        lockImage.setFitWidth(50);
        lockImage.setFitHeight(50);
        dialog.setGraphic(lockImage);

        ButtonType buttonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(buttonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField name = new TextField();
        name.setPromptText("Name");
        name.setText(memberStringProperty.getName());

        TextField number = new TextField();
        number.setPromptText("Number");
        number.setText(memberStringProperty.getNumber());

        grid.add(new Label("Name:"), 0, 0);
        grid.add(name, 1, 0);
        grid.add(new Label("Number:"), 0, 1);
        grid.add(number, 1, 1);

        Node node = dialog.getDialogPane().lookupButton(buttonType);
        node.setDisable(true);

        name.textProperty().addListener((observable, oldValue, newValue) -> {
            node.setDisable(newValue.trim().isEmpty());
        });

        number.textProperty().addListener((observable, oldValue, newValue) -> {
            node.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        Platform.runLater(name::requestFocus);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == buttonType) {
                return new Pair<>(name.getText(), number.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(data -> {
            Member member = new Member(Integer.parseInt(memberStringProperty.getId()), data.getKey(), data.getValue());
            MemberService memberService = new MemberService();
            memberService.update(member);
            try {
                table();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return memberStringProperty;
    }

    private MemberStringProperty memberDeleteConfirmation(MemberStringProperty p) {
        int id = Integer.parseInt(p.getId());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("This process is irreversible");

        Optional<ButtonType> option = alert.showAndWait();
        if (option.isPresent()) {
            if (option.get() == ButtonType.OK) {
                tableView.getItems().remove(p);
                MemberService memberService = new MemberService();
                memberService.delete(id);
            }
        }
        return p;
    }

    public void setLoggedInUser(String username) {
        labelLoggedInUser.setText("Welcome " + username + "!");
    }
}