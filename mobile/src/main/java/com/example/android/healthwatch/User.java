package com.example.android.healthwatch;

/**
 * Created by faitholadele on 4/10/17.
 */

public class User {

    int Id; //auto generated in DB
    public String firstName;
    public String lastName;
    public String userName;
    public String email;
    public String password;

    public User(){}

    public User(int ID)
    {
        //DB Connection and request existing user
    }

    // Add new user to DB
    public void addNewUser()
    {

    }

    public User(String email, String fname, String lname, String password){
        this.email = email;
        this.firstName = fname;
        this.lastName = lname;
        this.password = password;
    }

    //check if user is in DB
    /*public boolean existingUserLogin()
    {
        return false;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
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
    }*/
}
