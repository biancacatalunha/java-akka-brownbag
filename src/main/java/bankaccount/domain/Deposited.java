package bankaccount.domain;

public class Deposited extends Transaction {
    public Deposited(Double amount, long accountNo, String transactionMadeBy, Long transactionDate) {
        super(amount, accountNo, transactionMadeBy, transactionDate);
    }
}
