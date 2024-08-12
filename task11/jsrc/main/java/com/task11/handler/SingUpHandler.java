package com.task11.handler;

import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.cognitoidp.model.AdminConfirmSignUpRequest;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task11.model.SingUp;
import com.task11.Task11Util;

import java.util.Map;

public class SingUpHandler {
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        final LambdaLogger logger = context.getLogger();
        logger.log("SIGN UP(inside)");
        logger.log("SIGN UP(inside) Method" + request.getHttpMethod());
        logger.log("SIGN UP(inside) Body"+ request.getBody());

        if (!request.getHttpMethod().equals(Task11Util.HttpMethod.POST)) {
            logger.log("Incorrect method, need POST current: " + request.getHttpMethod());

            response.setStatusCode(Task11Util.StatusCode.BAD_REQUEST);
            return response;
        }

        logger.log("SIGN UP(inside, before try) Body"+ request.getBody());
        try {
            Map<String, String> singUpBody = new ObjectMapper().readValue(request.getBody(), Map.class);
            SingUp singUp = SingUp.getInstance(singUpBody);
            logger.log("SIGN UP(inside, try)SingUp body: " + request.getBody());
            logger.log("SIGN UP(inside, try)SingUp object: " + singUpBody.toString());

            logger.log("SIGN UP(inside, try) Create SingUpRequest");
            SignUpRequest signUpRequest = new SignUpRequest()
                    .withClientId(Task11Util.getUserClientId())
                    .withUsername(singUp.getEmail())
                    .withPassword(singUp.getPassword())
                    .withUserAttributes(
                            new AttributeType().withName(Task11Util.SingUpAttributesName.FIRST_NAME)
                                    .withValue(singUp.getFirstName()),
                            new AttributeType().withName(Task11Util.SingUpAttributesName.LAST_NAME)
                                    .withValue(singUp.getLastName()),
                            new AttributeType().withName(Task11Util.SingUpAttributesName.EMAIL)
                                    .withValue(singUp.getEmail()));
            logger.log("SIGN UP(inside, try) SingUpRequest was created");
            logger.log("SIGN UP(inside, try) Create Cognito client");
            AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();
            logger.log("SIGN UP(inside, try) Cognito client was created");

            cognitoClient.signUp(signUpRequest);
            logger.log("SIGN UP(inside, try) Cognito client  singUp was called");
            logger.log("SIGN UP(inside, try) Create AdminConfirmSignUpRequest");
            AdminConfirmSignUpRequest adminConfirm = new AdminConfirmSignUpRequest()
                    .withUsername(singUp.getEmail())
                    .withUserPoolId(Task11Util.getUserPoolId());
            logger.log(adminConfirm.getUserPoolId());

            logger.log("SIGN UP(inside, try) AdminConfirmSignUpRequest was created");
            logger.log("SIGN UP(inside, try) Call AdminConfirmSignUpRequest adminConfirmSignUp");
            cognitoClient.adminConfirmSignUp(adminConfirm);
            logger.log("SIGN UP(inside, try) AdminConfirmSignUpRequest adminConfirmSignUp was called");
            response.setStatusCode(Task11Util.StatusCode.SUCCESS);
            return response;
        } catch (Exception e) {
            logger.log("Exception: " + e.getMessage());

            response.setStatusCode(Task11Util.StatusCode.BAD_REQUEST);
            return response;
        }
    }
}
