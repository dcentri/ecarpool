package com.dicentrix.ecarpool.user;

import com.dicentrix.ecarpool.util.Address;

/**
 * Created by Akash on 9/26/2015.
 */
public class User {
    public enum UserType{
        PASSENGER,
        DRIVER;
    }

    private int id;

    private String login;
    private String firstName;
    private String lastName;
    private UserType type;
    private char gender;
    private String email;
    private String phone;
    private String password;
    private Address address;
    public String remoteAddress;
    public String[] remoteDemandeParcours;
    public String[] reomoteDemandeTrajet;

    public User() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

}
