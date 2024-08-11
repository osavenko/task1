package com.task10;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import com.task10.handler.SingInHandler;
import com.task10.handler.SingUpHandler;

@LambdaHandler(lambdaName = "api_handler",
        roleName = "api_handler-role",
        isPublishVersion = false,
        logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        APIGatewayProxyResponseEvent responseEvent = new APIGatewayProxyResponseEvent();
        String path = request.getPath();
        final LambdaLogger logger = context.getLogger();

        logger.log("Current request :" + request);
        logger.log("Current path: " + path);
        logger.log("Current method: " + request.getHttpMethod());
        logger.log("Current body: " + request.getBody());
        switch (path) {
            case Task10Util.Path.SING_IN: {
                logger.log("Singin operation ");
                return new SingInHandler().execute(request,context);
            }
            case Task10Util.Path.SING_UP: {
                logger.log("Singup operation ");
                return new SingUpHandler().execute(request,context);
            }
            case Task10Util.Path.TABLES:
                logger.log("Tables operation ");
            case Task10Util.Path.RESERVATIONS:
                logger.log("Reservation operation ");
        }
		logger.log("Unknown path");
        responseEvent.setStatusCode(201);
        responseEvent.setBody("Unknown path");
        return responseEvent;
    }
}
