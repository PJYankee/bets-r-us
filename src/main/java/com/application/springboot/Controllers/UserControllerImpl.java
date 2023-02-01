package com.application.springboot.Controllers;

import com.application.springboot.persistence.User;
import java.util.ArrayList;
import java.util.List;
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
import springfox.documentation.annotations.ApiIgnore;

/**
 *
 * @author "paul.perez"
 */
@RestController
public class UserControllerImpl implements UserOperationInterface{
    private static final Logger LOGGER = LogManager.getLogger(UserControllerImpl.class);
    private MongoTemplate mongoTemplate;
     
    @Autowired
    public void setMongoTemplate(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    @GetMapping("/user/addUser")
    @ResponseBody
    public User addUser(@RequestParam String userName, String firstName, String lastName, String email, 
                                                String streetAddress, String city, String state, String zip) {
        User newUser = new User();
        newUser.setUserName(userName);
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setEmail(email);
        newUser.setStreet_Address(streetAddress);
        newUser.setCity(city);
        newUser.setState(state);
        newUser.setZip_code(zip);
        
        LOGGER.info("The user " + userName + " has successfully been added to the database");
        return mongoTemplate.save(newUser);
    }


    @Override
    @GetMapping("/user/getUser")
    @ResponseBody
    public List<User> getUser(@RequestParam String userName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userName").is(userName));

        return mongoTemplate.find(query, User.class, "users");
    }
    
    @Override    
    @GetMapping("/user/deleteUser")
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
        try{
        updatedUser = getUser(userName).get(0);
        }
        catch (IndexOutOfBoundsException ex){
        LOGGER.error("Cannot edit user's address, the username provided does not exist in the system ");
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
        try{
        updatedUser = getUser(userName).get(0);
        }
        catch (IndexOutOfBoundsException ex){
        LOGGER.error("Cannot edit the user's name, the username provided does not exist in the system");
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
        try{
        updatedUser = getUser(userName).get(0);
        }
        catch (IndexOutOfBoundsException ex){
        LOGGER.error("Cannot edit the user's email, the username provided does not exist in the system");
        }
        return updatedUser;        
    }

    @Override
    @GetMapping("/user/listUsers")
    @ResponseBody
    public List<String> listUsers() {
        LOGGER.info("Listing all user names from endpoint /user/listUsers");
        List<String> userNames =  new ArrayList();
        List<User> users = mongoTemplate.findAll(User.class);
        for (User user : users){
        userNames.add(user.getUserName());
        }
        return userNames; 
    }

    @ApiIgnore
    @GetMapping("/user/deleteAllUsers")
    public void deleteAllUsers() {
        List<String> userNames = listUsers();
        for(String userName : userNames)
        {
            Query query = new Query();
            query.addCriteria(Criteria.where("userName").is(userName));
            mongoTemplate.findAllAndRemove(query, User.class, "users");
        }
    } 
}
