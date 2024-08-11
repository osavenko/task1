package com.task10.handler;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task10.model.SingIn;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.task10.Task10Util.*;

public class SingInHandler {
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        final LambdaLogger logger = context.getLogger();
        if (!request.getHttpMethod().equals(HttpMethod.POST)) {
            logger.log("Incorrect method, need POST current: " + request.getHttpMethod());

            response.setStatusCode(StatusCode.BAD_REQUEST);
            return response;

        }

        try {
            final Map<String, String> singInRequest = new ObjectMapper().readValue(request.getBody(), Map.class);
            final SingIn singIn = SingIn.getInstance(singInRequest);
            logger.log("SingIp body: " + request.getBody());
            logger.log("SingIp object: " + singIn.toString());


            AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();

            List<UserType> userTypeList = getUserTypeList(cognitoClient, singIn);
            if (userTypeList.isEmpty()) {
                logger.log("userTypeList.isEmpty()");
                response.setStatusCode(StatusCode.BAD_REQUEST);
                return response;
            }

            final String accessToken = getAccessToken(singIn, cognitoClient);

            response.setStatusCode(StatusCode.SUCCESS);
            response.setBody("{\"accessToken\": \"" + accessToken + "\"}");
            return response;
        } catch (Exception e) {
            logger.log("Exception: " + e.getMessage());

            response.setStatusCode(StatusCode.BAD_REQUEST);
            return response;
        }

    }

    private String getAccessToken(SingIn singIn, AWSCognitoIdentityProvider cognitoClient) {
        AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest()
                .withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .withUserPoolId(getUserPoolId())
                .withClientId(getUserClientId())
                .addAuthParametersEntry(SingInAttributesName.USER_NAME, singIn.getEmail())
                .addAuthParametersEntry(SingInAttributesName.PASSWORD, singIn.getPassword());

        return cognitoClient.adminInitiateAuth(authRequest)
                .getAuthenticationResult()
                .getAccessToken();
    }

    private List<UserType> getUserTypeList(AWSCognitoIdentityProvider cognitoClient, SingIn singIn) {
        ListUsersRequest listUsersRequest = new ListUsersRequest()
                .withUserPoolId(getUserPoolId())
                .withLimit(60);
        ListUsersResult listUsersResult = cognitoClient.listUsers(listUsersRequest);

        List<UserType> userTypeList = listUsersResult.getUsers().stream()
                .filter(u -> u.getUsername().equals(singIn.getEmail()))
                .collect(Collectors.toList());
        return userTypeList;
    }
}
