package com.task05;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.spec.PutItemSpec;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.syndicate.deployment.annotations.environment.EnvironmentVariable;
import com.syndicate.deployment.annotations.lambda.LambdaHandler;
import com.syndicate.deployment.model.RetentionSetting;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@LambdaHandler(lambdaName = "api_handler",
        roleName = "task05-role",
        isPublishVersion = false,
        logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@EnvironmentVariable(key = "TABLE_NAME", value = "${target_table}")
public class ApiHandler implements RequestHandler<Map<String, Object>, Map<String, Object>> {
    private final AmazonDynamoDB dynamoDBClient;
    private final DynamoDB dynamoDB;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String TABLE_NAME_ENV = System.getenv("TABLE_NAME");

    public ApiHandler() {
        dynamoDBClient = new AmazonDynamoDBClient();
        dynamoDBClient.setRegion(Region.getRegion(Regions.EU_CENTRAL_1));
        dynamoDB = new DynamoDB(dynamoDBClient);
    }

    public Map<String, Object> handleRequest(Map<String, Object> request, Context context) {
        final LambdaLogger logger = context.getLogger();
        Map<String, String> content = (Map<String, String>) request.get("content");
        String createdAt = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
        int principalId = Integer.parseInt(request.get("principalId").toString());
        String id = java.util.UUID.randomUUID().toString();

        Event event = new Event();
        event.setBody(content);
        event.setId(id);
        event.setCreatedAt(createdAt);
        event.setPrincipalId(principalId);

/*
        Table table = dynamoDB.getTable(TABLE_NAME_ENV);
        table.putItem(new PutItemSpec().withItem(item));
*/


        Map<String, Object> response = new HashMap<>();
        response.put("event",event);
        response.put("statusCode",TABLE_NAME_ENV);
        return response;
    }
}
