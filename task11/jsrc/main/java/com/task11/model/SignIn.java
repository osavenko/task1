package com.task11.model;

import com.task11.Task11Util;

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
        signIn.email = body.get(Task11Util.SingInField.EMAIL);
        signIn.password = body.get(Task11Util.SingInField.PASSWORD);
        return signIn;
    }
}
