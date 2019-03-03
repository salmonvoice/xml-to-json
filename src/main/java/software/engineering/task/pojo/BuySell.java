package software.engineering.task.pojo;


import java.time.LocalDate;

public class BuySell {
    private LocalDate buyDate;
    private LocalDate sellDate;
    private String currency;

    public BuySell(LocalDate buyDate, LocalDate sellDate, String currency) {
        this.buyDate = buyDate;
        this.sellDate = sellDate;
        this.currency = currency;
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
