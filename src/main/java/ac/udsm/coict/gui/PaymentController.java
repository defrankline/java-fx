package ac.udsm.coict.gui;

import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.math.BigDecimal;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class PaymentController implements Initializable {
    private final String[] months = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};

    @FXML
    private ComboBox<String> comboBoxMonth;

    @FXML
    private TextField textFieldAmount;

    @FXML
    private Label labelFeedbackMessage;

    @FXML
    private Button btnSave;

    @FXML
    private TableView<PaymentStringProperty> tableView;

    @FXML
    private TableColumn<PaymentStringProperty, String> tableColumnId;

    @FXML
    private TableColumn<PaymentStringProperty, String> tableColumnMonth;

    @FXML
    private TableColumn<PaymentStringProperty, String> tableColumnAmount;

    @FXML
    private TableColumn<PaymentStringProperty, Button> tableColumnDelete;

    private MemberStringProperty currentUser;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboBoxMonth.getItems().addAll(months);
        labelFeedbackMessage.setVisible(false);
        btnSave.setOnAction(event -> {
            if (textFieldAmount.getText() == null || comboBoxMonth.getValue() == null || textFieldAmount.getText().isEmpty()) {
                alert(labelFeedbackMessage, "Fill the form properly", "ERROR", 5);
            } else {
                save();
            }
        });

        textFieldAmount.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                textFieldAmount.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    private void alert(Label label, String message, String type, int timeout) {
        label.setVisible(true);
        label.setText(message);
        if (type.equals("SUCCESS")) {
            label.setTextFill(Color.valueOf("#009688"));
        } else {
            label.setTextFill(Color.valueOf("#f44336"));
        }
        PauseTransition pause = new PauseTransition(Duration.seconds(timeout));
        pause.setOnFinished(e -> {
            label.setText(null);
            label.setVisible(false);
        });
        pause.play();
    }

    private void save() {
        String month = comboBoxMonth.getValue();
        String amountValue = textFieldAmount.getText();
        assert amountValue != null;
        BigDecimal amount = new BigDecimal(amountValue);
        Payment payment = new Payment(month, amount, Integer.parseInt(currentUser.getId()));
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            PaymentService paymentService = new PaymentService();
            paymentService.save(payment);
            alert(labelFeedbackMessage, "Payment Saved Successfully", "SUCCESS", 5);
        }
        try {
            table();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadCurrentUserPayments(MemberStringProperty p) {
        currentUser = p;
        try {
            this.table();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    private void table() throws SQLException {
        PaymentService paymentService = new PaymentService();
        ObservableList<PaymentStringProperty> observableList = FXCollections.observableArrayList();
        observableList.addAll(paymentService.findAll(Integer.parseInt(currentUser.getId())));
        tableView.setItems(observableList);
        tableColumnId.setCellValueFactory(f -> f.getValue().idProperty());
        tableColumnMonth.setCellValueFactory(f -> f.getValue().monthProperty());
        tableColumnAmount.setCellValueFactory(f -> f.getValue().amountProperty());
        tableColumnDelete.setCellFactory(ActionButtonTableCell.forTableColumn("Delete", this::deleteConfirmation));
    }

    private PaymentStringProperty deleteConfirmation(PaymentStringProperty p) {
        int id = Integer.parseInt(p.getId());
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure?");
        alert.setContentText("This process is irreversible");

        Optional<ButtonType> option = alert.showAndWait();
        if (option.isPresent()) {
            if (option.get() == ButtonType.OK) {
                tableView.getItems().remove(p);
                PaymentService paymentService = new PaymentService();
                paymentService.delete(id);
            }
        }
        return p;
    }
}