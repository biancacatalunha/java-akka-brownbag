package bankaccount.domain;

public class Statement extends Transaction {
    Statement(Double balance, long accountNo) {
        super(balance, accountNo);
    }

    @Override
    public String getLog() {
        return null;
    }

    @Override
    public Double calculateBalance(Double balance) {
        return null;
    }
}
