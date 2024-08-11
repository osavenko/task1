package com.task10.model;

import com.task10.Task10Util;

import java.util.Map;

public class Reservation {
    private String id;
    private int tableNumber;
    private String clientName;
    private String phoneNumber;
    private String date;
    private String slotTimeStart;
    private String slotTimeEnd;

    public Reservation() {
    }

    public String getId() {
        return id;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public String getClientName() {
        return clientName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDate() {
        return date;
    }

    public String getSlotTimeStart() {
        return slotTimeStart;
    }

    public String getSlotTimeEnd() {
        return slotTimeEnd;
    }
    public static Reservation getInstance(Map<String, Object> body){
        final Reservation reservation = new Reservation();
        reservation.id = body.get(Task10Util.ReservationField.ID).toString();
        reservation.tableNumber = (int)body.get(Task10Util.ReservationField.TABLE_NUMBER);
        reservation.clientName = body.get(Task10Util.ReservationField.CLIENT_NAME).toString();
        reservation.date = body.get(Task10Util.ReservationField.DATE).toString();
        reservation.slotTimeStart = body.get(Task10Util.ReservationField.SLOT_TIME_START).toString();
        reservation.slotTimeEnd = body.get(Task10Util.ReservationField.SLOT_TIME_END).toString();
        return reservation;
    }
}
