package bankaccount.domain;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class Transaction implements Serializable {

    private static final long serialVersionUID = 3118106768699356719L;
    private Double amount;
    private Long accountNo;
    private String transactionMadeBy;
    private Long transactionDate;

    public Transaction(Double amount, long accountNo, String transactionMadeBy, Long transactionDate) {
        this.amount = amount;
        this.accountNo = accountNo;
        this.transactionMadeBy = transactionMadeBy;
        this.transactionDate = transactionDate;
    }
}
