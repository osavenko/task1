package com.task11.handler;

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
import com.task11.DynamoDBUtils;
import com.task11.Task11Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TableHandler {
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        LambdaLogger logger = context.getLogger();
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient();
            dynamoDBClient.setRegion(Region.getRegion(Regions.EU_CENTRAL_1));
            DynamoDB dynamoDB = new DynamoDB(dynamoDBClient);

            Table tables = dynamoDB.getTable(Task11Util.DYNAMODB_TABLE);
            response.setStatusCode(Task11Util.StatusCode.SUCCESS);
            switch (request.getHttpMethod()) {
                case Task11Util.HttpMethod.GET: {
                    String[] splitPath = request.getPath().split("/");
//                    logger.log("++++++++++++++++++>>>"+request.getPath());
                    if (splitPath.length > 2) {
//                        logger.log("++++++++++++++++++>>> splitPath.length > 2");
                        Item item = tables.getItem(Task11Util.TableField.ID, Integer.parseInt(splitPath[splitPath.length - 1]));
                        Map<String, Object> body = convertItemToMap(item);
                        response.setBody(new ObjectMapper().writeValueAsString(body));
                    } else {
//                        logger.log("++++++++++++++++++>>> splitPath.length < 2"+request.getPath());
                        List<Map<String, AttributeValue>> items = DynamoDBUtils.getAllItemsFromTable(dynamoDBClient, Task11Util.DYNAMODB_TABLE);
                        Map<String, List<Map<String, Object>>> body = convertItemToMap(items);
                        response.setBody(new ObjectMapper().writeValueAsString(body));
                    }
                    return response;
                }
                case Task11Util.HttpMethod.POST: {
                    Map tableBody = new ObjectMapper().readValue(request.getBody(), Map.class);
                    final com.task11.model.Table tableObject = com.task11.model.Table.getInstance(tableBody);
                    Item item = new Item()
                            .withPrimaryKey(Task11Util.TableField.ID, tableObject.getId())
                            .withInt(Task11Util.TableField.NUMBER, tableObject.getNumber())
                            .withInt(Task11Util.TableField.PLACES, tableObject.getPlaces())
                            .withBoolean(Task11Util.TableField.IS_VIP, tableObject.isVip())
                            .withInt(Task11Util.TableField.MIN_ORDER, tableObject.getMinOrder());
                    tables.putItem(item);

                    response.setBody("{\"id\": " + tableObject.getId() + "}");
                    return response;
                }
            }
        } catch (Exception e) {
            logger.log("Exception :"+e.getMessage());
        }
        response.setStatusCode(Task11Util.StatusCode.BAD_REQUEST);
        return response;
    }

    private static Map<String, Object> convertItemToMap(Item item) {
        Map<String, Object> map = new HashMap<>();
        map.put(Task11Util.TableField.ID, item.getNumber(Task11Util.TableField.ID));
        map.put(Task11Util.TableField.NUMBER, item.getNumber(Task11Util.TableField.NUMBER));
        map.put(Task11Util.TableField.PLACES, item.getNumber(Task11Util.TableField.PLACES));
        map.put(Task11Util.TableField.IS_VIP, item.getBOOL(Task11Util.TableField.IS_VIP));
        map.put(Task11Util.TableField.MIN_ORDER, item.getNumber(Task11Util.TableField.MIN_ORDER));
        return map;
    }
    private static Map<String, List<Map<String, Object>>> convertItemToMap(List<Map<String, AttributeValue>> items) {
        List<Map<String, Object>> jItems = new ArrayList<>();

        for (Map<String, AttributeValue> item : items) {
            Map<String, Object> jItem = new HashMap<>();
            jItem.put(Task11Util.TableField.ID, Integer.parseInt(item.get(Task11Util.TableField.ID).getN()));
            jItem.put(Task11Util.TableField.NUMBER, Integer.parseInt(item.get(Task11Util.TableField.NUMBER).getN()));
            jItem.put(Task11Util.TableField.PLACES, Integer.parseInt(item.get(Task11Util.TableField.PLACES).getN()));
            jItem.put(Task11Util.TableField.IS_VIP, item.get(Task11Util.TableField.IS_VIP).getBOOL());
            jItem.put(Task11Util.TableField.MIN_ORDER, Integer.parseInt(item.get(Task11Util.TableField.MIN_ORDER).getN()));
            jItems.add(jItem);
        }
        Map<String, List<Map<String, Object>>> jFinal = new HashMap<>();
        jFinal.put("tables", jItems);
        return jFinal;
    }
}
