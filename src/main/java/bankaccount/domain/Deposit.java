package bankaccount.domain;

public class Deposit extends Transaction {

    public Deposit(Double amount, long accountNo, String transactionMadeBy, Long transactionDate) {
        super(amount, accountNo, transactionMadeBy, transactionDate);
    }
}
