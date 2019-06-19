package bankaccount.domain;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.ArrayList;

@Slf4j
@Data
public class AccountState implements Serializable {

    private static final long serialVersionUID = -6197770602747503848L;
    private final ArrayList<Transaction> transactions;
    private Double balance;
    private Long accountId;

    public void apply(Transaction transaction) {
        update(transaction);
        calculateBalance(transaction);
    }

    public AccountState(ArrayList<Transaction> transactions, Double balance, Long accountId) {
        this.transactions = transactions;
        this.balance = balance;
        this.accountId = accountId;
    }

    public AccountState copy() {
        return new AccountState(new ArrayList<>(transactions), balance, accountId);
    }

    public void update(Transaction transaction) {
        transactions.add(transaction);
    }

    public int size() {
        return transactions.size();
    }

    public void calculateBalance(Transaction transaction) {
        if(transaction instanceof Deposit || transaction instanceof Deposited) {
            this.balance += transaction.getAmount();
        } else if (transaction instanceof Withdraw || transaction instanceof Withdrawn) {
            this.balance -= transaction.getAmount();
        }
    }
}
