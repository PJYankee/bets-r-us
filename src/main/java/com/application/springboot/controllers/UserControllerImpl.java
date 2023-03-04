package com.application.springboot.controllers;

import com.application.springboot.interfaces.UserOperationInterface;
import com.application.springboot.objects.Bankroll;
import com.application.springboot.objects.User;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.web.bind.annotation.PostMapping;
import springfox.documentation.annotations.ApiIgnore;

/**
 *
 * @author "paul.perez"
 */
@RestController
public class UserControllerImpl implements UserOperationInterface {

    private static final Logger LOGGER = LogManager.getLogger(UserControllerImpl.class);
    private MongoTemplate mongoTemplate;

    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    @PostMapping("/user/addUser")
    @ResponseBody
    public void addUser(@RequestParam String userName, String firstName, String lastName, String email,
            String streetAddress, String city, String state, String zip) throws Exception {
        User newUser = new User();
        newUser.setUserName(userName);
        newUser.setUniqueUserId(createUserId(userName));
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setStreet_Address(streetAddress);
        newUser.setCity(city);
        newUser.setState(state);
        newUser.setZip_code(zip);

        try {
            LOGGER.info("adding the user " + newUser + " to the database");
            mongoTemplate.save(newUser);
        } catch (Exception ex) {
            if (ex.getMessage().contains("userName")) {
                throw new Exception("Caught Mongo write error duplicate key, userName " + userName + " is already in use");
            } else if (ex.getMessage().contains("email")) {
                throw new Exception("Caught Mongo write error duplicate key, email " + email + " is already in use");
            } else {
                throw new Exception(ex);
            }
        }
        //If user is added to the DB successfully then create the user's bankroll with 0.00 balance 
        Bankroll newBankroll = new Bankroll();
        newBankroll.setUserName(userName);
        newBankroll.setBalance(0.00F);
        LOGGER.info("Bankroll for user " + newUser.getUserName() + " is available for deposit");
        mongoTemplate.save(newBankroll);
    }

    @Override
    @GetMapping("/user/getUser")
    @ResponseBody
    public User getUser(@RequestParam String userName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(userName));
        List<User> userList = mongoTemplate.find(query, User.class, "users");
        return userList.get(0);
    }

    @Override
    @PostMapping("/user/deleteUser")
    public void deleteUser(@RequestParam String userName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(userName));
        mongoTemplate.findAllAndRemove(query, User.class, "users");
    }

    @Override
    @GetMapping("/user/editUserAddress")
    @ResponseBody
    public User editUserAddress(String userName, String streetAddress, String city, String state, String zip) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(userName));
        Update update = new Update();
        update.set("Street_Address", streetAddress);
        update.set("city", city);
        update.set("state", state);
        update.set("zip_code", zip);
        User updatedUser = mongoTemplate.findAndModify(query, update, User.class);
        try {
            updatedUser = getUser(userName);
        } catch (IndexOutOfBoundsException ex) {
            LOGGER.error("Cannot edit user's address, the username provided (" + userName + ") does not exist in the system ");
        }
        return updatedUser;
    }

    @Override
    @GetMapping("/user/editUserProperName")
    @ResponseBody
    public User editUserProperName(String userName, String firstName, String lastName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(userName));
        Update update = new Update();
        update.set("firstName", firstName);
        update.set("lastName", lastName);
        User updatedUser = mongoTemplate.findAndModify(query, update, User.class);
        try {
            updatedUser = getUser(userName);
        } catch (IndexOutOfBoundsException ex) {
            LOGGER.error("Cannot edit the user's name, the username provided (" + userName + ") does not exist in the system");
        }
        return updatedUser;

    }

    @Override
    @GetMapping("/user/editUserEmail")
    @ResponseBody
    public User editUserEmail(String userName, String email) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(userName));
        Update update = new Update();
        update.set("email", email);
        User updatedUser = mongoTemplate.findAndModify(query, update, User.class);
        try {
            updatedUser = getUser(userName);
        } catch (IndexOutOfBoundsException ex) {
            LOGGER.error("Cannot edit the user's email, the username provided (" + userName + ") does not exist in the system");
        }
        return updatedUser;
    }

    @Override
    @GetMapping("/user/listUsers")
    @ResponseBody
    public List<User> listUsers() {
        List<User> users = mongoTemplate.findAll(User.class);
        return users;
    }

    /* Do not expose the following endpoints to the end user */
    @ApiIgnore
    @GetMapping("/user/deleteAllUsers")
    public void deleteAllUsers() {
        List<User> allUsers = listUsers();
        for (User user : allUsers) {
            Query query = new Query();
            query.addCriteria(Criteria.where("userName").is(user.getUserName()));
            mongoTemplate.findAllAndRemove(query, User.class, "users");
        }
    }

    public String createUserId(String userName) {
        return userName + ":" + UUID.randomUUID().toString();
    }
}
