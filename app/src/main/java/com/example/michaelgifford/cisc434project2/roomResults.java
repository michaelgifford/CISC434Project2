package com.example.michaelgifford.cisc434project2;

/**
 * Created by michaelgifford on 15-11-29.
 */

public class roomResults {
    private String name = "";
    private String numCapacity = "";
    private String numCurrentUsers = "";

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setCapacity(String numCapacity) {
        this.numCapacity = numCapacity;
    }

    public String getCapacity() {
        return numCapacity;
    }

    public void setNumCurrentUsers(String numCurrentUsers) {
        this.numCurrentUsers = numCurrentUsers;
    }

    public String getNumCurrentUsers() {
        return numCurrentUsers;
    }
}