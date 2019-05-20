package bankaccount.domain;

public class Deposit extends Transaction {

    public Deposit(Double amount) {
        super(amount);
    }

    @Override
    public String getLog(String accountNo) {
        return "Account number: " + accountNo + " -> Deposit made of " + this.getAmount();
    }

    @Override
    public Double calculateBalance(Double balance) {
        return balance + this.getAmount();
    }
}
