package com.task10.handler;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task10.DynamoDBUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.task10.Task10Util.*;

public class TableHandler {
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        final LambdaLogger logger = context.getLogger();
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient();
            dynamoDBClient.setRegion(Region.getRegion(Regions.EU_CENTRAL_1));
            DynamoDB dynamoDB = new DynamoDB(dynamoDBClient);

            Table tables = dynamoDB.getTable(DYNAMODB_TABLE);
            response.setStatusCode(StatusCode.SUCCESS);
            switch (request.getHttpMethod()) {
                case HttpMethod.GET: {
                    String[] splitPath = request.getPath().split("/");
                    logger.log("++++++++++++++++++>>>"+request.getPath());
                    if (splitPath.length > 2) {
                        logger.log("++++++++++++++++++>>> splitPath.length > 2");
                        Item item = tables.getItem(TableField.ID, Integer.parseInt(splitPath[splitPath.length - 1]));
                        Map<String, Object> body = convertItemToMap(item);
                        response.setBody(new ObjectMapper().writeValueAsString(body));
                    } else {
                        logger.log("++++++++++++++++++>>> splitPath.length < 2"+request.getPath());
                        List<Map<String, AttributeValue>> items = DynamoDBUtils.getAllItemsFromTable(dynamoDBClient, DYNAMODB_TABLE);
                        Map<String, List<Map<String, Object>>> body = convertItemToMap(items);
                        response.setBody(new ObjectMapper().writeValueAsString(body));
                    }
                    return response;
                }
                case HttpMethod.POST: {
                    Map tableBody = new ObjectMapper().readValue(request.getBody(), Map.class);
                    final com.task10.model.Table tableObject = com.task10.model.Table.getInstance(tableBody);
                    Item item = new Item()
                            .withPrimaryKey(TableField.ID, tableObject.getId())
                            .withInt(TableField.NUMBER, tableObject.getNumber())
                            .withInt(TableField.PLACES, tableObject.getPlaces())
                            .withBoolean(TableField.IS_VIP, tableObject.isVip())
                            .withInt(TableField.MIN_ORDER, tableObject.getMinOrder());
                    tables.putItem(item);

                    response.setBody("{\"id\": " + tableObject.getId() + "}");
                    return response;
                }
            }
        } catch (Exception e) {
            logger.log("Exception :"+e.getMessage());
        }
        response.setStatusCode(StatusCode.BAD_REQUEST);
        return response;
    }

    private static Map<String, Object> convertItemToMap(Item item) {
        Map<String, Object> map = new HashMap<>();
        map.put(TableField.ID, item.getNumber(TableField.ID));
        map.put(TableField.NUMBER, item.getNumber(TableField.NUMBER));
        map.put(TableField.PLACES, item.getNumber(TableField.PLACES));
        map.put(TableField.IS_VIP, item.getBOOL(TableField.IS_VIP));
        map.put(TableField.MIN_ORDER, item.getNumber(TableField.MIN_ORDER));
        return map;
    }
    private static Map<String, List<Map<String, Object>>> convertItemToMap(List<Map<String, AttributeValue>> items) {
        List<Map<String, Object>> jItems = new ArrayList<>();

        for (Map<String, AttributeValue> item : items) {
            Map<String, Object> jItem = new HashMap<>();
            jItem.put(TableField.ID, Integer.parseInt(item.get(TableField.ID).getN()));
            jItem.put(TableField.NUMBER, Integer.parseInt(item.get(TableField.NUMBER).getN()));
            jItem.put(TableField.PLACES, Integer.parseInt(item.get(TableField.PLACES).getN()));
            jItem.put(TableField.IS_VIP, item.get(TableField.IS_VIP).getBOOL());
            jItem.put(TableField.MIN_ORDER, Integer.parseInt(item.get(TableField.MIN_ORDER).getN()));
            jItems.add(jItem);
        }
        Map<String, List<Map<String, Object>>> jFinal = new HashMap<>();
        jFinal.put("tables", jItems);
        return jFinal;
    }
}
