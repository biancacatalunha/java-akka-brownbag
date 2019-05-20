package bankaccount.domain;

public abstract class Transaction {

    private Double amount;

    Transaction(Double amount) {
        this.amount = amount;
    }

    public Double getAmount() {
        return this.amount;
    }

    public abstract String getLog(String accountNo);

    public abstract Double calculateBalance(Double balance);

}
