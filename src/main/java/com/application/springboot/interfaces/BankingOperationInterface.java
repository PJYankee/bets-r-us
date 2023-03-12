package com.application.springboot.interfaces;

import com.application.springboot.objects.Bankroll;
import org.springframework.web.bind.annotation.RequestParam;
/**
 *
 * @author "paul.perez"
 */
public interface BankingOperationInterface {
//all banking transactions return a Bankroll with the new balance of the account after the transfer is complete
public Bankroll addFundsCreditCard (@RequestParam String userName, String cardNumber, String expiration, String cvv, double amount);

public Bankroll addFundsBankAccout (@RequestParam String userName, String accountNumber, String routingNumber, double amount);

public Bankroll withdrawFundsBankAccount (@RequestParam String userName, String accountNumber, String routingNumber, double amount);

public Bankroll withdrawFundsCheck (@RequestParam String userName, double amount);

public Bankroll getBankroll(String userName);
}
