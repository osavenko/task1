package com.task10.model;

import com.task10.Task10Util;

import java.util.Map;

public class SingUp {
    private String firstName;
    private String lastName;
    private String email;
    private String password;

    private SingUp() {
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
    public static SingUp getInstance(Map<String,String> body){
        final SingUp singUp = new SingUp();
        singUp.firstName = body.get(Task10Util.SingUpField.FIRST_NAME);
        singUp.lastName = body.get(Task10Util.SingUpField.LAST_NAME);
        singUp.email = body.get(Task10Util.SingUpField.EMAIL);
        singUp.password = body.get(Task10Util.SingUpField.PASSWORD);
        return singUp;
    }

    @Override
    public String toString() {
        return "SingUp{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
