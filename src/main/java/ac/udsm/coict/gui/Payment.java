package ac.udsm.coict.gui;

import java.math.BigDecimal;

public class Payment {
    private Integer id;
    private String month;
    private BigDecimal amount;

    private Integer memberId;

    public Payment() {
    }


    public Payment(String month, BigDecimal amount, Integer memberId) {
        this.month = month;
        this.amount = amount;
        this.memberId = memberId;
    }

    public Payment(Integer id, String month, BigDecimal amount, Integer memberId) {
        this.id = id;
        this.month = month;
        this.amount = amount;
        this.memberId = memberId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getMemberId() {
        return memberId;
    }

    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }
}
