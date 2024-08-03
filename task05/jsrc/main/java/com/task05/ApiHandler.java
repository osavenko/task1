package com.task05;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

@LambdaHandler(lambdaName = "api_handler",
        roleName = "task05-role",
        isPublishVersion = false,
        logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@EnvironmentVariable(key = "TABLE_NAME", value = "${target_table}")
public class ApiHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {
    private static final String TABLE_NAME_ENV = "TABLE_NAME";
    private final String tableName;
    private final DynamoDbClient db = DynamoDbClient.builder().build();
    public ApiHandler(){
        tableName = System.getenv(TABLE_NAME_ENV).contains("smtr-2c83ab08")
                ? System.getenv(TABLE_NAME_ENV) : "cmtr-2c83ab08-Events";
    }
    public Map<String, Object> handleRequest(Map<String, Object> request, Context context) {
        Event event = parseEvent(request);
        saveEvent(event);
        Map<String, Object> response = new HashMap<>();
        response.put("statusCode", 201);
        response.put("event", event);
        return response;
    }

    @SuppressWarnings("unchecked")
    private Event parseEvent(Map<String, Object> request) {
        int principalId = (int) request.get("principalId");
        Map<String, Object> content = (Map<String, Object>) request.get("content");
        Map<String, String> contentStringMap = new HashMap<>();
        content.forEach((key, value) -> contentStringMap.put(key, String.valueOf(value)));
        return new Event(principalId, contentStringMap);
    }

    private void saveEvent(Event event) {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("id", AttributeValue.builder().s(event.getId()).build());
        item.put("principalId", AttributeValue.builder().n(String.valueOf(event.getPrincipalId())).build());
        item.put("createdAt", AttributeValue.builder().s(event.getCreatedAt()).build());
        item.put("body", AttributeValue.builder().m(convertMapToAttributeValue(event.getBody())).build());
        db.putItem(builder -> builder.tableName(tableName).item(item));
    }

    private Map<String, AttributeValue> convertMapToAttributeValue(Map<String, String> map) {
        Map<String, AttributeValue> result = new HashMap<>();
        map.forEach((key, value) -> result.put(key, AttributeValue.builder().s(value).build()));
        return result;
    }
}
