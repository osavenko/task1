package com.task11.handler;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.*;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task11.model.SignIn;
import com.task11.Task11Util;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SingInHandler {
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        final LambdaLogger logger = context.getLogger();
//        logger.log(">>>>>>>>>>>>>>SingIp body: " + request.getBody());


        if (!request.getHttpMethod().equals(Task11Util.HttpMethod.POST)) {
            logger.log("Incorrect method, need POST current: " + request.getHttpMethod());
            response.setStatusCode(Task11Util.StatusCode.BAD_REQUEST);
            return response;

        }

        try {
            final Map<String, String> singInRequest = new ObjectMapper().readValue(request.getBody(), Map.class);
            final SignIn signIn = SignIn.getInstance(singInRequest);
/*
            logger.log(">>>>>>>>>>>>>>>SingIp body: " + request.getBody());
            logger.log(">>>>>>>>>>>>>>>SingIp object: " + signIn);
*/

//            logger.log(">>>>>>>>>>>> Create AWSCognitoIdentityProvider");
            AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();

//            logger.log(">>>>>>>>>>>> AWSCognitoIdentityProvider was created");
//            logger.log(">>>>>>>>>>>> getUserPoolId(): "+getUserPoolId());
     //       List<UserType> userTypeList = getUserTypeList(cognitoClient, signIn,context);
            ListUsersRequest listUsersRequest = new ListUsersRequest()
                    .withUserPoolId(Task11Util.getUserPoolId())
                    .withLimit(60);
//            logger.log(">>>>>>>>>>>> listUsersRequest: "+listUsersRequest);
            ListUsersResult result = cognitoClient.listUsers(listUsersRequest);
//            logger.log(">>>>>>>>>>>> listUsers was called: "+result.getUsers());
            List<UserType> userTypeList = result.getUsers().stream()
                    .filter(userType ->
                            userType.getUsername().equals(signIn.getEmail())).collect(Collectors.toList());
/*
            result.getUsers().stream()
                    .forEach(userType->{
                        logger.log(">>>>>>>>>>>========"+userType.getUsername());
                        logger.log(">>>>>>>>>>>========"+userType.getAttributes());
                    });
*/

//            logger.log(">>>>>>>>>>>> userTypeList.isEmpty():  " + userTypeList.isEmpty());
            if (userTypeList.isEmpty()) {
                response.setStatusCode(Task11Util.StatusCode.BAD_REQUEST);
                return response;
            }
/*
            logger.log(">>>>>>>>>>>> getAccessToken:  " );
            logger.log(">>>>>>>>>>>> getAccessToken(PoolId):  " + getUserPoolId().toString() );
            logger.log(">>>>>>>>>>>> getAccessToken(UserClient):  " +getUserClientId());
*/
            AdminInitiateAuthRequest authRequest = new AdminInitiateAuthRequest()
                    .withAuthFlow(AuthFlowType.ADMIN_NO_SRP_AUTH)
                    .withUserPoolId(Task11Util.getUserPoolId())
                    .withClientId(Task11Util.getUserClientId())
                    .addAuthParametersEntry(Task11Util.SingInAttributesName.USER_NAME, signIn.getEmail())
                    .addAuthParametersEntry(Task11Util.SingInAttributesName.PASSWORD, signIn.getPassword());
//            logger.log(">>>>>>>>>>>> authRequest:  " +authRequest.toString());

            final String accessToken = cognitoClient.adminInitiateAuth(authRequest)
                    .getAuthenticationResult()
                    .getAccessToken();

            //final String accessToken = getAccessToken(signIn, cognitoClient);
//            logger.log(">>>>>>>>>>>> AccessToken:  "+accessToken );
            response.setStatusCode(Task11Util.StatusCode.SUCCESS);
            response.setBody("{\"accessToken\": \"" + accessToken + "\"}");
//            logger.log(">>>>>>>>>>>> ++++++++++++++++++++++++++++++++++++" );
            return response;
        } catch (Exception e) {
            logger.log("Exception: " + e.getMessage());

            response.setStatusCode(Task11Util.StatusCode.BAD_REQUEST);
            return response;
        }
    }
}
