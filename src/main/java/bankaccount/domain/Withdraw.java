package bankaccount.domain;

public class Withdraw extends Transaction {

    public Withdraw(Double amount) {
        super(amount);
    }


    @Override
    public String getLog(String accountNo) {
        return "Account number: " + accountNo + " -> Withdraw made of " + this.getAmount();
    }

    @Override
    public Double calculateBalance(Double balance) {
        return balance - this.getAmount();
    }
}
