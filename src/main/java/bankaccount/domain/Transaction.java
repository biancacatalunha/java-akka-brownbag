package bankaccount.domain;

public abstract class Transaction {

    private Double amount;
    private Long accountNo;

    Transaction(Double amount, long accountNo) {
        this.amount = amount;
        this.accountNo = accountNo;
    }

    public Double getAmount() {
        return this.amount;
    }

    public Long getAccountNo() {
        return this.accountNo;
    }

    public abstract String getLog();

    public abstract Double calculateBalance(Double balance);

}
