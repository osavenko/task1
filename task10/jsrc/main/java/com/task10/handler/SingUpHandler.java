package com.task10.handler;

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
import com.task10.model.SingUp;

import java.util.Map;

import static com.task10.Task10Util.*;

public class SingUpHandler {
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        final LambdaLogger logger = context.getLogger();
/*
        logger.log("SING UP(inside)");
        logger.log("SING UP(inside) Method" + request.getHttpMethod());
        logger.log("SING UP(inside) Body"+ request.getBody());
*/

        if (!request.getHttpMethod().equals(HttpMethod.POST)) {
            logger.log("Incorrect method, need POST current: " + request.getHttpMethod());

            response.setStatusCode(StatusCode.BAD_REQUEST);
            return response;
        }

//        logger.log("SING UP(inside, before try) Body"+ request.getBody());
        try {
            Map<String, String> singUpBody = new ObjectMapper().readValue(request.getBody(), Map.class);
            SingUp singUp = SingUp.getInstance(singUpBody);


/*
            logger.log("SING UP(inside, try)SingUp body: " + request.getBody());
            logger.log("SING UP(inside, try)SingUp object: " + singUpBody.toString());

            logger.log("SING UP(inside, try) Create SingUpRequest");
*/
            SignUpRequest signUpRequest = new SignUpRequest()
                    .withClientId(getUserClientId())
                    .withUsername(singUp.getEmail())
                    .withPassword(singUp.getPassword())
                    .withUserAttributes(
                            new AttributeType().withName(SingUpAttributesName.FIRST_NAME)
                                    .withValue(singUp.getFirstName()),
                            new AttributeType().withName(SingUpAttributesName.LAST_NAME)
                                    .withValue(singUp.getLastName()),
                            new AttributeType().withName(SingUpAttributesName.EMAIL)
                                    .withValue(singUp.getEmail()));
/*
            logger.log("SING UP(inside, try) SingUpRequest was created");
            logger.log("SING UP(inside, try) Create Cognito client");
*/
            AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();
//            logger.log("SING UP(inside, try) Cognito client was created");

            cognitoClient.signUp(signUpRequest);
/*
            logger.log("SING UP(inside, try) Cognito client  singUp was called");
            logger.log("SING UP(inside, try) Create AdminConfirmSignUpRequest");
*/
            AdminConfirmSignUpRequest adminConfirm = new AdminConfirmSignUpRequest()
                    .withUsername(singUp.getEmail())
                    .withUserPoolId(getUserPoolId());
            logger.log(adminConfirm.getUserPoolId());

/*
            logger.log("SING UP(inside, try) AdminConfirmSignUpRequest was created");
            logger.log("SING UP(inside, try) Call AdminConfirmSignUpRequest adminConfirmSignUp");
*/
            cognitoClient.adminConfirmSignUp(adminConfirm);
//            logger.log("SING UP(inside, try) AdminConfirmSignUpRequest adminConfirmSignUp was called");
            response.setStatusCode(StatusCode.SUCCESS);
            return response;
        } catch (Exception e) {
            logger.log("Exception: " + e.getMessage());

            response.setStatusCode(StatusCode.BAD_REQUEST);
            return response;
        }
    }
}
