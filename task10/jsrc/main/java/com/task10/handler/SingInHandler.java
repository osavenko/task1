package com.task10.handler;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task10.model.SignIn;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.task10.Task10Util.*;

public class SingInHandler {
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        final LambdaLogger logger = context.getLogger();
        logger.log(">>>>>>>>>>>>>>SingIp body: " + request.getBody());


        if (!request.getHttpMethod().equals(HttpMethod.POST)) {
            logger.log("Incorrect method, need POST current: " + request.getHttpMethod());
            response.setStatusCode(StatusCode.BAD_REQUEST);
            return response;

        }

        try {
            final Map<String, String> singInRequest = new ObjectMapper().readValue(request.getBody(), Map.class);
            final SignIn signIn = SignIn.getInstance(singInRequest);
            logger.log(">>>>>>>>>>>>>>>SingIp body: " + request.getBody());
            logger.log(">>>>>>>>>>>>>>>SingIp object: " + signIn);

            logger.log(">>>>>>>>>>>> Create AWSCognitoIdentityProvider");
            AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();

            logger.log(">>>>>>>>>>>> AWSCognitoIdentityProvider was created");
            List<UserType> userTypeList = getUserTypeList(cognitoClient, signIn);
            logger.log(">>>>>>>>>>>> getUserTypeList:  " + userTypeList.isEmpty());
            if (userTypeList.isEmpty()) {
                response.setStatusCode(StatusCode.BAD_REQUEST);
                return response;
            }
            logger.log(">>>>>>>>>>>> getAccessToken:  " );
            logger.log(">>>>>>>>>>>> getAccessToken(PoolId):  " + getUserPoolId().toString() );
            logger.log(">>>>>>>>>>>> getAccessToken(UserClient):  " +getUserClientId());
            AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest()
                    .withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                    .withUserPoolId(getUserPoolId())
                    .withClientId(getUserClientId())
                    .addAuthParametersEntry(SingInAttributesName.USER_NAME, signIn.getEmail())
                    .addAuthParametersEntry(SingInAttributesName.PASSWORD, signIn.getPassword());
            logger.log(">>>>>>>>>>>> authRequest:  " +authRequest.toString());

            final String accessToken = cognitoClient.adminInitiateAuth(authRequest)
                    .getAuthenticationResult()
                    .getAccessToken();

            //final String accessToken = getAccessToken(signIn, cognitoClient);
            logger.log(">>>>>>>>>>>> AccessToken:  "+accessToken );
            response.setStatusCode(StatusCode.SUCCESS);
            response.setBody("{\"accessToken\": \"" + accessToken + "\"}");
            logger.log(">>>>>>>>>>>> ++++++++++++++++++++++++++++++++++++" );
            return response;
        } catch (Exception e) {
            logger.log("Exception: " + e.getMessage());

            response.setStatusCode(StatusCode.BAD_REQUEST);
            return response;
        }

    }

    private String getAccessToken(SignIn signIn, AWSCognitoIdentityProvider cognitoClient) {

        AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest()
                .withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                .withUserPoolId(getUserPoolId())
                .withClientId(getUserClientId())
                .addAuthParametersEntry(SingInAttributesName.USER_NAME, signIn.getEmail())
                .addAuthParametersEntry(SingInAttributesName.PASSWORD, signIn.getPassword());

        return cognitoClient.adminInitiateAuth(authRequest)
                .getAuthenticationResult()
                .getAccessToken();
    }

    private List<UserType> getUserTypeList(AWSCognitoIdentityProvider cognitoClient, SignIn signIn) {
        ListUsersRequest listUsersRequest = new ListUsersRequest()
                .withUserPoolId(getUserPoolId())
                .withLimit(60);
        ListUsersResult listUsersResult = cognitoClient.listUsers(listUsersRequest);

        return listUsersResult.getUsers().stream()
                .filter(u -> u.getUsername().equals(signIn.getEmail()))
                .collect(Collectors.toList());
    }
}
