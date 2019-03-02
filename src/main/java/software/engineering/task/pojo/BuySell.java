package software.engineering.task.pojo;


import java.time.LocalDate;

public class BuySell {
    private LocalDate buyDate;
    private LocalDate sellDate;
    private String currency;

    public BuySell(LocalDate buyDate, LocalDate sellDate) {
        this.buyDate = buyDate;
        this.sellDate = sellDate;
    }

    public LocalDate getBuyDate() {
        return buyDate;
    }

    public LocalDate getSellDate() {
        return sellDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
