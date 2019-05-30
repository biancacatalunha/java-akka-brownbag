package bankaccount.http;

import bankaccount.actors.Account;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import bankaccount.domain.Deposit;
import bankaccount.domain.Withdraw;

public class AccountApp {

    public static void main(String[] args) {
        final ActorSystem system = ActorSystem.create("AccountApp"); //creates an actor system and names it

        //creates an actor account, sets and account number and an actor name
        final ActorRef account1 = system.actorOf(Account.props(1L), "account1");
        final ActorRef account2 = system.actorOf(Account.props(2L), "account2");

        account1.tell(new Deposit(10.0, 1L), ActorRef.noSender());//doesn't expect an answer
        account2.tell(new Withdraw(5.0, 2L), ActorRef.noSender());//doesn't expect an answer
        account2.tell(new Deposit(2.0, 2L), ActorRef.noSender());//doesn't expect an answer
    }
}
