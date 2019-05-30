package bankaccount.domain;

public class Withdraw extends Transaction {

    public Withdraw(Double amount, Long accountNo) {
        super(amount, accountNo);
    }

    @Override
    public String getLog() {
        return "Account number: " + this.getAccountNo() + " -> Withdraw made of " + this.getAmount();
    }

    @Override
    public Double calculateBalance(Double balance) {
        return balance - this.getAmount();
    }
}
