package com.task10.model;

import com.task10.Task10Util;

import java.util.Map;

public class SingIn {
    private String email;
    private String password;

    private SingIn() {
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "SingIn{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public static SingIn getInstance(Map<String, String> body){
        final SingIn singIn = new SingIn();
        singIn.email = body.get(Task10Util.SingInField.EMAIL);
        singIn.password = body.get(Task10Util.SingInField.PASSWORD);
        return singIn;
    }
}
