package com.application.springboot.objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
/**
 *
 * @author "paul.perez"
 */
@Document(collection = "users")
public class User {
    @Id
    private String uniqueUserId;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String Street_Address;
    private String city;
    private String state;
    private String zip_code;

    @Override
    public String toString() {
        return "User{"
                + "  userName = " + userName + " "
                + "  uniqueUserId = " + uniqueUserId + " "
                + ", firstName = " + firstName + " "
                + ", lastName=" + lastName + " "
                + ", email=" + email + " "
                + ", Street_Address=" + Street_Address + " "
                + ", City=" + city + " "
                + ", state=" + state + " "
                + ", zip_code=" + zip_code + " "
                + "}";
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStreet_Address() {
        return Street_Address;
    }

    public void setStreet_Address(String Street_Address) {
        this.Street_Address = Street_Address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public String getUniqueUserId() {
        return uniqueUserId;
    }

    public void setUniqueUserId(String uniqueUserId) {
        this.uniqueUserId = uniqueUserId;
    }

}