package com.task10.model;

import com.task10.Task10Util;

import java.util.Map;

public class SignIn {
    private String email;
    private String password;

    private SignIn() {
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "SignIn{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public static SignIn getInstance(Map<String, String> body){
        final SignIn signIn = new SignIn();
        signIn.email = body.get(Task10Util.SingInField.EMAIL);
        signIn.password = body.get(Task10Util.SingInField.PASSWORD);
        return signIn;
    }
}
