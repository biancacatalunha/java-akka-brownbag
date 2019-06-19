package bankaccount.domain;

public class Withdrawn extends Transaction {
    public Withdrawn(Double amount, long accountNo, String transactionMadeBy, Long transactionDate) {
        super(amount, accountNo, transactionMadeBy, transactionDate);
    }
}
