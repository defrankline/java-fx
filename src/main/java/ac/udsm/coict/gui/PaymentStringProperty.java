package ac.udsm.coict.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PaymentStringProperty {
    private final StringProperty id;
    private final StringProperty month;
    private final StringProperty amount;

    public PaymentStringProperty() {
        id = new SimpleStringProperty(this, "id");
        month = new SimpleStringProperty(this, "month");
        amount = new SimpleStringProperty(this, "amount");
    }

    public String getId() {
        return id.get();
    }

    public StringProperty idProperty() {
        return id;
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public String getMonth() {
        return month.get();
    }

    public StringProperty monthProperty() {
        return month;
    }

    public void setMonth(String month) {
        this.month.set(month);
    }

    public String getAmount() {
        return amount.get();
    }

    public StringProperty amountProperty() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount.set(amount);
    }


}
