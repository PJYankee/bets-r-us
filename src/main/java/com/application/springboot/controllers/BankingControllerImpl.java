package com.application.springboot.controllers;

import com.application.springboot.interfaces.BankingOperationInterface;
import com.application.springboot.objects.Bankroll;
import io.swagger.annotations.ApiOperation;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

/**
 *
 * @author "paul.perez"
 */
@RestController
public class BankingControllerImpl implements BankingOperationInterface {

    private static final Logger LOGGER = LogManager.getLogger(BankingControllerImpl.class);
    private MongoTemplate mongoTemplate;
    DecimalFormat decimalFormat = new DecimalFormat("#.##");

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    @GetMapping("/bankroll/addFundsCreditCard")
    @ResponseBody
    @ApiOperation(value = "Allows the user to add funds to their bankroll with a valid credit card", notes = "Returns the updated Bankroll object")    
    public Bankroll addFundsCreditCard(String userName, String cardNumber, String expiration, String cvv, Float amount) {
        Bankroll bankroll = getBankroll(userName);
        if (checkCreditCardValid(cardNumber, expiration, cvv)) {
            Query query = new Query();
            query.addCriteria(Criteria.where("userName").is(userName));
            Float oldBalance = bankroll.getBalance();
            Float newBalance = Float.valueOf(decimalFormat.format(oldBalance + amount));
            Update update = new Update();
            update.set("balance", newBalance);
            Bankroll updatedBankroll = mongoTemplate.findAndModify(query, update, Bankroll.class);
            try {
                updatedBankroll = getBankroll(userName);
            } catch (IndexOutOfBoundsException ex) {
                LOGGER.error("Cannot add funds to the account, the userName provided (" + userName + ") does not exist in the system ");
            }
            return updatedBankroll;
        } else {
            return bankroll;
        }
    }

    @Override
    @GetMapping("/bankroll/addFundsBankAccount")
    @ResponseBody
    @ApiOperation(value = "Allows the user to add funds to their bankroll with a valid bank account", notes = "Returns the updated Bankroll object")      
    public Bankroll addFundsBankAccout(String userName, String accountNumber, String routingNumber, Float amount) {
        Bankroll bankroll = getBankroll(userName);
        if (checkBankAccountValid(accountNumber, routingNumber)) {
            Query query = new Query();
            query.addCriteria(Criteria.where("userName").is(userName));
            Float oldBalance = bankroll.getBalance();
            Float newBalance = Float.valueOf(decimalFormat.format(oldBalance + amount));
            Update update = new Update();
            update.set("balance", newBalance);
            Bankroll updatedBankroll = mongoTemplate.findAndModify(query, update, Bankroll.class);
            try {
                updatedBankroll = getBankroll(userName);
            } catch (IndexOutOfBoundsException ex) {
                LOGGER.error("Cannot add funds to the account, the userName provided (" + userName + ") does not exist in the system ");
            }
            return updatedBankroll;
        } else {
            return bankroll;
        }
    }

    @Override
    @GetMapping("/bankroll/withdrawFundsBankAccount")
    @ResponseBody
    @ApiOperation(value = "Allows the user to withdraw funds from their bankroll to an external bank account", notes = "Returns the updated Bankroll object")      
    public Bankroll withdrawFundsBankAccount(String userName, String accountNumber, String routingNumber, Float amount) {
        Bankroll bankroll = getBankroll(userName);
        if (checkBankAccountValid(accountNumber, routingNumber)) {
            Query query = new Query();
            query.addCriteria(Criteria.where("userName").is(userName));
            Float oldBalance = bankroll.getBalance();
            Float newBalance = Float.valueOf(decimalFormat.format(oldBalance - amount));
            if (newBalance < 0.00F) {
                LOGGER.error("withdraw failed, user does not have sufficient funds to withdraw");
                return bankroll;
            } else {
                Update update = new Update();
                update.set("balance", newBalance);
                Bankroll updatedBankroll = mongoTemplate.findAndModify(query, update, Bankroll.class);
                try {
                    updatedBankroll = getBankroll(userName);
                } catch (IndexOutOfBoundsException ex) {
                    LOGGER.error("Cannot withdraw funds from the account, the userName provided (" + userName + ") does not exist in the system ");
                }
                return updatedBankroll;
            }
        } else {
            return bankroll;
        }
    }

