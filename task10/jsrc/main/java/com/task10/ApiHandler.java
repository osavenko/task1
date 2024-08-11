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
import com.task10.handler.TableHandler;

import static com.task10.Task10Util.*;

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

        logger.log("=========Current request :" + request);
        logger.log("=========Current path: " + path);
        logger.log("=========Current method: " + request.getHttpMethod());
        logger.log("=========Current body: " + request.getBody());
        logger.log("========= SIGN IN" + path.equals(Path.SING_IN));
        logger.log("========= SIGN UP" + path.equals(Path.SING_UP));
        logger.log("========= RESERVATION" + path.equals(Path.RESERVATIONS));
        logger.log("========= TABLES" + path.equals(Path.TABLES));

        switch (path) {
            case Path.SING_IN: {
                logger.log("========SingIn operation ");
                return new SingInHandler().handleRequest(request, context);
            }
            case Path.SING_UP: {
                logger.log("========SingUp operation ");
                return new SingUpHandler().handleRequest(request, context);
            }
            case Path.TABLES: {
                logger.log("===========Tables operation ");
                return new TableHandler().handleRequest(request, context);
            }
            case Path.RESERVATIONS: {
                logger.log("=======Reservation operation ");
                return new TableHandler().handleRequest(request,context);
            }
            default:
                logger.log("00000000000000000000 0  00000000000 0000000 Неочикувана фігня");
        }
        logger.log("======Unknown path");
        responseEvent.setStatusCode(StatusCode.BAD_REQUEST);
        return responseEvent;
    }
}
