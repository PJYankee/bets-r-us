package com.application.springboot.Controllers;

import com.application.springboot.persistence.User;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author "paul.perez"
 */
public interface UserOperationInterface {

    public void addUser(@RequestParam String userName, String firstName, String lastName, String email, 
                                String streetAddress, String city, String state, String zip) throws Exception;

    public void deleteUser(@RequestParam String userName);

    public List<User> listUsers();

    public User getUser(@RequestParam String userName);

    public User editUserAddress(@RequestParam String userName, String streetAddress, String city, String state, String zip);

    public User editUserProperName(@RequestParam String userName, String firstName, String lastName);

    public User editUserEmail(@RequestParam String userName, String email);

}
