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
import com.task11.model.Reservation;
import com.task11.Task11Util;

import java.util.*;
import java.util.stream.Collectors;

public class ReservationHandler {
    public static APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        final LambdaLogger logger = context.getLogger();
        try {
            AmazonDynamoDBClient dynamoDBClient = new AmazonDynamoDBClient();
            dynamoDBClient.setRegion(Region.getRegion(Regions.EU_CENTRAL_1));
            DynamoDB dynamoDB = new DynamoDB(dynamoDBClient);

            Table reservationTables = dynamoDB.getTable(Task11Util.DYNAMODB_RESERVATION);
            logger.log(">>>>>>>>>"+reservationTables.getTableName());
            response.setStatusCode(Task11Util.StatusCode.SUCCESS);
            switch (request.getHttpMethod()) {
                case Task11Util.HttpMethod.GET: {
                    logger.log(">>>>>>>>> GET TO "+request.getPath()+" and table name "+reservationTables.getTableName());
                    List<Map<String, AttributeValue>> items = DynamoDBUtils.getAllItemsFromTable(dynamoDBClient, Task11Util.DYNAMODB_RESERVATION);
                    Map<String, List<Map<String, Object>>> body = convertToJSON(items);
                    response.setBody(new ObjectMapper().writeValueAsString(body));
                    return response;
                }
                case Task11Util.HttpMethod.POST: {
                    logger.log(">>>>>>>>> POST TO "+request.getPath()+" and table name "+reservationTables.getTableName());
                    logger.log(">>>>>>>>>>> reservation body: "+request.getBody());
                    Map<String, Object> mapReservation = new ObjectMapper().readValue(request.getBody(), Map.class);

                    logger.log(">>>>>>>>>>> reservation body: "+mapReservation);

                    final Reservation reservation = Reservation.getInstance(mapReservation);
                    logger.log(">>>>>>>>>>> reservation object: "+reservation);
                    List<Map<String, AttributeValue>> reservationRecords = DynamoDBUtils.getAllItemsFromTable(dynamoDBClient, Task11Util.DYNAMODB_RESERVATION);

                    logger.log(">>>>>>>>>>> reservationRecords: "+reservationRecords.size());

                    for (Map<String, AttributeValue> reservationRecord : reservationRecords) {
                        int tableNumber = Integer.parseInt(reservationRecord.get(Task11Util.ReservationField.TABLE_NUMBER).getN());

                        if (tableNumber == reservation.getTableNumber()) {
                            response.setStatusCode(Task11Util.StatusCode.BAD_REQUEST);
                            return response;
                        }
                    }

                    List<Map<String, AttributeValue>> tableRecords = DynamoDBUtils.getAllItemsFromTable(dynamoDBClient, Task11Util.DYNAMODB_TABLE);
                    logger.log(">>>>>>>>>>>> tableRecords"+tableRecords);
                    List<Map<String, AttributeValue>> findTable = tableRecords.stream().filter(tableRecord ->
                                    Integer.parseInt(tableRecord.get(Task11Util.TableField.NUMBER).getN()) == reservation.getTableNumber())
                            .collect(Collectors.toList());
                    logger.log(">>>>>>>>>>>> findTable"+findTable);
                    logger.log(">>>>>>>> findTable.isEmpty()"+findTable.isEmpty());
                    if (findTable.isEmpty()) {
                        response.setStatusCode(Task11Util.StatusCode.BAD_REQUEST);
                        return response;
                    }
//                    String id = "uuid v4";
                    Item item = new Item()
                            .withPrimaryKey(Task11Util.ReservationField.ID, reservation.getId())
                            .withInt(Task11Util.ReservationField.TABLE_NUMBER, reservation.getTableNumber())
                            .withString(Task11Util.ReservationField.CLIENT_NAME, reservation.getClientName())
                            .withString(Task11Util.ReservationField.PHONE_NUMBER, reservation.getPhoneNumber())
                            .withString(Task11Util.ReservationField.DATE, reservation.getDate())
                            .withString(Task11Util.ReservationField.SLOT_TIME_START, reservation.getSlotTimeStart())
                            .withString(Task11Util.ReservationField.SLOT_TIME_END, reservation.getSlotTimeEnd());
                    reservationTables.putItem(item);

                    response.setBody("{\"reservationId\": \"" + reservation.getId() + "\"}");
                    response.setStatusCode(Task11Util.StatusCode.SUCCESS);
                    return response;
                }
            }
        } catch (Exception e) {
            logger.log("Exception :" + e.getMessage());
        }
        response.setStatusCode(Task11Util.StatusCode.BAD_REQUEST);
        return response;
    }

    private static Map<String, List<Map<String, Object>>> convertToJSON(List<Map<String, AttributeValue>> items) {
        List<Map<String, Object>> jItems = new ArrayList<>();
        for (Map<String, AttributeValue> item : items) {
            Map<String, Object> jItem = new HashMap<>();
            jItem.put(Task11Util.ReservationField.TABLE_NUMBER, Integer.parseInt(item.get(Task11Util.ReservationField.TABLE_NUMBER).getN()));
            jItem.put(Task11Util.ReservationField.CLIENT_NAME, item.get(Task11Util.ReservationField.CLIENT_NAME).getS());
            jItem.put(Task11Util.ReservationField.PHONE_NUMBER, item.get(Task11Util.ReservationField.PHONE_NUMBER).getS());
            jItem.put(Task11Util.ReservationField.DATE, item.get(Task11Util.ReservationField.DATE).getS());
            jItem.put(Task11Util.ReservationField.SLOT_TIME_START, item.get(Task11Util.ReservationField.SLOT_TIME_START).getS());
            jItem.put(Task11Util.ReservationField.SLOT_TIME_END, item.get(Task11Util.ReservationField.SLOT_TIME_END).getS());
            jItems.add(jItem);
        }
        Map<String, List<Map<String, Object>>> jFinal = new HashMap<>();
        jFinal.put("reservations", jItems);
        return jFinal;
    }
}
