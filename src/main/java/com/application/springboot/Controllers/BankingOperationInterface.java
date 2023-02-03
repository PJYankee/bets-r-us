package com.application.springboot.Controllers;

import com.application.springboot.persistence.Bankroll;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author "paul.perez"
 */
public interface BankingOperationInterface {
    
//all banking transactions return a Bankroll with the new balance of the account after the transfer is complete
public Bankroll addFundsCreditCard (@RequestParam String userName, String cardNumber, String expiration, String cvv, Float amount);

public Bankroll addFundsBankAccout (@RequestParam String userName, String accountNumber, String routingNumber, Float amount);

public Bankroll withdrawFundsBankAccount (@RequestParam String userName, String accountNumber, String routingNumber, Float amount);

public Bankroll withdrawFundsCheck (@RequestParam String userName, Float amount);

public Bankroll deductBetAmount (@RequestParam String userName, Float amount);

public Bankroll addWinningsToBank (@RequestParam String userName, Float amount);

public Bankroll getBankroll(String userName);

}
