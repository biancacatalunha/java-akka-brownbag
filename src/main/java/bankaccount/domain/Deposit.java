package bankaccount.domain;

public class Deposit extends Transaction {

    public Deposit(Double amount, Long accountNo) {
        super(amount, accountNo);
    }

    @Override
    public String getLog() {
        return "Account number: " + this.getAccountNo() + " -> Deposit made of " + this.getAmount();
    }

    @Override
    public Double calculateBalance(Double balance) {
        return balance + this.getAmount();
    }
}
