package bankaccount.domain;

public class Withdraw extends Transaction {

    public Withdraw(Double amount, long accountNo, String transactionMadeBy, Long transactionDate) {
        super(amount, accountNo, transactionMadeBy, transactionDate);
    }
}
