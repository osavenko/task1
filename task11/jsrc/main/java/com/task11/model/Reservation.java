package com.task11.model;

import com.task11.Task11Util;

import java.util.Map;
import java.util.UUID;

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
        reservation.id = UUID.randomUUID().toString();
        reservation.tableNumber = (int)body.get(Task11Util.ReservationField.TABLE_NUMBER);
        reservation.clientName = body.get(Task11Util.ReservationField.CLIENT_NAME).toString();
        reservation.phoneNumber = body.get(Task11Util.ReservationField.PHONE_NUMBER).toString();
        reservation.date = body.get(Task11Util.ReservationField.DATE).toString();
        reservation.slotTimeStart = body.get(Task11Util.ReservationField.SLOT_TIME_START).toString();
        reservation.slotTimeEnd = body.get(Task11Util.ReservationField.SLOT_TIME_END).toString();
        return reservation;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id='" + id + '\'' +
                ", tableNumber=" + tableNumber +
                ", clientName='" + clientName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", date='" + date + '\'' +
                ", slotTimeStart='" + slotTimeStart + '\'' +
                ", slotTimeEnd='" + slotTimeEnd + '\'' +
                '}';
    }
}
