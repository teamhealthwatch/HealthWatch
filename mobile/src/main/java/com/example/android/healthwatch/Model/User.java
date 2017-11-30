package com.example.android.healthwatch.Model;

/**
 * Created by faitholadele on 4/10/17.
 */

public class User {
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String password;

    public User(String email, String fname, String lname, String password){
        this.email = email;
        this.firstName = fname;
        this.lastName = lname;
        this.password = password;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
