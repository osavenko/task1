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

        if (!request.getHttpMethod().equals(HttpMethod.POST)) {
            //logger.log("Incorrect method, need POST current: " + request.getHttpMethod());

            response.setStatusCode(StatusCode.BAD_REQUEST);
            return response;
        }

        try {
            Map<String, String> singUpBody = new ObjectMapper().readValue(request.getBody(), Map.class);
            SingUp singUp = SingUp.getInstance(singUpBody);

/*
            logger.log("SingUp body: " + request.getBody());
            logger.log("SingUp object: " + singUpBody.toString());
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

            AWSCognitoIdentityProvider cognitoClient = AWSCognitoIdentityProviderClientBuilder.defaultClient();
            cognitoClient.signUp(signUpRequest);

            AdminConfirmSignUpRequest adminConfirm = new AdminConfirmSignUpRequest()
                    .withUsername(singUp.getEmail())
                    .withUserPoolId(getUserPoolId());

            cognitoClient.adminConfirmSignUp(adminConfirm);

            response.setStatusCode(StatusCode.SUCCESS);
            return response;
        } catch (Exception e) {
//            logger.log("Exception: " + e.getMessage());

            response.setStatusCode(StatusCode.BAD_REQUEST);
            return response;
        }
    }
}
