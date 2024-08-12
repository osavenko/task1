package com.task11;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import com.task11.handler.ReservationHandler;
import com.task11.handler.SingInHandler;
import com.task11.handler.SingUpHandler;
import com.task11.handler.TableHandler;

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

        switch (path) {
            case Task11Util.Path.SIGN_IN: {
                responseEvent = new SingInHandler().handleRequest(request, context);
                responseEvent.setHeaders(Task11Util.getHeaders());
                return responseEvent;
            }
            case Task11Util.Path.SING_UP: {
                responseEvent = new SingUpHandler().handleRequest(request, context);
                responseEvent.setHeaders(Task11Util.getHeaders());
                return responseEvent;
            }
            case Task11Util.Path.TABLES: {
                responseEvent = new TableHandler().handleRequest(request, context);
                responseEvent.setHeaders(Task11Util.getHeaders());
                return responseEvent;
            }
            case Task11Util.Path.RESERVATIONS: {
                responseEvent = ReservationHandler.handleRequest(request,context);
                responseEvent.setHeaders(Task11Util.getHeaders());
                return responseEvent;
            }
        }
        responseEvent = new TableHandler().handleRequest(request, context);
        responseEvent.setHeaders(Task11Util.getHeaders());
        return responseEvent;
    }
}
