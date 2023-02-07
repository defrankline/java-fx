package ac.udsm.coict.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class MemberStringProperty {
    private final StringProperty id;
    private final StringProperty name;
    private final StringProperty number;
    private final StringProperty totalPayment;

    public MemberStringProperty() {
        id = new SimpleStringProperty(this,"id");
        name = new SimpleStringProperty(this,"name");
        number = new SimpleStringProperty(this,"number");
        totalPayment = new SimpleStringProperty(this,"totalPayment");
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

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getNumber() {
        return number.get();
    }

    public StringProperty numberProperty() {
        return number;
    }

    public void setNumber(String number) {
        this.number.set(number);
    }

    public String getTotalPayment() {
        return totalPayment.get();
    }

    public StringProperty totalPaymentProperty() {
        return totalPayment;
    }

    public void setTotalPayment(String totalPayment) {
        this.totalPayment.set(totalPayment);
    }
}
