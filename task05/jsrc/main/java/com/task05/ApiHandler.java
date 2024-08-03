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

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@LambdaHandler(lambdaName = "api_handler",
        roleName = "task05-role",
        isPublishVersion = false,
        logsExpiration = RetentionSetting.SYNDICATE_ALIASES_SPECIFIED
)
@EnvironmentVariable(key = "TABLE_NAME", value = "${target_table}")
public class ApiHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private final AmazonDynamoDB dynamoDBClient;
    private final DynamoDB dynamoDB;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static final String TABLE_NAME_ENV = System.getenv("TABLE_NAME");

    public ApiHandler() {
        dynamoDBClient = new AmazonDynamoDBClient();
        dynamoDBClient.setRegion(Region.getRegion(Regions.EU_CENTRAL_1));
        dynamoDB = new DynamoDB(dynamoDBClient);
    }

    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent requestEvent, Context context) {
        final LambdaLogger logger = context.getLogger();

        logger.log(requestEvent.getPath());
        logger.log(requestEvent.getHttpMethod());
        logger.log(requestEvent.getBody());
        Event event = null;
        try {
            event = objectMapper.readValue(requestEvent.getBody(), Event.class);
        } catch (JsonProcessingException e) {
            logger.log("Error objectMapper");
            e.printStackTrace();
        }

        Item item = new Item()
                .withPrimaryKey("id", UUID.randomUUID().toString())
                .withNumber("principalId", event.getPrincipalId())
                .withString("createdAt", Instant.now().toString())
                .withMap("body", event.getBody());
        logger.log("Current Item: "+item.toJSON());
        logger.log("Current table name: "+TABLE_NAME_ENV);
        Table table = dynamoDB.getTable(TABLE_NAME_ENV);
        table.putItem(new PutItemSpec().withItem(item));


        Map<String, String> response = new HashMap<>();
        try {
            response.put("event", objectMapper.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return new APIGatewayProxyResponseEvent()
                .withStatusCode(201)
                .withBody(response.toString());
    }
}
