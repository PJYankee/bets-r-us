package com.application.springboot.objects;

import io.swagger.annotations.ApiModelProperty;
import java.text.DecimalFormat;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author "paul.perez"
 */
@Document(collection = "bankrolls")
public class Bankroll {
    @Id
    private String userName;
    @Indexed(unique = true)
    @ApiModelProperty(required = true)
    private double balance;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "Bankroll{"
                + ", userName = " + userName + " "
                + ", balence = " + balance
                + '}';
    }  
}
