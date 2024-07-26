package com.task02;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.annotations.lambda.LambdaUrlConfig;
import com.syndicate.deployment.model.RetentionSetting;
import com.syndicate.deployment.model.lambda.url.AuthType;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(lambdaName = "hello_world",
        roleName = "hello_world-role",
        isPublishVersion = false,
        logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@LambdaUrlConfig(authType = AuthType.NONE)
public class HelloWorld implements RequestHandler<Object, Map<String, Object>> {

    public Map<String, Object> handleRequest(Object request, Context context) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        final HashMap<String, Object> input = (HashMap<String, Object>) request;
        final String path = input.get("rawPath").toString();
        if ("/hello".equals(path)) {
            resultMap.put("statusCode", 200);
            resultMap.put("body", "{\"statusCode\":200, \"message\": \"Hello from Lambda\"}");
            return resultMap;
        }
        Map<String, Object> requestContext = (((Map<String, Map<String, Object>>)request).get("requestContext"));
        Map<String, Object> http = (Map<String, Object>) requestContext.get("http");

        String message = "Bad request syntax or unsupported method. Request path: " + http.get("path") + ". HTTP method: " + http.get("method");

        resultMap.put("statusCode", 400);
        resultMap.put("body",  "{\"statusCode\":400, \"message\": \"" + message + "\"}");
        return resultMap;
    }
}