    @Override
    @GetMapping("/bankroll/withdrawFundsCheck")
    @ResponseBody
    @ApiOperation(value = "Allows the user to withdraw funds from their bankroll via check by mail", notes = "Returns the updated Bankroll object")      
    public Bankroll withdrawFundsCheck(String userName, Float amount) {
        Bankroll bankroll = getBankroll(userName);
        
        return bankroll;
    }

    @Override
    @GetMapping("/bankroll/getBankroll")
    @ResponseBody
    @ApiOperation(value = "Allows the user to view the balance of their bankroll", notes = "Returns the Bankroll object")  
    public Bankroll getBankroll(String userName) {
        List<Bankroll> bankrollList = new ArrayList();
        Bankroll bankroll = new Bankroll();
        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(userName));
        bankrollList = mongoTemplate.find(query, Bankroll.class, "bankrolls");
        try {
            bankroll = bankrollList.get(0);
        } catch (IndexOutOfBoundsException ex) {
            LOGGER.error("Cannot retrieve the bankroll for this user, the userName provided (" + userName + ") does not exist in the system ");
        }
        return bankroll;
    }

    public List<Bankroll> listAllBankrolls() {
        List<Bankroll> bankrollList = mongoTemplate.findAll(Bankroll.class);
        return bankrollList;
    }

    public Boolean checkBankAccountValid(String accountNumber, String routingNumber) {
        Boolean isValid = true;
        if (accountNumber == null || routingNumber == null) {
            isValid = false;
            LOGGER.error("Cannot complete transaction, bank account information is not valid");
        }
        return isValid;
    }

    public Boolean checkCreditCardValid(String cardNumber, String expiration, String CVV) {
        Boolean isValid = true;
        if (cardNumber == null || expiration == null || CVV == null) {
            isValid = false;
            LOGGER.error("Cannot complete transaction, credit card information is not valid");
        }
        if (!cardNumberValid(cardNumber)) {
            isValid = false;
            LOGGER.error("Cannot complete transaction, credit card number is not valid");
        }
        if (expiration == null || !expirationValid(expiration)) {
            isValid = false;
            LOGGER.error("Cannot complete transaction, credit card is past its expiration date");
        }
        return isValid;
    }

    public Boolean cardNumberValid(String cardNumber) {
        Boolean isValid = false;
        if (cardNumber.length() == 15 && cardNumber.startsWith("3")) {
            isValid = true;
        }
        if ((cardNumber.length() == 16) && ((cardNumber.startsWith("4")) || (cardNumber.startsWith("5")) || (cardNumber.startsWith("6")))) {
            isValid = true;
        }
        return isValid;
    }

    public Boolean expirationValid(String expiration) {
        Date expirationDate = null;
        String pattern = "MMyy";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date today = new Date();

        try {
            expirationDate = sdf.parse(expiration);
        } catch (ParseException ex) {
            LOGGER.error("ParseException throw while parsing Date " + expiration);
            return false;
        }
        return today.before(expirationDate);
    }
    
     /* Do not expose the following endpoints to the end user */
    @ApiIgnore
    @GetMapping("/bankroll/deleteAllBankrolls")
    public void deleteAllBankrolls() {
        List<Bankroll> allBankrolls = listAllBankrolls();
        for (Bankroll bankroll : allBankrolls) {
            Query query = new Query();
            query.addCriteria(Criteria.where("userName").is(bankroll.getUserName()));
            mongoTemplate.findAllAndRemove(query, Bankroll.class, "bankrolls");
        }
    }
    
    @Override
    @GetMapping("/bankroll/deductBetAmount")
    @ResponseBody
    @ApiIgnore
    public Bankroll deductBetAmount(String userName, Float amount) {
        Bankroll bankroll = getBankroll(userName);
        //TODO deduct bet amount from account when bet placed
        return bankroll;
    }

    @Override
    @GetMapping("/bankroll/addWinningsToBank")
    @ResponseBody
    @ApiIgnore
    public Bankroll addWinningsToBank(String userName, Float amount) {
        Bankroll bankroll = getBankroll(userName);
        //TODO if block for if bet won add winnings
        return bankroll;
    }    
}
