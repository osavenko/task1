package com.task10;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;

public class Task10Util {
    private static final String USER_POOL_NAME ="cmtr-2c83ab08-simple-booking-userpool";
    public static class StatusCode{
        public static final int SUCCESS = 200;
        public static final int BAD_REQUEST = 400;
    }
    public static class SingUpField{
        public static final String FIRST_NAME = "firstName";
        public static final String LAST_NAME = "lastName";
        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
    }
    public static class SingUpAttributesName{
        public static final String FIRST_NAME = "given_name";
        public static final String LAST_NAME = "family_name";
        public static final String EMAIL = "email";
    }
    public static class SingInField{
        public static final String EMAIL = "email";
        public static final String PASSWORD = "password";
    }
    public static class SingInAttributesName{
        public static final String USER_NAME = "USERNAME";
        public static final String PASSWORD = "PASSWORD";
    }
    public static class TableField{
        public static final String ID = "id";
        public static final String NUMBER = "number";
        public static final String PLACES = "places";
        public static final String IS_VIP = "isVip";
        public static final String MIN_ORDER = "minOrder";
    }
    public static class ReservationField{
        public static final String ID = "id";
        public static final String TABLE_NUMBER = "tableNumber";
        public static final String CLIENT_NAME = "clientName";
        public static final String PHONE_NUMBER = "phoneNumber";
        public static final String DATE = "date";
        public static final String SLOT_TIME_START = "slotTimeStart";
        public static final String SLOT_TIME_END = "slotTimeEnd";
    }
    public static class Path{
        public static final String SING_UP = "/singup";
        public static final String SING_IN = "/singin";
        public static final String TABLES = "/tables";
        public static final String RESERVATIONS = "/reservations";
    }

    public static String getUserPoolId() {
        AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();

        ListUserPoolsRequest listUserPoolsRequest = new ListUserPoolsRequest()
                .withMaxResults(60);

        ListUserPoolsResult listUserPoolsResult = cognitoClient.listUserPools(listUserPoolsRequest);

        for (UserPoolDescriptionType userPool : listUserPoolsResult.getUserPools()) {
            if (USER_POOL_NAME.equals(userPool.getName())) {
                return userPool.getId();
            }
        }

        throw new IllegalArgumentException("User pool with name " + USER_POOL_NAME + " not found");
    }
    public static String getUserClientId() {
        AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();
        ListUserPoolClientsRequest request = new ListUserPoolClientsRequest()
                .withUserPoolId(getUserPoolId());

        ListUserPoolClientsResult result = cognitoClient.listUserPoolClients(request);
        return result.getUserPoolClients().get(0).getClientId();
    }

}
